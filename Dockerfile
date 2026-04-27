FROM gradle:8.13-jdk17 AS builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

RUN gradle dependencies --no-daemon || true

COPY . .

RUN gradle bootJar -x test --no-daemon

RUN mv build/libs/*.jar app.jar


FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/app.jar app.jar

RUN useradd -m appuser
USER appuser

EXPOSE 8088

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]