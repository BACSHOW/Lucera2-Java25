#!/usr/bin/env bash
cd "$(dirname "$0")"
USER="$1"
PASS="$2"
if [ -z "$USER" ] || [ -z "$PASS" ]; then
    read -p "Enter account name: " USER
    read -s -p "Enter account pass: " PASS
    echo ""
fi
java -Dfile.encoding=UTF-8 -Xms256m -Xmx512m -cp "config:../libs/*" l2.authserver.AccountManager -c "$USER" "$PASS"
