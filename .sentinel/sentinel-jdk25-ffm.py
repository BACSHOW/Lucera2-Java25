#!/usr/bin/env python3
"""
Sentinel — Indexa documentação da FFM API (JDK 25) no Turbovec.

Duas fontes:
  1. Fontes Java locais extraídas do GraalVM src.zip
     (~/.sdkman/candidates/java/.../lib/src.zip → java.base/java/lang/foreign/*.java)
  2. Documentação HTML oficial da Oracle JDK 25
     (https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/)

Adiciona chunks ao índice turbovec + sentinel-map.db existente, sem resetar.
Tabela chunks ganha rows com source="graalvm-jdk25-sources" ou "oracle-jdk25-docs".

Uso:
    python sentinel-jdk25-ffm.py            # faz as duas etapas
    python sentinel-jdk25-ffm.py --sources  # só fontes locais
    python sentinel-jdk25-ffm.py --docs     # só scraping Oracle
"""
import os

os.environ.setdefault("HF_HUB_OFFLINE", "1")
os.environ.setdefault("TRANSFORMERS_OFFLINE", "1")
os.environ.setdefault("TOKENIZERS_PARALLELISM", "false")
os.environ.setdefault("OMP_NUM_THREADS", "1")
os.environ.setdefault("MKL_NUM_THREADS", "1")

import sys
import sqlite3
import hashlib
import time
import zipfile
import re
import shutil

import numpy as np
from turbovec import IdMapIndex
from sentence_transformers import SentenceTransformer

# --- Configuração (espelha sentinel-indexer.py) ---
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)
INDEX_PATH = os.path.join(SCRIPT_DIR, "turbovec_index.tvim")
DB_PATH = os.path.join(SCRIPT_DIR, "sentinel-map.db")
MODEL_NAME = "all-MiniLM-L6-v2"
EMBEDDING_DIM = 384

CHUNK_SIZE = 2000
CHUNK_OVERLAP = 200

GRAALVM_SRC_ZIP = os.path.expanduser(
    "~/.sdkman/candidates/java/25.3.4+1.r25-graal/lib/src.zip"
)
EXTERNAL_DIR = os.path.join(SCRIPT_DIR, "external", "jdk25-ffm-sources")

ORACLE_BASE = (
    "https://docs.oracle.com/en/java/javase/25/docs/api/"
    "java.base/java/lang/foreign/"
)

# 25 classes/interfaces (inclui nested types como arquivos próprios p/ scraping,
# mas eles vivem dentro dos pais nas fontes Java — chunking resolve)
ORACLE_CLASSES = [
    "AddressLayout.html",
    "Arena.html",
    "FunctionDescriptor.html",
    "GroupLayout.html",
    "Linker.Option.html",
    "Linker.html",
    "MemoryLayout.PathElement.html",
    "MemoryLayout.html",
    "MemorySegment.Scope.html",
    "MemorySegment.html",
    "PaddingLayout.html",
    "SegmentAllocator.html",
    "SequenceLayout.html",
    "StructLayout.html",
    "SymbolLookup.html",
    "UnionLayout.html",
    "ValueLayout.OfBoolean.html",
    "ValueLayout.OfByte.html",
    "ValueLayout.OfChar.html",
    "ValueLayout.OfDouble.html",
    "ValueLayout.OfFloat.html",
    "ValueLayout.OfInt.html",
    "ValueLayout.OfLong.html",
    "ValueLayout.OfShort.html",
    "ValueLayout.html",
]


def make_embedding_id(rel: str, idx: int) -> np.uint64:
    """Mesmo hashing do sentinel-indexer.py — IDs únicos globais."""
    hasher = hashlib.md5(f"{rel}:{idx}".encode())
    hash_bytes = hasher.digest()[:8]
    return np.uint64(int.from_bytes(hash_bytes, byteorder="little"))


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


def init_db():
    """Abre o SQLite existente (schema já criado pelo sentinel-indexer.py)."""
    conn = sqlite3.connect(DB_PATH)
    return conn


def delete_existing_source_chunks(conn, source_value: str):
    """Remove rows do índice vindas desta fonte (idempotente: re-rodar não duplica)."""
    cur = conn.cursor()
    cur.execute("SELECT embedding_id, path FROM chunks WHERE source = ?", (source_value,))
    rows = cur.fetchall()
    cur.execute("DELETE FROM chunks WHERE source = ?", (source_value,))
    conn.commit()
    return len(rows)


def flush_batch(model, index, conn, batch_ids, batch_docs, batch_meta, total):
    if not batch_ids:
        return total
    for eid in batch_ids:
        if index.contains(int(eid)):
            index.remove(int(eid))
    embeddings = model.encode(batch_docs, show_progress_bar=False).astype(np.float32)
    ids_array = np.array(batch_ids, dtype=np.uint64)
    index.add_with_ids(embeddings, ids_array)
    cur = conn.cursor()
    for i in range(len(batch_meta)):
        mid = hashlib.md5(
            f"{batch_meta[i]['path']}:{batch_meta[i]['chunk']}".encode()
        ).hexdigest()
        cur.execute(
            """
            INSERT OR REPLACE INTO chunks
              (id, path, chunk_idx, ext, text, embedding_id, source)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """,
            (
                mid,
                batch_meta[i]["path"],
                batch_meta[i]["chunk"],
                batch_meta[i]["ext"],
                batch_docs[i],
                str(int(batch_ids[i])),
                batch_meta[i].get("source", "brproject"),
            ),
        )
    conn.commit()
    return total + len(batch_ids)


# =========================================================================
# ETAPA 1: Fontes Java locais do GraalVM
# =========================================================================

def extract_graalvm_sources():
    """Extrai java.base/java/lang/foreign/*.java do src.zip para EXTERNAL_DIR."""
    if os.path.exists(EXTERNAL_DIR):
        shutil.rmtree(EXTERNAL_DIR)
    os.makedirs(EXTERNAL_DIR, exist_ok=True)
    if not os.path.exists(GRAALVM_SRC_ZIP):
        print(f"[Sources] ❌ src.zip não encontrado: {GRAALVM_SRC_ZIP}")
        return []
    with zipfile.ZipFile(GRAALVM_SRC_ZIP) as zf:
        names = [
            n for n in zf.namelist()
            if n.startswith("java.base/java/lang/foreign/")
            and n.endswith(".java")
        ]
        zf.extractall(EXTERNAL_DIR, names)
    extracted = []
    for name in names:
        full = os.path.join(EXTERNAL_DIR, name)
        if os.path.isfile(full):
            extracted.append(full)
    print(f"[Sources] Extraídos {len(extracted)} arquivos do src.zip")
    return extracted


def index_sources(model, index, conn):
    """Indexa as fontes Java extraídas."""
    files = extract_graalvm_sources()
    if not files:
        return 0

    removed = delete_existing_source_chunks(conn, "graalvm-jdk25-sources")
    if removed:
        print(f"[Sources] Limpando {removed} chunks anteriores desta fonte")

    total_chunks = 0
    batch_ids, batch_docs, batch_meta = [], [], []
    start = time.time()

    for path in sorted(files):
        rel = os.path.relpath(path, PROJECT_ROOT)
        try:
            with open(path, "r", encoding="utf-8", errors="ignore") as f:
                content = f.read()
        except OSError:
            continue
        if not content.strip():
            continue

        # Header sintético: o modelo Embedding diferencia "doc de API" de "código de prod"
        # quando vê "Java 25 Standard Library Source — FFM"
        header = (
            f"// Java 25 Standard Library Source — FFM (java.lang.foreign)\n"
            f"// File: {rel}\n\n"
        )
        full_content = header + content

        for idx, chunk in enumerate(chunk_text(full_content)):
            eid = make_embedding_id(rel, idx)
            batch_ids.append(eid)
            batch_docs.append(chunk)
            batch_meta.append({
                "path": rel,
                "chunk": idx,
                "ext": ".java",
                "source": "graalvm-jdk25-sources",
            })
            if len(batch_ids) >= 128:
                total_chunks = flush_batch(
                    model, index, conn,
                    batch_ids, batch_docs, batch_meta, total_chunks,
                )
                batch_ids, batch_docs, batch_meta = [], [], []

    total_chunks = flush_batch(
        model, index, conn,
        batch_ids, batch_docs, batch_meta, total_chunks,
    )
    elapsed = time.time() - start
    print(f"[Sources] ✅ {total_chunks} chunks de {len(files)} arquivos em {elapsed:.1f}s")
    return total_chunks


# =========================================================================
# ETAPA 2: Scraping Oracle JDK 25 docs
# =========================================================================

def _ensure_bs4_and_requests():
    try:
        import requests
        from bs4 import BeautifulSoup
        return requests, BeautifulSoup
    except ImportError:
        print("[Docs] ❌ Faltam 'requests' e/ou 'beautifulsoup4' na venv")
        print("       Ative .sentinel/venv e rode:")
        print("       pip install requests beautifulsoup4 lxml")
        return None, None


def clean_text(text: str) -> str:
    lines = (line.strip() for line in text.splitlines())
    return " ".join(line for line in lines if line)


def parse_oracle_class(class_html: str, requests, BeautifulSoup):
    """Extrai (class_summary, [(method_name, signature, body), ...]) de uma página."""
    url = f"{ORACLE_BASE}{class_html}"
    class_name = class_html.replace(".html", "")
    headers = {"User-Agent": "Mozilla/5.0 (RAG Pipeline Ingestion)"}
    resp = requests.get(url, headers=headers, timeout=20)
    if resp.status_code != 200:
        print(f"[Docs]   ⚠️  {class_name}: HTTP {resp.status_code}")
        return class_name, None, []

    soup = BeautifulSoup(resp.text, "html.parser")

    # Class summary
    summary_text = ""
    desc = soup.find("section", class_="class-description")
    if desc:
        summary_text = clean_text(desc.get_text())

    # Method details
    chunks = []
    method_details = soup.find("section", class_="method-details")
    if method_details:
        for section in method_details.find_all("section", class_="detail"):
            header = section.find(["h3", "h4"])
            if not header:
                continue
            method_name = header.get_text().strip()
            sig_block = section.find("div", class_="member-signature")
            signature = clean_text(sig_block.get_text()) if sig_block else ""
            body = clean_text(section.get_text())
            chunk = (
                f"Class: java.lang.foreign.{class_name}\n"
                f"Signature: {signature}\n"
                f"Details: {body}"
            )
            chunks.append((method_name, signature, chunk))

    # Field details (constantes e.g. ValueLayout.OfInt têm só fields, não methods)
    field_details = soup.find("section", class_="field-details")
    if field_details:
        for section in field_details.find_all("section", class_="detail"):
            header = section.find(["h3", "h4"])
            if not header:
                continue
            field_name = header.get_text().strip()
            sig_block = section.find("div", class_="member-signature")
            signature = clean_text(sig_block.get_text()) if sig_block else ""
            body = clean_text(section.get_text())
            chunk = (
                f"Class: java.lang.foreign.{class_name}\n"
                f"Field: {field_name}\n"
                f"Signature: {signature}\n"
                f"Details: {body}"
            )
            chunks.append((f"[field] {field_name}", signature, chunk))

    return class_name, summary_text, chunks


def index_oracle_docs(model, index, conn):
    requests, BeautifulSoup = _ensure_bs4_and_requests()
    if requests is None:
        return 0

    removed = delete_existing_source_chunks(conn, "oracle-jdk25-docs")
    if removed:
        print(f"[Docs] Limpando {removed} chunks anteriores desta fonte")

    total_chunks = 0
    batch_ids, batch_docs, batch_meta = [], [], []
    start = time.time()

    for class_html in ORACLE_CLASSES:
        class_name, summary, methods = parse_oracle_class(
            class_html, requests, BeautifulSoup,
        )
        print(f"[Docs]   {class_name}: summary={'sim' if summary else 'não'} · "
              f"{len(methods)} método(s)/field(s)")

        # Indexa o class summary primeiro (pode estar vazio)
        if summary:
            doc_id_path = f"oracle-docs/java.lang.foreign/{class_name}/class_summary"
            eid = make_embedding_id(doc_id_path, 0)
            batch_ids.append(eid)
            batch_docs.append(
                f"Class: java.lang.foreign.{class_name}\nSummary: {summary}"
            )
            batch_meta.append({
                "path": doc_id_path,
                "chunk": 0,
                "ext": ".html",
                "source": "oracle-jdk25-docs",
            })

        # Indexa cada método/field
        for idx, (method_name, signature, body) in enumerate(methods, start=1):
            doc_id_path = f"oracle-docs/java.lang.foreign/{class_name}/m{idx}_{method_name}"
            eid = make_embedding_id(doc_id_path, 0)
            batch_ids.append(eid)
            batch_docs.append(body)
            batch_meta.append({
                "path": doc_id_path,
                "chunk": 0,
                "ext": ".html",
                "source": "oracle-jdk25-docs",
            })

        # Flush se batch encheu
        if len(batch_ids) >= 64:
            total_chunks = flush_batch(
                model, index, conn,
                batch_ids, batch_docs, batch_meta, total_chunks,
            )
            batch_ids, batch_docs, batch_meta = [], [], []

    total_chunks = flush_batch(
        model, index, conn,
        batch_ids, batch_docs, batch_meta, total_chunks,
    )
    elapsed = time.time() - start
    print(f"[Docs] ✅ {total_chunks} chunks de {len(ORACLE_CLASSES)} classes em {elapsed:.1f}s")
    return total_chunks


# =========================================================================
# Main
# =========================================================================

def main():
    only_sources = "--sources" in sys.argv
    only_docs = "--docs" in sys.argv
    do_sources = not only_docs
    do_docs = not only_sources

    print(f"[FFM] Carregando índice: {INDEX_PATH}")
    index = IdMapIndex.load(INDEX_PATH)
    print(f"[FFM] Carregando modelo: {MODEL_NAME}")
    model = SentenceTransformer(MODEL_NAME)
    conn = init_db()

    # Migração defensiva: adicionar coluna 'source' se não existir (schema antigo)
    cur = conn.cursor()
    cur.execute("PRAGMA table_info(chunks)")
    cols = {row[1] for row in cur.fetchall()}
    if "source" not in cols:
        print("[FFM] Migrando schema: adicionando coluna 'source'")
        cur.execute("ALTER TABLE chunks ADD COLUMN source TEXT NOT NULL DEFAULT 'brproject'")
        cur.execute("CREATE INDEX IF NOT EXISTS idx_source ON chunks(source)")
        conn.commit()

    sources_count = 0
    docs_count = 0

    if do_sources:
        print("\n[FFM] === ETAPA 1: Fontes Java do GraalVM JDK 25 ===")
        sources_count = index_sources(model, index, conn)

    if do_docs:
        print("\n[FFM] === ETAPA 2: Documentação Oracle JDK 25 ===")
        docs_count = index_oracle_docs(model, index, conn)

    print(f"\n[FFM] Salvando índice turbovec em {INDEX_PATH}")
    index.write(INDEX_PATH)

    print(f"[FFM] ✅ Concluído.")
    print(f"      Sources: {sources_count} chunks")
    print(f"      Docs:    {docs_count} chunks")

    conn.close()


if __name__ == "__main__":
    main()
