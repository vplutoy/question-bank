package com.example.questionbank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.questionbank.entity.Teacher;
import com.example.questionbank.mapper.TeacherMapper;
import com.example.questionbank.service.TeacherService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherMapper teacherMapper;

    public TeacherServiceImpl(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
    }

    @Override
    public List<Teacher> listAll() {
        return teacherMapper.selectList(new LambdaQueryWrapper<Teacher>()
                .orderByAsc(Teacher::getId));
    }

    @Override
    public Teacher getById(Long id) {
        return teacherMapper.selectById(id);
    }
}
