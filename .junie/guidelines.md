# Project Guidelines

## Build System & Tooling
- **Java Version**: 25.
- **Build Tool**: Gradle (Kotlin DSL).
- **Spring Boot**: 4.0.2.
- **Testing**:
  - JUnit 5.
  - Testcontainers are used for integration tests, specifically for PostgreSQL.
  - Mockito is configured with a `-javaagent` in `build.gradle.kts` to avoid self-attachment warnings during tests.
  - Parallel test execution is enabled.
  - **Re-running tests**: To force Gradle to re-run tests (bypassing the build cache), use `./gradlew cleanTest test` or `./gradlew test --rerun-tasks`.
- **Modulith**: Spring Modulith 2.0.2 is used to enforce architectural boundaries and handle event publication.

## Architecture & Modularity
- **Spring Modulith**: The project uses Spring Modulith. Each sub-package of `me.profiluefter.vibeworker` is a module.
- **Module Encapsulation**:
  - Public API (Interfaces, DTOs, Events) should be in the module's root package (e.g., `me.profiluefter.vibeworker.mensa`).
  - Implementation details (Entities, Repositories, Service implementations) should be moved to a `.service` sub-package or similar to remain internal to the module.
- **External Events**: Events published to other modules or external systems via Spring Modulith's event registry should use the `@Externalized` annotation.

## Database & Persistence
- **PostgreSQL**: Version 18.
- **Migrations**: Flyway handles database migrations. Migration files are located in `src/main/resources/db/migration`.
- **Transactions**: Methods that publish Spring Modulith events (like those in `MensaPollingService`) MUST be annotated with `@Transactional` to ensure the events are correctly recorded in the event publication registry.

## Local Environment
- **Docker Compose**: A `compose.yaml` file is provided to spin up the required local services:
  - **PostgreSQL**: Accessible on port 5432.
  - **Dex**: An OIDC provider for local development, accessible on port 5556.
- **Local Profile**: Use the `local` Spring profile to connect to these services.

## API Integration
- **JKU Mensa API**: The project integrates with the JKU Mensa menu API. The base URL is configured via `JkuEndpoints`.
