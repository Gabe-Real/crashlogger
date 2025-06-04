# escape=\
# syntax=docker/dockerfile:1

# Build arguments for versions
ARG JAVA_VERSION=21
ARG PROJECT_NAME=crashlogger
ARG PROJECT_VERSION=1.0-SNAPSHOT

FROM openjdk:${JAVA_VERSION}-jre-slim

# Create required directories
RUN mkdir -p /bot/plugins
RUN mkdir -p /bot/data

# Declare required volumes
VOLUME [ "/bot/data" ]
VOLUME [ "/bot/plugins" ]

# Copy the JAR file into the container
COPY [ "build/libs/${PROJECT_NAME}-${PROJECT_VERSION}-all.jar", "/bot/app.jar" ]

# Set the correct working directory
WORKDIR /bot

# Run the JAR file
ENTRYPOINT [ "java", "-jar", "/bot/app.jar" ]
