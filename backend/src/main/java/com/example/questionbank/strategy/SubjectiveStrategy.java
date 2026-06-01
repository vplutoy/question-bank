package com.example.questionbank.strategy;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class SubjectiveStrategy implements QuestionTypeStrategy {

    @Override
    public void validate(String contentJson) {
        JSONObject json = JSONObject.parseObject(contentJson);
        String referenceAnswer = json.getString("referenceAnswer");
        if (referenceAnswer == null || referenceAnswer.trim().isEmpty()) {
            throw new IllegalArgumentException("主观题必须包含参考答案");
        }
    }

    @Override
    public String normalizeContent(String contentJson) {
        return contentJson;
    }
}
