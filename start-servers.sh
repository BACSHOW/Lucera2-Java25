#!/usr/bin/env bash
DIR="$(cd "$(dirname "$0")" && pwd)"
mkdir -p "$DIR/logs"

echo "=== Iniciando L2JLucera2 ==="

# 1. Checar MariaDB
if ! mariadb -h 127.0.0.1 -u root -pnexoral2j -D l2j -e "SELECT 1;" >/dev/null 2>&1; then
    echo "[!] MariaDB offline. Tentando iniciar serviço brew..."
    brew services start mariadb@10.11
    sleep 2
fi

# 2. Iniciar AuthServer
echo "Iniciando AuthServer (Login)..."
cd "$DIR/login"
nohup ./StartAuthServer.sh > "$DIR/logs/authserver.log" 2>&1 &
AUTH_PID=$!
echo $AUTH_PID > "$DIR/logs/authserver.pid"
echo "AuthServer PID: $AUTH_PID"

sleep 3

# 3. Iniciar GameServer
echo "Iniciando GameServer..."
cd "$DIR/game"
nohup ./StartGameServer.sh > "$DIR/logs/gameserver.log" 2>&1 &
GAME_PID=$!
echo $GAME_PID > "$DIR/logs/gameserver.pid"
echo "GameServer PID: $GAME_PID"

echo "=== Servidores iniciados ==="
echo "Acompanhe os logs em:"
echo "  tail -f $DIR/logs/authserver.log"
echo "  tail -f $DIR/logs/gameserver.log"
