# Base image for Node.js environment
FROM node:18-alpine AS base

# Set working directory
WORKDIR /app

# Install dependencies
COPY package.json pnpm-lock.yaml* ./
RUN npm install -g pnpm && pnpm install

# Copy application code
COPY . .

# Stage 1: Development
FROM base AS dev-env
RUN npm install -g @angular/cli

# Development server
CMD ["ng", "serve", "--host", "0.0.0.0"]

# Stage 2: Production
FROM base AS builder
RUN npm install -g @angular/cli
RUN ng build

# Final Stage: Serve with Nginx
FROM nginx:alpine AS production
# Copy built app to nginx html folder
COPY --from=builder /app/dist/admin /usr/share/nginx/html
# Expose default nginx port
EXPOSE 80

# Start Nginx server
CMD ["nginx", "-g", "daemon off;"]
