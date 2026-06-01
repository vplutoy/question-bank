package com.example.questionbank.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("question_attachment")
public class QuestionAttachment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long questionId;
    private String fileName;
    private String filePath;
    private String fileType;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime uploadTime;
}
