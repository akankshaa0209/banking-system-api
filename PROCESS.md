## Step 1 - Created base repo with dummy structure.

## Step 2 — Create Spring Boot Project generates the base project structure for a Spring Boot application so you don’t have to manually create configuration files.

Think of it like a project template generator.
Go to Spring Initializr
Project: Maven: commonly used in Spring Boot projects, easier dependency mngmnt, It creates a pom.xml file which manages all libraries.
Language: Java
Spring Boot version: 3.5.11
Project metadata: Group: com.akanksha: base package of your project. com.akanksha.bank
Artifact: banking-system-api
Description: Production-ready banking backend built with Spring Boot
Package name: com.akanksha.bank
Packaging: Jar: Spring Boot runs as a standalone application.
Java: 17: Spring Boot 3 works perfectly with Java 17.
Configuration Type: Properties: This means configuration will be stored in: application.properties instead of YAML. Both are fine, but properties is simpler.

Add Dependencies (Very Important)
Click: ADD DEPENDENCIES.
Spring Web : Build rest apis
Spring Data JPA : Database ORM
Spring Security: authentication
MySQL Driver: connect mysql
Lombok: reduce boilerplate code
Validation: validate input requests
Spring Boot DevTools: faster dev
Springdoc OpenAPI: Swagger api docs
