package com.example.demo.controller;

import com.example.demo.dto.common.MessageResponse;
import com.example.demo.dto.study.AnswerRequest;
import com.example.demo.dto.study.ScoreAllRequest;
import com.example.demo.dto.study.ScoreAllResponse;
import com.example.demo.dto.study.ScoredAnswerDto;
import com.example.demo.service.StudyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/score")
@RequiredArgsConstructor
public class ScoreController {

    private final StudyService studyService;
    private static final String SESSION_USER_ID = "LOGIN_USER_ID";

    @PostMapping("/all")
    public ResponseEntity<?> scoreAll(@RequestBody ScoreAllRequest request,
                                      HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute(SESSION_USER_ID) == null) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        ScoreAllResponse response = studyService.scoreAll(userId, request);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<?> scoreOne(@RequestBody AnswerRequest request,
                                      HttpServletRequest httpRequest) {

        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute(SESSION_USER_ID) == null) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        Long userId = (Long) session.getAttribute(SESSION_USER_ID);

        ScoredAnswerDto response = studyService.scoreOne(userId, request);
        return ResponseEntity.ok(response);
    }
}
