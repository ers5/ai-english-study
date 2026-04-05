// src/main/java/com/example/demo/repository/StudySessionRepository.java
package com.example.demo.repository;

import com.example.demo.domain.StudySession;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    // 특정 유저의 세션들 (최근 순으로)
    List<StudySession> findByUserOrderByCreatedAtDesc(User user);
}
