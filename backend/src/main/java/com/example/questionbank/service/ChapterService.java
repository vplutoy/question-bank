package com.example.questionbank.service;

import com.example.questionbank.entity.CourseChapter;

import java.util.List;

public interface ChapterService {
    List<CourseChapter> tree();
    CourseChapter create(CourseChapter chapter);
    CourseChapter update(Long id, CourseChapter chapter);
    void delete(Long id);
}
