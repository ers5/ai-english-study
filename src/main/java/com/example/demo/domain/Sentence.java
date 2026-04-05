// src/main/java/com/example/demo/domain/Sentence.java
package com.example.demo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "sentence")
public class Sentence extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 문장이 속한 세션
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_session_id", nullable = false)
    private StudySession studySession;

    @Column(nullable = false, length = 500)
    private String englishText;

    // 나중에 난이도 분류에 쓸 수 있음 (예: BEGINNER, INTERMEDIATE 등)
    @Column(length = 20)
    private String level;

    // 세션 내에서 몇 번째 문장인지 (1~10)
    @Column(nullable = false)
    private int orderIndex;

    protected Sentence() {
    }

    public Sentence(StudySession studySession, String englishText, int orderIndex) {
        this.studySession = studySession;
        this.englishText = englishText;
        this.orderIndex = orderIndex;
    }

    public Long getId() {
        return id;
    }

    public StudySession getStudySession() {
        return studySession;
    }

    public String getEnglishText() {
        return englishText;
    }

    public String getLevel() {
        return level;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
