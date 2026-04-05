package com.example.demo.dto.study;

import java.time.LocalDateTime;

public record WrongNoteResponse(
        Long id,              // 오답노트 id
        Long sentenceId,      // 원문 문장 id
        String englishText,   // 원문 영어 문장
        String userTranslation, // 내가 쓴 번역
        String feedback,        // 피드백(한국어 설명)
        String correctExample,  // 더 좋은 예시 문장
        LocalDateTime createdAt // 언제 틀렸는지
) {
}
