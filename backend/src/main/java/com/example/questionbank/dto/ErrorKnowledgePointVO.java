package com.example.questionbank.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ErrorKnowledgePointVO {

    private String knowledgePoint;

    private Long errorCount;

    private Long totalCount;

    private BigDecimal errorRate;
}
