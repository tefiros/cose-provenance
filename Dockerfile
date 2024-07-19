# Use a Maven image-base to compile the application
FROM maven:4.0.0-jdk-11 AS build

# Establish the working directory
WORKDIR /app

# Copy the Maven configuration file
COPY pom.xml .

# Download Maven dependencies
RUN mvn dependency:go-offline -B

# Copy the rest of the source code from the project
COPY src ./src

# Compile the project and build the package (jar)
RUN mvn package -DskipTests

# Use a Java image-base to execute the application
FROM openjdk:21-jre-slim

# Establish the working directory
WORKDIR /app

# Copy the jar file from the compilation state
COPY --from=build /app/target/provenance-api-1.0-SNAPSHOT.jar app.jar

# Copy the container resources
COPY src/main/resources ./resources

# Show the port in which the application is to be executed
EXPOSE 8080

# Specify that the container will accept arguments
ENTRYPOINT ["java", "-cp", "app.jar"]
