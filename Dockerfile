# Use an official Gradle image with Kotlin support to build the project
FROM gradle:7.0.2-jdk11 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the entire project to the container
COPY . .

# Build the project using Gradle (including the shadowJar task)
RUN gradle shadowJar --no-daemon

# Use an official JDK image to run the application
FROM openjdk:11-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/* /app/provenance-api.jar

# Copy the resources directory to the appropriate location in the container
COPY src/main/resources /app/src/main/resources


EXPOSE 8000

# Set the default command to run the Kotlin main class
ENTRYPOINT ["java", "-jar", "/app/provenance-api.jar"]

