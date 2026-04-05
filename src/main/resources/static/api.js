// resources/static/api.js

const API_BASE = ""; // 같은 도메인/포트니까 굳이 안 써도 됨

async function apiRequest(path, options = {}) {
  const res = await fetch(API_BASE + path, {
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include", // 세션 쿠키 같이 보내기
    ...options,
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(`API Error ${res.status}: ${text}`);
  }

  // 비어있는 응답도 있을 수 있으니 방어적으로 처리
  const contentType = res.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    return res.json();
  }
  return res.text();
}

/** 회원가입 */
export async function signup(username, password, email) {
  return apiRequest("/api/auth/signup", {
    method: "POST",
    body: JSON.stringify({ username, password, email }),
  });
}

/** 로그인 */
export async function login(username, password) {
  return apiRequest("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });
}

/** 공부 시작 (문장 10개 + studySessionId) */
export async function startStudy() {
  return apiRequest("/api/study/start", {
    method: "POST",
  });
}

/** 채점 요청 */
export async function scoreStudy(studySessionId, answers) {
  // answers: [{ sentenceId, userTranslation }, ...]
  return apiRequest("/api/score/all", {
    method: "POST",
    body: JSON.stringify({ studySessionId, answers }),
  });
}

/** 내 오답노트 리스트 */
export async function getWrongNotes() {
  return apiRequest("/api/study/wrong-notes", {
    method: "GET",
  });
}

/** 게시글 작성 */
export async function createBoard(title, content, type) {
  // type: "FREE" 또는 "QUESTION"
  return apiRequest("/api/boards", {
    method: "POST",
    body: JSON.stringify({ title, content, type }),
  });
}

/** 게시글 목록 조회 */
export async function getBoards(type) {
  const query = type ? `?type=${encodeURIComponent(type)}` : "";
  return apiRequest(`/api/boards${query}`, {
    method: "GET",
  });
}

/** 게시글 상세 */
export async function getBoardDetail(boardId) {
  return apiRequest(`/api/boards/${boardId}`, {
    method: "GET",
  });
}

/** 댓글 작성 */
export async function addReply(boardId, content) {
  return apiRequest(`/api/boards/${boardId}/replies`, {
    method: "POST",
    body: JSON.stringify({ content }),
  });
}

/** 개별 문장 채점 */
export async function scoreOne(sentenceId, userTranslation) {
  return apiRequest("/api/score", {
    method: "POST",
    body: JSON.stringify({
      sentenceId,
      userTranslation,
    }),
  });
}

/** 로그아웃 */
export async function logout() {
  return apiRequest("/api/auth/logout", {
    method: "POST",
  });
}
