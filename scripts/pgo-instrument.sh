#!/usr/bin/env bash
#
# PGO Step 1/3 — Compilar o binário nativo INSTRUMENTADO
# Compila o gameserver com a flag --pgo-instrument para coletar métricas de perfil em runtime.
#
# Output: pgo/gameserver-instrumented
#
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "=================================================="
echo "  PGO 1/3: Compilando binário nativo instrumentado "
echo "=================================================="

# 1. Compila os jars de suporte primeiro
./gradlew jarServer jarScripts --console=plain

# 2. Compila a imagem nativa com instrumentação PGO
./gradlew nativeCompile \
    --console=plain \
    -PnativeExtraArgs="--pgo-instrument"

mkdir -p pgo
cp build/native/nativeCompile/gameserver pgo/gameserver-instrumented
chmod +x pgo/gameserver-instrumented
if [ "$(uname)" = "Darwin" ]; then
    echo "[CodeSign] Assinando binário instrumentado ad-hoc para macOS..."
    codesign -s - --force pgo/gameserver-instrumented || true
fi

echo ""
echo "=== [PGO 1/3] Concluído. Binário instrumentado: pgo/gameserver-instrumented ($(du -h pgo/gameserver-instrumented | cut -f1)) ==="
echo "Próximo passo: ./scripts/pgo-train.sh (Treino de 60s sob carga sintética)"
