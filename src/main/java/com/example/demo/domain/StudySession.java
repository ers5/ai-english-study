// src/main/java/com/example/demo/domain/StudySession.java
package com.example.demo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "study_session")
public class StudySession extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 유저의 세션인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StudyStatus status = StudyStatus.IN_PROGRESS;

    protected StudySession() {
    }

    public StudySession(User user) {
        this.user = user;
        this.status = StudyStatus.IN_PROGRESS;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public StudyStatus getStatus() {
        return status;
    }

    public void setStatus(StudyStatus status) {
        this.status = status;
    }
}
