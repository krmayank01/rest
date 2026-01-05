package com.example.rest.service;

import com.example.rest.dto.TeacherDto;
import com.example.rest.entity.Teacher;
import com.example.rest.exception.DuplicateResourceException;
import com.example.rest.exception.ResourceNotFoundException;
import com.example.rest.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher1;
    private Teacher teacher2;
    private TeacherDto teacherDto1;

    @BeforeEach
    void setUp() {
        teacher1 = new Teacher(1L, "John Smith", "john.smith@example.com", "Mathematics", 35);
        teacher2 = new Teacher(2L, "Jane Doe", "jane.doe@example.com", "Physics", 40);
        teacherDto1 = new TeacherDto(1L, "John Smith", "john.smith@example.com", "Mathematics", 35);
    }

    @Test
    void testGetAllTeachers_Success() {
        when(teacherRepository.findAll()).thenReturn(Arrays.asList(teacher1, teacher2));

        List<TeacherDto> result = teacherService.getAllTeachers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Smith", result.get(0).getTeacherName());
        assertEquals("Jane Doe", result.get(1).getTeacherName());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void testGetAllTeachers_EmptyList() {
        when(teacherRepository.findAll()).thenReturn(Arrays.asList());

        List<TeacherDto> result = teacherService.getAllTeachers();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void testGetTeacherById_Success() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));

        TeacherDto result = teacherService.getTeacherById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getTeacherId());
        assertEquals("John Smith", result.getTeacherName());
        assertEquals("john.smith@example.com", result.getEmail());
        assertEquals("Mathematics", result.getSubject());
        assertEquals(35, result.getAge());
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTeacherById_NotFound() {
        when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> teacherService.getTeacherById(999L)
        );

        assertEquals("Teacher not found with id: 999", exception.getMessage());
        verify(teacherRepository, times(1)).findById(999L);
    }

    @Test
    void testCreateTeacher_Success() {
        TeacherDto inputDto = new TeacherDto(null, "John Smith", "john.smith@example.com", "Mathematics", 35);
        when(teacherRepository.existsByEmail("john.smith@example.com")).thenReturn(false);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher1);

        TeacherDto result = teacherService.createTeacher(inputDto);

        assertNotNull(result);
        assertEquals("John Smith", result.getTeacherName());
        assertEquals("john.smith@example.com", result.getEmail());
        assertEquals("Mathematics", result.getSubject());
        verify(teacherRepository, times(1)).existsByEmail("john.smith@example.com");
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    void testCreateTeacher_DuplicateEmail() {
        TeacherDto inputDto = new TeacherDto(null, "John Smith", "john.smith@example.com", "Mathematics", 35);
        when(teacherRepository.existsByEmail("john.smith@example.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> teacherService.createTeacher(inputDto)
        );

        assertEquals("Teacher with email john.smith@example.com already exists", exception.getMessage());
        verify(teacherRepository, times(1)).existsByEmail("john.smith@example.com");
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    void testUpdateTeacher_Success() {
        TeacherDto updateDto = new TeacherDto(1L, "John Updated", "john.updated@example.com", "Physics", 36);
        Teacher updatedTeacher = new Teacher(1L, "John Updated", "john.updated@example.com", "Physics", 36);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));
        when(teacherRepository.existsByEmail("john.updated@example.com")).thenReturn(false);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(updatedTeacher);

        TeacherDto result = teacherService.updateTeacher(1L, updateDto);

        assertNotNull(result);
        assertEquals("John Updated", result.getTeacherName());
        assertEquals("john.updated@example.com", result.getEmail());
        assertEquals("Physics", result.getSubject());
        assertEquals(36, result.getAge());
        verify(teacherRepository, times(1)).findById(1L);
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    void testUpdateTeacher_NotFound() {
        TeacherDto updateDto = new TeacherDto(999L, "John Updated", "john@example.com", "Mathematics", 36);
        when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> teacherService.updateTeacher(999L, updateDto)
        );

        assertEquals("Teacher not found with id: 999", exception.getMessage());
        verify(teacherRepository, times(1)).findById(999L);
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    void testUpdateTeacher_DuplicateEmail() {
        TeacherDto updateDto = new TeacherDto(1L, "John Updated", "jane.doe@example.com", "Mathematics", 36);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));
        when(teacherRepository.existsByEmail("jane.doe@example.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> teacherService.updateTeacher(1L, updateDto)
        );

        assertEquals("Teacher with email jane.doe@example.com already exists", exception.getMessage());
        verify(teacherRepository, times(1)).findById(1L);
        verify(teacherRepository, times(1)).existsByEmail("jane.doe@example.com");
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    void testUpdateTeacher_SameEmail_Success() {
        TeacherDto updateDto = new TeacherDto(1L, "John Updated", "john.smith@example.com", "Physics", 36);
        Teacher updatedTeacher = new Teacher(1L, "John Updated", "john.smith@example.com", "Physics", 36);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(updatedTeacher);

        TeacherDto result = teacherService.updateTeacher(1L, updateDto);

        assertNotNull(result);
        assertEquals("John Updated", result.getTeacherName());
        verify(teacherRepository, times(1)).findById(1L);
        verify(teacherRepository, never()).existsByEmail(anyString()); // Should not check email if it's the same
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    void testDeleteTeacher_Success() {
        when(teacherRepository.existsById(1L)).thenReturn(true);
        doNothing().when(teacherRepository).deleteById(1L);

        assertDoesNotThrow(() -> teacherService.deleteTeacher(1L));

        verify(teacherRepository, times(1)).existsById(1L);
        verify(teacherRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTeacher_NotFound() {
        when(teacherRepository.existsById(999L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> teacherService.deleteTeacher(999L)
        );

        assertEquals("Teacher not found with id: 999", exception.getMessage());
        verify(teacherRepository, times(1)).existsById(999L);
        verify(teacherRepository, never()).deleteById(999L);
    }
}
