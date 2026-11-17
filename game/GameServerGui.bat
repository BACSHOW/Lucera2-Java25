@echo off
rem ---------------------------------------------------------------------------
rem  GameServer com a janela propria (sem janela de console).
rem  Nao abra este arquivo direto: use StartGameServerGui.vbs, que o executa oculto.
rem  O laco de reinicio continua funcionando (codigo 2 = reiniciar).
rem  Para mudar memoria/flags da JVM, edite a linha "java" abaixo (e a do StartGameServer.bat).
rem
rem  Logs:
rem    log\server.log  - log completo do servidor (um arquivo por dia)
rem    log\stdout.log  - saida da propria JVM (erros antes da janela abrir, flags invalidas, falta de memoria)
rem ---------------------------------------------------------------------------
cd /d "%~dp0"
if not exist log mkdir log
set "JAVA_EXE=java"
if defined JAVA_HOME set "JAVA_EXE=%JAVA_HOME%\bin\java"
:start
echo.>> log\stdout.log
echo ===== %date% %time% - iniciando GameServer =====>> log\stdout.log
"%JAVA_EXE%" -server -Dfile.encoding=UTF-8 -Dl2.hiddenConsole=true -Xms2G -Xmx5G -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UseStringDeduplication -XX:+AlwaysPreTouch -XX:+UseCompactObjectHeaders -XX:G1PeriodicGCInterval=300000 --enable-native-access=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED -cp config;../libs/* l2.gameserver.GameServer >> log\stdout.log 2>&1
if ERRORLEVEL 2 goto start
if ERRORLEVEL 1 goto error
goto end
:error
rem Terminou com erro: abre um console visivel com o final dos logs.
start "GameServer - erro" cmd /k "echo O GameServer terminou com erro. & echo. & echo ---- final de log\stdout.log ---- & powershell -NoProfile -Command Get-Content -Tail 25 log\stdout.log & echo. & echo ---- final de log\server.log ---- & powershell -NoProfile -Command Get-Content -Tail 25 log\server.log & echo. & echo Para iniciar de novo, use StartGameServerGui.vbs"
:end
