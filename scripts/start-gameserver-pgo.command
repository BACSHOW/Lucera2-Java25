#!/bin/bash
DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$DIR/game" || exit 1

echo "=================================================="
echo "       Iniciando L2JLucera2 - GameServer          "
echo "          GraalVM Native Image PGO Engine         "
echo "=================================================="

# Executa o binário nativo de produção (Assembly compilado direto para CPU host)
exec "$DIR/pgo/gameserver-pgo" 2> >(grep -v -E "sun.misc.Unsafe|terminally deprecated|Please consider reporting" >&2)
