package com.example.questionbank.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StudentAnalysisVO {

    private String studentNo;

    private String studentName;

    private String className;

    private String department;

    private Long totalExamsTaken;

    private BigDecimal averageScore;

    private BigDecimal highestScore;

    private BigDecimal lowestScore;
}
