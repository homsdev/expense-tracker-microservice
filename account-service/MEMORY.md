# Project Decisions

- The service targets Java 21 and Spring Boot 4.1.1. Run tests with a JDK 21 runtime: `.\mvnw.cmd test`.
- Account transaction events update balances through `TransactionEventsListener`. Missing accounts publish a compensation event; other processing failures are rethrown for RabbitMQ dead-letter handling.
- Event listener tests use JUnit 5 and Mockito only; they must not start a Spring context.
- API documentation uses Springdoc OpenAPI. Swagger UI is available at `/swagger-ui/index.html`, and the OpenAPI definition is available at `/v3/api-docs`.
- JaCoCo is the only reporting tool. It generates coverage reports during Maven's `test` phase at `target\site\jacoco\index.html`.
