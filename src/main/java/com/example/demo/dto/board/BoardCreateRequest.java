package com.example.demo.dto.board;

import com.example.demo.domain.BoardType;

public record BoardCreateRequest(
        String title,
        String content,
        BoardType type   // FREE / QUESTION
) {
}
