# ai-english-study

Spring Boot 기반의 AI 영어 학습 프로젝트입니다.  
사용자가 Gemini로 부터 영어 문장 10개를 받아오고 해석을 입력하면 다시 Gemini에게 보내 채점과 피드백을 받고 오답노트를 만들어주는 웹사이트 입니다.

## 주요 기능
- 회원가입 / 로그인
- 학습 세션 진행
- AI 기반 답변 평가
- 오답노트 확인
- 게시판 기능

## Tech Stack
- Java 17
- Spring Boot
- Gradle
- Gemini API / OpenAI API

## 실행 방법
1. Java 17 이상 설치
2. 환경변수 설정
   - `GEMINI_API_KEY`
3. 프로젝트 실행
   ```bash
   ./gradlew bootRun
