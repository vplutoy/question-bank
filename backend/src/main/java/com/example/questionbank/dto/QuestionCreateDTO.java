package com.example.questionbank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class QuestionCreateDTO {
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    @NotBlank(message = "题型不能为空")
    private String type;

    @NotBlank(message = "题目标题不能为空")
    private String title;

    @NotBlank(message = "题目内容不能为空")
    private String contentJson;

    @NotBlank(message = "难度不能为空")
    private String difficulty;

    @NotBlank(message = "章节不能为空")
    private String chapter;

    private String knowledgePoints;
    private BigDecimal errorRate;
    private String commonMistakes;
    private List<Long> attachmentIds;
    private List<Long> knowledgePointIds;
}
