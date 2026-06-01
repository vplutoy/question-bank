package com.example.questionbank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.questionbank.entity.CourseChapter;
import com.example.questionbank.mapper.CourseChapterMapper;
import com.example.questionbank.service.ChapterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChapterServiceImpl implements ChapterService {

    private final CourseChapterMapper mapper;

    public ChapterServiceImpl(CourseChapterMapper mapper) { this.mapper = mapper; }

    @Override
    public List<CourseChapter> tree() {
        return mapper.selectList(
                new LambdaQueryWrapper<CourseChapter>().orderByAsc(CourseChapter::getSortOrder));
    }

    @Override
    public CourseChapter create(CourseChapter chapter) {
        mapper.insert(chapter);
        return chapter;
    }

    @Override
    public CourseChapter update(Long id, CourseChapter chapter) {
        chapter.setId(id);
        mapper.updateById(chapter);
        return mapper.selectById(id);
    }

    @Override
    public void delete(Long id) {
        List<CourseChapter> children = mapper.selectList(
                new LambdaQueryWrapper<CourseChapter>().eq(CourseChapter::getParentId, id));
        for (CourseChapter child : children) delete(child.getId());
        mapper.deleteById(id);
    }
}
