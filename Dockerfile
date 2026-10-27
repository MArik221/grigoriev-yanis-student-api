# Multi-stage: compile source with the official Gradle JDK image.
FROM gradle:8.14.3-jdk21 AS builder
WORKDIR /workspace
COPY --chown=gradle:gradle . .
RUN gradle --no-daemon clean bootJar

# Runtime has only the JRE and application JAR.
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN useradd --system --uid 10001 --create-home appuser
COPY --from=builder /workspace/build/libs/*.jar /app/app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
