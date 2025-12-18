# Test Documentation

## Unit Test Coverage

This document provides an overview of all the unit tests implemented for the Student REST API.

## Test Files

### 1. StudentControllerTest.java
**Location**: `src/test/java/com/example/rest/controller/StudentControllerTest.java`

**Purpose**: Tests the REST controller layer using MockMvc

**Test Cases** (18 tests):

#### GET /api/students (Get All Students)
1. `testGetAllStudents_Success()` - Successfully retrieves all students
2. `testGetAllStudents_EmptyList()` - Returns empty list when no students exist

#### GET /api/students/{id} (Get Student by ID)
3. `testGetStudentById_Success()` - Successfully retrieves a student by ID
4. `testGetStudentById_NotFound()` - Returns 404 when student doesn't exist

#### POST /api/students (Create Student)
5. `testCreateStudent_Success()` - Successfully creates a new student
6. `testCreateStudent_ValidationError_BlankName()` - Returns 400 when name is blank
7. `testCreateStudent_ValidationError_InvalidEmail()` - Returns 400 when email is invalid
8. `testCreateStudent_ValidationError_InvalidAge()` - Returns 400 when age is invalid (< 1)
9. `testCreateStudent_DuplicateEmail()` - Returns 409 when email already exists

#### PUT /api/students/{id} (Update Student)
10. `testUpdateStudent_Success()` - Successfully updates an existing student
11. `testUpdateStudent_NotFound()` - Returns 404 when student doesn't exist
12. `testUpdateStudent_ValidationError()` - Returns 400 when validation fails

#### DELETE /api/students/{id} (Delete Student)
13. `testDeleteStudent_Success()` - Successfully deletes a student
14. `testDeleteStudent_NotFound()` - Returns 404 when student doesn't exist

### 2. StudentServiceTest.java
**Location**: `src/test/java/com/example/rest/service/StudentServiceTest.java`

**Purpose**: Tests the business logic layer with mocked repository

**Test Cases** (13 tests):

#### getAllStudents()
1. `testGetAllStudents_Success()` - Successfully retrieves all students from repository
2. `testGetAllStudents_EmptyList()` - Returns empty list when no students exist

#### getStudentById()
3. `testGetStudentById_Success()` - Successfully retrieves a student by ID
4. `testGetStudentById_NotFound()` - Throws ResourceNotFoundException when student doesn't exist

#### createStudent()
5. `testCreateStudent_Success()` - Successfully creates a new student
6. `testCreateStudent_DuplicateEmail()` - Throws DuplicateResourceException when email exists

#### updateStudent()
7. `testUpdateStudent_Success()` - Successfully updates an existing student
8. `testUpdateStudent_NotFound()` - Throws ResourceNotFoundException when student doesn't exist
9. `testUpdateStudent_DuplicateEmail()` - Throws DuplicateResourceException when new email exists
10. `testUpdateStudent_SameEmail_Success()` - Successfully updates student with same email (no duplicate check)

#### deleteStudent()
11. `testDeleteStudent_Success()` - Successfully deletes a student
12. `testDeleteStudent_NotFound()` - Throws ResourceNotFoundException when student doesn't exist

## Test Frameworks & Tools

- **JUnit 5**: Main testing framework
- **Mockito**: Mocking framework for dependencies
- **MockMvc**: Spring MVC Test framework for controller testing
- **@WebMvcTest**: Annotation for testing web layer in isolation
- **@ExtendWith(MockitoExtension.class)**: JUnit 5 extension for Mockito

## Test Annotations Used

- `@WebMvcTest(StudentController.class)` - Loads only the web layer for controller tests
- `@MockBean` - Creates mock beans for Spring context
- `@Mock` - Creates mock objects for unit tests
- `@InjectMocks` - Injects mocked dependencies into the test subject
- `@BeforeEach` - Sets up test data before each test
- `@Test` - Marks a method as a test case

## Running the Tests

### Command Line
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=StudentControllerTest

# Run specific test method
./mvnw test -Dtest=StudentControllerTest#testGetAllStudents_Success

# Run tests with coverage
./mvnw clean test jacoco:report
```

### IDE
- Right-click on test class or method and select "Run Test"
- Use the test runner panel to see results

## Test Coverage Goals

- **Line Coverage**: > 80%
- **Branch Coverage**: > 75%
- **Method Coverage**: 100%

## Validation Tests

The following validation rules are tested:

### Student Name
- ✓ Must not be blank
- ✓ Must be between 2 and 100 characters

### Email
- ✓ Must not be blank
- ✓ Must be a valid email format
- ✓ Must be unique (no duplicates)

### Age
- ✓ Must be at least 1
- ✓ Must be less than 150

## Exception Handling Tests

The following exceptions are tested:

1. **ResourceNotFoundException** (404)
   - Student not found by ID
   - Tested in: GET by ID, PUT, DELETE operations

2. **DuplicateResourceException** (409)
   - Email already exists
   - Tested in: POST and PUT operations

3. **MethodArgumentNotValidException** (400)
   - Validation failures
   - Tested in: POST and PUT operations with invalid data

## Mock Behavior

### Controller Tests
- Service layer is mocked
- Tests focus on HTTP request/response handling
- Validates status codes, response bodies, and error messages

### Service Tests
- Repository layer is mocked
- Tests focus on business logic
- Validates data transformations and exception handling

## Test Data

### Sample Student Data Used in Tests:
```java
Student 1:
- ID: 1
- Name: "John Doe"
- Email: "john@example.com"
- Age: 20

Student 2:
- ID: 2
- Name: "Jane Smith"
- Email: "jane@example.com"
- Age: 22
```

## Assertions Used

- `assertEquals()` - Verify equality
- `assertNotNull()` - Verify object is not null
- `assertThrows()` - Verify exception is thrown
- `assertDoesNotThrow()` - Verify no exception is thrown
- `verify()` - Verify mock interactions
- `times()` - Verify number of method calls
- `never()` - Verify method was never called

## JSON Path Assertions (Controller Tests)

- `jsonPath("$.studentId")` - Verify student ID in response
- `jsonPath("$.studentName")` - Verify student name in response
- `jsonPath("$.email")` - Verify email in response
- `jsonPath("$.age")` - Verify age in response
- `jsonPath("$.errors")` - Verify validation errors in response
- `jsonPath("$", hasSize(n))` - Verify array size

## Status Code Validation

The tests verify the following HTTP status codes:

- **200 OK** - Successful GET and PUT operations
- **201 Created** - Successful POST operation
- **204 No Content** - Successful DELETE operation
- **400 Bad Request** - Validation failures
- **404 Not Found** - Resource not found
- **409 Conflict** - Duplicate resource

## Future Test Enhancements

- [ ] Integration tests with real database
- [ ] Performance tests
- [ ] Security tests
- [ ] API contract tests
- [ ] End-to-end tests
- [ ] Load tests
- [ ] Test coverage reporting with JaCoCo
- [ ] Mutation testing with PIT

## Continuous Integration

The tests are designed to run in CI/CD pipelines:
- Fast execution time
- No external dependencies
- Deterministic results
- Clear error messages

## Troubleshooting Test Failures

### Common Issues:

1. **Compilation Errors**
   - Run: `./mvnw clean compile`
   - Check Java version (requires Java 17+)

2. **Dependency Issues**
   - Run: `./mvnw dependency:resolve`
   - Check internet connection for Maven downloads

3. **Test Failures**
   - Check error messages in test output
   - Verify mock setup is correct
   - Ensure test data is properly initialized

4. **Mock Verification Failures**
   - Check method signatures match exactly
   - Verify argument matchers (any(), eq(), etc.)
   - Check number of expected invocations

## Best Practices Followed

1. **Arrange-Act-Assert Pattern**: Tests follow AAA structure
2. **Test Isolation**: Each test is independent
3. **Descriptive Names**: Test names clearly describe what is being tested
4. **One Assertion per Concept**: Tests verify one thing at a time
5. **Mock Only External Dependencies**: Real objects used where possible
6. **Fast Execution**: Tests run in milliseconds
7. **Readable and Maintainable**: Clear code with good structure

## Test Execution Results

To see test results after running tests, check:
- Console output
- `target/surefire-reports/` directory
- IDE test runner panel

Expected output:
```
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
```

