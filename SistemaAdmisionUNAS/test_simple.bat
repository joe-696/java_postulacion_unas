@echo off
chcp 65001 > nul
echo ========================================
echo   COMPILACION Y TEST SIMPLE
echo ========================================

cd /d "d:\descaargas 2\finn-project\java_postulacion_unas\java_postulacion_unas\SistemaAdmisionUNAS"

echo.
echo 1. COMPILANDO...
javac -encoding UTF-8 -cp "." -d "." src\model\*.java src\util\ExcelUtilsSimple.java src\dao\*.java src\main\TestImportacionSimple.java

if %ERRORLEVEL% NEQ 0 (
    echo ❌ ERROR DE COMPILACION
    pause
    exit /b 1
)

echo ✓ Compilacion exitosa

echo.
echo 2. EJECUTANDO TEST...
java -cp "." main.TestImportacionSimple

echo.
echo 3. PRESIONE CUALQUIER TECLA PARA SALIR...
pause > nul
