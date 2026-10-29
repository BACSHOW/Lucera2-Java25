#!/bin/bash
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR/login" || exit 1

# Trava obrigatória para usar o Java 25 LTS
if [ -d "$HOME/.sdkman/candidates/java/25.3.4+1.r25-graal/bin" ]; then
    export JAVA_HOME="$HOME/.sdkman/candidates/java/25.3.4+1.r25-graal"
    export PATH="$JAVA_HOME/bin:$PATH"
elif [ -d "$HOME/.sdkman/candidates/java/current/bin" ]; then
    export JAVA_HOME="$HOME/.sdkman/candidates/java/current"
    export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "=================================================="
echo "       Iniciando L2JLucera2 - LoginServer         "
echo "          Java 25 LTS Performance Engine          "
echo "=================================================="

java -server \
     -Dfile.encoding=UTF-8 \
     -Xms256m \
     -Xmx512m \
     -XX:+UseG1GC \
     -XX:+UseStringDeduplication \
     --enable-native-access=ALL-UNNAMED \
     --sun-misc-unsafe-memory-access=allow \
     --add-opens=java.base/java.lang=ALL-UNNAMED \
     --add-opens=java.base/java.nio=ALL-UNNAMED \
     --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
     -XX:+IgnoreUnrecognizedVMOptions \
     -cp "config:../libs/*" \
     l2.authserver.AuthServer
