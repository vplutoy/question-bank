package com.example.questionbank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StudentDTO {

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "学号不能为空")
    private String studentNo;

    private Long classId;

    private String department;
}
