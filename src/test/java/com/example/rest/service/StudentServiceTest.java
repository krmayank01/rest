package com.example.rest.service;

import com.example.rest.dto.StudentDto;
import com.example.rest.entity.Student;
import com.example.rest.exception.DuplicateResourceException;
import com.example.rest.exception.ResourceNotFoundException;
import com.example.rest.repository.StudentRepository;
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
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student1;
    private Student student2;
    private StudentDto studentDto1;

    @BeforeEach
    void setUp() {
        student1 = new Student(1L, "John Doe", "john@example.com", 20);
        student2 = new Student(2L, "Jane Smith", "jane@example.com", 22);
        studentDto1 = new StudentDto(1L, "John Doe", "john@example.com", 20);
    }

    @Test
    void testGetAllStudents_Success() {
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        List<StudentDto> result = studentService.getAllStudents();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getStudentName());
        assertEquals("Jane Smith", result.get(1).getStudentName());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void testGetAllStudents_EmptyList() {
        when(studentRepository.findAll()).thenReturn(Arrays.asList());

        List<StudentDto> result = studentService.getAllStudents();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void testGetStudentById_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));

        StudentDto result = studentService.getStudentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getStudentId());
        assertEquals("John Doe", result.getStudentName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(20, result.getAge());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetStudentById_NotFound() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.getStudentById(999L)
        );

        assertEquals("Student not found with id: 999", exception.getMessage());
        verify(studentRepository, times(1)).findById(999L);
    }

    @Test
    void testCreateStudent_Success() {
        StudentDto inputDto = new StudentDto(null, "John Doe", "john@example.com", 20);
        when(studentRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student1);

        StudentDto result = studentService.createStudent(inputDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getStudentName());
        assertEquals("john@example.com", result.getEmail());
        verify(studentRepository, times(1)).existsByEmail("john@example.com");
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testCreateStudent_DuplicateEmail() {
        StudentDto inputDto = new StudentDto(null, "John Doe", "john@example.com", 20);
        when(studentRepository.existsByEmail("john@example.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> studentService.createStudent(inputDto)
        );

        assertEquals("Student with email john@example.com already exists", exception.getMessage());
        verify(studentRepository, times(1)).existsByEmail("john@example.com");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testUpdateStudent_Success() {
        StudentDto updateDto = new StudentDto(1L, "John Updated", "john.updated@example.com", 21);
        Student updatedStudent = new Student(1L, "John Updated", "john.updated@example.com", 21);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(studentRepository.existsByEmail("john.updated@example.com")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        StudentDto result = studentService.updateStudent(1L, updateDto);

        assertNotNull(result);
        assertEquals("John Updated", result.getStudentName());
        assertEquals("john.updated@example.com", result.getEmail());
        assertEquals(21, result.getAge());
        verify(studentRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testUpdateStudent_NotFound() {
        StudentDto updateDto = new StudentDto(999L, "John Updated", "john@example.com", 21);
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.updateStudent(999L, updateDto)
        );

        assertEquals("Student not found with id: 999", exception.getMessage());
        verify(studentRepository, times(1)).findById(999L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testUpdateStudent_DuplicateEmail() {
        StudentDto updateDto = new StudentDto(1L, "John Updated", "jane@example.com", 21);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(studentRepository.existsByEmail("jane@example.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> studentService.updateStudent(1L, updateDto)
        );

        assertEquals("Student with email jane@example.com already exists", exception.getMessage());
        verify(studentRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).existsByEmail("jane@example.com");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testUpdateStudent_SameEmail_Success() {
        StudentDto updateDto = new StudentDto(1L, "John Updated", "john@example.com", 21);
        Student updatedStudent = new Student(1L, "John Updated", "john@example.com", 21);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        StudentDto result = studentService.updateStudent(1L, updateDto);

        assertNotNull(result);
        assertEquals("John Updated", result.getStudentName());
        verify(studentRepository, times(1)).findById(1L);
        verify(studentRepository, never()).existsByEmail(anyString()); // Should not check email if it's the same
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testDeleteStudent_Success() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        assertDoesNotThrow(() -> studentService.deleteStudent(1L));

        verify(studentRepository, times(1)).existsById(1L);
        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteStudent_NotFound() {
        when(studentRepository.existsById(999L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.deleteStudent(999L)
        );

        assertEquals("Student not found with id: 999", exception.getMessage());
        verify(studentRepository, times(1)).existsById(999L);
        verify(studentRepository, never()).deleteById(999L);
    }
}

