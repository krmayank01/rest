package com.example.rest.controller;

import com.example.rest.dto.StudentDto;
import com.example.rest.exception.ResourceNotFoundException;
import com.example.rest.exception.DuplicateResourceException;
import com.example.rest.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    private StudentDto studentDto1;
    private StudentDto studentDto2;

    @BeforeEach
    void setUp() {
        studentDto1 = new StudentDto(1L, "John Doe", "john@example.com", 20);
        studentDto2 = new StudentDto(2L, "Jane Smith", "jane@example.com", 22);
    }

    @Test
    void testGetAllStudents_Success() throws Exception {
        List<StudentDto> students = Arrays.asList(studentDto1, studentDto2);
        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/api/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].studentName", is("John Doe")))
                .andExpect(jsonPath("$[1].studentName", is("Jane Smith")));

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testGetAllStudents_EmptyList() throws Exception {
        when(studentService.getAllStudents()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testGetStudentById_Success() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(studentDto1);

        mockMvc.perform(get("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId", is(1)))
                .andExpect(jsonPath("$.studentName", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.age", is(20)));

        verify(studentService, times(1)).getStudentById(1L);
    }

    @Test
    void testGetStudentById_NotFound() throws Exception {
        when(studentService.getStudentById(999L))
                .thenThrow(new ResourceNotFoundException("Student not found with id: 999"));

        mockMvc.perform(get("/api/students/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Student not found with id: 999")));

        verify(studentService, times(1)).getStudentById(999L);
    }

    @Test
    void testCreateStudent_Success() throws Exception {
        StudentDto inputDto = new StudentDto(null, "John Doe", "john@example.com", 20);
        when(studentService.createStudent(any(StudentDto.class))).thenReturn(studentDto1);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId", is(1)))
                .andExpect(jsonPath("$.studentName", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(studentService, times(1)).createStudent(any(StudentDto.class));
    }

    @Test
    void testCreateStudent_ValidationError_BlankName() throws Exception {
        StudentDto invalidDto = new StudentDto(null, "", "john@example.com", 20);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.studentName").exists());

        verify(studentService, never()).createStudent(any(StudentDto.class));
    }

    @Test
    void testCreateStudent_ValidationError_InvalidEmail() throws Exception {
        StudentDto invalidDto = new StudentDto(null, "John Doe", "invalid-email", 20);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());

        verify(studentService, never()).createStudent(any(StudentDto.class));
    }

    @Test
    void testCreateStudent_ValidationError_InvalidAge() throws Exception {
        StudentDto invalidDto = new StudentDto(null, "John Doe", "john@example.com", 0);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.age").exists());

        verify(studentService, never()).createStudent(any(StudentDto.class));
    }

    @Test
    void testCreateStudent_DuplicateEmail() throws Exception {
        StudentDto inputDto = new StudentDto(null, "John Doe", "john@example.com", 20);
        when(studentService.createStudent(any(StudentDto.class)))
                .thenThrow(new DuplicateResourceException("Student with email john@example.com already exists"));

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Student with email john@example.com already exists")));

        verify(studentService, times(1)).createStudent(any(StudentDto.class));
    }

    @Test
    void testUpdateStudent_Success() throws Exception {
        StudentDto updatedDto = new StudentDto(1L, "John Updated", "john.updated@example.com", 21);
        when(studentService.updateStudent(eq(1L), any(StudentDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentName", is("John Updated")))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")))
                .andExpect(jsonPath("$.age", is(21)));

        verify(studentService, times(1)).updateStudent(eq(1L), any(StudentDto.class));
    }

    @Test
    void testUpdateStudent_NotFound() throws Exception {
        StudentDto updatedDto = new StudentDto(999L, "John Updated", "john@example.com", 21);
        when(studentService.updateStudent(eq(999L), any(StudentDto.class)))
                .thenThrow(new ResourceNotFoundException("Student not found with id: 999"));

        mockMvc.perform(put("/api/students/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Student not found with id: 999")));

        verify(studentService, times(1)).updateStudent(eq(999L), any(StudentDto.class));
    }

    @Test
    void testUpdateStudent_ValidationError() throws Exception {
        StudentDto invalidDto = new StudentDto(1L, "", "invalid-email", 0);

        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());

        verify(studentService, never()).updateStudent(eq(1L), any(StudentDto.class));
    }

    @Test
    void testDeleteStudent_Success() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(studentService, times(1)).deleteStudent(1L);
    }

    @Test
    void testDeleteStudent_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Student not found with id: 999"))
                .when(studentService).deleteStudent(999L);

        mockMvc.perform(delete("/api/students/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Student not found with id: 999")));

        verify(studentService, times(1)).deleteStudent(999L);
    }
}

