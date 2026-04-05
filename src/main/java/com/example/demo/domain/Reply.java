// src/main/java/com/example/demo/domain/Reply.java
package com.example.demo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "reply")
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 게시글의 댓글인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    // 댓글 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 1000)
    private String content;

    protected Reply() {
    }

    public Reply(Board board, User author, String content) {
        this.board = board;
        this.author = author;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public User getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
