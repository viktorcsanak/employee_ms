# Frontend

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 18.1.2.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via a platform of your choice. To use this command, you need to first add a package that implements end-to-end testing capabilities.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.

# Spring-boot(Debian 12)

## Local

To run the application with a local kafka and postgres setup run ` mvn clean install -Dspring.profiles.active=local` and `mvn spring-boot:run`

## Docker 
To run the application in containers, build it with `mvn clean install -DskipTest` (the tests expect kafka and postgres at localhost, so they will fail with the docker environment parameters) and the build the containers and run them with `docker compose up --build`

NOTE that if you already started kafka or postgres locally you will have to stop them, since for now the ports are colliding.
