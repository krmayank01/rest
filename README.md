# Student REST API

A complete CRUD REST API for managing students with input validation and comprehensive unit tests.

## Features

- **Full CRUD Operations**: Create, Read, Update, and Delete students
- **Input Validation**: Bean validation using Jakarta Validation API
- **Exception Handling**: Global exception handler for consistent error responses
- **H2 In-Memory Database**: Easy testing and development
- **Unit Tests**: Comprehensive test coverage for controllers and services
- **API Documentation**: Well-documented REST endpoints

## Technologies Used

- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Validation**
- **H2 Database**
- **Lombok**
- **JUnit 5 & Mockito**

## Project Structure

```
src/
├── main/
│   ├── java/com/example/rest/
│   │   ├── RestApplication.java          # Main application class
│   │   ├── controller/
│   │   │   └── StudentController.java    # REST controller
│   │   ├── dto/
│   │   │   └── StudentDto.java           # Data Transfer Object with validation
│   │   ├── entity/
│   │   │   └── Student.java              # JPA entity
│   │   ├── exception/
│   │   │   ├── ResourceNotFoundException.java
│   │   │   ├── DuplicateResourceException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── repository/
│   │   │   └── StudentRepository.java    # JPA repository
│   │   └── service/
│   │       └── StudentService.java       # Business logic layer
│   └── resources/
│       └── application.properties        # Application configuration
└── test/
    └── java/com/example/rest/
        ├── controller/
        │   └── StudentControllerTest.java # Controller unit tests
        └── service/
            └── StudentServiceTest.java     # Service unit tests
```

## API Endpoints

### Get All Students
```
GET /api/students
Response: 200 OK
```

### Get Student by ID
```
GET /api/students/{id}
Response: 200 OK | 404 Not Found
```

### Create Student
```
POST /api/students
Content-Type: application/json

Request Body:
{
  "studentName": "John Doe",
  "email": "john@example.com",
  "age": 20
}

Response: 201 Created | 400 Bad Request | 409 Conflict
```

### Update Student
```
PUT /api/students/{id}
Content-Type: application/json

Request Body:
{
  "studentName": "John Updated",
  "email": "john.updated@example.com",
  "age": 21
}

Response: 200 OK | 400 Bad Request | 404 Not Found | 409 Conflict
```

### Delete Student
```
DELETE /api/students/{id}
Response: 204 No Content | 404 Not Found
```

## Validation Rules

### Student DTO Validations:
- **studentName**: Required, 2-100 characters
- **email**: Required, valid email format
- **age**: Required, between 1 and 150

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven (or use included Maven Wrapper)

### Build the Project
```bash
# Using Maven Wrapper (Windows)
.\mvnw.cmd clean install

# Using Maven Wrapper (Linux/Mac)
./mvnw clean install

# Using system Maven
mvn clean install
```

### Run the Application
```bash
# Using Maven Wrapper (Windows)
.\mvnw.cmd spring-boot:run

# Using Maven Wrapper (Linux/Mac)
./mvnw spring-boot:run

# Using system Maven
mvn spring-boot:run

# Or run the JAR file
java -jar target/rest-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

### Access H2 Console
Visit `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:studentdb`
- Username: `sa`
- Password: (leave empty)

## Running Tests

### Run All Tests
```bash
# Using Maven Wrapper (Windows)
.\mvnw.cmd test

# Using Maven Wrapper (Linux/Mac)
./mvnw test

# Using system Maven
mvn test
```

### Test Coverage
- **Controller Tests**: `StudentControllerTest.java`
  - Tests for all CRUD operations
  - Validation error scenarios
  - Exception handling scenarios
  
- **Service Tests**: `StudentServiceTest.java`
  - Tests for all business logic
  - Edge cases and error scenarios
  - Repository interaction tests

## Example API Usage

### Using cURL

#### Create a Student
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "studentName": "John Doe",
    "email": "john@example.com",
    "age": 20
  }'
```

#### Get All Students
```bash
curl http://localhost:8080/api/students
```

#### Get Student by ID
```bash
curl http://localhost:8080/api/students/1
```

#### Update a Student
```bash
curl -X PUT http://localhost:8080/api/students/1 \
  -H "Content-Type: application/json" \
  -d '{
    "studentName": "John Updated",
    "email": "john.updated@example.com",
    "age": 21
  }'
```

#### Delete a Student
```bash
curl -X DELETE http://localhost:8080/api/students/1
```

### Using PowerShell

#### Create a Student
```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/students" `
  -ContentType "application/json" `
  -Body '{"studentName":"John Doe","email":"john@example.com","age":20}'
```

#### Get All Students
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/students"
```

## Error Responses

### Validation Error (400 Bad Request)
```json
{
  "status": 400,
  "errors": {
    "studentName": "Student name is required",
    "email": "Email should be valid"
  },
  "timestamp": "2025-12-18T23:15:30"
}
```

### Not Found Error (404)
```json
{
  "status": 404,
  "message": "Student not found with id: 999",
  "timestamp": "2025-12-18T23:15:30"
}
```

### Duplicate Resource Error (409 Conflict)
```json
{
  "status": 409,
  "message": "Student with email john@example.com already exists",
  "timestamp": "2025-12-18T23:15:30"
}
```

## Development Notes

### Input Validation
The application uses Jakarta Validation API (formerly Java EE Bean Validation) to validate incoming requests. All validation annotations are defined in the `StudentDto` class.

### Exception Handling
The `GlobalExceptionHandler` class handles all exceptions globally and returns appropriate HTTP status codes and error messages.

### Database
The application uses an H2 in-memory database that is recreated on every startup. To switch to a persistent database, update the `application.properties` file.

### Logging
The application uses SLF4J with Logback. Log levels can be configured in `application.properties`.

## Future Enhancements

- Add pagination and sorting for GET all students
- Add search and filtering capabilities
- Implement API documentation with Swagger/OpenAPI
- Add integration tests
- Add security with Spring Security
- Implement caching with Redis
- Add database migration with Flyway/Liquibase

## License

This project is created for educational purposes.

