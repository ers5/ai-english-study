package com.example.demo.controller;

import com.example.demo.dto.WrongNoteResponse;
import com.example.demo.dto.study.StudyStartResponse;
import com.example.demo.dto.common.MessageResponse;
import com.example.demo.service.StudyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private static final String SESSION_USER_ID = "LOGIN_USER_ID";

    @PostMapping("/start")
    public ResponseEntity<?> start(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        StudyStartResponse response = studyService.startStudySession(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/wrong-notes")
    public ResponseEntity<?> getWrongNotes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        List<WrongNoteResponse> notes = studyService.getMyWrongNotes(userId);
        return ResponseEntity.ok(notes);
    }
}
