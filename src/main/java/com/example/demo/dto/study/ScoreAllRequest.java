package com.example.demo.dto.study;

import java.util.List;

public record ScoreAllRequest(
        Long studySessionId,
        List<AnswerRequest> answers
) {
}
