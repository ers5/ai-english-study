// src/main/java/com/example/demo/domain/UserAnswer.java
package com.example.demo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "user_answer")
public class UserAnswer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 유저의 답인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 어떤 문장에 대한 답인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sentence_id", nullable = false)
    private Sentence sentence;

    @Column(nullable = false, length = 1000)
    private String userTranslation;

    // 0~10점
    @Column(nullable = false)
    private int score;

    // GPT가 준 피드백
    @Column(length = 2000)
    private String feedback;

    protected UserAnswer() {
    }

    public UserAnswer(User user, Sentence sentence, String userTranslation, int score, String feedback) {
        this.user = user;
        this.sentence = sentence;
        this.userTranslation = userTranslation;
        this.score = score;
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

    public int getScore() {
        return score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
