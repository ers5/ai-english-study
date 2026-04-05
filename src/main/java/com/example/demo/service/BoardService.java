// src/main/java/com/example/demo/service/BoardService.java
package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.dto.board.*;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.ReplyRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;

    /** 글 작성 */
    public BoardDetailResponse createBoard(Long userId, BoardCreateRequest req) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        Board board = new Board(
                author,
                req.title(),
                req.content(),
                req.type()
        );
        Board saved = boardRepository.save(board);

        return new BoardDetailResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getBoardType(),
                saved.getAuthor().getUsername(),
                List.of()   // 처음엔 댓글 없음
        );
    }

    /** 글 목록 */
    public List<BoardSummaryResponse> getBoardList(BoardType type) {
        List<Board> boards = (type == null)
                ? boardRepository.findAllByOrderByCreatedAtDesc()
                : boardRepository.findByBoardTypeOrderByCreatedAtDesc(type);

        return boards.stream()
                .map(b -> new BoardSummaryResponse(
                        b.getId(),
                        b.getTitle(),
                        b.getAuthor().getUsername(),
                        b.getBoardType()
                ))
                .toList();
    }

    /** 글 상세 */
    public BoardDetailResponse getBoardDetail(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        List<ReplyResponse> replies = replyRepository
                .findByBoardOrderByCreatedAtAsc(board).stream()
                .map(r -> new ReplyResponse(
                        r.getId(),
                        r.getAuthor().getUsername(),
                        r.getContent()
                ))
                .toList();

        return new BoardDetailResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getBoardType(),
                board.getAuthor().getUsername(),
                replies
        );
    }

    /** 댓글 작성 */
    public ReplyResponse addReply(Long userId, Long boardId, String content) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Reply reply = new Reply(board, author, content);
        Reply saved = replyRepository.save(reply);

        return new ReplyResponse(
                saved.getId(),
                saved.getAuthor().getUsername(),
                saved.getContent()
        );
    }


}

