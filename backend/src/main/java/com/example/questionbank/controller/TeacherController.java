package com.example.questionbank.controller;

import com.example.questionbank.entity.Teacher;
import com.example.questionbank.service.TeacherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public List<Teacher> list() {
        return teacherService.listAll();
    }

    @GetMapping("/{id}")
    public Teacher getById(@PathVariable Long id) {
        return teacherService.getById(id);
    }
}
