package com.example.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OpenAIService {

    // [#3] 수정 기존에는 openAI API를 사용할려고 했으나, API 사용이 유료라 무료로 받아올 수 있는 gemini로 변경
    private static final String GEMINI_MODEL = "gemini-2.5-flash";

    private final Client geminiClient;
    private final ObjectMapper objectMapper;

    // application.properties 에서 gemini.api.key 읽어와서 Client 생성
    public OpenAIService(
            @Value("${gemini.api.key}") String geminiApiKey,
            ObjectMapper objectMapper
    ) {
        this.geminiClient = Client.builder()
                .apiKey(geminiApiKey)
                .build();
        this.objectMapper = objectMapper;
    }

    public ScoreResult scoreSingle(Long sentenceId, String english, String userTranslation) {

        List<ScoreTarget> targets = List.of(
                new ScoreTarget(sentenceId, english, userTranslation)
        );

        List<ScoreResult> results = scoreTranslations(targets);

        return results.isEmpty()
                ? new ScoreResult(
                1,                // ← 여기! index 자리는 int
                0,
                "오류가 있습니다. 다시 채점하기를 눌러 주세요",
                ""
        )// [#4] 수정 기존에는 에러가 있으면 만점을 부여 했는데 0점 으로 조정후 사용자에게 오류가 있음을 알림
                : results.get(0);
    }

    /** 영어 문장 n개 생성 */
    public List<String> generateEnglishSentences(int count) {
        String prompt = """
                너는 한국인 성인 학습자를 위한 영어 선생님이다.
                다음 조건을 만족하는 영어 문장을 %d개 만들어라.

                - 일상 회화에서 자주 쓰는 자연스러운 문장
                - 난이도: CEFR A2~B1 정도
                - 각 문장은 10~15 단어 이내
                - 한국어 번역, 설명, 따옴표는 절대 쓰지 말 것

                출력 형식:
                각 줄에 "번호. 영어 문장" 형태로만 출력해라.
                예)
                1. I like coffee.
                2. I usually wake up at seven o'clock.
                ...
                """.formatted(count);

        try {
            GenerateContentResponse response =
                    geminiClient.models
                            .generateContent(GEMINI_MODEL, prompt, null);

            String content = Optional.ofNullable(response.text()).orElse("");

            if (content.isBlank()) {
                log.warn("Gemini 문장 생성 응답이 비어있음, 임시 문장 사용");
                return fallbackSentences();
            }

            return Arrays.stream(content.split("\\r?\\n"))
                    .map(line -> line.replaceAll("^\\d+\\.\\s*", "").trim())
                    .filter(s -> !s.isEmpty())
                    .limit(count)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Gemini 문장 생성 호출 실패, 임시 문장 사용", e);
            return fallbackSentences();
        }
    }

    /** 번역 채점 */
    public List<ScoreResult> scoreTranslations(List<ScoreTarget> targets) {
        if (targets.isEmpty()) {
            return List.of();
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("""
            You are an English teacher.
            For each numbered pair, grade the Korean translation of the English sentence.
            Scoring: 0 to 10 (integer). Consider meaning accuracy and naturalness.
            Return ONLY a JSON array, no extra text.

            JSON format:
            [
              { "index": 1, "score": 8, "feedback": "short Korean explanation", "correctExample": "better English sentence" },
              ...
            ]
            Pairs:
            """);

        int idx = 1;
        for (ScoreTarget t : targets) {
            prompt.append(idx).append(". English: ").append(t.english()).append("\n");
            prompt.append("   Korean: ").append(t.userTranslation()).append("\n\n");
            idx++;
        }

        try {
            GenerateContentResponse response =
                    geminiClient.models
                            .generateContent(GEMINI_MODEL, prompt.toString(), null);

            String content = Optional.ofNullable(response.text()).orElse("");

            String cleanedJson = extractJsonArray(content);   // JSON 부분만 추출
            ScoreResult[] array = objectMapper.readValue(cleanedJson, ScoreResult[].class);
            return Arrays.asList(array);                      // ✅ 이 한 줄만 남기기

        } catch (Exception e) {
            log.error("Gemini 채점 호출 실패, 기본 점수 사용", e);

            // 실패 시 임시 만점 부여
            List<ScoreResult> fallback = new ArrayList<>();
            for (int i = 0; i < targets.size(); i++) {
                fallback.add(new ScoreResult(
                        i + 1,
                        0,
                        "오류가 발생하였습니다. 다시 채점하기를 눌러주세요",
                        ""
                        //[#5] 수정 기존에는 만점을 부여했으나 0점으로 조정후 사용자에게 오류가 발생함을 알림
                ));
            }
            return fallback;
        }
    }


    private String extractJsonArray(String raw) {
        if (raw == null) return "[]";

        String s = raw.trim();

        // 1) ``` 로 시작하면 코드블럭 제거
        if (s.startsWith("```")) {
            // 첫 줄(``` 또는 ```json) 날리기
            int firstNewLine = s.indexOf('\n');
            if (firstNewLine != -1) {
                s = s.substring(firstNewLine + 1);
            } else {
                s = s.substring(3); // 그래도 그냥 ``` 뒤부터
            }

            // 마지막 ``` 잘라내기
            int lastFence = s.lastIndexOf("```");
            if (lastFence != -1) {
                s = s.substring(0, lastFence);
            }
            s = s.trim();
        }

        // 2) 앞에 이상한 텍스트 있으면 [ 또는 { 부터 시작
        int idxArray = s.indexOf('[');
        int idxObj = s.indexOf('{');
        int start;
        if (idxArray == -1) start = idxObj;
        else if (idxObj == -1) start = idxArray;
        else start = Math.min(idxArray, idxObj);

        if (start > 0) {
            s = s.substring(start);
        }

        // 3) 뒤에 설명이 더 붙었으면 마지막 ] 또는 } 까지만 자르기
        int endArray = s.lastIndexOf(']');
        int endObj = s.lastIndexOf('}');
        int end = Math.max(endArray, endObj);

        if (end != -1 && end + 1 < s.length()) {
            s = s.substring(0, end + 1);
        }

        return s.trim();
    }

    /** OpenAI 실패 시 쓰던 기본 문장들 */
    private List<String> fallbackSentences() {
        return List.of(
                "문장을 불러오는데 오류가 있습니다. 다시 시도해주세요.",
                "문장을 불러오는데 오류가 있습니다. 다시 시도해주세요.",
                "문장을 불러오는데 오류가 있습니다. 다시 시도해주세요.",
                "문장을 불러오는데 오류가 있습니다. 다시 시도해주세요.",
                "문장을 불러오는데 오류가 있습니다. 다시 시도해주세요.",
                "문장을 불러오는데 오류가 있습니다. 다시 시도해주세요.",
                "문장을 불러오는데 오류가 있습니다. 다시 시도해주세요.",
                "문장을 불러오는데 오류가 있습니다. 다시 시도해주세요.",
                "문장을 불러오는데 오류가 있습니다. 다시 시도해주세요.",
                "문장을 불러오는데 오류가 있습니다. 다시 시도해주세요."
        ); // [#5] 수정 기존에는 문장을 불러올 때 오류가 있다면 미리 하드코딩된 영어 문장 10개를 제공했으나 이를 오류가 발생함을 알리는 것으로 수정
    }

    /** 채점 대상 */
    public static record ScoreTarget(
            Long sentenceId,
            String english,
            String userTranslation
    ) {
    }

    /** 채점 결과 */
    public static record ScoreResult(
            int index,
            int score,
            String feedback,
            String correctExample
    ) {
    }
}

