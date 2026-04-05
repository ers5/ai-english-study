# ai-english-study

Spring Boot 기반의 AI 영어 학습 프로젝트입니다.

## 주요 기능
- 회원가입 / 로그인
- 학습 세션 진행
- AI 기반 답변 평가
- 오답노트 확인
- 게시판 기능

## 실행 방법
1. Java 17 이상 설치
2. 환경변수 설정
   - `OPENAI_API_KEY`
   - `GEMINI_API_KEY`
3. 실행
   ```bash
   ./gradlew bootRun
   ```

## 주의
- 민감한 API 키는 `application.properties`에 직접 넣지 말고 환경변수로 관리하세요.
