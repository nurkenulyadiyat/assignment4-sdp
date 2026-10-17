@echo off
setlocal
cd /d "%~dp0"
if not exist out mkdir out
(for %%f in (src\*.java) do @echo "%%f"
 for %%f in (tests\*.java) do @echo "%%f") > sources.txt
javac --release 17 -d out @sources.txt
if errorlevel 1 (
  del sources.txt
  echo Compilation failed. Install JDK 17 or newer.
  exit /b 1
)
del sources.txt
echo === DEMO ===
java -cp out Main
if errorlevel 1 exit /b 1
echo.
echo === TESTS ===
java -cp out OrderFacadeTest
endlocal
