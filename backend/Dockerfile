# Multi-stage Dockerfile for building and running the Spring Boot app
# Build stage
FROM gradle:7.6-jdk17 AS builder
WORKDIR /home/gradle/project
# Copy the full project context (avoids missing gradle/gradlew copy issues)
COPY . .

# Run the Gradle build (skip tests for faster builds in CI)
# Use the project's Gradle wrapper for reproducible builds and to avoid relying on the builder image's gradle binary
RUN chmod +x ./gradlew || true
RUN ./gradlew bootJar -x test --no-daemon

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copy the fat jar produced by spring boot
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080
# Use server.port from environment (Render provides $PORT)
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
