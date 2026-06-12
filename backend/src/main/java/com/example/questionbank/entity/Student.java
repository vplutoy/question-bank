package com.example.questionbank.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student")
public class Student {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String studentNo;

    private Long classId;

    private String department;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
