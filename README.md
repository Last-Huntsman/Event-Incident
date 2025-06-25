# Event Processing System

## Описание

Проект состоит из двух микросервисов:

1. **event-generator-service** — генерирует события (TYPE_1, TYPE_2, и др.) вручную и автоматически.
2. **event-processor-service** — принимает события, обрабатывает их, создает инциденты.

## Запуск

```bash
./mvnw clean package -DskipTests

docker-compose up --build
```

## Основные классы

### event-generator-service

- **EventGeneratorServiceApplication** — точка входа Spring Boot.
- **RestTemplateConfig** — создает RestTemplate для HTTP-запросов.
- **EventController** — REST-контроллер для вручной генерации событий.
- **EventGenerator** — автоматически генерирует события в отдельном потоке.
- **EventDto** — DTO события (идентификатор, тип, время).
- **EventTypeEnum** — перечисление типов событий: TYPE_1, TYPE_2, TYPE_3, TYPE_4.

### event-processor-service

- **EventController** — REST-контроллер, принимает события и возвращает инциденты.
- **EventProcessingService** — сохраняет событие в базу, передает его на обработку.
- **IncidentService** — определяет логику объединения TYPE_1 и TYPE_2 в COMPOSITE инцидент.
- **Event / Incident** — модели для базы данных.
- **EventRepository / IncidentRepository** — Spring Data JPA репозитории.
- **EventTypeEnum / IncidentTypeEnum** — перечисления типов.

## API Swagger

- Generator UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Processor UI: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

## Логика обработки событий

- TYPE_1:
  - При наличии TYPE_2 (свежего < 20 сек) → COMPOSITE incident.
  - Если нет TYPE_2 рядом → SIMPLE incident.
- TYPE_2:
  - Буферизуется до 20 сек.

## 🚀 API Endpoints

#### Generator (8080)

- `POST /api/events/generate?type=TYPE_1` — ручная генерация

#### Processor (8081)

- `POST /api/events` — прием события
- `GET /api/events/incidents` — все инциденты

## 🧰 База данных

- **event**: id, type, time
- **incident**: id, type, time, events (many-to-many)

## 🚫 Тесты

- EventControllerTest
- EventProcessingServiceTest
- IncidentServiceTest

```bash
./mvnw test
```

## 📀 Docker Compose

```yaml
  services:
  postgres:
    image: postgres:15
    ports:
      - "5433:5432"
  event-processor-service:
    build: ./event-processor-service
    ports:
      - "8081:8081"
  event-generator-service:
    build: ./event-generator-service
    ports:
      - "8080:8080"
```

## 👤 Автор

Egor