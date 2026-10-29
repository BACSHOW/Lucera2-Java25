#!/usr/bin/env bash
#
# L2JLucera2 — Full Native Build com PGO (Profile-Guided Optimization)
# Executa a esteira completa:
#   1. Compila os JARs (./gradlew jarServer jarScripts)
#   2. PGO 1/3: Compilação do binário instrumentado (pgo-instrument.sh)
#   3. PGO 2/3: Treino sintético de 60s sob carga pesada (pgo-train.sh) -> default.iprof
#   4. PGO 3/3: Compilação otimizada nativa final (pgo-optimize.sh) -> pgo/gameserver-pgo
#
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "================================================================="
echo "    Iniciando Pipeline de Compilação Nativa PGO - L2JLucera2     "
echo "================================================================="

# 0. Limpeza total de binários antigos e perfis anteriores
echo "=== [Limpeza] Removendo binários antigos e perfis residuais ==="
rm -rf build/native
mkdir -p pgo
rm -f pgo/gameserver-pgo pgo/gameserver-instrumented default.iprof game/default.iprof

# 1. Compilação de JARs
echo "=== [Passo 0] Compilando JARs de suporte ==="
./gradlew jarServer jarScripts --console=plain

# 2. PGO 1/3 - Binário Instrumentado
"$ROOT_DIR/scripts/pgo-instrument.sh"

# 3. PGO 2/3 - Treino Sintético de 60s (Saturação de Geodata, Rede, Fórmulas e Skills)
TRAIN_SECONDS="${TRAIN_SECONDS:-60}" "$ROOT_DIR/scripts/pgo-train.sh"

# 4. PGO 3/3 - Binário Otimizado com Perfil (-O3, PGO, march=native)
"$ROOT_DIR/scripts/pgo-optimize.sh"

# 5. Limpeza de binários intermediários (mantém somente o binário final de produção)
echo "=== [Limpeza Final] Removendo binário instrumentado temporário ==="
rm -f pgo/gameserver-instrumented

echo ""
echo "================================================================="
echo "           BUILD NATIVO PGO CONCLUÍDO COM SUCESSO!               "
echo "================================================================="
echo "  Binário Nativo Final: pgo/gameserver-pgo ($(du -h pgo/gameserver-pgo | cut -f1))"
echo "================================================================="
echo ""
