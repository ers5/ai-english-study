package com.example.demo.dto.study;

public record AnswerRequest(
        Long sentenceId,
        String userTranslation
) {
}
