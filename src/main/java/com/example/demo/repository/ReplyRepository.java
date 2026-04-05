// src/main/java/com/example/demo/repository/ReplyRepository.java
package com.example.demo.repository;

import com.example.demo.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.demo.domain.Board;
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByBoardOrderByCreatedAtAsc(Board board);
}
