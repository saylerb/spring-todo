# Spring Todo API

A RESTful Todo API built with Spring Boot that implements the
[TodoBackend](https://www.todobackend.com/) specification.

## Features

- Full CRUD operations for todos
- PostgreSQL persistence with Flyway migrations
- CORS support for web clients
- Comprehensive test suite (unit + integration)
- Docker containerization

## Quick Start

### Prerequisites
- Java 25 (EA) – make sure a compatible toolchain is installed locally
- Docker and Docker Compose

### Installation

1. Clone this repository:
   ```bash
   git clone <repo-url>
   cd spring-todo
   ```

2. Set the Java 25 environment. For example on macOS:

   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 25)
   ```

3. Start PostgreSQL:
   ```bash
   docker-compose up -d
   ```

4. Run tests:
   ```bash
   ./gradlew check
   ```

5. Start the application:
   ```bash
   ./gradlew bootRun -Dspring.profiles.active=dev
   ```

The API will be available at `http://localhost:8080/todos`

## API Testing

Test the API against the TodoBackend specification:

1. Start the application in development mode
2. Visit the [TodoBackend test suite](https://www.todobackend.com/specs/index.html?http://localhost:8080/todos)
3. All tests should pass ✅

## API Endpoints

- `GET /todos` - List all todos
- `POST /todos` - Create a new todo
- `GET /todos/{id}` - Get a specific todo
- `PATCH /todos/{id}` - Update a todo
- `DELETE /todos/{id}` - Delete a specific todo
- `DELETE /todos` - Delete all todos

## Technology Stack

- **Java 25** with Spring Boot 3.5.0
- **PostgreSQL 15** for persistence
- **Flyway** for database migrations
- **JUnit 5** for testing
- **Gradle 9.1** for build automation
- **Docker** for containerization

## Building Custom PostgreSQL Docker Image

Publishing the Database Docker image is a manual process at the moment. 
This enables CI to pull the image to run integration tests. To build and
publish the custom PostgreSQL image for CI/CD:

1. Navigate to the postgres directory:
   ```bash
   cd postgres
   ```

2. Build the Docker image:
   ```bash
   docker build -t saylerb/spring-todo-postgres:0.1.0 .
   ```

3. Login to Docker Hub:
   ```bash
   docker login
   ```

4. Push the image:
   ```bash
   docker push saylerb/spring-todo-postgres:0.1.0
   ```
