package com.example.questionbank.dto;

import com.example.questionbank.entity.QuestionAttachment;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionVO {
    private Long id;
    private Long teacherId;
    private String teacherName;
    private String type;
    private String title;
    private String contentJson;
    private String difficulty;
    private String chapter;
    private String knowledgePoints;
    private BigDecimal errorRate;
    private String commonMistakes;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<QuestionAttachment> attachments;
    private List<Long> knowledgePointIds;
}
