package com.example.questionbank.controller;

import com.example.questionbank.dto.StudentClassDTO;
import com.example.questionbank.dto.StudentClassVO;
import com.example.questionbank.service.StudentClassService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student-classes")
public class StudentClassController {

    private final StudentClassService studentClassService;

    public StudentClassController(StudentClassService studentClassService) {
        this.studentClassService = studentClassService;
    }

    @GetMapping
    public List<StudentClassVO> list() {
        return studentClassService.listAll();
    }

    @GetMapping("/{id}")
    public StudentClassVO getById(@PathVariable Long id) {
        return studentClassService.getById(id);
    }

    @PostMapping
    public StudentClassVO create(@Valid @RequestBody StudentClassDTO dto) {
        return studentClassService.create(dto);
    }

    @PutMapping("/{id}")
    public StudentClassVO update(@PathVariable Long id, @Valid @RequestBody StudentClassDTO dto) {
        return studentClassService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        studentClassService.delete(id);
        return Map.of("success", true);
    }
}
