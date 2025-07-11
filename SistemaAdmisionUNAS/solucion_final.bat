@echo off
chcp 65001 > nul
cls
echo ==========================================
echo   SISTEMA DE ADMISION UNAS
echo   SOLUCION FINAL - IMPORTACION EXCEL
echo ==========================================

rem Limpiar compilacion anterior
if exist "build" rmdir /s /q build
mkdir build

echo.
echo [1/4] Compilando modelo...
javac -d build -encoding UTF-8 src\model\Postulante.java
if %ERRORLEVEL% NEQ 0 goto error

echo [2/4] Compilando utilidades...
javac -d build -cp build -encoding UTF-8 src\util\ExcelUtils.java
if %ERRORLEVEL% NEQ 0 goto error

echo [3/4] Compilando test...
javac -d build -cp build -encoding UTF-8 src\main\TestExcelSimple.java
if %ERRORLEVEL% NEQ 0 goto error

echo [4/4] Ejecutando test de importacion...
echo.
echo ==========================================
java -cp build main.TestExcelSimple
echo ==========================================

echo.
echo IMPORTACION COMPLETADA!
echo.
echo Para usar en la aplicacion principal:
echo - Use compilar_ejecutar.bat
echo.
goto fin

:error
echo.
echo ERROR DE COMPILACION!
echo Verifique el codigo fuente.
echo.

:fin
pause
