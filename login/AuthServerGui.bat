@echo off
rem ---------------------------------------------------------------------------
rem  Login Server com a janela propria (sem janela de console).
rem  Nao abra este arquivo direto: use StartAuthServerGui.vbs, que o executa oculto.
rem  O laco de reinicio continua funcionando (codigo 2 = reiniciar).
rem  Para mudar memoria/flags da JVM, edite a linha "java" abaixo (e a do StartAuthServer.bat).
rem
rem  Logs:
rem    log\java.log    - log completo do login
rem    log\stdout.log  - saida da propria JVM (erros antes da janela abrir, flags invalidas)
rem ---------------------------------------------------------------------------
cd /d "%~dp0"
if not exist log mkdir log
set "JAVA_EXE=java"
if defined JAVA_HOME set "JAVA_EXE=%JAVA_HOME%\bin\java"
:start
echo.>> log\stdout.log
echo ===== %date% %time% - iniciando AuthServer =====>> log\stdout.log
"%JAVA_EXE%" -server -Dfile.encoding=UTF-8 -Xms128m -Xmx256m -XX:+UseG1GC -XX:+UseCompactObjectHeaders -XX:G1PeriodicGCInterval=300000 --enable-native-access=ALL-UNNAMED -Dl2.hiddenConsole=true -cp config;../libs/* l2.authserver.AuthServer >> log\stdout.log 2>&1
if ERRORLEVEL 2 goto start
if ERRORLEVEL 1 goto error
goto end
:error
rem Terminou com erro: abre um console visivel com o final dos logs.
start "AuthServer - erro" cmd /k "echo O Login Server terminou com erro. & echo. & echo ---- final de log\stdout.log ---- & powershell -NoProfile -Command Get-Content -Tail 25 log\stdout.log & echo. & echo ---- final de log\java.log ---- & powershell -NoProfile -Command Get-Content -Tail 25 log\java.log & echo. & echo Para iniciar de novo, use StartAuthServerGui.vbs"
:end
