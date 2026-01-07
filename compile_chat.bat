@echo off
echo ========================================
echo Compilation du Systeme de Chat
echo ========================================
echo.

REM Definir les chemins
set SRC_DIR=src\main\java
set OUT_DIR=target\classes
set CP=target\classes

REM Creer le repertoire de sortie
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

echo [1/5] Compilation de Message.java...
javac -d "%OUT_DIR%" -cp "%CP%" "%SRC_DIR%\model\Message.java"
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Echec de compilation de Message.java
    pause
    exit /b 1
)

echo [2/5] Compilation de MessageDAO.java...
javac -d "%OUT_DIR%" -cp "%CP%" "%SRC_DIR%\dao\MessageDAO.java"
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Echec de compilation de MessageDAO.java
    pause
    exit /b 1
)

echo [3/5] Compilation de ChatService.java...
javac -d "%OUT_DIR%" -cp "%CP%" "%SRC_DIR%\service\ChatService.java"
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Echec de compilation de ChatService.java
    pause
    exit /b 1
)

echo [4/5] Compilation de ChatController.java...
javac -d "%OUT_DIR%" -cp "%CP%" "%SRC_DIR%\controller\ChatController.java"
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Echec de compilation de ChatController.java
    pause
    exit /b 1
)

echo [5/5] Compilation de MainController.java...
javac -d "%OUT_DIR%" -cp "%CP%" "%SRC_DIR%\controller\MainController.java"
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Echec de compilation de MainController.java
    pause
    exit /b 1
)

echo.
echo ========================================
echo SUCCES! Tous les fichiers sont compiles
echo ========================================
echo.
echo Prochaines etapes:
echo 1. Executez le script SQL: database\add_message_table.sql
echo 2. Lancez l'application via votre IDE
echo 3. Cliquez sur "Chat / Aide" dans la navigation
echo.
pause
