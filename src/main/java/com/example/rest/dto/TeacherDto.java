package com.example.rest.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto {

    private Long teacherId;

    @NotBlank(message = "Teacher name is required")
    @Size(min = 2, max = 100, message = "Teacher name must be between 2 and 100 characters")
    private String teacherName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Subject is required")
    @Size(min = 2, max = 50, message = "Subject must be between 2 and 50 characters")
    private String subject;

    @Min(value = 22, message = "Age must be at least 22")
    @Max(value = 100, message = "Age must be less than 100")
    private Integer age;
}
