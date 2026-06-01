package com.example.questionbank.strategy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class MultipleChoiceStrategy implements QuestionTypeStrategy {

    @Override
    public void validate(String contentJson) {
        JSONObject json = JSONObject.parseObject(contentJson);
        JSONArray options = json.getJSONArray("options");
        if (options == null || options.size() < 3) {
            throw new IllegalArgumentException("多选题至少需要3个选项");
        }
        long correctCount = options.stream()
                .map(o -> (JSONObject) o)
                .filter(o -> o.getBooleanValue("isCorrect"))
                .count();
        if (correctCount < 2) {
            throw new IllegalArgumentException("多选题至少需要2个正确答案");
        }
        for (int i = 0; i < options.size(); i++) {
            JSONObject opt = options.getJSONObject(i);
            if (!opt.containsKey("label") || !opt.containsKey("text")) {
                throw new IllegalArgumentException("选项 " + (i + 1) + " 缺少label或text字段");
            }
        }
    }

    @Override
    public String normalizeContent(String contentJson) {
        return contentJson;
    }
}
