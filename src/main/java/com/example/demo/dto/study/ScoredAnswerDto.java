package com.example.demo.dto.study;

public record ScoredAnswerDto(
        Long sentenceId,
        int score,
        String feedback,
        String correctExample
) {
}
