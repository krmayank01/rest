package com.example.rest.controller;

import com.example.rest.dto.TeacherDto;
import com.example.rest.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Slf4j
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * Get all teachers
     * @return List of all teachers
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TeacherDto>> getAllTeachers() {
        log.info("GET /api/teachers - Fetching all teachers");
        List<TeacherDto> teachers = teacherService.getAllTeachers();
        return ResponseEntity.ok(teachers);
    }

    /**
     * Get a teacher by ID
     * @param id Teacher ID
     * @return Teacher details
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeacherDto> getTeacherById(@PathVariable Long id) {
        log.info("GET /api/teachers/{} - Fetching teacher by id", id);
        TeacherDto teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(teacher);
    }

    /**
     * Create a new teacher
     * @param teacherDto Teacher details
     * @return Created teacher
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeacherDto> createTeacher(@Valid @RequestBody TeacherDto teacherDto) {
        log.info("POST /api/teachers - Creating new teacher: {}", teacherDto.getTeacherName());
        TeacherDto createdTeacher = teacherService.createTeacher(teacherDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTeacher);
    }

    /**
     * Update an existing teacher
     * @param id Teacher ID
     * @param teacherDto Updated teacher details
     * @return Updated teacher
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeacherDto> updateTeacher(@PathVariable Long id,
                                                     @Valid @RequestBody TeacherDto teacherDto) {
        log.info("PUT /api/teachers/{} - Updating teacher", id);
        TeacherDto updatedTeacher = teacherService.updateTeacher(id, teacherDto);
        return ResponseEntity.ok(updatedTeacher);
    }

    /**
     * Delete a teacher
     * @param id Teacher ID
     * @return No content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        log.info("DELETE /api/teachers/{} - Deleting teacher", id);
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
}
