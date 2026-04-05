package com.example.demo.dto.study;

import java.util.List;

public class StudyStartResponse {

    private Long studySessionId;
    private List<SentenceDto> sentences;

    public StudyStartResponse(Long studySessionId, List<SentenceDto> sentences) {
        this.studySessionId = studySessionId;
        this.sentences = sentences;
    }

    public Long getStudySessionId() {
        return studySessionId;
    }

    public List<SentenceDto> getSentences() {
        return sentences;
    }
}
