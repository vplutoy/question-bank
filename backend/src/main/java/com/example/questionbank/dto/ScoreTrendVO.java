package com.example.questionbank.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScoreTrendVO {

    private Long examId;

    private String examName;

    private LocalDateTime examTime;

    private BigDecimal totalScore;

    private BigDecimal paperTotalScore;
}
