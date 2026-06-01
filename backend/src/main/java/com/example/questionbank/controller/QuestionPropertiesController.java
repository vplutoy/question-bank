package com.example.questionbank.controller;

import com.example.questionbank.dto.QuestionPropertiesDTO;
import com.example.questionbank.dto.QuestionVO;
import com.example.questionbank.service.QuestionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question-properties")
public class QuestionPropertiesController {

    private final QuestionService service;

    public QuestionPropertiesController(QuestionService service) { this.service = service; }

    @PutMapping("/{id}")
    public QuestionVO updateProperties(@PathVariable Long id, @RequestBody QuestionPropertiesDTO dto) {
        return service.updateProperties(id, dto);
    }
}
