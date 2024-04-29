# user-api-spring

## Overview
The User API Service is a Spring Boot-based application designed to manage user data through a RESTful API. It provides functionalities for creating, updating, deleting, and querying user information based on various attributes such as email, name, and birthdate. The service is built to demonstrate backend practices with a focus on REST API design, validation, and error handling.

## Technology Stack
- Spring Boot
- Spring Data JPA
- Hibernate
- Jakarta Bean Validation
- Lombok
- Maven - Dependency Management
- JUnit
- Mockito

## Branching Information
- **Main branch**: Configured to use H2, an in-memory database. 
- **db/migrate-to-postgres branch**: This branch includes implementation changes to support PostgreSQL. 

## API Endpoints
The application exposes several REST endpoints for managing users:

- `POST /users`: Create a new user.
- `PUT /users/{id}`: Update an existing user.
- `PATCH /users/{id}`: Partially update an existing user.
- `DELETE /users/{id}`: Delete a user by ID.
- `GET /users/search`: Search for users by birth date range.

## Help
Ask questions at [Yana Koroliuk](https://t.me/Koroliuk_Yana) and post issues on GitHub.

## License
This project is [GNU General Public](https://www.gnu.org/licenses/gpl-3.0) licensed.
