@echo off
chcp 65001 > nul
echo ==========================================
echo   TEST IMPORTACION EXCEL - SIMPLE
echo ==========================================

rem Crear directorio build
if not exist "build" mkdir build

echo Compilando...

rem Compilar en orden correcto
javac -d build -encoding UTF-8 src\model\Postulante.java
javac -d build -cp build -encoding UTF-8 src\util\ExcelUtils.java  
javac -d build -cp build -encoding UTF-8 src\main\TestExcelSimple.java

if %ERRORLEVEL% NEQ 0 (
    echo Error de compilacion
    pause
    exit /b 1
)

echo Compilacion OK!
echo.
echo Ejecutando test...
java -cp build main.TestExcelSimple

echo.
echo Test completado.
pause
