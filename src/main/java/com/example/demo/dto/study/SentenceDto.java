package com.example.demo.dto.study;

import com.example.demo.domain.Sentence;

public class SentenceDto {

    private Long id;
    private String englishText;
    private int orderIndex;

    public SentenceDto(Long id, String englishText, int orderIndex) {
        this.id = id;
        this.englishText = englishText;
        this.orderIndex = orderIndex;
    }

    public Long getId() {
        return id;
    }

    public String getEnglishText() {
        return englishText;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public static SentenceDto from(Sentence sentence) {
        return new SentenceDto(
                sentence.getId(),
                sentence.getEnglishText(),
                sentence.getOrderIndex()
        );
    }
}
