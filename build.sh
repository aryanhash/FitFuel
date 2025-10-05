#!/bin/bash
set -e

echo "Building FITFUEL Backend..."

# Install Java 17 if not available
if ! command -v java &> /dev/null; then
    echo "Installing Java 17..."
    apt-get update && apt-get install -y openjdk-17-jdk
fi

# Navigate to backend directory
cd backend

# Make Maven wrapper executable
chmod +x ./mvnw

# Build the application
echo "Running Maven build..."
./mvnw clean package -DskipTests

echo "Build completed successfully!"
