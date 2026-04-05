package com.example.demo.service;

import com.example.demo.domain.Sentence;
import com.example.demo.domain.StudySession;
import com.example.demo.domain.User;
import com.example.demo.domain.UserAnswer;
import com.example.demo.domain.WrongNote;
import com.example.demo.dto.WrongNoteResponse;
import com.example.demo.dto.study.AnswerRequest;
import com.example.demo.dto.study.ScoreAllRequest;
import com.example.demo.dto.study.ScoreAllResponse;
import com.example.demo.dto.study.ScoredAnswerDto;
import com.example.demo.dto.study.SentenceDto;
import com.example.demo.dto.study.StudyStartResponse;
import com.example.demo.repository.SentenceRepository;
import com.example.demo.repository.StudySessionRepository;
import com.example.demo.repository.UserAnswerRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WrongNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final UserRepository userRepository;
    private final StudySessionRepository studySessionRepository;
    private final SentenceRepository sentenceRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final WrongNoteRepository wrongNoteRepository;
    private final OpenAIService openAIService;

    /**
     * 학습 세션 시작: StudySession 생성 + 문장 10개 생성/저장
     */
    @Transactional(readOnly = true)
    public List<WrongNoteResponse> getMyWrongNotes(Long userId) {
        return wrongNoteRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(WrongNoteResponse::from)
                .toList();
    }
    @Transactional
    public StudyStartResponse startStudySession(Long userId) {
        // 1) 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        // 2) StudySession 생성 (엔티티에서 만든 생성자 사용: 기본 상태 IN_PROGRESS)
        StudySession session = new StudySession(user);
        studySessionRepository.save(session);

        // 3) GPT로 문장 10개 생성
        java.util.List<String> texts = openAIService.generateEnglishSentences(10);

        // 4) Sentence 엔티티 저장 + DTO 만들기
        java.util.List<SentenceDto> sentenceDtos = new ArrayList<>();
        int orderIndex = 1;
        for (String text : texts) {
            Sentence sentence = new Sentence(session, text, orderIndex++);
            sentenceRepository.save(sentence);
            sentenceDtos.add(SentenceDto.from(sentence));
        }

        // 5) 응답
        return new StudyStartResponse(session.getId(), sentenceDtos);
    }

    /**
     * 사용자 번역 전체 채점
     */
    @Transactional
    public ScoreAllResponse scoreAll(Long userId, ScoreAllRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        if (request.answers() == null || request.answers().isEmpty()) {
            return new ScoreAllResponse(List.of(), 0, 0.0);
        }

        // sentenceId 목록
        List<Long> sentenceIds = request.answers().stream()
                .map(AnswerRequest::sentenceId)
                .toList();

        // sentenceId -> Sentence 맵
        List<Sentence> sentences = sentenceRepository.findAllById(sentenceIds);
        Map<Long, Sentence> sentenceMap = sentences.stream()
                .collect(Collectors.toMap(Sentence::getId, s -> s));

        // OpenAI로 보낼 타겟 리스트 (순서 유지)
        List<OpenAIService.ScoreTarget> targets = new ArrayList<>();
        for (AnswerRequest answer : request.answers()) {
            Sentence sentence = sentenceMap.get(answer.sentenceId());
            if (sentence == null) {
                throw new IllegalArgumentException("문장을 찾을 수 없습니다. id=" + answer.sentenceId());
            }
            targets.add(new OpenAIService.ScoreTarget(
                    sentence.getId(),
                    sentence.getEnglishText(),
                    answer.userTranslation()
            ));
        }

        // GPT 채점
        List<OpenAIService.ScoreResult> scoreResults = openAIService.scoreTranslations(targets);

        List<ScoredAnswerDto> resultDtos = new ArrayList<>();
        int totalScore = 0;

        for (int i = 0; i < targets.size(); i++) {
            OpenAIService.ScoreTarget target = targets.get(i);

            OpenAIService.ScoreResult sr =
                    (i < scoreResults.size())
                            ? scoreResults.get(i)
                            : new OpenAIService.ScoreResult(i + 1, 0, "채점 과정에서 오류가 있습니다.", "");
            //[#1] 보완 기존에는 채점 과정 중 오류가 발생하면 임의로 만점을 부여했는데, 이를 0점으로 수정 후 피드백으로 오류가 있음을 알림

            int score = sr.score();
            String feedback = sr.feedback();
            String correctExample = sr.correctExample();

            totalScore += score;

            Sentence sentence = sentenceMap.get(target.sentenceId());

            // UserAnswer 저장
            UserAnswer ua = new UserAnswer(
                    user,
                    sentence,
                    target.userTranslation(),
                    score,
                    feedback
            );
            userAnswerRepository.save(ua);

            // 점수가 낮으면 오답노트에 저장 (예: 8점 미만)
            if (score < 8) {
                WrongNote wn = new WrongNote(
                        user,
                        sentence,
                        target.userTranslation(),
                        correctExample,
                        feedback
                );
                wrongNoteRepository.save(wn);
            }

            resultDtos.add(new ScoredAnswerDto(
                    target.sentenceId(),
                    score,
                    feedback,
                    correctExample
            ));
        }

        double avg = targets.isEmpty() ? 0.0 : (double) totalScore / targets.size();
        return new ScoreAllResponse(resultDtos, totalScore, avg);
    }

    @Transactional
    public ScoredAnswerDto scoreOne(Long userId, AnswerRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        Sentence sentence = sentenceRepository.findById(request.sentenceId())
                .orElseThrow(() -> new IllegalArgumentException("문장을 찾을 수 없습니다. id=" + request.sentenceId()));

        // GPT 채점 1개만 요청
        OpenAIService.ScoreResult sr = openAIService.scoreSingle(
                sentence.getId(),
                sentence.getEnglishText(),
                request.userTranslation()
        );

        int score = sr.score();
        String feedback = sr.feedback();
        String correctExample = sr.correctExample();

        // UserAnswer 저장
        UserAnswer ua = new UserAnswer(
                user,
                sentence,
                request.userTranslation(),
                score,
                feedback
        );
        userAnswerRepository.save(ua);

        // 오답노트 저장
        if (score < 8) {
            WrongNote wn = new WrongNote(
                    user,
                    sentence,
                    request.userTranslation(),
                    correctExample,
                    feedback
            );
            wrongNoteRepository.save(wn);
        }
        //[#2] 추가 기존에는 삭제 기능이 없었는데, 오답노트의 채점에서 8점 이상이면 이 문제를 삭제하도록 추가함
        else{wrongNoteRepository.deleteById(request.sentenceId());}

        return new ScoredAnswerDto(
                sentence.getId(),
                score,
                feedback,
                correctExample
        );
    }

}
