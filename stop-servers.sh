#!/usr/bin/env bash
DIR="$(cd "$(dirname "$0")" && pwd)"

echo "=== Parando L2JLucera2 ==="
if [ -f "$DIR/logs/gameserver.pid" ]; then
    PID=$(cat "$DIR/logs/gameserver.pid")
    if ps -p $PID >/dev/null 2>&1; then
        echo "Parando GameServer (PID: $PID)..."
        kill -TERM $PID 2>/dev/null || kill -9 $PID 2>/dev/null
    fi
    rm -f "$DIR/logs/gameserver.pid"
fi

if [ -f "$DIR/logs/authserver.pid" ]; then
    PID=$(cat "$DIR/logs/authserver.pid")
    if ps -p $PID >/dev/null 2>&1; then
        echo "Parando AuthServer (PID: $PID)..."
        kill -TERM $PID 2>/dev/null || kill -9 $PID 2>/dev/null
    fi
    rm -f "$DIR/logs/authserver.pid"
fi

# Fallback para garantir
pkill -f "l2.gameserver.GameServer" 2>/dev/null || true
pkill -f "l2.authserver.AuthServer" 2>/dev/null || true

echo "=== Servidores parados ==="
