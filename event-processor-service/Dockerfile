FROM eclipse-temurin:21-jdk
LABEL authors="Egor"

WORKDIR /app

COPY target/event-processor-service-0.0.1-SNAPSHOT.jar app.jar


ENTRYPOINT ["java", "-jar", "app.jar"]
