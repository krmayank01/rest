package com.example.rest.service;

import com.example.rest.dto.StudentDto;
import com.example.rest.entity.Student;
import com.example.rest.exception.ResourceNotFoundException;
import com.example.rest.exception.DuplicateResourceException;
import com.example.rest.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public List<StudentDto> getAllStudents() {
        log.debug("Fetching all students");
        return studentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentDto getStudentById(Long id) {
        log.debug("Fetching student with id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return convertToDto(student);
    }

    @Transactional
    public StudentDto createStudent(StudentDto studentDto) {
        log.debug("Creating new student: {}", studentDto.getStudentName());

        // Check if email already exists
        if (studentRepository.existsByEmail(studentDto.getEmail())) {
            throw new DuplicateResourceException("Student with email " + studentDto.getEmail() + " already exists");
        }

        Student student = convertToEntity(studentDto);
        Student savedStudent = studentRepository.save(student);
        log.info("Student created successfully with id: {}", savedStudent.getStudentId());
        return convertToDto(savedStudent);
    }

    @Transactional
    public StudentDto updateStudent(Long id, StudentDto studentDto) {
        log.debug("Updating student with id: {}", id);

        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        // Check if email is being changed and if it already exists
        if (!existingStudent.getEmail().equals(studentDto.getEmail()) &&
            studentRepository.existsByEmail(studentDto.getEmail())) {
            throw new DuplicateResourceException("Student with email " + studentDto.getEmail() + " already exists");
        }

        existingStudent.setStudentName(studentDto.getStudentName());
        existingStudent.setEmail(studentDto.getEmail());
        existingStudent.setAge(studentDto.getAge());

        Student updatedStudent = studentRepository.save(existingStudent);
        log.info("Student updated successfully with id: {}", id);
        return convertToDto(updatedStudent);
    }

    @Transactional
    public void deleteStudent(Long id) {
        log.debug("Deleting student with id: {}", id);

        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }

        studentRepository.deleteById(id);
        log.info("Student deleted successfully with id: {}", id);
    }

    private StudentDto convertToDto(Student student) {
        return new StudentDto(
                student.getStudentId(),
                student.getStudentName(),
                student.getEmail(),
                student.getAge()
        );
    }

    private Student convertToEntity(StudentDto dto) {
        return new Student(
                null,  // ID will be generated
                dto.getStudentName(),
                dto.getEmail(),
                dto.getAge()
        );
    }
}

