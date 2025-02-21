# Use an official Gradle image with JDK 11 to build the project
FROM gradle:7.0.2-jdk11 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the entire project to the container
COPY . .

# Build the project using Gradle (including the shadowJar task)
RUN gradle shadowJar --no-daemon

# Use OpenJDK 11 for the final image
FROM openjdk:11-jre-slim

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/* /app/provenance-api.jar

# Copy the resources directory to the appropriate location in the container
COPY src/main/resources /app/src/main/resources
COPY libs /app/lib


# Install the OpenFaaS watchdog binary
COPY --from=ghcr.io/openfaas/classic-watchdog:0.3.1 /fwatchdog /usr/bin/fwatchdog
RUN chmod +x /usr/bin/fwatchdog

# Set the working directory in the container
WORKDIR /app

# Create a non-root user
RUN addgroup --system app && adduser --system --ingroup app app
USER app

# Define the fprocess and watchdog environment variables
ENV fprocess="java -jar /app/provenance-api.jar"
ENV write_debug="true"

# Expose the OpenFaaS function port
EXPOSE 8080

# Start the watchdog
CMD ["fwatchdog"]


