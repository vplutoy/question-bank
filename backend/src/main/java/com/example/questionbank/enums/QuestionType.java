package com.example.questionbank.enums;

public enum QuestionType {
    SINGLE_CHOICE("单选题"),
    MULTIPLE_CHOICE("多选题"),
    FILL_BLANK("填空题"),
    SUBJECTIVE("主观题");

    private final String label;

    QuestionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
