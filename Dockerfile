# Use the official Maven image for building
FROM maven:3.9.5 AS build

# Set the working directory for building
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the application code
COPY . .

# Build the application
RUN mvn clean package

# Use the same Maven image for runtime to run exec:java
FROM maven:3.9.5

# Set the working directory for runtime
WORKDIR /app

# Copy the built JAR and all necessary dependencies
COPY --from=build /app/target/*.jar ./app.jar

# Copy the local dependency jars (if needed)
COPY --from=build /root/.m2/repository /root/.m2/repository

# Command to run the application
CMD ["mvn", "exec:java", "-Dexec.mainClass=com.telefonica.cose.provenance.example.Signer", "-Dexec.args='ietf-interfaces.xml'"]
