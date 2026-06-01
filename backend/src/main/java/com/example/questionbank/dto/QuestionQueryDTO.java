package com.example.questionbank.dto;

import lombok.Data;

@Data
public class QuestionQueryDTO {
    private Long teacherId;
    private String type;
    private String difficulty;
    private String chapter;
    private String keyword;
    private Integer page = 1;
    private Integer size = 10;
}
