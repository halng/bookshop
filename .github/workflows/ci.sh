#!/bin/bash

set -euo pipefail

################################################################################################################################
## HELPER FUNCTIONS
################################################################################################################################

# Detect changed folders
detect_changed_folders() {
    local current_branch
    current_branch=$(git rev-parse --abbrev-ref HEAD)
    if [[ "$current_branch" == "main" ]]; then
        find . -maxdepth 1 -type d -not -path '*/\.*' -exec basename {} \;
    else
        git fetch origin main
        git diff --name-only origin/main | awk -F'/' '{print $1}' | sort -u
    fi
}

# Detect language based on folder contents
detect_language() {
    local folder=$1
    if [[ -f "$folder/pom.xml" ]]; then
        echo "java"
    elif [[ -f "$folder/package.json" ]]; then
        echo "js"
    elif compgen -G "$folder/*.go" >/dev/null; then
        echo "go"
    elif [[ -f "$folder/pyproject.toml" ]]; then
        echo "py"
    else
        echo "unknown"
    fi
}

################################################################################################################################
## INSTALL DEPENDENCIES
################################################################################################################################

install_dependencies() {
    
    curl -O https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-6.2.1.4610-linux-x64.zip
    unzip *.zip
    export PATH=$PATH:"${GITHUB_WORKSPACE}/sonar-scanner-6.2.1.4610-linux-x64/bin"
    echo "Done: Download and Unzip Sonar Cloud"

    echo "Installing Golang CLI Linter..."
    go install github.com/golangci/golangci-lint/cmd/golangci-lint@v1.61.0

    echo "Installing Poetry..."
    curl -fsSL https://install.python-poetry.org | python3 -
}

install_dependencies

################################################################################################################################
## MAIN CI PROCESS
################################################################################################################################

run_ci() {
    local folder=$1
    local language=$2
    local project_key="anyshop_${folder}"
    
    echo "==========================================================================="
    echo "== Processing folder: $folder - Language: $language"
    echo "==========================================================================="
    
    cd "$folder"
    case "$language" in
        java)
            mvn clean install -DskipTests=true
            mvn verify
            ;;
        js | ts)
            npm install
            npm run lint
            ;;
        go)
            golangci-lint run
            go test -coverprofile=coverage.out ./...
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

    if [[ "$language" == "java" ]]; then
        mvn verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.token=$PSON_TOKEN -Dsonar.projectKey=$project_key
    else
        sonar-scanner \
            -Dsonar.token=$PSON_TOKEN \
            -Dsonar.sources=. \
            -Dsonar.host.url=https://sonarcloud.io \
            -Dsonar.organization=tanhao111 \
            -Dsonar.projectKey=$project_key \
            -Dsonar.projectName=$folder
    fi

    if [[ -f "Dockerfile" ]]; then
        COMMIT_HASH=$(git rev-parse --short HEAD)

        if [[ "$EVENT_NAME" == "pull_request" ]]; then
            IMAGE_TAG="PR"
        elif [[ "$REF" == "refs/heads/main" ]]; then
            IMAGE_TAG="latest"
        else
            IMAGE_TAG="$COMMIT_HASH"
        fi

        # Build Docker image with the commit hash as the tag
        IMAGE_NAME="ghcr.io/$DOCKER_USERNAME/anyshop-$folder:$IMAGE_TAG"
        docker build -t "$IMAGE_NAME" .

        # Login to GitHub Container Registry
        echo "$DOCKER_PASSWORD" | docker login ghcr.io -u "$DOCKER_USERNAME" --password-stdin

        # Push Docker image to GitHub registry
        docker push "$IMAGE_NAME"
    fi

    
    cd ..
    echo "==========================================================================="
    echo "== Completed CI for $folder"
    echo "==========================================================================="
}

################################################################################################################################
## RUN CI FOR CHANGED FOLDERS
################################################################################################################################

echo "Detecting changed folders..."
CHANGED_FOLDERS=$(detect_changed_folders)

if [[ -z "$CHANGED_FOLDERS" ]]; then
    echo "No relevant changes detected. Exiting CI process."
    exit 0
fi

echo "Detected changed folders: $CHANGED_FOLDERS"

for folder in $CHANGED_FOLDERS; do
    if [[ ! -d "$folder" ]]; then
        echo "Skipping $folder: Not a valid directory."
        continue
    fi

    case "$folder" in
        .github | shop | script | .vscode)
            echo "Skipping $folder: Excluded from CI process."
            continue
            ;;
    esac

    language=$(detect_language "$folder")
    if [[ "$language" == "unknown" ]]; then
        echo "Skipping $folder: Unknown language."
        continue
    fi

    run_ci "$folder" "$language"
done
