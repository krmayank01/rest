package com.example.rest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "teacher_name", nullable = false)
    private String teacherName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "age", nullable = false)
    private Integer age;
}
