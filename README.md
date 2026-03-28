# Task Manager Backend

A **Reactive Kotlin Spring Boot backend** for managing tasks.  
Supports creating, updating, deleting, and listing tasks with **pagination and filtering**.  
Fully reactive using **Mono/Flux**, with **validation and centralized error handling**.

---

## **Features**

- Create tasks (`POST /api/tasks`)
- Retrieve tasks by ID (`GET /api/tasks/{id}`)
- List tasks with pagination and optional status filter (`GET /api/tasks?page=&size=&status=`)
- Update task status (`PATCH /api/tasks/{id}/status`)
- Delete tasks (`DELETE /api/tasks/{id}`)
- Reactive Mono/Flux API using Spring WebFlux
- Validation for inputs (`@Valid`)
- Centralized error handling via `@RestControllerAdvice`
- Unit tests for service and controller
- Flyway migrations for database schema

---

## **Technology Stack**

- Kotlin, Spring Boot 3.x
- Spring WebFlux, R2DBC
- H2 (in-memory) or PostgreSQL
- Flyway for database migrations
- Project Reactor (Mono/Flux)
- MockK + WebTestClient for unit tests
- Gradle build system

---

## **Requirements**

- Java 17+ installed
- Gradle wrapper included (no separate Gradle installation required)

> ⚠️ **Important:** The project is configured to use **Java 17 toolchain**. Ensure your machine has **Java 17+** to build and run successfully.

---

## **Getting Started**

1. **Clone the repository**

```bash
git clone https://github.com/katherinek727/task-manager-backend.git
cd task-manager-backend