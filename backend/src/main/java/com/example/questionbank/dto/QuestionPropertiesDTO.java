package com.example.questionbank.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class QuestionPropertiesDTO {
    private String difficulty;
    private String chapter;
    private String knowledgePoints;
    private BigDecimal errorRate;
    private String commonMistakes;
    private List<Long> knowledgePointIds;
}
