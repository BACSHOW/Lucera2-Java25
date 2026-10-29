#!/usr/bin/env bash
cd "$(dirname "$0")"
echo "Starting AuthServer (Login)..."
exec java -server -Dfile.encoding=UTF-8 -Xmx512m --enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow --add-opens=java.base/java.lang=ALL-UNNAMED -XX:+IgnoreUnrecognizedVMOptions -cp "config:../libs/*" l2.authserver.AuthServer
