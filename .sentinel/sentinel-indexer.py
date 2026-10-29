#!/usr/bin/env python3
"""
Sentinel Indexer (Turbovec) — Varre o codebase e gera embeddings vetoriais no turbovec + SQLite id-map.

Uso:
    python sentinel-indexer.py            # indexa tudo incrementally
    python sentinel-indexer.py --reset    # limpa tudo e reindexa do zero
"""
import os

# Use the locally cached embedding model — never hit the network at startup.
# Without this, SentenceTransformer stalls for minutes probing the HF Hub.
os.environ.setdefault("HF_HUB_OFFLINE", "1")
os.environ.setdefault("TRANSFORMERS_OFFLINE", "1")
# Avoid tokenizer fork-parallelism warnings/stalls in daemon/non-TTY contexts.
os.environ.setdefault("TOKENIZERS_PARALLELISM", "false")
# Single-thread the native math pools (OpenMP/MKL). Without this, torch's worker
# pool deadlocks at model load when the process has no controlling TTY (daemon,
# nohup, backgrounded). Embedding on this tiny model is CPU-cheap anyway.
os.environ.setdefault("OMP_NUM_THREADS", "1")
os.environ.setdefault("MKL_NUM_THREADS", "1")

import sys
import sqlite3
import hashlib
import time

import numpy as np
from turbovec import IdMapIndex
from sentence_transformers import SentenceTransformer

# --- Configuração ---
PROJECT_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
INDEX_PATH = os.path.join(os.path.dirname(__file__), "turbovec_index.tvim")
DB_PATH = os.path.join(os.path.dirname(__file__), "sentinel-map.db")
MODEL_NAME = "all-MiniLM-L6-v2"  # rápido, 384 dims, roda em CPU

# Extensões de código a indexar
CODE_EXTENSIONS = {".java", ".kt", ".kts", ".sql", ".xml", ".properties", ".md"}
# Diretórios a ignorar
IGNORE_DIRS = {".sentinel", ".git", "build", "bin", "geodata", ".settings"}
# Tamanho máximo por chunk (caracteres)
CHUNK_SIZE = 2000
CHUNK_OVERLAP = 200
# Ignora arquivos maiores que isso (bytes)
MAX_FILE_BYTES = 500_000

# Embedding dimension (must match sentence-transformers all-MiniLM-L6-v2)
EMBEDDING_DIM = 384


def init_db():
    """Create SQLite schema if not exists."""
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS chunks (
        id TEXT PRIMARY KEY,
        path TEXT NOT NULL,
        chunk_idx INTEGER NOT NULL,
        ext TEXT NOT NULL,
        text TEXT NOT NULL,
        embedding_id TEXT NOT NULL
    )
    """)
    cursor.execute("CREATE INDEX IF NOT EXISTS idx_path ON chunks(path)")
    conn.commit()
    return conn


def should_index(path: str) -> bool:
    ext = os.path.splitext(path)[1].lower()
    if ext not in CODE_EXTENSIONS:
        return False
    parts = set(path.split(os.sep))
    if parts & IGNORE_DIRS:
        return False
    try:
        if os.path.getsize(path) > MAX_FILE_BYTES:
            return False
    except OSError:
        return False
    return True


def chunk_text(text: str) -> list[str]:
    """Divide texto em chunks com sobreposição."""
    if len(text) <= CHUNK_SIZE:
        return [text]
    chunks = []
    start = 0
    while start < len(text):
        end = start + CHUNK_SIZE
        chunks.append(text[start:end])
        start += CHUNK_SIZE - CHUNK_OVERLAP
    return chunks


def make_embedding_id(rel: str, idx: int) -> np.uint64:
    """Deterministic uint64 ID from path and chunk index."""
    hasher = hashlib.md5(f"{rel}:{idx}".encode())
    hash_bytes = hasher.digest()[:8]
    return np.uint64(int.from_bytes(hash_bytes, byteorder='little'))


def collect_files() -> list[str]:
    files = []
    for root, dirs, filenames in os.walk(PROJECT_ROOT):
        dirs[:] = [d for d in dirs if d not in IGNORE_DIRS]
        for fn in filenames:
            full = os.path.join(root, fn)
            if should_index(full):
                files.append(full)
    return files


def main():
    reset = "--reset" in sys.argv

    print(f"[Indexer] Carregando modelo de embeddings: {MODEL_NAME}")
    model = SentenceTransformer(MODEL_NAME)

    # Initialize DB
    print(f"[Indexer] Inicializando banco SQLite em {DB_PATH}")
    conn = init_db()

    # Handle --reset
    if reset:
        print("[Indexer] --reset: limpando índice antigo...")
        try:
            os.remove(INDEX_PATH)
            print(f"[Indexer] Removido {INDEX_PATH}")
        except FileNotFoundError:
            pass
        cursor = conn.cursor()
        cursor.execute("DELETE FROM chunks")
        conn.commit()
        print("[Indexer] Banco SQLite truncado")

    # Create or load turbovec index
    if os.path.exists(INDEX_PATH) and not reset:
        print(f"[Indexer] Carregando índice existente de {INDEX_PATH}")
        index = IdMapIndex.load(INDEX_PATH)
    else:
        print(f"[Indexer] Criando novo índice (dim={EMBEDDING_DIM})")
        index = IdMapIndex(dim=EMBEDDING_DIM, bit_width=4)

    files = collect_files()
    print(f"[Indexer] {len(files)} arquivos de código encontrados. Indexando...")

    batch_ids, batch_docs, batch_meta = [], [], []
    total_chunks = 0
    start_time = time.time()

    def flush():
        nonlocal batch_ids, batch_docs, batch_meta, total_chunks
        if not batch_ids:
            return
        # Incremental safety: turbovec's add_with_ids rejects IDs already present
        # (unlike ChromaDB's upsert). Drop any that survive from a previous run so
        # re-indexing without --reset never raises "id already present".
        for eid in batch_ids:
            if index.contains(int(eid)):
                index.remove(int(eid))
        embeddings_array = model.encode(batch_docs, show_progress_bar=False).astype(np.float32)
        ids_array = np.array(batch_ids, dtype=np.uint64)
        index.add_with_ids(embeddings_array, ids_array)
        cursor = conn.cursor()
        for i in range(len(batch_meta)):
            mid = hashlib.md5(f"{batch_meta[i]['path']}:{batch_meta[i]['chunk']}".encode()).hexdigest()
            cursor.execute("""
            INSERT OR REPLACE INTO chunks (id, path, chunk_idx, ext, text, embedding_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """, (mid, batch_meta[i]["path"], batch_meta[i]["chunk"], batch_meta[i]["ext"], batch_docs[i], str(int(batch_ids[i]))))
        conn.commit()
        total_chunks += len(batch_ids)
        elapsed = time.time() - start_time
        print(f"[Indexer] {total_chunks} chunks indexados ({elapsed:.1f}s)...", end="\r")
        batch_ids, batch_docs, batch_meta = [], [], []

    for i, path in enumerate(files):
        try:
            with open(path, "r", encoding="utf-8", errors="ignore") as f:
                content = f.read()
        except OSError:
            continue
        if not content.strip():
            continue

        rel = os.path.relpath(path, PROJECT_ROOT)

        # Incremental mode: drop this file's previously-indexed chunks first.
        # Without this, if a file shrank (fewer chunks than before), the old
        # higher-index chunks would be orphaned in both the index and SQLite.
        # (Skipped on --reset — the table was already truncated.)
        if not reset:
            flush()  # commit any pending batch before mutating this path
            cur = conn.cursor()
            cur.execute("SELECT embedding_id FROM chunks WHERE path = ?", (rel,))
            for (old_eid,) in cur.fetchall():
                eid = int(old_eid)
                if index.contains(eid):
                    index.remove(eid)
            cur.execute("DELETE FROM chunks WHERE path = ?", (rel,))
            conn.commit()

        for idx, chunk in enumerate(chunk_text(content)):
            embedding_id = make_embedding_id(rel, idx)
            batch_ids.append(embedding_id)
            batch_docs.append(chunk)
            batch_meta.append({
                "path": rel,
                "chunk": idx,
                "ext": os.path.splitext(path)[1].lower(),
            })
            if len(batch_ids) >= 128:
                flush()

    flush()

    elapsed = time.time() - start_time
    print(f"\n[Indexer] Salvando índice turbovec em {INDEX_PATH}")
    index.write(INDEX_PATH)
    print(f"[Indexer] ✅ Concluído. {total_chunks} chunks de {len(files)} arquivos em {elapsed:.1f}s")
    print(f"[Indexer] Index: {INDEX_PATH}")
    print(f"[Indexer] Map DB: {DB_PATH}")
    
    conn.close()


if __name__ == "__main__":
    main()
