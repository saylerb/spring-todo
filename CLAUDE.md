# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with
code in this repository.

## Project Overview

This is a Spring Boot TODO API application that implements the TodoBackend
specification (https://www.todobackend.com/). The application uses PostgreSQL
for persistence with Flyway for database migrations.

## Architecture

- **Main Application**: Standard Spring Boot structure in `src/main/java/todo/`
- **Database Layer**: JPA repositories with PostgreSQL backend
- **REST API**: TodoController provides CRUD operations for todos at `/todos`
endpoint
- **Database Migrations**: Flyway migrations in
`src/main/resources/db/migration/`
- **Testing**: Two test suites - unit tests (`src/test/`) and database
integration tests (`src/databaseTest/`)

## Java Version Requirements

**Important**: This project requires Java 24 to run properly. The project uses
Spring Boot 3.5.0 which supports Java 24.

**Set Java 24 environment (recommended):**
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-24.jdk/Contents/Home
```

**Alternative**: Prefix commands with Java path:
```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-24.jdk/Contents/Home ./gradlew [command]
```

## Development Commands

**Prerequisites:**
- Set JAVA_HOME as shown above, or prefix all Gradle commands with the JAVA_HOME path
- Start PostgreSQL: `docker-compose up -d`

**Common Gradle tasks:**
```bash
./gradlew check           # Run all tests (unit + database)
./gradlew test            # Run unit tests only  
./gradlew databaseTest    # Run database integration tests only
./gradlew build           # Build the application
./gradlew bootRun -Dspring.profiles.active=dev  # Start in dev mode
```

## Configuration Profiles

- **dev profile**: Connects to local PostgreSQL database `todo`, uses
  `validate` for DDL
- **test profile**: Connects to test database `todo_test`, uses `create` for
  DDL
- Database credentials: admin/password for both environments

## Test Structure

The project has a custom `databaseTest` source set for integration tests that
require a real database connection, separate from unit tests that can run
without external dependencies.

## Technical Notes

- **Spring Boot 3.5.0**: Uses Jakarta EE instead of Java EE
  (jakarta.persistence vs javax.persistence)
- **JUnit 5**: Tests use JUnit Jupiter annotations (@Test from
  org.junit.jupiter.api)
- **Native Access**: JVM args include --enable-native-access=ALL-UNNAMED to
  handle Java module system warnings

## Version History

This project was upgraded from:
- Java 8 → Java 24
- Spring Boot 2.1.4 → 3.5.0  
- Gradle 7.6.1 → 8.14
- JUnit 4 → JUnit 5
- PostgreSQL 11 → 15
