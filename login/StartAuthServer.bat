@echo off
rem Login Server com console visivel. Para iniciar so com a janela propria, use StartAuthServerGui.vbs.
rem Flags do Java 25: cabecalhos de objeto compactos (menos memoria) e devolucao periodica de memoria livre.
cd /d "%~dp0"
set "JAVA_EXE=java"
if defined JAVA_HOME set "JAVA_EXE=%JAVA_HOME%\bin\java"
:start
echo Starting AuthServer.
echo.
"%JAVA_EXE%" -server -Dfile.encoding=UTF-8 -Xms128m -Xmx256m -XX:+UseG1GC -XX:+UseCompactObjectHeaders -XX:G1PeriodicGCInterval=300000 --enable-native-access=ALL-UNNAMED -cp config;../libs/* l2.authserver.AuthServer
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Server restarted ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly ...
echo.
:end
echo.
echo Server terminated ...
echo.

pause
