@echo off
echo Building and testing the Student REST API...
echo.

cd /d "%~dp0"

echo Step 1: Clean previous builds
call mvnw.cmd clean
if errorlevel 1 (
    echo Clean failed!
    exit /b 1
)

echo.
echo Step 2: Compile the project
call mvnw.cmd compile
if errorlevel 1 (
    echo Compilation failed!
    exit /b 1
)

echo.
echo Step 3: Run tests
call mvnw.cmd test
if errorlevel 1 (
    echo Tests failed!
    exit /b 1
)

echo.
echo Step 4: Package the application
call mvnw.cmd package
if errorlevel 1 (
    echo Packaging failed!
    exit /b 1
)

echo.
echo ========================================
echo Build and test completed successfully!
echo ========================================
echo.
echo You can now run the application with:
echo   mvnw.cmd spring-boot:run
echo.
echo Or use the JAR file:
echo   java -jar target\rest-0.0.1-SNAPSHOT.jar
echo.

