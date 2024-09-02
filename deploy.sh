#!/bin/bash

# Make sure the script is executable: chmod +x deploy.sh

# Example usage of environment variables
echo "Deploying application with the following environment variables:"
echo "JWT_SECRET: $JWT_SECRET"
echo "JWT_REFRESH_SECRET: $JWT_REFRESH_SECRET"

# Build the application
./gradlew build

# Deploy the application
# For example:
# scp build/libs/myapp.jar user@server:/path/to/deploy/
# ssh user@server 'java -jar /path/to/deploy/myapp.jar'