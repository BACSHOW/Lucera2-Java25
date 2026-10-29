#!/usr/bin/env bash
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR" || exit 1

# Prioridade 1: Executável nativo PGO
if [ -x "$DIR/../pgo/gameserver-pgo" ]; then
    SIZE=$(du -h "$DIR/../pgo/gameserver-pgo" | awk '{print $1}')
    echo "[Engine] Executando binário nativo PGO ultra-otimizado (${SIZE} Assembly)..."
    exec "$DIR/../pgo/gameserver-pgo" 2> >(grep -v -E "sun.misc.Unsafe|terminally deprecated|Please consider reporting" >&2)
fi

# Fallback JVM
echo "Starting GameServer (Java 25 Fallback)..."
exec java -server \
     -Dfile.encoding=UTF-8 \
     -Xms2G \
     -Xmx4G \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=50 \
     -XX:+UseStringDeduplication \
     -XX:+AlwaysPreTouch \
     --enable-native-access=ALL-UNNAMED \
     --sun-misc-unsafe-memory-access=allow \
     --add-opens=java.base/java.lang=ALL-UNNAMED \
     --add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
     --add-opens=java.base/java.nio=ALL-UNNAMED \
     --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
     --add-opens=java.base/java.util=ALL-UNNAMED \
     -XX:+IgnoreUnrecognizedVMOptions \
     -cp "config:../libs/*" \
     l2.gameserver.GameServer
