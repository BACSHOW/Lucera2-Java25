#!/usr/bin/env python3
"""
Sentinel Cross-Query (Diff) — Busca semântica simultânea entre L2JLucera2 e BrProject-Native.

Uso:
    .sentinel/venv/bin/python .sentinel/sentinel-diff-query.py "sua query aqui" [--top N] [--proj all|lucera|brproject]
"""
import os
os.environ.setdefault("HF_HUB_OFFLINE", "1")
os.environ.setdefault("TRANSFORMERS_OFFLINE", "1")
os.environ.setdefault("TOKENIZERS_PARALLELISM", "false")
os.environ.setdefault("OMP_NUM_THREADS", "1")
os.environ.setdefault("MKL_NUM_THREADS", "1")

import sys
import sqlite3
import argparse
import numpy as np
from turbovec import IdMapIndex
from sentence_transformers import SentenceTransformer

PROJECTS = {
    "L2JLucera2": {
        "index": "/Users/victorhugo/Downloads/arquivos/L2JLucera2/.sentinel/turbovec_index.tvim",
        "db": "/Users/victorhugo/Downloads/arquivos/L2JLucera2/.sentinel/sentinel-map.db"
    },
    "BrProject-Native": {
        "index": "/Users/victorhugo/Downloads/BrProject-Native/.sentinel/turbovec_index.tvim",
        "db": "/Users/victorhugo/Downloads/BrProject-Native/.sentinel/sentinel-map.db"
    }
}

def search_project(name, paths, model, query_vec, top_k):
    print(f"\n{'='*35} [{name}] {'='*35}")
    if not os.path.exists(paths["index"]) or not os.path.exists(paths["db"]):
        print(f"[!] Índice ou DB não encontrado para {name}")
        return

    index = IdMapIndex.load(paths["index"])
    conn = sqlite3.connect(paths["db"])
    conn.row_factory = sqlite3.Row
    cursor = conn.cursor()

    scores, result_ids = index.search(query_vec, k=top_k)
    if len(result_ids[0]) == 0:
        print("Nenhum resultado encontrado.")
        conn.close()
        return

    for rank, (score, embedding_id) in enumerate(zip(scores[0], result_ids[0]), 1):
        id_str = str(int(embedding_id))
        cursor.execute("SELECT path, chunk_idx, text FROM chunks WHERE embedding_id = ?", (id_str,))
        row = cursor.fetchone()
        if row:
            path = row["path"]
            chunk_idx = row["chunk_idx"]
            text = row["text"].strip()
            similarity = float(score) * 100
            print(f"\n--- [{rank}] {path} (chunk {chunk_idx}) — {similarity:.2f}% match ---")
            preview = text[:400]
            if len(text) > 400:
                preview += "\n  [...truncado]"
            print(preview)
    conn.close()

def main():
    parser = argparse.ArgumentParser(description="Busca semântica simultânea nos RAGs L2JLucera2 e BrProject-Native")
    parser.add_argument("query", type=str, help="Texto da busca")
    parser.add_argument("--top", type=int, default=3, help="Resultados por projeto (padrão: 3)")
    parser.add_argument("--proj", type=str, choices=["all", "lucera", "brproject"], default="all", help="Filtrar por projeto")
    args = parser.parse_args()

    print(f"🔍 Carregando modelo e embedding: \"{args.query}\"")
    model = SentenceTransformer("all-MiniLM-L6-v2")
    query_vec = model.encode([args.query]).astype(np.float32)

    if args.proj in ("all", "lucera"):
        search_project("L2JLucera2", PROJECTS["L2JLucera2"], model, query_vec, args.top)
    if args.proj in ("all", "brproject"):
        search_project("BrProject-Native", PROJECTS["BrProject-Native"], model, query_vec, args.top)
    print(f"\n{'='*82}")

if __name__ == "__main__":
    main()
