package com.example.questionbank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.questionbank.dto.StudentClassDTO;
import com.example.questionbank.dto.StudentClassVO;
import com.example.questionbank.entity.Student;
import com.example.questionbank.entity.StudentClass;
import com.example.questionbank.mapper.StudentClassMapper;
import com.example.questionbank.mapper.StudentMapper;
import com.example.questionbank.service.StudentClassService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentClassServiceImpl implements StudentClassService {

    private final StudentClassMapper classMapper;
    private final StudentMapper studentMapper;

    public StudentClassServiceImpl(StudentClassMapper classMapper,
                                    StudentMapper studentMapper) {
        this.classMapper = classMapper;
        this.studentMapper = studentMapper;
    }

    @Override
    public List<StudentClassVO> listAll() {
        List<StudentClass> classes = classMapper.selectList(
                new LambdaQueryWrapper<StudentClass>()
                        .orderByDesc(StudentClass::getCreateTime));
        List<StudentClassVO> vos = new ArrayList<>();
        for (StudentClass sc : classes) {
            vos.add(buildVO(sc));
        }
        return vos;
    }

    @Override
    public StudentClassVO getById(Long id) {
        StudentClass sc = classMapper.selectById(id);
        if (sc == null) {
            throw new IllegalArgumentException("班级不存在");
        }
        return buildVO(sc);
    }

    @Override
    @Transactional
    public StudentClassVO create(StudentClassDTO dto) {
        StudentClass sc = new StudentClass();
        sc.setClassName(dto.getClassName());
        sc.setDepartment(dto.getDepartment());
        sc.setDescription(dto.getDescription());
        classMapper.insert(sc);
        return buildVO(sc);
    }

    @Override
    @Transactional
    public StudentClassVO update(Long id, StudentClassDTO dto) {
        StudentClass sc = classMapper.selectById(id);
        if (sc == null) {
            throw new IllegalArgumentException("班级不存在");
        }
        sc.setClassName(dto.getClassName());
        sc.setDepartment(dto.getDepartment());
        sc.setDescription(dto.getDescription());
        classMapper.updateById(sc);
        return buildVO(sc);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (classMapper.selectById(id) == null) {
            throw new IllegalArgumentException("班级不存在");
        }
        classMapper.deleteById(id);
    }

    private StudentClassVO buildVO(StudentClass sc) {
        StudentClassVO vo = new StudentClassVO();
        vo.setId(sc.getId());
        vo.setClassName(sc.getClassName());
        vo.setDepartment(sc.getDepartment());
        vo.setDescription(sc.getDescription());
        vo.setCreateTime(sc.getCreateTime());

        Long count = studentMapper.selectCount(
                new LambdaQueryWrapper<Student>()
                        .eq(Student::getClassId, sc.getId()));
        vo.setStudentCount(count.intValue());
        return vo;
    }
}
