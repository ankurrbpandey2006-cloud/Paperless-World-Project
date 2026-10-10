@echo off
REM Paperless World - Build & Run script
REM Requires Java 17+

echo [1/2] Compiling...
if not exist bin mkdir bin
javac --release 17 -cp "lib/*" -d bin src\App.java src\com\paperless\model\*.java src\com\paperless\exception\*.java src\com\paperless\util\*.java src\com\paperless\dao\*.java src\com\paperless\service\*.java src\com\paperless\ui\*.java
if errorlevel 1 (
  echo Compile failed
  exit /b 1
)
echo [2/2] Launching...
java -cp "bin;lib/*" App
