package com.example.questionbank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("question_knowledge_point")
public class QuestionKnowledgePoint {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long questionId;
    private Long knowledgePointId;
}
