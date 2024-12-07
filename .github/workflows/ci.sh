#!/bin/bash

set -e

# Detect changed folders
detect_changed_folders() {
    git fetch origin main
    echo "$(git diff --name-only origin/main | awk -F'/' '{print $1}' | sort -u)"
}

install_sonar_cloud() {
    curl -O https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-6.2.1.4610-linux-x64.zip
    unzip *.zip
    export PATH=$PATH:"${GITHUB_WORKSPACE}/sonar-scanner-6.2.1.4610-linux-x64/bin"
    echo "Done: Download and Unzip Sonar Cloud"
}

install_golang_cli_lint() {
  go install github.com/golangci/golangci-lint/cmd/golangci-lint@v1.61.0
}

install_poetry() {
   curl -sSL https://install.python-poetry.org | python3 -
}

# Detect language based on folder contents
detect_language() {
    local folder=$1
    if [[ -f "$folder/pom.xml" ]]; then
        echo "java"
        elif [[ -f "$folder/package.json" ]]; then
        echo "js"
        elif ls "$folder"/*.go >/dev/null 2>&1; then
        echo "go"
        elif [[ -f "$folder/pyproject.toml" ]]; then
        echo "py"
    else
        echo "unknown"
    fi
}

# Main CI process
run_ci() {
    local folder=$1
    local language=$2

    echo "==========================================================================="
    echo "========================== $folder - $language  ==========================="
    echo "==========================================================================="
    
    cd "$folder"
    
    case $language in
        java)
            #   ./gradlew sonarqube -Dsonar.login=$SONAR_TOKEN
            mvn clean compile
            mvn test
            rm -rf target/
        ;;
        js | ts)
            npm install
            npm run lint
        ;;
        go)
            golangci-lint run
            go test -coverprofile coverage.out ./...
            go test -json ./... > test-report.out
        ;;
        py)
            poetry install
            poetry run black --check .
            poetry run pytest --cov=app --cov-report=xml:coverage.xml
        ;;
        *)
            echo "No CI steps for $language in $folder, skipping."
        ;;
    esac
    
    local project_key="anyshop_$folder"
    sonar-scanner -Dsonar.token=$PSON_TOKEN -Dsonar.sources=. -Dsonar.host.url=https://sonarcloud.io -Dsonar.organization=tanhao111 -Dsonar.projectKey=$project_key -Dsonar.projectName=$folder
    
    # Build and push Docker image
    #   if [[ -f "Dockerfile" ]]; then
    #     docker build -t $DOCKER_USERNAME/$folder:latest .
    #     echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
    #     docker push "$DOCKER_USERNAME/$folder:latest"
    #   fi
    
    cd ..
    
    echo "============================================================================"
    echo "================================== DONE  ==================================="
    echo "============================================================================"
}

# Run CI for each changed folder
CHANGED_FOLDERS=$(detect_changed_folders)

if [[ -z "$CHANGED_FOLDERS" ]]; then
    echo "No relevant changes detected."
    exit 0
fi

echo "Detected changed folders: $CHANGED_FOLDERS"

echo "Starting install dependencies..."

install_sonar_cloud
install_golang_cli_lint
install_poetry

echo "Starting verify components..."
for folder in $CHANGED_FOLDERS; do
    if [[ ! -d "$folder" ]]; then
        echo "Folder $folder does not exist, skipping."
        continue
    fi
    
    if [[ "$folder" ==  ".github" ]]; then
        continue
    fi

    if [[ "$folder" ==  "api" ]]; then
        echo "Skip for now...."
        continue
    fi
    
    language=$(detect_language "$folder")
    echo "Detected language for $folder: $language"
    
    if [[ "$language" == "unknown" ]]; then
        echo "Unknown language in $folder, skipping."
        continue
    fi
    
    run_ci "$folder" "$language"
done
