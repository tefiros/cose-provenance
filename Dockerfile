# Use the official Maven image for building
FROM maven:3.9.5 AS build

# Set the working directory for building
WORKDIR /app

# Copy the application code
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Use the same Maven image for runtime to run exec:java
FROM maven:3.9.5

# Set the working directory for runtime
WORKDIR /app

COPY . .

# Command to run the application
CMD ["mvn", "exec:java", "-Dexec.mainClass=com.telefonica.cose.provenance.example.Signer", "-Dexec.args='ietf-interfaces.xml'"]