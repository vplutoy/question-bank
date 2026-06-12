package com.example.questionbank.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentVO {

    private Long id;

    private String name;

    private String studentNo;

    private Long classId;

    private String className;

    private String department;

    private LocalDateTime createTime;
}
