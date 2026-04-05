// src/main/java/com/example/demo/repository/WrongNoteRepository.java
package com.example.demo.repository;

import com.example.demo.domain.User;
import com.example.demo.domain.WrongNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WrongNoteRepository extends JpaRepository<WrongNote, Long> {

    // 내 오답노트 목록
    List<WrongNote> findByUser(User user);
    List<WrongNote> findByUserIdOrderByCreatedAtDesc(Long userId);

}
