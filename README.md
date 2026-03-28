# Task Management API

A RESTful task management service built with Kotlin, Spring Boot, WebFlux, and JdbcClient.

## Stack

- Kotlin + Spring Boot 3.4.4
- Spring WebFlux (reactive HTTP)
- Project Reactor (Mono/Flux)
- JdbcClient + native SQL (no ORM)
- H2 in-memory database
- Flyway migrations
- MockK for unit tests

## Run

```bash
./gradlew bootRun
```

The app starts on `http://localhost:8080`.

## Test

```bash
./gradlew test
```

## API

### Create task
```
POST /api/tasks
Content-Type: application/json

{ "title": "Prepare report", "description": "Monthly financial report" }
```

### Get task by id
```
GET /api/tasks/{id}
```

### List tasks (paginated)
```
GET /api/tasks?page=0&size=10&status=NEW
```
`status` is optional. Possible values: `NEW`, `IN_PROGRESS`, `DONE`, `CANCELLED`.

Response:
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

### Update task status
```
PATCH /api/tasks/{id}/status
Content-Type: application/json

{ "status": "DONE" }
```

### Delete task
```
DELETE /api/tasks/{id}
```
Returns `204 No Content`.

## Project Structure

```
src/main/kotlin/com/example/demo/
├── controller/   # HTTP layer
├── service/      # Business logic, Mono/Flux
├── repository/   # JdbcClient + native SQL
├── model/        # Task, TaskStatus
├── dto/          # Request/Response DTOs
├── mapper/       # Task -> TaskResponse
├── exception/    # TaskNotFoundException, GlobalExceptionHandler
└── config/       # JdbcConfig
```
