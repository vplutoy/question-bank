package com.example.questionbank.controller;

import com.example.questionbank.entity.CourseChapter;
import com.example.questionbank.service.ChapterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    private final ChapterService service;

    public ChapterController(ChapterService service) { this.service = service; }

    @GetMapping
    public List<CourseChapter> tree() { return service.tree(); }

    @PostMapping
    public CourseChapter create(@RequestBody CourseChapter chapter) { return service.create(chapter); }

    @PutMapping("/{id}")
    public CourseChapter update(@PathVariable Long id, @RequestBody CourseChapter chapter) {
        return service.update(id, chapter);
    }

    @DeleteMapping("/{id}")
    public java.util.Map<String, Object> delete(@PathVariable Long id) {
        service.delete(id);
        return java.util.Map.of("success", true);
    }
}
