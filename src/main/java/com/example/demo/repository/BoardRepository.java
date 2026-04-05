// src/main/java/com/example/demo/repository/BoardRepository.java
package com.example.demo.repository;

import com.example.demo.domain.Board;
import com.example.demo.domain.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 타입별 게시글
    List<Board> findByBoardTypeOrderByCreatedAtDesc(BoardType boardType);

    // 전체 게시글
    List<Board> findAllByOrderByCreatedAtDesc();
}
