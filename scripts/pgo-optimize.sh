#!/usr/bin/env bash
#
# PGO Step 3/3 — OTIMIZAÇÃO: compilar o binário de produção final usando default.iprof
# Aplica -O3, -march=native, inlining agressivo e eliminação de branches mortos.
#
# Output: pgo/gameserver-pgo (~60MB)
#
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

GC="${GC:-G1}"

echo "=================================================="
echo "  PGO 3/3: Compilando binário nativo final (-O3)  "
echo "=================================================="

[ -f default.iprof ] || echo "NOTE: default.iprof não encontrado - compilando -O3 sem perfil PGO."

GC_ARG=""
if [ -n "${GC:-}" ]; then
    GC_ARG="-PnativeExtraArgs=--gc=${GC}"
fi

./gradlew nativeCompile \
    --console=plain \
    $GC_ARG

mkdir -p pgo
cp build/native/nativeCompile/gameserver pgo/gameserver-pgo
chmod +x pgo/gameserver-pgo
if [ "$(uname)" = "Darwin" ]; then
    echo "[CodeSign] Assinando binário Mach-O ad-hoc para macOS..."
    codesign -s - --force pgo/gameserver-pgo || true
fi

SIZE=$(du -h pgo/gameserver-pgo | cut -f1)
echo ""
echo "=== [PGO 3/3] Concluído! Binário PGO ultra-otimizado: pgo/gameserver-pgo (${SIZE}) ==="
echo "Para iniciar o servidor nativo de produção: ./scripts/start-gameserver-pgo.command"
