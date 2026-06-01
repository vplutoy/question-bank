package com.example.questionbank.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questionbank.dto.QuestionCreateDTO;
import com.example.questionbank.dto.QuestionQueryDTO;
import com.example.questionbank.dto.QuestionVO;
import com.example.questionbank.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public QuestionVO create(@Valid @RequestBody QuestionCreateDTO dto) {
        return questionService.create(dto);
    }

    @PutMapping("/{id}")
    public QuestionVO update(@PathVariable Long id, @Valid @RequestBody QuestionCreateDTO dto) {
        return questionService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        questionService.delete(id);
        return Map.of("success", true);
    }

    @GetMapping("/{id}")
    public QuestionVO getById(@PathVariable Long id) {
        return questionService.getById(id);
    }

    @GetMapping
    public Page<QuestionVO> page(QuestionQueryDTO query) {
        return questionService.page(query);
    }
}
