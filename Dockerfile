# Use the official OpenJDK 17 image as a base
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven/Gradle build file and project files
COPY target/ledger-posting-system-0.0.1.jar /app/ledger-system.jar

# Expose the port that the application runs on
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "/app/ledger-system.jar"]
