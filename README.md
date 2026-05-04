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
