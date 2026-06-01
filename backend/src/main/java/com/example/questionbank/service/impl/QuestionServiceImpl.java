package com.example.questionbank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questionbank.dto.QuestionCreateDTO;
import com.example.questionbank.dto.QuestionPropertiesDTO;
import com.example.questionbank.dto.QuestionQueryDTO;
import com.example.questionbank.dto.QuestionVO;
import com.example.questionbank.entity.Question;
import com.example.questionbank.entity.QuestionAttachment;
import com.example.questionbank.entity.QuestionKnowledgePoint;
import com.example.questionbank.entity.Teacher;
import com.example.questionbank.mapper.QuestionAttachmentMapper;
import com.example.questionbank.mapper.QuestionKnowledgePointMapper;
import com.example.questionbank.mapper.QuestionMapper;
import com.example.questionbank.mapper.TeacherMapper;
import com.example.questionbank.service.QuestionService;
import com.example.questionbank.strategy.QuestionStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;
    private final QuestionAttachmentMapper attachmentMapper;
    private final QuestionKnowledgePointMapper qkpMapper;
    private final TeacherMapper teacherMapper;
    private final QuestionStrategyFactory strategyFactory;

    public QuestionServiceImpl(QuestionMapper questionMapper,
                               QuestionAttachmentMapper attachmentMapper,
                               QuestionKnowledgePointMapper qkpMapper,
                               TeacherMapper teacherMapper,
                               QuestionStrategyFactory strategyFactory) {
        this.questionMapper = questionMapper;
        this.attachmentMapper = attachmentMapper;
        this.qkpMapper = qkpMapper;
        this.teacherMapper = teacherMapper;
        this.strategyFactory = strategyFactory;
    }

    @Override
    @Transactional
    public QuestionVO create(QuestionCreateDTO dto) {
        strategyFactory.getStrategy(dto.getType()).validate(dto.getContentJson());

        Question question = new Question();
        question.setTeacherId(dto.getTeacherId());
        question.setType(dto.getType());
        question.setTitle(dto.getTitle());
        question.setContentJson(dto.getContentJson());
        question.setDifficulty(dto.getDifficulty());
        question.setChapter(dto.getChapter());
        question.setKnowledgePoints(dto.getKnowledgePoints());
        question.setErrorRate(dto.getErrorRate());
        question.setCommonMistakes(dto.getCommonMistakes());
        questionMapper.insert(question);

        syncAttachments(dto.getAttachmentIds(), question.getId());
        syncKnowledgePoints(dto.getKnowledgePointIds(), question.getId());

        return toVO(question);
    }

    @Override
    @Transactional
    public QuestionVO update(Long id, QuestionCreateDTO dto) {
        Question question = questionMapper.selectById(id);
        if (question == null) throw new IllegalArgumentException("题目不存在");

        strategyFactory.getStrategy(dto.getType()).validate(dto.getContentJson());

        question.setTeacherId(dto.getTeacherId());
        question.setType(dto.getType());
        question.setTitle(dto.getTitle());
        question.setContentJson(dto.getContentJson());
        question.setDifficulty(dto.getDifficulty());
        question.setChapter(dto.getChapter());
        question.setKnowledgePoints(dto.getKnowledgePoints());
        question.setErrorRate(dto.getErrorRate());
        question.setCommonMistakes(dto.getCommonMistakes());
        questionMapper.updateById(question);

        syncAttachments(dto.getAttachmentIds(), question.getId());
        syncKnowledgePoints(dto.getKnowledgePointIds(), question.getId());

        return toVO(question);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) throw new IllegalArgumentException("题目不存在");
        syncAttachments(null, id);
        qkpMapper.delete(new LambdaQueryWrapper<QuestionKnowledgePoint>()
                .eq(QuestionKnowledgePoint::getQuestionId, id));
        questionMapper.deleteById(id);
    }

    @Override
    public QuestionVO getById(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) throw new IllegalArgumentException("题目不存在");
        return toVO(question);
    }

    @Override
    public Page<QuestionVO> page(QuestionQueryDTO query) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        if (query.getTeacherId() != null) wrapper.eq(Question::getTeacherId, query.getTeacherId());
        if (query.getType() != null && !query.getType().isEmpty()) wrapper.eq(Question::getType, query.getType());
        if (query.getDifficulty() != null && !query.getDifficulty().isEmpty()) wrapper.eq(Question::getDifficulty, query.getDifficulty());
        if (query.getChapter() != null && !query.getChapter().isEmpty()) wrapper.like(Question::getChapter, query.getChapter());
        if (query.getKeyword() != null && !query.getKeyword().isEmpty())
            wrapper.and(w -> w.like(Question::getTitle, query.getKeyword())
                    .or().like(Question::getKnowledgePoints, query.getKeyword()));
        wrapper.orderByDesc(Question::getUpdateTime);

        Page<Question> page = new Page<>(query.getPage(), query.getSize());
        Page<Question> result = questionMapper.selectPage(page, wrapper);
        Page<QuestionVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return voPage;
    }

    @Transactional
    public QuestionVO updateProperties(Long id, QuestionPropertiesDTO dto) {
        Question question = questionMapper.selectById(id);
        if (question == null) throw new IllegalArgumentException("题目不存在");
        if (dto.getDifficulty() != null) question.setDifficulty(dto.getDifficulty());
        if (dto.getChapter() != null) question.setChapter(dto.getChapter());
        if (dto.getKnowledgePoints() != null) question.setKnowledgePoints(dto.getKnowledgePoints());
        question.setErrorRate(dto.getErrorRate());
        question.setCommonMistakes(dto.getCommonMistakes());
        questionMapper.updateById(question);
        if (dto.getKnowledgePointIds() != null) syncKnowledgePoints(dto.getKnowledgePointIds(), id);
        return toVO(question);
    }

    private void syncAttachments(List<Long> attachmentIds, Long questionId) {
        List<QuestionAttachment> existing = attachmentMapper.selectList(
                new LambdaQueryWrapper<QuestionAttachment>().eq(QuestionAttachment::getQuestionId, questionId));
        for (QuestionAttachment att : existing) { att.setQuestionId(null); attachmentMapper.updateById(att); }
        if (attachmentIds != null) {
            for (Long aid : attachmentIds) {
                QuestionAttachment att = attachmentMapper.selectById(aid);
                if (att != null) { att.setQuestionId(questionId); attachmentMapper.updateById(att); }
            }
        }
    }

    private void syncKnowledgePoints(List<Long> kpIds, Long questionId) {
        qkpMapper.delete(new LambdaQueryWrapper<QuestionKnowledgePoint>()
                .eq(QuestionKnowledgePoint::getQuestionId, questionId));
        if (kpIds != null) {
            for (Long kpId : kpIds) {
                QuestionKnowledgePoint qkp = new QuestionKnowledgePoint();
                qkp.setQuestionId(questionId);
                qkp.setKnowledgePointId(kpId);
                qkpMapper.insert(qkp);
            }
        }
    }

    private QuestionVO toVO(Question question) {
        QuestionVO vo = new QuestionVO();
        vo.setId(question.getId());
        vo.setTeacherId(question.getTeacherId());
        vo.setType(question.getType());
        vo.setTitle(question.getTitle());
        vo.setContentJson(question.getContentJson());
        vo.setDifficulty(question.getDifficulty());
        vo.setChapter(question.getChapter());
        vo.setKnowledgePoints(question.getKnowledgePoints());
        vo.setErrorRate(question.getErrorRate());
        vo.setCommonMistakes(question.getCommonMistakes());
        vo.setCreateTime(question.getCreateTime());
        vo.setUpdateTime(question.getUpdateTime());

        Teacher teacher = teacherMapper.selectById(question.getTeacherId());
        if (teacher != null) vo.setTeacherName(teacher.getName());

        List<QuestionAttachment> attachments = attachmentMapper.selectList(
                new LambdaQueryWrapper<QuestionAttachment>().eq(QuestionAttachment::getQuestionId, question.getId()));
        vo.setAttachments(attachments);

        List<QuestionKnowledgePoint> qkps = qkpMapper.selectList(
                new LambdaQueryWrapper<QuestionKnowledgePoint>().eq(QuestionKnowledgePoint::getQuestionId, question.getId()));
        vo.setKnowledgePointIds(qkps.stream().map(QuestionKnowledgePoint::getKnowledgePointId).collect(Collectors.toList()));

        return vo;
    }
}
