# Stage 1: Build the application using Poetry
FROM python:3.12-slim AS builder

# Install system dependencies for Poetry and other Python packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    build-essential \
    libssl-dev \
    libsasl2-dev \
    python3-dev \
    curl && \
    rm -rf /var/lib/apt/lists/*

# Install Poetry
RUN curl -sSL https://install.python-poetry.org | python3 -

# Set environment variables for Poetry
ENV PATH="/root/.local/bin:$PATH"

# Set working directory inside the container
WORKDIR /app

# Copy only the poetry files (to leverage Docker cache during rebuilds)
COPY pyproject.toml poetry.lock ./

# Install dependencies (this will install the project dependencies defined in poetry.toml)
RUN poetry install --no-root --only main

# Stage 2: Create a smaller runtime image
FROM python:3.12-slim AS runtime

# Install system dependencies (only needed for runtime)
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    libsasl2-dev \
    libssl-dev \
    && rm -rf /var/lib/apt/lists/*

# Create a non-root user for running the application
RUN adduser --disabled-password --gecos "" appuser

# Set working directory for runtime
WORKDIR /app

# Copy installed dependencies from the build stage
COPY --from=builder /app /app

# Change ownership of the directory to the non-root user
RUN chown -R appuser:appuser /app

# Switch to the non-root user
USER appuser

# Define the entrypoint for your application (adjust with your script or executable)
ENTRYPOINT ["poetry", "run", "python", "main.py"]
