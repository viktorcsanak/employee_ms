# Employee Management System

Employee Management System is a full-stack application built as a migration of an earlier MEAN-stack project to an Angular + Spring Boot + PostgreSQL architecture.

The main focus of this version is the Java backend, which demonstrates REST API design, layered service architecture, authentication, role-based authorization, persistence with JPA/Hibernate, Kafka-based event publishing, and containerized local development.

## Features

- User registration and management
- Role-based access control
- JWT-based authentication with session tracking
- Admin and HR-specific user views
- Password management
- User deletion and permission management
- PostgreSQL persistence using Spring Data JPA / Hibernate
- Kafka event publishing for user-created events
- Docker Compose setup for local development
- Unit and web-layer tests with JUnit, Mockito, and MockMvc

## Tech Stack

### Backend

- Java 17
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA / Hibernate
- Spring Kafka
- Maven
- PostgreSQL
- JUnit
- Mockito
- MockMvc

### Infrastructure

- Docker
- Docker Compose
- Apache Kafka
- PostgreSQL

### Frontend

- Angular

## Backend Architecture

The backend follows a layered Spring Boot structure:

```text
Controller layer
    |
Facade / Service layer
    |
Repository layer
    |
PostgreSQL database
```

Main backend responsibilities:

- expose REST endpoints for user and administration workflows
- validate incoming requests
- authenticate users through JWT/session cookies
- authorize actions based on roles
- persist user and session data
- publish user-related events to Kafka

## Security Model

The application uses Spring Security with a custom JWT/session filter.

Authenticated requests are validated using a token stored in a cookie. The backend verifies the session, loads the related user roles, and adds the corresponding authorities to the Spring Security context.

Example roles:

- `BASIC`
- `HR`
- `ADMIN`

Selected endpoints are protected with method-level authorization using `@PreAuthorize`.

## Kafka Integration

The backend publishes user-related events to Kafka.

Current event topic:

```text
user.account.created
```

Kafka is included in the Docker Compose setup for local development.

## Local Development

### Prerequisites

Make sure the following are installed:

- Java 17
- Maven
- Docker
- Docker Compose

## Running with Docker Compose

From the project root, run:

```bash
docker compose up --build
```

This starts:

- PostgreSQL
- Kafka
- Spring Boot backend service

The backend service is exposed on:

```text
http://localhost:8080
```

## Running the Backend Locally

Start PostgreSQL and Kafka locally, then run the backend from the `user-service` directory:

```bash
cd user-service
mvn clean install -Dspring.profiles.active=local
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## Running Tests

From the `user-service` directory:

```bash
mvn test
```

The current test suite contains unit and web-layer tests for service, facade, authentication, and controller behavior.

## API Overview

Base user endpoint:

```text
/api/user
```

Example protected user-management endpoints:

```text
GET    /api/user
POST   /api/user/management/admin/register
GET    /api/user/management/admin
PUT    /api/user/management/admin/permissions/{id}
PUT    /api/user/management/admin/password/{id}
DELETE /api/user/management/admin/delete/{id}
GET    /api/user/management/hr
```

Example authentication endpoints:

```text
POST   /api/auth/login
POST   /api/auth/logout
GET    /api/auth/verify-token
```

## Project Purpose

This project was built to practice and demonstrate backend development with Java and Spring Boot in a realistic application structure.

It covers common enterprise backend concerns:

- REST API design
- layered architecture
- dependency injection
- persistence with JPA/Hibernate
- authentication and authorization
- role-based access control
- event-driven communication with Kafka
- containerized development environment
- automated testing

## Notes

This project is intended as a development and portfolio project.

Configuration values used in the repository are local-development defaults and should be externalized before production use.
