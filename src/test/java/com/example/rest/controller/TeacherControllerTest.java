package com.example.rest.controller;

import com.example.rest.dto.TeacherDto;
import com.example.rest.exception.ResourceNotFoundException;
import com.example.rest.exception.DuplicateResourceException;
import com.example.rest.service.TeacherService;
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

@WebMvcTest(TeacherController.class)
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeacherService teacherService;

    private TeacherDto teacherDto1;
    private TeacherDto teacherDto2;

    @BeforeEach
    void setUp() {
        teacherDto1 = new TeacherDto(1L, "John Smith", "john.smith@example.com", "Mathematics", 35);
        teacherDto2 = new TeacherDto(2L, "Jane Doe", "jane.doe@example.com", "Physics", 40);
    }

    @Test
    void testGetAllTeachers_Success() throws Exception {
        List<TeacherDto> teachers = Arrays.asList(teacherDto1, teacherDto2);
        when(teacherService.getAllTeachers()).thenReturn(teachers);

        mockMvc.perform(get("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].teacherName", is("John Smith")))
                .andExpect(jsonPath("$[1].teacherName", is("Jane Doe")));

        verify(teacherService, times(1)).getAllTeachers();
    }

    @Test
    void testGetAllTeachers_EmptyList() throws Exception {
        when(teacherService.getAllTeachers()).thenReturn(List.of());

        mockMvc.perform(get("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(teacherService, times(1)).getAllTeachers();
    }

    @Test
    void testGetTeacherById_Success() throws Exception {
        when(teacherService.getTeacherById(1L)).thenReturn(teacherDto1);

        mockMvc.perform(get("/api/teachers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teacherId", is(1)))
                .andExpect(jsonPath("$.teacherName", is("John Smith")))
                .andExpect(jsonPath("$.email", is("john.smith@example.com")))
                .andExpect(jsonPath("$.subject", is("Mathematics")))
                .andExpect(jsonPath("$.age", is(35)));

        verify(teacherService, times(1)).getTeacherById(1L);
    }

    @Test
    void testGetTeacherById_NotFound() throws Exception {
        when(teacherService.getTeacherById(999L))
                .thenThrow(new ResourceNotFoundException("Teacher not found with id: 999"));

        mockMvc.perform(get("/api/teachers/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Teacher not found with id: 999")));

        verify(teacherService, times(1)).getTeacherById(999L);
    }

    @Test
    void testCreateTeacher_Success() throws Exception {
        TeacherDto inputDto = new TeacherDto(null, "John Smith", "john.smith@example.com", "Mathematics", 35);
        when(teacherService.createTeacher(any(TeacherDto.class))).thenReturn(teacherDto1);

        mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teacherId", is(1)))
                .andExpect(jsonPath("$.teacherName", is("John Smith")))
                .andExpect(jsonPath("$.email", is("john.smith@example.com")))
                .andExpect(jsonPath("$.subject", is("Mathematics")));

        verify(teacherService, times(1)).createTeacher(any(TeacherDto.class));
    }

    @Test
    void testCreateTeacher_ValidationError_BlankName() throws Exception {
        TeacherDto invalidDto = new TeacherDto(null, "", "john.smith@example.com", "Mathematics", 35);

        mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.teacherName").exists());

        verify(teacherService, never()).createTeacher(any(TeacherDto.class));
    }

    @Test
    void testCreateTeacher_ValidationError_InvalidEmail() throws Exception {
        TeacherDto invalidDto = new TeacherDto(null, "John Smith", "invalid-email", "Mathematics", 35);

        mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());

        verify(teacherService, never()).createTeacher(any(TeacherDto.class));
    }

    @Test
    void testCreateTeacher_ValidationError_BlankSubject() throws Exception {
        TeacherDto invalidDto = new TeacherDto(null, "John Smith", "john.smith@example.com", "", 35);

        mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.subject").exists());

        verify(teacherService, never()).createTeacher(any(TeacherDto.class));
    }

    @Test
    void testCreateTeacher_ValidationError_InvalidAge() throws Exception {
        TeacherDto invalidDto = new TeacherDto(null, "John Smith", "john.smith@example.com", "Mathematics", 20);

        mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.age").exists());

        verify(teacherService, never()).createTeacher(any(TeacherDto.class));
    }

    @Test
    void testCreateTeacher_DuplicateEmail() throws Exception {
        TeacherDto inputDto = new TeacherDto(null, "John Smith", "john.smith@example.com", "Mathematics", 35);
        when(teacherService.createTeacher(any(TeacherDto.class)))
                .thenThrow(new DuplicateResourceException("Teacher with email john.smith@example.com already exists"));

        mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Teacher with email john.smith@example.com already exists")));

        verify(teacherService, times(1)).createTeacher(any(TeacherDto.class));
    }

    @Test
    void testUpdateTeacher_Success() throws Exception {
        TeacherDto updatedDto = new TeacherDto(1L, "John Updated", "john.updated@example.com", "Physics", 36);
        when(teacherService.updateTeacher(eq(1L), any(TeacherDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/teachers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teacherName", is("John Updated")))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")))
                .andExpect(jsonPath("$.subject", is("Physics")))
                .andExpect(jsonPath("$.age", is(36)));

        verify(teacherService, times(1)).updateTeacher(eq(1L), any(TeacherDto.class));
    }

    @Test
    void testUpdateTeacher_NotFound() throws Exception {
        TeacherDto updatedDto = new TeacherDto(999L, "John Updated", "john@example.com", "Mathematics", 36);
        when(teacherService.updateTeacher(eq(999L), any(TeacherDto.class)))
                .thenThrow(new ResourceNotFoundException("Teacher not found with id: 999"));

        mockMvc.perform(put("/api/teachers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Teacher not found with id: 999")));

        verify(teacherService, times(1)).updateTeacher(eq(999L), any(TeacherDto.class));
    }

    @Test
    void testUpdateTeacher_ValidationError() throws Exception {
        TeacherDto invalidDto = new TeacherDto(1L, "", "invalid-email", "", 20);

        mockMvc.perform(put("/api/teachers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());

        verify(teacherService, never()).updateTeacher(eq(1L), any(TeacherDto.class));
    }

    @Test
    void testDeleteTeacher_Success() throws Exception {
        doNothing().when(teacherService).deleteTeacher(1L);

        mockMvc.perform(delete("/api/teachers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(teacherService, times(1)).deleteTeacher(1L);
    }

    @Test
    void testDeleteTeacher_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Teacher not found with id: 999"))
                .when(teacherService).deleteTeacher(999L);

        mockMvc.perform(delete("/api/teachers/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Teacher not found with id: 999")));

        verify(teacherService, times(1)).deleteTeacher(999L);
    }
}
