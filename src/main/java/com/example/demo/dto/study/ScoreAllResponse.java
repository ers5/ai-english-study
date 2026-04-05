package com.example.demo.dto.study;

import java.util.List;

public record ScoreAllResponse(
        List<ScoredAnswerDto> results,
        int totalScore,
        double averageScore
) {
}
