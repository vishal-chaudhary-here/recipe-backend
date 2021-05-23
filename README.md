# RecipeBackend

This project was generated with [Spring-Boot](https://start.spring.io/) version 2.4.6.

## Prerequisites

- `java 11`: Make sure java 11 is installed. [Java 11] (https://adoptopenjdk.net/)
- `MongoDb`: Mongodb installed and configured. [MongoDb Installation] (https://docs.mongodb.com/manual/installation/)

## Configuration

### Configure Mongodb and swagger related Properties and Read it in Project

application.yaml
```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    url: /api.yaml
  api-docs:
    enabled: true

server:
  port: 8081

spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017
      database: recipe
```

## Running locally

### Run server (Development Mode)

Run `mvnw spring-boot:run` to run the application.

### Packaging as jar

To build the final jar and optimize the RecipeBackend application for production, run:

```
./mvnw clean verify
```

To ensure everything worked, run:

```
java -jar target/*.jar
```

### API Documentation

Navigate to [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) in your browser.

### Running unit tests

Run `mvnw test` to run the unit and integration test.

