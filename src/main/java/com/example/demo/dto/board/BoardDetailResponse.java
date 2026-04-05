// src/main/java/com/example/demo/dto/board/BoardDetailResponse.java
package com.example.demo.dto.board;

import com.example.demo.domain.BoardType;

import java.util.List;

public record BoardDetailResponse(
        Long id,
        String title,
        String content,
        BoardType boardType,
        String authorName,
        List<ReplyResponse> replies
) {
}
