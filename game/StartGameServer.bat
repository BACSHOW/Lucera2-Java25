@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-25.0.3
set PATH=%JAVA_HOME%\bin;%PATH%
:start
echo Starting GameServer (Java 21/25 Tuning)...
echo.
"%JAVA_HOME%\bin\java" -server -Dfile.encoding=UTF-8 -Xms2G -Xmx5G -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UseStringDeduplication -XX:+AlwaysPreTouch -XX:+UseCompactObjectHeaders -XX:G1PeriodicGCInterval=300000 --enable-native-access=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED -cp "config;../libs/*" l2.gameserver.GameServer

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