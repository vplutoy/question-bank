package com.example.questionbank.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student_class")
public class StudentClass {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String className;

    private String department;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
