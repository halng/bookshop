
FROM golang:1.23-alpine AS builder

WORKDIR /app
RUN apk --no-cache update && \
    apk --no-cache add git gcc libc-dev

ENV GOPROXY=https://proxy.golang.org,direct
ENV CGO_ENABLED=1
ENV GOOS=linux
ENV GOARCH=amd64
RUN export GO111MODULE=on


COPY go.mod go.sum ./

RUN go mod tidy
RUN go mod download

COPY . .

RUN go build -tags musl -o main .

FROM alpine:latest

WORKDIR /app

COPY --from=builder /app/main .

EXPOSE 9001

# Command to run the application
ENTRYPOINT ["./main"]
