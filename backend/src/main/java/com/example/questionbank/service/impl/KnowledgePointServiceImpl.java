package com.example.questionbank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.questionbank.entity.KnowledgePoint;
import com.example.questionbank.mapper.KnowledgePointMapper;
import com.example.questionbank.service.KnowledgePointService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgePointServiceImpl implements KnowledgePointService {

    private final KnowledgePointMapper mapper;

    public KnowledgePointServiceImpl(KnowledgePointMapper mapper) { this.mapper = mapper; }

    @Override
    public List<KnowledgePoint> listByChapter(Long chapterId) {
        return mapper.selectList(
                new LambdaQueryWrapper<KnowledgePoint>()
                        .eq(chapterId != null, KnowledgePoint::getChapterId, chapterId)
                        .orderByAsc(KnowledgePoint::getId));
    }

    @Override
    public List<KnowledgePoint> listAll() {
        return mapper.selectList(
                new LambdaQueryWrapper<KnowledgePoint>().orderByAsc(KnowledgePoint::getId));
    }

    @Override
    public KnowledgePoint create(KnowledgePoint kp) {
        mapper.insert(kp);
        return kp;
    }

    @Override
    public KnowledgePoint update(Long id, KnowledgePoint kp) {
        kp.setId(id);
        mapper.updateById(kp);
        return mapper.selectById(id);
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
