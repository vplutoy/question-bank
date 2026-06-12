package com.example.questionbank.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentClassVO {

    private Long id;

    private String className;

    private String department;

    private String description;

    private LocalDateTime createTime;

    private Integer studentCount;
}
