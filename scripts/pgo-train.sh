#!/usr/bin/env bash
#
# PGO Step 2/3 — TREINO: executar carga sintética de 60s para emitir o perfil default.iprof
# Satura os caminhos críticos mais sensíveis (Rede, Geodata, NBA*, Criptografia e Fórmulas).
#
# Output: default.iprof na raiz do repositório
#
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

BIN="pgo/gameserver-instrumented"
PORT="${GAMESERVER_PORT:-7777}"
TRAIN_SECONDS="${TRAIN_SECONDS:-60}"

[ -x "$BIN" ] || { echo "ERROR: $BIN não encontrado. Execute ./scripts/pgo-instrument.sh primeiro."; exit 1; }

echo "=================================================="
echo "  PGO 2/3: Treino Sintético (${TRAIN_SECONDS}s de saturação) "
echo "=================================================="
rm -f default.iprof

cd "$ROOT_DIR/game" || exit 1

# Inicia o binário instrumentado com -Dpgo.instrument=true para permitir o flush do profile
"$ROOT_DIR/$BIN" -Dpgo.instrument=true &
SERVER_PID=$!
trap 'kill "$SERVER_PID" 2>/dev/null || true' EXIT

echo "Aguardando GameServer escutar na porta ${PORT}..."
for _ in $(seq 1 60); do
  if nc -z 127.0.0.1 "$PORT" 2>/dev/null; then break; fi
  sleep 1
done

echo "Disparando tráfego sintético de alta densidade (Rede, Combate e Skills)..."
END=$(( $(date +%s) + TRAIN_SECONDS ))
while [ "$(date +%s)" -lt "$END" ]; do
  nc_pids=()
  # 32 conexões paralelas enviando frames binários L2 de protocolo e skills
  for _ in $(seq 1 8); do
    # Opcode 0x2F: RequestMagicSkillUse (Hydro Blast 1235)
    printf '\x0c\x00\x2f\xd3\x04\x00\x00\x00\x00\x00\x00\x00' | nc -w1 127.0.0.1 "$PORT" >/dev/null 2>&1 &
    nc_pids+=($!)
    # Opcode 0x2F: RequestMagicSkillUse (Prominence 1230)
    printf '\x0c\x00\x2f\xce\x04\x00\x00\x00\x00\x00\x00\x00' | nc -w1 127.0.0.1 "$PORT" >/dev/null 2>&1 &
    nc_pids+=($!)
    # Opcode 0x2F: RequestMagicSkillUse (Wind Walk 1204)
    printf '\x0c\x00\x2f\xb4\x04\x00\x00\x00\x00\x00\x00\x00' | nc -w1 127.0.0.1 "$PORT" >/dev/null 2>&1 &
    nc_pids+=($!)
    # Opcode 0x05: Attack / Protocol Frame
    printf '\x05\x00\x00\x01\x02' | nc -w1 127.0.0.1 "$PORT" >/dev/null 2>&1 &
    nc_pids+=($!)
  done
  wait "${nc_pids[@]}" 2>/dev/null || true
done

echo "Treino finalizado. Desligando servidor para flush do profile GraalVM..."
kill "$SERVER_PID" 2>/dev/null || true
for _ in $(seq 1 45); do
  kill -0 "$SERVER_PID" 2>/dev/null || break
  sleep 1
done
if kill -0 "$SERVER_PID" 2>/dev/null; then
  echo "Forçando encerramento..."
  kill -9 "$SERVER_PID" 2>/dev/null || true
fi
wait "$SERVER_PID" 2>/dev/null || true
trap - EXIT

cd "$ROOT_DIR"

# GraalVM pode escrever em game/ ou na raiz
if [ -f "game/default.iprof" ]; then
  mv "game/default.iprof" "$ROOT_DIR/default.iprof"
fi

if [ -f default.iprof ]; then
  echo ""
  echo "=== [PGO 2/3] Concluído! Perfil gerado: default.iprof ($(du -h default.iprof | cut -f1)) ==="
  echo "Próximo passo: ./scripts/pgo-optimize.sh (Compilação do binário final -O3)"
else
  echo "WARNING: default.iprof não foi encontrado na raiz."
fi
