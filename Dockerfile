# syntax=docker/dockerfile:1
# Active les fonctionnalités BuildKit avancées (cache mounts)

# =====================================================
# Stage 1 : Build — compilation Maven avec cache BuildKit
# =====================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# --mount=type=cache : le repo Maven local (~/.m2) est conservé entre les builds
# sur l'hôte Docker. Les JARs ne sont re-téléchargés que s'ils sont absents.
# Cela évite les re-téléchargements complets à chaque changement de pom.xml.
RUN --mount=type=cache,target=/root/.m2 \
    chmod +x mvnw && ./mvnw dependency:resolve -B

COPY src src

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests -B

# =====================================================
# Stage 2 : Runtime — image vulnérable pour l'exercice
# =====================================================
# Utilisation d'une image volontairement ancienne pour déclencher Trivy
FROM eclipse-temurin:21.0.1_12-jre-jammy AS runtime

LABEL maintainer="TaskFlow API"
LABEL description="TaskFlow API - Spring Boot REST API"
LABEL version="0.0.1-SNAPSHOT"

# Sécurité : utilisateur non-root dédié (syntaxe Ubuntu/Jammy)
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

WORKDIR /app

COPY --from=builder /app/target/taskflowapi-0.0.1-SNAPSHOT.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

ENV JAVA_TOOL_OPTIONS="-Xms256m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

# Format exec (sans shell) — meilleure gestion des signaux SIGTERM
ENTRYPOINT ["java", "-jar", "app.jar"]
