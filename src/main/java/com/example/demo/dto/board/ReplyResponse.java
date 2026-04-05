// src/main/java/com/example/demo/dto/board/ReplyResponse.java
package com.example.demo.dto.board;

public record ReplyResponse(
        Long id,
        String authorName,
        String content
) {
}
