package com.example.questionbank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questionbank.dto.*;
import com.example.questionbank.entity.*;
import com.example.questionbank.mapper.*;
import com.example.questionbank.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;
    private final StudentClassMapper classMapper;
    private final ExamStudentResultMapper resultMapper;
    private final ExamStudentAnswerMapper answerMapper;
    private final ExamMapper examMapper;
    private final ExamPaperMapper examPaperMapper;
    private final QuestionMapper questionMapper;
    private final ExamPaperQuestionMapper paperQuestionMapper;

    public StudentServiceImpl(StudentMapper studentMapper,
                               StudentClassMapper classMapper,
                               ExamStudentResultMapper resultMapper,
                               ExamStudentAnswerMapper answerMapper,
                               ExamMapper examMapper,
                               ExamPaperMapper examPaperMapper,
                               QuestionMapper questionMapper,
                               ExamPaperQuestionMapper paperQuestionMapper) {
        this.studentMapper = studentMapper;
        this.classMapper = classMapper;
        this.resultMapper = resultMapper;
        this.answerMapper = answerMapper;
        this.examMapper = examMapper;
        this.examPaperMapper = examPaperMapper;
        this.questionMapper = questionMapper;
        this.paperQuestionMapper = paperQuestionMapper;
    }

    // ==================== CRUD ====================

    @Override
    public Page<StudentVO> pageStudents(Integer page, Integer size, Long classId, String keyword) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if (classId != null) {
            wrapper.eq(Student::getClassId, classId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Student::getName, keyword)
                    .or().like(Student::getStudentNo, keyword));
        }
        wrapper.orderByDesc(Student::getCreateTime);

        Page<Student> mpPage = new Page<>(page, size);
        Page<Student> result = studentMapper.selectPage(mpPage, wrapper);

        Page<StudentVO> voPage = new Page<>(page, size, result.getTotal());
        List<StudentVO> vos = new ArrayList<>();
        for (Student s : result.getRecords()) {
            vos.add(buildStudentVO(s));
        }
        voPage.setRecords(vos);
        return voPage;
    }

    @Override
    public StudentVO getById(Long id) {
        Student s = requireStudent(id);
        return buildStudentVO(s);
    }

    @Override
    public StudentVO getByStudentNo(String studentNo) {
        Student s = studentMapper.selectOne(
                new LambdaQueryWrapper<Student>()
                        .eq(Student::getStudentNo, studentNo));
        if (s == null) {
            return null;
        }
        return buildStudentVO(s);
    }

    @Override
    @Transactional
    public StudentVO create(StudentDTO dto) {
        // 检查学号唯一
        Long cnt = studentMapper.selectCount(
                new LambdaQueryWrapper<Student>()
                        .eq(Student::getStudentNo, dto.getStudentNo()));
        if (cnt > 0) {
            throw new IllegalArgumentException("学号 " + dto.getStudentNo() + " 已存在");
        }

        Student s = new Student();
        s.setName(dto.getName());
        s.setStudentNo(dto.getStudentNo());
        s.setClassId(dto.getClassId());
        s.setDepartment(dto.getDepartment());
        studentMapper.insert(s);
        return buildStudentVO(s);
    }

    @Override
    @Transactional
    public StudentVO update(Long id, StudentDTO dto) {
        Student s = requireStudent(id);

        // 检查学号唯一（排除自身）
        Long cnt = studentMapper.selectCount(
                new LambdaQueryWrapper<Student>()
                        .eq(Student::getStudentNo, dto.getStudentNo())
                        .ne(Student::getId, id));
        if (cnt > 0) {
            throw new IllegalArgumentException("学号 " + dto.getStudentNo() + " 已存在");
        }

        s.setName(dto.getName());
        s.setStudentNo(dto.getStudentNo());
        s.setClassId(dto.getClassId());
        s.setDepartment(dto.getDepartment());
        studentMapper.updateById(s);
        return buildStudentVO(s);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        requireStudent(id);
        studentMapper.deleteById(id);
    }

    // ==================== 学情分析 ====================

    @Override
    public List<ScoreTrendVO> getScoreTrend(String studentNo) {
        // 查询该生所有考试成绩，按提交时间排序
        List<ExamStudentResult> results = resultMapper.selectList(
                new LambdaQueryWrapper<ExamStudentResult>()
                        .eq(ExamStudentResult::getStudentNo, studentNo)
                        .orderByAsc(ExamStudentResult::getSubmitTime));

        List<ScoreTrendVO> vos = new ArrayList<>();
        for (ExamStudentResult r : results) {
            Exam exam = examMapper.selectById(r.getExamId());
            if (exam == null) continue;

            ScoreTrendVO vo = new ScoreTrendVO();
            vo.setExamId(exam.getId());
            vo.setExamName(exam.getExamName());
            vo.setExamTime(exam.getStartTime());
            vo.setTotalScore(r.getTotalScore());

            // 获取试卷总分
            if (exam.getPaperId() != null) {
                ExamPaper paper = examPaperMapper.selectById(exam.getPaperId());
                if (paper != null) {
                    vo.setPaperTotalScore(paper.getTotalScore());
                }
            }
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<ErrorKnowledgePointVO> getErrorAnalysis(String studentNo) {
        // 查询该生所有答题记录
        List<ExamStudentAnswer> answers = answerMapper.selectList(
                new LambdaQueryWrapper<ExamStudentAnswer>()
                        .eq(ExamStudentAnswer::getStudentNo, studentNo));

        // 按知识点聚合：knowledgePoint -> [errorCount, totalCount]
        Map<String, long[]> kpStats = new LinkedHashMap<>();

        for (ExamStudentAnswer a : answers) {
            Question q = questionMapper.selectById(a.getQuestionId());
            if (q == null || !StringUtils.hasText(q.getKnowledgePoints())) {
                continue;
            }

            // 获取题目分值（从 exam_paper_question 查询）
            // 需要通过 examStudentAnswer -> examId -> paperId -> paperQuestion -> question_score
            // 简化：跳过无 finalScore 的（还没评分的）
            if (a.getFinalScore() == null) continue;

            BigDecimal maxScore = getQuestionMaxScore(a.getExamId(), a.getQuestionId());

            // 拆分知识点
            String[] kps = q.getKnowledgePoints().split(",");
            boolean isWrong = maxScore != null && a.getFinalScore().compareTo(maxScore) < 0;

            for (String kp : kps) {
                String kpTrim = kp.trim();
                if (kpTrim.isEmpty()) continue;

                kpStats.putIfAbsent(kpTrim, new long[]{0, 0});
                kpStats.get(kpTrim)[1]++; // totalCount
                if (isWrong) {
                    kpStats.get(kpTrim)[0]++; // errorCount
                }
            }
        }

        List<ErrorKnowledgePointVO> vos = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : kpStats.entrySet()) {
            ErrorKnowledgePointVO vo = new ErrorKnowledgePointVO();
            vo.setKnowledgePoint(entry.getKey());
            vo.setErrorCount(entry.getValue()[0]);
            vo.setTotalCount(entry.getValue()[1]);
            if (entry.getValue()[1] > 0) {
                vo.setErrorRate(BigDecimal.valueOf(entry.getValue()[0])
                        .divide(BigDecimal.valueOf(entry.getValue()[1]), 4, RoundingMode.HALF_UP));
            } else {
                vo.setErrorRate(BigDecimal.ZERO);
            }
            vos.add(vo);
        }
        // 按错误次数降序
        vos.sort((a, b) -> Long.compare(b.getErrorCount(), a.getErrorCount()));
        return vos;
    }

    @Override
    public List<StudentAnalysisVO> getClassAnalysis(Long classId) {
        // 确定要分析的学生范围
        List<Student> students;
        if (classId != null) {
            students = studentMapper.selectList(
                    new LambdaQueryWrapper<Student>()
                            .eq(Student::getClassId, classId));
        } else {
            students = studentMapper.selectList(
                    new LambdaQueryWrapper<Student>()
                            .orderByAsc(Student::getStudentNo));
        }

        // 获取所有考试成绩，按 student_no 分组
        List<ExamStudentResult> allResults = resultMapper.selectList(
                new LambdaQueryWrapper<ExamStudentResult>()
                        .orderByAsc(ExamStudentResult::getStudentNo));
        Map<String, List<ExamStudentResult>> grouped = allResults.stream()
                .collect(Collectors.groupingBy(ExamStudentResult::getStudentNo));

        List<StudentAnalysisVO> vos = new ArrayList<>();
        for (Student s : students) {
            List<ExamStudentResult> results = grouped.getOrDefault(s.getStudentNo(), Collections.emptyList());

            StudentAnalysisVO vo = new StudentAnalysisVO();
            vo.setStudentNo(s.getStudentNo());
            vo.setStudentName(s.getName());
            vo.setDepartment(s.getDepartment());

            // 班级名称
            if (s.getClassId() != null) {
                StudentClass sc = classMapper.selectById(s.getClassId());
                vo.setClassName(sc != null ? sc.getClassName() : "");
            } else {
                vo.setClassName("");
            }

            vo.setTotalExamsTaken((long) results.size());

            if (!results.isEmpty()) {
                BigDecimal total = BigDecimal.ZERO;
                BigDecimal highest = null;
                BigDecimal lowest = null;
                for (ExamStudentResult r : results) {
                    BigDecimal score = r.getTotalScore() != null ? r.getTotalScore() : BigDecimal.ZERO;
                    total = total.add(score);
                    if (highest == null || score.compareTo(highest) > 0) {
                        highest = score;
                    }
                    if (lowest == null || score.compareTo(lowest) < 0) {
                        lowest = score;
                    }
                }
                vo.setAverageScore(total.divide(
                        BigDecimal.valueOf(results.size()), 2, RoundingMode.HALF_UP));
                vo.setHighestScore(highest);
                vo.setLowestScore(lowest);
            } else {
                vo.setAverageScore(BigDecimal.ZERO);
                vo.setHighestScore(BigDecimal.ZERO);
                vo.setLowestScore(BigDecimal.ZERO);
            }

            vos.add(vo);
        }
        return vos;
    }

    // ==================== 辅助方法 ====================

    private Student requireStudent(Long id) {
        Student s = studentMapper.selectById(id);
        if (s == null) {
            throw new IllegalArgumentException("学生不存在");
        }
        return s;
    }

    private StudentVO buildStudentVO(Student s) {
        StudentVO vo = new StudentVO();
        vo.setId(s.getId());
        vo.setName(s.getName());
        vo.setStudentNo(s.getStudentNo());
        vo.setClassId(s.getClassId());
        vo.setDepartment(s.getDepartment());
        vo.setCreateTime(s.getCreateTime());

        if (s.getClassId() != null) {
            StudentClass sc = classMapper.selectById(s.getClassId());
            vo.setClassName(sc != null ? sc.getClassName() : "");
        }
        return vo;
    }

    /**
     * 获取某场考试中某道题的满分分值
     */
    private BigDecimal getQuestionMaxScore(Long examId, Long questionId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) return null;

        ExamPaperQuestion pq = paperQuestionMapper.selectOne(
                new LambdaQueryWrapper<ExamPaperQuestion>()
                        .eq(ExamPaperQuestion::getPaperId, exam.getPaperId())
                        .eq(ExamPaperQuestion::getQuestionId, questionId));
        return pq != null ? pq.getQuestionScore() : null;
    }
}
