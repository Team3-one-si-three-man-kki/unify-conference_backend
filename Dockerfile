# 1. Build Stage: Build the application using Gradle
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /app

# Copy the entire project
COPY . .

# Build the application, skipping tests for faster CI builds
RUN ./gradlew build -x test

# 2. Runtime Stage: Create the final, smaller image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the default port for Spring Boot applications
EXPOSE 9093

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
