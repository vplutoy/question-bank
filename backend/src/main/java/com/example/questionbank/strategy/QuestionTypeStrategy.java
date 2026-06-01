package com.example.questionbank.strategy;

import com.alibaba.fastjson.JSONObject;

public interface QuestionTypeStrategy {
    void validate(String contentJson);
    String normalizeContent(String contentJson);
}
