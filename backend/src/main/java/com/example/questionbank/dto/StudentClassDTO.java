package com.example.questionbank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StudentClassDTO {

    @NotBlank(message = "班级名称不能为空")
    private String className;

    private String department;

    private String description;
}
