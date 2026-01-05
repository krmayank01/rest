package com.example.rest.service;

import com.example.rest.dto.TeacherDto;
import com.example.rest.entity.Teacher;
import com.example.rest.exception.ResourceNotFoundException;
import com.example.rest.exception.DuplicateResourceException;
import com.example.rest.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherService {

    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public List<TeacherDto> getAllTeachers() {
        log.debug("Fetching all teachers");
        return teacherRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeacherDto getTeacherById(Long id) {
        log.debug("Fetching teacher with id: {}", id);
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        return convertToDto(teacher);
    }

    @Transactional
    public TeacherDto createTeacher(TeacherDto teacherDto) {
        log.debug("Creating new teacher: {}", teacherDto.getTeacherName());

        // Check if email already exists
        if (teacherRepository.existsByEmail(teacherDto.getEmail())) {
            throw new DuplicateResourceException("Teacher with email " + teacherDto.getEmail() + " already exists");
        }

        Teacher teacher = convertToEntity(teacherDto);
        Teacher savedTeacher = teacherRepository.save(teacher);
        log.info("Teacher created successfully with id: {}", savedTeacher.getTeacherId());
        return convertToDto(savedTeacher);
    }

    @Transactional
    public TeacherDto updateTeacher(Long id, TeacherDto teacherDto) {
        log.debug("Updating teacher with id: {}", id);

        Teacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        // Check if email is being changed and if it already exists
        if (!existingTeacher.getEmail().equals(teacherDto.getEmail()) &&
            teacherRepository.existsByEmail(teacherDto.getEmail())) {
            throw new DuplicateResourceException("Teacher with email " + teacherDto.getEmail() + " already exists");
        }

        existingTeacher.setTeacherName(teacherDto.getTeacherName());
        existingTeacher.setEmail(teacherDto.getEmail());
        existingTeacher.setSubject(teacherDto.getSubject());
        existingTeacher.setAge(teacherDto.getAge());

        Teacher updatedTeacher = teacherRepository.save(existingTeacher);
        log.info("Teacher updated successfully with id: {}", id);
        return convertToDto(updatedTeacher);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        log.debug("Deleting teacher with id: {}", id);

        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teacher not found with id: " + id);
        }

        teacherRepository.deleteById(id);
        log.info("Teacher deleted successfully with id: {}", id);
    }

    private TeacherDto convertToDto(Teacher teacher) {
        return new TeacherDto(
                teacher.getTeacherId(),
                teacher.getTeacherName(),
                teacher.getEmail(),
                teacher.getSubject(),
                teacher.getAge()
        );
    }

    private Teacher convertToEntity(TeacherDto dto) {
        return new Teacher(
                null,  // ID will be generated
                dto.getTeacherName(),
                dto.getEmail(),
                dto.getSubject(),
                dto.getAge()
        );
    }
}
