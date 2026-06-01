package com.example.questionbank.strategy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class FillBlankStrategy implements QuestionTypeStrategy {

    @Override
    public void validate(String contentJson) {
        JSONObject json = JSONObject.parseObject(contentJson);
        JSONArray blanks = json.getJSONArray("blanks");
        if (blanks == null || blanks.isEmpty()) {
            throw new IllegalArgumentException("填空题至少需要一个填空答案");
        }
        for (int i = 0; i < blanks.size(); i++) {
            JSONObject blank = blanks.getJSONObject(i);
            String answer = blank.getString("answer");
            if (answer == null || answer.trim().isEmpty()) {
                throw new IllegalArgumentException("填空 " + (i + 1) + " 的答案不能为空");
            }
        }
    }

    @Override
    public String normalizeContent(String contentJson) {
        return contentJson;
    }
}
