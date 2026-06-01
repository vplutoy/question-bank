package com.example.questionbank.controller;

import com.example.questionbank.entity.KnowledgePoint;
import com.example.questionbank.service.KnowledgePointService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge-points")
public class KnowledgePointController {

    private final KnowledgePointService service;

    public KnowledgePointController(KnowledgePointService service) { this.service = service; }

    @GetMapping
    public List<KnowledgePoint> list(@RequestParam(required = false) Long chapterId) {
        if (chapterId != null) return service.listByChapter(chapterId);
        return service.listAll();
    }

    @PostMapping
    public KnowledgePoint create(@RequestBody KnowledgePoint kp) { return service.create(kp); }

    @PutMapping("/{id}")
    public KnowledgePoint update(@PathVariable Long id, @RequestBody KnowledgePoint kp) {
        return service.update(id, kp);
    }

    @DeleteMapping("/{id}")
    public java.util.Map<String, Object> delete(@PathVariable Long id) {
        service.delete(id);
        return java.util.Map.of("success", true);
    }
}
