# Multi-stage build for production
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy dependency files first for better layer caching
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Make mvnw executable
RUN chmod +x ./mvnw

# Copy source code and build (dependencies will be downloaded automatically)
COPY src ./src
RUN ./mvnw clean package -DskipTests -Dspring.profiles.active=prod -B

# Production runtime stage - use JRE instead of JDK
FROM eclipse-temurin:17-jre AS production

# Install security updates
RUN apt-get update && apt-get upgrade -y && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring

WORKDIR /app

# Copy JAR file with specific name
COPY --from=build /app/target/*.jar app.jar

# Set ownership and permissions
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring

# Configure JVM for production with better memory management
ENV JAVA_OPTS="-server \
    -Xms256m \
    -Xmx512m \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:+OptimizeStringConcat \
    -XX:+UseCompressedOops \
    -XX:+UseCompressedClassPointers \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.profiles.active=prod"

EXPOSE 8080

# Optimized health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 --start-period=60s \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Use exec form for better signal handling
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
