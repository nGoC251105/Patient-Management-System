@echo off
chcp 65001 >nul
cd /d "%~dp0"
if not exist build mkdir build
javac -encoding UTF-8 -d build src\*.java
if errorlevel 1 (echo Compilation failed.& pause & exit /b 1)
java -cp build PatientManager
pause
