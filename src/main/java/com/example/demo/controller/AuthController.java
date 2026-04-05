// src/main/java/com/example/demo/controller/AuthController.java
package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.SignupRequest;
import com.example.demo.dto.auth.UserInfoResponse;
import com.example.demo.dto.common.MessageResponse;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String SESSION_USER_ID = "LOGIN_USER_ID";

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("이미 존재하는 아이디입니다."));
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = new User(request.username(), encodedPassword, request.email());
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("회원가입 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest) {

        User user = userRepository.findByUsername(request.username())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("아이디 또는 비밀번호가 올바르지 않습니다."));
        }

        HttpSession session = httpRequest.getSession(true); // 세션 생성
        session.setAttribute(SESSION_USER_ID, user.getId()); // 🔹 세션에 유저ID 저장

        return ResponseEntity.ok(new MessageResponse("로그인 성공"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(new MessageResponse("로그아웃 성공"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(401).body(new MessageResponse("로그인 필요"));
        }

        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).body(new MessageResponse("로그인 필요"));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return ResponseEntity.ok(
                new UserInfoResponse(user.getId(), user.getUsername(), user.getEmail())
        );
    }
}
