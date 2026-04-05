// src/main/java/com/example/demo/repository/UserAnswerRepository.java
package com.example.demo.repository;

import com.example.demo.domain.User;
import com.example.demo.domain.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    // 내 모든 답변
    List<UserAnswer> findByUser(User user);
}
