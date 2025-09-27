@echo off

REM === CONFIGURATION ===
set "PROJECT_ROOT=D:\S5\Mr_Naina\sprint1"
set "FRAMEWORK_DIR=%PROJECT_ROOT%\Framework"
set "BUILD_DIR=%FRAMEWORK_DIR%\build"
set "TEST_DIR=%PROJECT_ROOT%\TestProject"
set "SERVLET_JAR=%TEST_DIR%\lib\jakarta.servlet-api-6.0.0.jar"
set "PROJECT_NAME=TestProject"
set "TOMCAT_HOME=C:\Services\Tomcat 11.0"

REM === VÉRIFICATION DE L'EXISTENCE DU JAR ===
echo Vérification de la présence de servlet-api.jar...
if not exist "%SERVLET_JAR%" (
    echo [ERREUR] Fichier introuvable: %SERVLET_JAR%
    pause
    exit /b 1
)

REM === NETTOYAGE ===
echo Nettoyage des anciens builds...
if exist "%BUILD_DIR%" rmdir /S /Q "%BUILD_DIR%"
if exist "%TEST_DIR%\web\WEB-INF\classes" rmdir /S /Q "%TEST_DIR%\web\WEB-INF\classes"
if exist "%TEST_DIR%\temp_war" rmdir /S /Q "%TEST_DIR%\temp_war"

mkdir "%BUILD_DIR%\classes"
mkdir "%TEST_DIR%\web\WEB-INF\classes"
mkdir "%TEST_DIR%\temp_war"

REM === COMPILATION DU FRAMEWORK ===
for /R "%FRAMEWORK_DIR%\src" %%f in (*.java) do (
    echo   Compilation de %%~nxf
    javac -classpath "%SERVLET_JAR%" -d "%BUILD_DIR%\classes" "%%f"
    if errorlevel 1 (
        echo [ERREUR] Echec compilation du framework
        pause
        exit /b 1
    )
)

REM === CRÉATION DU JAR DU FRAMEWORK ===
cd /d "%BUILD_DIR%"
if exist "framework.jar" del "framework.jar"
jar cf "framework.jar" -C "classes" .

REM === COPIE DU JAR DANS TestProject ===
echo Copie du framework.jar dans WEB-INF/lib...
if not exist "%TEST_DIR%\web\WEB-INF\lib" mkdir "%TEST_DIR%\web\WEB-INF\lib"
copy /Y "%BUILD_DIR%\framework.jar" "%TEST_DIR%\web\WEB-INF\lib\"

REM === COMPILATION DU PROJET TEST ===
for /R "%TEST_DIR%\src" %%f in (*.java) do (
    echo   Compilation de %%~nxf
    javac -classpath "%SERVLET_JAR%;%TEST_DIR%\web\WEB-INF\lib\framework.jar" -d "%TEST_DIR%\web\WEB-INF\classes" "%%f"
    if errorlevel 1 (
        echo [ERREUR] Echec compilation du projet test
        pause
        exit /b 1
    )
)

REM === PRÉPARATION DE LA STRUCTURE WAR CORRECTE ===
REM Copier TOUT le dossier web/ comme racine du WAR
echo Copie de la structure web/...
xcopy "%TEST_DIR%\web" "%TEST_DIR%\temp_war\" /E /I /Y

REM Vérification de la structure créée
dir "%TEST_DIR%\temp_war\"

REM === CRÉATION DU WAR ===
echo Création du WAR %PROJECT_NAME%.war...
cd /d "%TEST_DIR%\temp_war"
if exist "%PROJECT_NAME%.war" del "%PROJECT_NAME%.war"
jar cf "%PROJECT_NAME%.war" *

REM === DÉPLACEMENT DU WAR VERS TOMCAT ===
move /Y "%PROJECT_NAME%.war" "%TOMCAT_HOME%\webapps\"

REM === NETTOYAGE ===
if exist "%TEST_DIR%\%PROJECT_NAME%.war" del "%TEST_DIR%\%PROJECT_NAME%.war"

REM === REDÉMARRAGE DE TOMCAT ===
echo Arrêt de Tomcat...
call "%TOMCAT_HOME%\bin\shutdown.bat"
timeout /t 5 /nobreak > nul

echo Démarrage de Tomcat...
call "%TOMCAT_HOME%\bin\startup.bat"
timeout /t 5 /nobreak > nul

echo ------------------------------------------------
echo DÉPLOIEMENT TERMINÉ AVEC SUCCÈS !

echo URLs à tester :
echo   http://localhost:8080/TestProject/
echo   http://localhost:8080/TestProject/urls
echo   http://localhost:8080/TestProject/client
echo ------------------------------------------------
pause