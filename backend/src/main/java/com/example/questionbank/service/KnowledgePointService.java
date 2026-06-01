package com.example.questionbank.service;

import com.example.questionbank.entity.KnowledgePoint;

import java.util.List;

public interface KnowledgePointService {
    List<KnowledgePoint> listByChapter(Long chapterId);
    List<KnowledgePoint> listAll();
    KnowledgePoint create(KnowledgePoint kp);
    KnowledgePoint update(Long id, KnowledgePoint kp);
    void delete(Long id);
}
