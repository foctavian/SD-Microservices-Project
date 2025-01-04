# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the entire project to the container
COPY . .


RUN mvn clean package -DskipTests -pl user-service

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/user-service/target/*.jar user-service.jar



# Command to run the application
ENTRYPOINT ["java", "-jar", "user-service.jar"]
