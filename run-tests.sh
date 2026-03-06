#!/bin/bash
# Load environment from .env file if it exists
if [ -f .env ]; then
    # Parse .env safely (handles special characters in values)
    while IFS='=' read -r key value; do
        # Skip empty lines and comments
        [[ -z "$key" || "$key" =~ ^#.* ]] && continue
        # Remove quotes if present and export
        value="${value%\"}"
        value="${value#\"}"
        value="${value%\'}"
        value="${value#\'}"
        export "$key=$value"
    done < .env
fi

# Run tests with Maven
# Default: exclude Selenium test that requires Chrome
if [ $# -eq 0 ]; then
    mvn test -Dtest='!OnlineSeleniumRecaptchaIntegrationTest'
else
    mvn "$@"
fi
