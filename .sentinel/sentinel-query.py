#!/usr/bin/env python3
"""
Sentinel Query (Turbovec) — Interface CLI para buscar fragmentos de código relevantes
no banco vetorial turbovec + SQLite de L2JLucera2 e opcionalmente em BrProject-Native.

Uso:
    python sentinel-query.py "Player damage calculation"
    python sentinel-query.py "database connection pool" --top 10
    python sentinel-query.py "GeoEngine pathfinding" --ext .java
    python sentinel-query.py "GeoEngine pathfinding" --target all
    python sentinel-query.py "Fake player AI" --target brproject
"""
import os

# Use the locally cached embedding model — never hit the network at startup.
os.environ.setdefault("HF_HUB_OFFLINE", "1")
os.environ.setdefault("TRANSFORMERS_OFFLINE", "1")
os.environ.setdefault("TOKENIZERS_PARALLELISM", "false")
os.environ.setdefault("OMP_NUM_THREADS", "1")
os.environ.setdefault("MKL_NUM_THREADS", "1")

import sys
import argparse
import sqlite3

import numpy as np
from turbovec import IdMapIndex
from sentence_transformers import SentenceTransformer

PROJECTS = {
    "lucera": {
        "name": "L2JLucera2",
        "index": os.path.join(os.path.dirname(__file__), "turbovec_index.tvim"),
        "db": os.path.join(os.path.dirname(__file__), "sentinel-map.db")
    },
    "brproject": {
        "name": "BrProject-Native",
        "index": "/Users/victorhugo/Downloads/BrProject-Native/.sentinel/turbovec_index.tvim",
        "db": "/Users/victorhugo/Downloads/BrProject-Native/.sentinel/sentinel-map.db"
    }
}

MODEL_NAME = "all-MiniLM-L6-v2"


def search_project(proj_key, cfg, model, query_vec, top_k, ext_filter=None):
    print(f"\n{'=' * 30} [{cfg['name']}] {'=' * 30}")
    if not os.path.exists(cfg["index"]) or not os.path.exists(cfg["db"]):
        print(f"[Query] ❌ Índice ou DB não encontrado para {cfg['name']}: {cfg['index']}")
        return

    index = IdMapIndex.load(cfg["index"])
    conn = sqlite3.connect(cfg["db"])
    conn.row_factory = sqlite3.Row
    cursor = conn.cursor()

    scores, result_ids = index.search(query_vec, k=top_k * 2 if ext_filter else top_k)

    if len(result_ids[0]) == 0:
        print("[Query] Nenhum resultado encontrado.")
        conn.close()
        return

    shown = 0
    for i, embedding_id in enumerate(result_ids[0]):
        id_str = str(int(embedding_id))
        cursor.execute("SELECT path, chunk_idx, ext, text FROM chunks WHERE embedding_id = ?", (id_str,))
        row = cursor.fetchone()
        if not row:
            continue

        if ext_filter and row["ext"] != ext_filter:
            continue

        score = float(scores[0][i]) if i < len(scores[0]) else 0.0
        shown += 1
        print(f"\n--- [{shown}] {row['path']} (chunk {row['chunk_idx']}) — {score:.2%} match ---")
        preview = row["text"][:600]
        if len(row["text"]) > 600:
            preview += "\n  [...truncado]"
        print(preview)

        if shown >= top_k:
            break

    if shown == 0:
        print("[Query] Nenhum resultado correspondente ao filtro.")
    else:
        print(f"\nTotal exibido: {shown} resultados em {cfg['name']}")

    conn.close()


def main():
    parser = argparse.ArgumentParser(description="Busca semântica nos codebases L2JLucera2 e BrProject-Native")
    parser.add_argument("query", help="Texto para buscar (linguagem natural ou código)")
    parser.add_argument("--top", type=int, default=5, help="Número de resultados por projeto (default: 5)")
    parser.add_argument("--ext", type=str, default=None, help="Filtrar por extensão (ex: .java, .kt)")
    parser.add_argument("--target", choices=["lucera", "brproject", "all"], default="lucera", help="Projeto alvo: lucera (default), brproject ou all")
    parser.add_argument("--cross", action="store_true", help="Atalho para --target all")
    args = parser.parse_args()

    target = "all" if args.cross else args.target

    print(f"[Query] Carregando modelo: {MODEL_NAME}")
    model = SentenceTransformer(MODEL_NAME)
    query_vec = model.encode([args.query]).astype(np.float32)

    print(f"\n🔍 Busca por: \"{args.query}\"")
    if target in ("lucera", "all"):
        search_project("lucera", PROJECTS["lucera"], model, query_vec, args.top, args.ext)
    if target in ("brproject", "all"):
        search_project("brproject", PROJECTS["brproject"], model, query_vec, args.top, args.ext)
    print(f"\n{'=' * 75}")


if __name__ == "__main__":
    main()
