package com.example.questionbank.service;

import com.example.questionbank.entity.Teacher;

import java.util.List;

public interface TeacherService {
    List<Teacher> listAll();
    Teacher getById(Long id);
}
