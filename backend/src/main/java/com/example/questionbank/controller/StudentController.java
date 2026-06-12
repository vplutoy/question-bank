package com.example.questionbank.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questionbank.dto.*;
import com.example.questionbank.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ========== CRUD ==========

    @GetMapping
    public Page<StudentVO> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String keyword) {
        return studentService.pageStudents(page, size, classId, keyword);
    }

    @GetMapping("/{id}")
    public StudentVO getById(@PathVariable Long id) {
        return studentService.getById(id);
    }

    @PostMapping
    public StudentVO create(@Valid @RequestBody StudentDTO dto) {
        return studentService.create(dto);
    }

    @PutMapping("/{id}")
    public StudentVO update(@PathVariable Long id, @Valid @RequestBody StudentDTO dto) {
        return studentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        studentService.delete(id);
        return Map.of("success", true);
    }

    // ========== 学情分析 ==========

    @GetMapping("/analysis")
    public List<StudentAnalysisVO> classAnalysis(@RequestParam(required = false) Long classId) {
        return studentService.getClassAnalysis(classId);
    }

    @GetMapping("/{studentNo}/score-trend")
    public List<ScoreTrendVO> scoreTrend(@PathVariable String studentNo) {
        return studentService.getScoreTrend(studentNo);
    }

    @GetMapping("/{studentNo}/error-analysis")
    public List<ErrorKnowledgePointVO> errorAnalysis(@PathVariable String studentNo) {
        return studentService.getErrorAnalysis(studentNo);
    }
}
