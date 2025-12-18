package com.example.rest.controller;

import com.example.rest.dto.StudentDto;
import com.example.rest.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    /**
     * Get all students
     * @return List of all students
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        log.info("GET /api/students - Fetching all students");
        List<StudentDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    /**
     * Get a student by ID
     * @param id Student ID
     * @return Student details
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        log.info("GET /api/students/{} - Fetching student by id", id);
        StudentDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    /**
     * Create a new student
     * @param studentDto Student details
     * @return Created student
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentDto studentDto) {
        log.info("POST /api/students - Creating new student: {}", studentDto.getStudentName());
        StudentDto createdStudent = studentService.createStudent(studentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    /**
     * Update an existing student
     * @param id Student ID
     * @param studentDto Updated student details
     * @return Updated student
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Long id,
                                                     @Valid @RequestBody StudentDto studentDto) {
        log.info("PUT /api/students/{} - Updating student", id);
        StudentDto updatedStudent = studentService.updateStudent(id, studentDto);
        return ResponseEntity.ok(updatedStudent);
    }

    /**
     * Delete a student
     * @param id Student ID
     * @return No content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        log.info("DELETE /api/students/{} - Deleting student", id);
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
