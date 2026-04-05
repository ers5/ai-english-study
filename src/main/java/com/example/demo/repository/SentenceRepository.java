// src/main/java/com/example/demo/repository/SentenceRepository.java
package com.example.demo.repository;

import com.example.demo.domain.Sentence;
import com.example.demo.domain.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SentenceRepository extends JpaRepository<Sentence, Long> {

    // 한 세션에 속한 10개 문장, 순서대로
    List<Sentence> findByStudySessionOrderByOrderIndexAsc(StudySession studySession);
}
