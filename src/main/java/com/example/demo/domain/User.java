// src/main/java/com/example/demo/domain/User.java
package com.example.demo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // user는 예약어일 수 있어서 테이블명 users로
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false)
    private String passwordHash;  // 나중에 BCrypt 사용 예정

    @Column(length = 100)
    private String email;

    protected User() {
        // JPA 기본 생성자
    }

    public User(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
