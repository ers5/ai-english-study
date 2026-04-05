// src/main/java/com/example/demo/domain/WrongNote.java
package com.example.demo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "wrong_note")
public class WrongNote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 유저의 오답노트인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 어떤 문장에 대한 오답인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sentence_id", nullable = false)
    private Sentence sentence;

    @Column(nullable = false, length = 1000)
    private String userTranslation;

    // (선택) GPT가 제안하는 모범 번역 같은 것
    @Column(length = 1000)
    private String correctExample;

    @Column(length = 2000)
    private String feedback;

    protected WrongNote() {
    }

    public WrongNote(User user, Sentence sentence, String userTranslation, String correctExample, String feedback) {
        this.user = user;
        this.sentence = sentence;
        this.userTranslation = userTranslation;
        this.correctExample = correctExample;
        this.feedback = feedback;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public String getUserTranslation() {
        return userTranslation;
    }

    public String getCorrectExample() {
        return correctExample;
    }

    public String getFeedback() {
        return feedback;
    }
}
