// src/main/java/com/example/demo/dto/board/BoardSummaryResponse.java
package com.example.demo.dto.board;

import com.example.demo.domain.BoardType;

public record BoardSummaryResponse(
        Long id,
        String title,
        String authorName,
        BoardType boardType
) {
}
