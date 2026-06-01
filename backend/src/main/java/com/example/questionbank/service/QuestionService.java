package com.example.questionbank.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questionbank.dto.QuestionCreateDTO;
import com.example.questionbank.dto.QuestionPropertiesDTO;
import com.example.questionbank.dto.QuestionQueryDTO;
import com.example.questionbank.dto.QuestionVO;

public interface QuestionService {
    QuestionVO create(QuestionCreateDTO dto);
    QuestionVO update(Long id, QuestionCreateDTO dto);
    void delete(Long id);
    QuestionVO getById(Long id);
    Page<QuestionVO> page(QuestionQueryDTO query);
    QuestionVO updateProperties(Long id, QuestionPropertiesDTO dto);
}
