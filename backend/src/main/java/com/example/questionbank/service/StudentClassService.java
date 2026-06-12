package com.example.questionbank.service;

import com.example.questionbank.dto.StudentClassDTO;
import com.example.questionbank.dto.StudentClassVO;

import java.util.List;

public interface StudentClassService {

    List<StudentClassVO> listAll();

    StudentClassVO getById(Long id);

    StudentClassVO create(StudentClassDTO dto);

    StudentClassVO update(Long id, StudentClassDTO dto);

    void delete(Long id);
}
