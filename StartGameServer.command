#!/bin/bash
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR/game" || exit 1

echo "=================================================="
echo "       Iniciando L2JLucera2 - GameServer          "
echo "       GraalVM Native Image PGO (-O3 NEON)        "
echo "=================================================="

# 1. Prioridade absoluta: Binário nativo compilado com PGO Trail 60s
if [ -x "$DIR/pgo/gameserver-pgo" ]; then
    SIZE=$(du -h "$DIR/pgo/gameserver-pgo" | awk "{print \$1}")
    echo "[Engine] Executando binário nativo PGO ultra-otimizado (${SIZE} Assembly)..."
    exec "$DIR/pgo/gameserver-pgo" 2> >(grep -v -E "sun.misc.Unsafe|terminally deprecated|Please consider reporting" >&2)
fi

# 2. Fallback caso o binário nativo não exista: JVM Java 25 LTS
echo "[Engine] Binário nativo pgo/gameserver-pgo não encontrado. Usando fallback JVM Java 25..."
if [ -d "$HOME/.sdkman/candidates/java/25.3.4+1.r25-graal/bin" ]; then
    export JAVA_HOME="$HOME/.sdkman/candidates/java/25.3.4+1.r25-graal"
    export PATH="$JAVA_HOME/bin:$PATH"
elif [ -d "$HOME/.sdkman/candidates/java/current/bin" ]; then
    export JAVA_HOME="$HOME/.sdkman/candidates/java/current"
    export PATH="$JAVA_HOME/bin:$PATH"
fi

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
