#!/bin/bash

echo "Building and testing the Student REST API..."
echo ""

cd "$(dirname "$0")"

echo "Step 1: Clean previous builds"
./mvnw clean
if [ $? -ne 0 ]; then
    echo "Clean failed!"
    exit 1
fi

echo ""
echo "Step 2: Compile the project"
./mvnw compile
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo ""
echo "Step 3: Run tests"
./mvnw test
if [ $? -ne 0 ]; then
    echo "Tests failed!"
    exit 1
fi

echo ""
echo "Step 4: Package the application"
./mvnw package
if [ $? -ne 0 ]; then
    echo "Packaging failed!"
    exit 1
fi

echo ""
echo "========================================"
echo "Build and test completed successfully!"
echo "========================================"
echo ""
echo "You can now run the application with:"
echo "  ./mvnw spring-boot:run"
echo ""
echo "Or use the JAR file:"
echo "  java -jar target/rest-0.0.1-SNAPSHOT.jar"
echo ""

