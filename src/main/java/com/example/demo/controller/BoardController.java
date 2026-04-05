// src/main/java/com/example/demo/controller/BoardController.java
package com.example.demo.controller;

import com.example.demo.domain.BoardType;
import com.example.demo.dto.board.BoardCreateRequest;
import com.example.demo.dto.board.BoardDetailResponse;
import com.example.demo.dto.board.BoardSummaryResponse;
import com.example.demo.dto.board.ReplyCreateRequest;
import com.example.demo.dto.board.ReplyResponse;
import com.example.demo.dto.common.MessageResponse;
import com.example.demo.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private static final String SESSION_USER_ID = "LOGIN_USER_ID";

    /** 글 작성 */
    @PostMapping
    public ResponseEntity<?> createBoard(
            HttpServletRequest request,
            @RequestBody BoardCreateRequest boardRequest
    ) {
        Long userId = getLoginUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        BoardDetailResponse response = boardService.createBoard(userId, boardRequest);
        return ResponseEntity.ok(response);
    }

    /** 글 목록 (type 파라미터 없으면 전체) */
    @GetMapping
    public List<BoardSummaryResponse> getBoards(
            @RequestParam(name = "type", required = false) BoardType type
    ) {
        return boardService.getBoardList(type);
    }

    /** 글 상세 */
    @GetMapping("/{boardId}")
    public BoardDetailResponse getBoardDetail(@PathVariable("boardId") Long boardId) {
        return boardService.getBoardDetail(boardId);
    }

    /** 댓글 작성 (로그인 필요) */
    @PostMapping("/{boardId}/replies")
    public ResponseEntity<?> addReply(
            @PathVariable("boardId") Long boardId,
            HttpServletRequest request,
            @RequestBody ReplyCreateRequest requestDto
    ) {
        Long userId = getLoginUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        ReplyResponse response =
                boardService.addReply(userId, boardId, requestDto.content());
        return ResponseEntity.ok(response);
    }

    // ──────────────────── 헬퍼 메서드 ────────────────────
    private Long getLoginUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (Long) session.getAttribute(SESSION_USER_ID);
    }
}

