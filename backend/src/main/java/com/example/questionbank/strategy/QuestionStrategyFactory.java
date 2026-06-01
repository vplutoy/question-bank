package com.example.questionbank.strategy;

import com.example.questionbank.enums.QuestionType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class QuestionStrategyFactory {

    private final Map<String, QuestionTypeStrategy> strategyMap;

    public QuestionStrategyFactory(SingleChoiceStrategy singleChoice,
                                   MultipleChoiceStrategy multipleChoice,
                                   FillBlankStrategy fillBlank,
                                   SubjectiveStrategy subjective) {
        strategyMap = Map.of(
                QuestionType.SINGLE_CHOICE.name(), singleChoice,
                QuestionType.MULTIPLE_CHOICE.name(), multipleChoice,
                QuestionType.FILL_BLANK.name(), fillBlank,
                QuestionType.SUBJECTIVE.name(), subjective
        );
    }

    public QuestionTypeStrategy getStrategy(String type) {
        QuestionTypeStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("不支持的题型: " + type);
        }
        return strategy;
    }
}
