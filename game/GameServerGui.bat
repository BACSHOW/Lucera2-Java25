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
if "%~1"==":mostrar_erro" goto mostrar_erro_inicio
if not exist log mkdir log
rem ---- Java 25 ------------------------------------------------------------
rem  Se o Java do PATH/JAVA_HOME nao for o 25, coloque aqui a pasta do JDK 25
rem  (a mesma do seu StartGameServer.bat), ex.: set "JAVA25_HOME=C:\Program Files\Java\jdk-25"
set "JAVA25_HOME="
rem -------------------------------------------------------------------------
set "JAVA_EXE=java"
if defined JAVA_HOME set "JAVA_EXE=%JAVA_HOME%\bin\java"
if defined JAVA25_HOME set "JAVA_EXE=%JAVA25_HOME%\bin\java"
:start
echo.>> log\stdout.log
echo ===== %date% %time% - iniciando GameServer =====>> log\stdout.log
"%JAVA_EXE%" -server -Dfile.encoding=UTF-8 -Dl2.hiddenConsole=true -Xms2G -Xmx5G -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UseStringDeduplication -XX:+AlwaysPreTouch -XX:+UseCompactObjectHeaders -XX:G1PeriodicGCInterval=300000 --enable-native-access=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED -cp config;../libs/* l2.gameserver.GameServer >> log\stdout.log 2>&1
if ERRORLEVEL 2 goto start
if ERRORLEVEL 1 goto error
goto end
:error
rem Terminou com erro: abre um console visivel com o motivo.
set "HINT="
findstr /c:"Unrecognized VM option" /c:"UnsupportedClassVersionError" /c:"Unrecognized option" log\stdout.log >nul 2>&1 && set "HINT=1"
start "GameServer - erro" cmd /k "call "%~dp0%~nx0" :mostrar_erro"
goto end
:mostrar_erro_inicio
:mostrar_erro
echo O GameServer terminou com erro.
echo.
if defined HINT (
  echo  ** O Java usado nao e o Java 25: ele nao reconhece as opcoes do Java 25. **
  echo     Java usado: "%JAVA_EXE%"
  "%JAVA_EXE%" -version
  echo     Abra o %~nx0 no Bloco de Notas e preencha JAVA25_HOME com a pasta do JDK 25.
  echo.
)
echo ---- final de log\stdout.log ----
if exist log\stdout.log powershell -NoProfile -Command "Get-Content -Tail 25 log\stdout.log"
echo.
if exist log\server.log (
  echo ---- final de log\server.log ----
  powershell -NoProfile -Command "Get-Content -Tail 25 log\server.log"
)
echo.
echo Para iniciar de novo, use StartGameServerGui.vbs
goto :eof
:end
