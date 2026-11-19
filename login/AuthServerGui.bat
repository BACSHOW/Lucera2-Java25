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
if "%~1"==":mostrar_erro" goto mostrar_erro_inicio
if not exist log mkdir log
rem ---- Java 25 ------------------------------------------------------------
rem  Se o Java do PATH/JAVA_HOME nao for o 25, coloque aqui a pasta do JDK 25
rem  (a mesma do seu StartGameServer.bat), ex.: set "JAVA25_HOME=C:\Program Files\Java\jdk-25"
set "JAVA25_HOME=C:\Program Files\Java\jdk-25.0.3"
rem -------------------------------------------------------------------------
set "JAVA_EXE=java"
if defined JAVA_HOME set "JAVA_EXE=%JAVA_HOME%\bin\java"
if defined JAVA25_HOME set "JAVA_EXE=%JAVA25_HOME%\bin\java"
:start
echo.>> log\stdout.log
echo ===== %date% %time% - iniciando AuthServer =====>> log\stdout.log
"%JAVA_EXE%" -server -Dfile.encoding=UTF-8 -Xms128m -Xmx256m -XX:+UseG1GC -XX:+UseCompactObjectHeaders -XX:G1PeriodicGCInterval=300000 --enable-native-access=ALL-UNNAMED -Dl2.hiddenConsole=true -cp config;../libs/* l2.authserver.AuthServer >> log\stdout.log 2>&1
if ERRORLEVEL 2 goto start
if ERRORLEVEL 1 goto error
goto end
:error
rem Terminou com erro: abre um console visivel com o motivo.
set "HINT="
findstr /c:"Unrecognized VM option" /c:"UnsupportedClassVersionError" /c:"Unrecognized option" log\stdout.log >nul 2>&1 && set "HINT=1"
start "Login Server - erro" cmd /k "call "%~dp0%~nx0" :mostrar_erro"
goto end
:mostrar_erro_inicio
:mostrar_erro
echo O Login Server terminou com erro.
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
if exist log\java.log (
  echo ---- final de log\java.log ----
  powershell -NoProfile -Command "Get-Content -Tail 25 log\java.log"
)
echo.
echo Para iniciar de novo, use StartAuthServerGui.vbs
goto :eof
:end
