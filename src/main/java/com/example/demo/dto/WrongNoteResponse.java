package com.example.demo.dto;

import com.example.demo.domain.WrongNote;
import lombok.Builder;

@Builder
public record WrongNoteResponse(
        Long wrongNoteId,
        Long sentenceId,
        String englishText,
        String userTranslation,
        String feedback,
        String correctExample
) {
    public static WrongNoteResponse from(WrongNote note) {
        return WrongNoteResponse.builder()
                .wrongNoteId(note.getId())
                .sentenceId(note.getSentence().getId())
                .englishText(note.getSentence().getEnglishText())
                .userTranslation(note.getUserTranslation())
                .feedback(note.getFeedback())
                .correctExample(note.getCorrectExample())
                .build();
    }
}
