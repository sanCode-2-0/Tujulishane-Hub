# Use a lightweight base image for the runtime
FROM openjdk:21-slim

# Working directory
WORKDIR /app

# Add metadata
LABEL maintainer="Briane Lomoni <kapolonbraine@gmail.com>"
LABEL version="1.0.0"
LABEL description="Backend for Tujulishane-Hub (Spring Boot)"

# Copy the built jar into the container (expect build/libs/*.jar to exist)
# Build the jar locally first: gradlew bootJar -x test
COPY build/libs/*.jar app.jar

# Expose port and use Render's provided PORT at runtime
EXPOSE 8080

# Run the application binding to the provided PORT (Render sets $PORT)
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
