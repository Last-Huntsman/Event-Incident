FROM eclipse-temurin:21-jdk
LABEL author="Egor"

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем jar-файл
COPY target/event-generator-service-0.0.1-SNAPSHOT.jar app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
