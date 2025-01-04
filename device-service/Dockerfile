# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the entire project to the container
COPY . .

# Build the specific service
RUN mvn clean package -DskipTests -pl device-service

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/device-service/target/*.jar device-service.jar

# Expose the port that the application will run on


# Command to run the application
ENTRYPOINT ["java", "-jar", "device-service.jar"]
