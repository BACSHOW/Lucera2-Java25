#!/usr/bin/env python3
"""
Sentinel Watchdog (Turbovec) — Daemon que monitora alterações em arquivos de código
e re-indexa automaticamente no turbovec + SQLite.

Uso:
    python sentinel-watchdog.py           # inicia em foreground
    python sentinel-watchdog.py &         # inicia em background
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

import sqlite3
import hashlib
import threading
import time

import numpy as np
from turbovec import IdMapIndex
from sentence_transformers import SentenceTransformer
from watchdog.observers.polling import PollingObserver as Observer
from watchdog.events import FileSystemEventHandler

# --- Configuração (deve bater com sentinel-indexer.py) ---
PROJECT_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
INDEX_PATH = os.path.join(os.path.dirname(__file__), "turbovec_index.tvim")
DB_PATH = os.path.join(os.path.dirname(__file__), "sentinel-map.db")
MODEL_NAME = "all-MiniLM-L6-v2"
EMBEDDING_DIM = 384

CODE_EXTENSIONS = {".java", ".kt", ".kts", ".sql", ".xml", ".properties", ".md"}
IGNORE_DIRS = {".sentinel", ".git", "build", "bin", "geodata", ".settings", "backup", "logs"}
CHUNK_SIZE = 2000
CHUNK_OVERLAP = 200
MAX_FILE_BYTES = 500_000


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


def reindex_file(path: str, model, index, conn):
    """Re-indexa um único arquivo no turbovec + SQLite."""
    if not should_index(path):
        return
    
    rel = os.path.relpath(path, PROJECT_ROOT)
    cursor = conn.cursor()

    # Find and remove old chunks by embedding_id (stored as string).
    # Guard with contains() so a stale SQLite row never crashes the reindex.
    cursor.execute("SELECT embedding_id FROM chunks WHERE path = ?", (rel,))
    old_rows = cursor.fetchall()
    for (embedding_id_str,) in old_rows:
        eid = int(embedding_id_str)
        if index.contains(eid):
            index.remove(eid)

    # Delete from SQLite
    cursor.execute("DELETE FROM chunks WHERE path = ?", (rel,))
    conn.commit()

    # Read and chunk new file
    try:
        with open(path, "r", encoding="utf-8", errors="ignore") as f:
            content = f.read()
    except OSError:
        return

    if not content.strip():
        return

    # Index new chunks
    ids, docs, metas = [], [], []
    for idx, chunk in enumerate(chunk_text(content)):
        embedding_id = make_embedding_id(rel, idx)
        ids.append(embedding_id)
        docs.append(chunk)
        metas.append({
            "path": rel,
            "chunk": idx,
            "ext": os.path.splitext(path)[1].lower(),
        })

    if ids:
        # Guard: drop any residual IDs still present so add_with_ids never
        # raises "id already present" (turbovec does not overwrite on add).
        for eid in ids:
            if index.contains(int(eid)):
                index.remove(int(eid))

        embeddings_array = model.encode(docs, show_progress_bar=False).astype(np.float32)
        ids_array = np.array(ids, dtype=np.uint64)
        index.add_with_ids(embeddings_array, ids_array)

        # Insert into SQLite
        for i in range(len(metas)):
            mid = hashlib.md5(f"{metas[i]['path']}:{metas[i]['chunk']}".encode()).hexdigest()
            cursor.execute("""
            INSERT INTO chunks (id, path, chunk_idx, ext, text, embedding_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """, (mid, metas[i]["path"], metas[i]["chunk"], metas[i]["ext"], docs[i], str(int(ids[i]))))
        conn.commit()

    # Sync index to disk
    index.sync(INDEX_PATH)
    print(f"[Watchdog] Re-indexado: {rel} ({len(ids)} chunks)")


def remove_file(path: str, index, conn):
    """Remove chunks de um arquivo deletado."""
    rel = os.path.relpath(path, PROJECT_ROOT)
    cursor = conn.cursor()
    
    cursor.execute("SELECT embedding_id FROM chunks WHERE path = ?", (rel,))
    rows = cursor.fetchall()
    if not rows:
        return  # nothing indexed for this path; skip the sync entirely

    for (embedding_id_str,) in rows:
        eid = int(embedding_id_str)
        if index.contains(eid):
            index.remove(eid)

    cursor.execute("DELETE FROM chunks WHERE path = ?", (rel,))
    conn.commit()

    index.sync(INDEX_PATH)
    print(f"[Watchdog] Removido do índice: {rel}")


# Ignore repeated events for the same path within this window (editors emit
# several on_modified per save). Coalesces a burst into a single reindex.
DEBOUNCE_SECONDS = 0.5


class CodeChangeHandler(FileSystemEventHandler):
    def __init__(self, model, index, conn):
        self.model = model
        self.index = index
        self.conn = conn
        # watchdog dispatches events on its own thread; the SQLite connection and
        # the turbovec index are NOT safe for concurrent use, so every mutation
        # is serialized through this lock (see main(): check_same_thread=False).
        self._lock = threading.Lock()
        self._last_seen: dict[str, float] = {}

    def _debounced(self, path: str) -> bool:
        """True if this path was handled too recently and should be skipped."""
        now = time.monotonic()
        last = self._last_seen.get(path, 0.0)
        self._last_seen[path] = now
        return (now - last) < DEBOUNCE_SECONDS

    def on_modified(self, event):
        if event.is_directory or not should_index(event.src_path):
            return
        if self._debounced(event.src_path):
            return
        with self._lock:
            reindex_file(event.src_path, self.model, self.index, self.conn)

    def on_created(self, event):
        if event.is_directory or not should_index(event.src_path):
            return
        if self._debounced(event.src_path):
            return
        with self._lock:
            reindex_file(event.src_path, self.model, self.index, self.conn)

    def on_deleted(self, event):
        if event.is_directory:
            return
        with self._lock:
            remove_file(event.src_path, self.index, self.conn)


def main():
    print(f"[Watchdog] Carregando índice de {INDEX_PATH}")
    index = IdMapIndex.load(INDEX_PATH)

    print(f"[Watchdog] Carregando modelo: {MODEL_NAME}")
    model = SentenceTransformer(MODEL_NAME)

    print(f"[Watchdog] Abrindo banco SQLite: {DB_PATH}")
    # check_same_thread=False: the connection is created on the main thread but
    # used from watchdog's event thread. Safe here because CodeChangeHandler
    # serializes every access through its lock.
    conn = sqlite3.connect(DB_PATH, check_same_thread=False)

    handler = CodeChangeHandler(model, index, conn)
    observer = Observer()
    observer.schedule(handler, PROJECT_ROOT, recursive=True)
    observer.start()

    print(f"[Watchdog] 👁️  Monitorando: {PROJECT_ROOT}")
    print("[Watchdog] Ctrl+C para parar.")

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        observer.stop()
        print("\n[Watchdog] Encerrando...")
        conn.close()
    observer.join()


if __name__ == "__main__":
    main()
