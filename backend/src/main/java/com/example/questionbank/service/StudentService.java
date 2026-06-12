package com.example.questionbank.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questionbank.dto.*;

import java.util.List;

public interface StudentService {

    // 基本 CRUD
    Page<StudentVO> pageStudents(Integer page, Integer size, Long classId, String keyword);
    StudentVO getById(Long id);
    StudentVO getByStudentNo(String studentNo);
    StudentVO create(StudentDTO dto);
    StudentVO update(Long id, StudentDTO dto);
    void delete(Long id);

    // 学情分析
    List<ScoreTrendVO> getScoreTrend(String studentNo);
    List<ErrorKnowledgePointVO> getErrorAnalysis(String studentNo);
    List<StudentAnalysisVO> getClassAnalysis(Long classId);
}
