// learn.js (백엔드 연동 버전)

// 현재 받아온 문장들 저장
let currentSentences = [];

document.addEventListener("DOMContentLoaded", () => {
  const problemCards = Array.from(
      document.querySelectorAll(".problem.card.item")
  );
  const textareas = Array.from(
      document.querySelectorAll(".problem.card.item textarea.ta")
  );

  // 🔹 [수정 1] 초기 로딩 시 하드코딩된 영어 문장 가리기
  problemCards.forEach((card) => {
    const enEl = card.querySelector(".en");
    if (enEl) enEl.textContent = "";  // 백엔드에서 문장 오기 전까지 비워둠
  });

  // 🔹 [수정 2] 메뉴로 버튼 동작 추가
  const goMenuBtn = document.querySelector(".btn.ghost");
  if (goMenuBtn) {
    goMenuBtn.addEventListener("click", (e) => {
      e.preventDefault();
      window.location.href = "../menu/index.html";
    });
  }

  // 요약 영역
  const totalScoreEl = document.querySelector("#total-score");
  const avgScoreEl = document.querySelector("#avg-score");

  // 하단 버튼 2개: [0] 문장 받아오기, [1] 전체 채점하기
  const summaryButtons = Array.from(
      document.querySelectorAll(".summary .btn.primary.wide")
  );
  const loadBtn = summaryButtons[0];
  const scoreAllBtn = summaryButtons[1];

  // 웹 스토리지 키
  const SENTENCES_KEY = "learn_sentences";
  const ANSWER_PREFIX = "learn_answer_";

  // --- 웹 스토리지: 사용자가 쓴 해석 복원 ---
  function loadAnswersFromStorage() {
    textareas.forEach((ta) => {
      const key = ANSWER_PREFIX + ta.id;
      const saved = localStorage.getItem(key);
      if (saved != null) {
        ta.value = saved;
      }
    });
  }

  // --- 웹 스토리지: 사용자가 입력할 때마다 저장 ---
  function setupAnswerAutoSave() {
    textareas.forEach((ta) => {
      const key = ANSWER_PREFIX + ta.id;
      ta.addEventListener("input", () => {
        localStorage.setItem(key, ta.value);
      });
    });
  }

  function clearAnswers() {
    textareas.forEach((ta) => {
      const key = ANSWER_PREFIX + ta.id;
      ta.value = "";
      localStorage.removeItem(key);
    });
  }

  // 화면에 문장 세트 채우기
  function fillSentences(sentences, resetAnswers) {
    currentSentences = sentences || [];

    problemCards.forEach((card, index) => {
      const sentence = currentSentences[index];

      const enEl = card.querySelector(".en");
      const textarea = card.querySelector("textarea, .answer-input");
      const scoreMini = card.querySelector(".score-mini");
      const pill = card.querySelector(".pill");

      if (!sentence) {
        card.style.display = "none";
        return;
      }

      card.style.display = "";
      card.dataset.sentenceId = sentence.id;

      if (pill) {
        pill.textContent = `문장 ${index + 1}`;
      }
      if (enEl) {
        enEl.textContent = sentence.englishText;
      }

      if (resetAnswers && textarea) {
        textarea.value = "";
        if (textarea.classList.contains("ta")) {
          const key = ANSWER_PREFIX + textarea.id;
          localStorage.removeItem(key);
        }
      }

      if (scoreMini) {
        scoreMini.textContent = "- / 10";
      }

      const feedbackEl = card.querySelector(".feedback");
      if (feedbackEl) {
        feedbackEl.innerHTML = "";
      }
    });

    if (totalScoreEl) totalScoreEl.textContent = "- / 100";
    if (avgScoreEl) avgScoreEl.textContent = "- 점";
  }

  // 새 문장 세트 API로 받아오기 (버튼 눌렀을 때만 호출)
  async function loadSentences() {
    try {
      const res = await fetch("/api/study/start", {
        method: "POST",
      });

      if (res.status === 401) {
        alert("로그인이 필요합니다. 다시 로그인해 주세요.");
        window.location.href = "/index.html";
        return;
      }

      if (!res.ok) {
        throw new Error("문장 불러오기 실패");
      }

      const data = await res.json();
      const sentences = data.sentences || [];

      // 새 세트 받으면 이전 저장값들 초기화
      localStorage.setItem(SENTENCES_KEY, JSON.stringify(sentences));
      clearAnswers();
      fillSentences(sentences, true);

    } catch (e) {
      console.error(e);
      alert("학습 문장을 불러오는 중 오류가 발생했습니다.");
    }
  }

  // 웹 스토리지에서 이전 세트 + 해석 복원 (새로고침 시용)
  (function restoreFromStorage() {
    try {
      const raw = localStorage.getItem(SENTENCES_KEY);
      if (raw) {
        const sentences = JSON.parse(raw);
        if (Array.isArray(sentences) && sentences.length > 0) {
          fillSentences(sentences, false);
        }
      }
    } catch (e) {
      console.warn("저장된 문장을 복원하는 중 오류", e);
    }
    loadAnswersFromStorage();
    setupAnswerAutoSave();
  })();

  // 👉 문장 받아오기 버튼: 이때만 API 호출
  if (loadBtn) {
    loadBtn.addEventListener("click", (e) => {
      e.preventDefault();
      loadSentences();
    });
  }

  // ----------  전체 채점 ----------
  if (scoreAllBtn) {
    scoreAllBtn.addEventListener("click", async (e) => {
      e.preventDefault();

      if (!currentSentences.length) {
        alert("먼저 문장을 불러와 주세요.");
        return;
      }

      // 사용자가 입력한 번역 모으기
      const answers = currentSentences.map((sentence) => {
        const card = document.querySelector(
            `.problem.card.item[data-sentence-id="${sentence.id}"]`
        );
        const textarea = card?.querySelector("textarea, .answer-input");

        return {
          sentenceId: sentence.id,
          userTranslation: textarea ? textarea.value.trim() : "",
        };
      });

      try {
        const res = await fetch("/api/score/all", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ answers }),
        });

        if (res.status === 401) {
          alert("로그인이 필요합니다. 다시 로그인해 주세요.");
          window.location.href = "/index.html";
          return;
        }

        if (!res.ok) {
          throw new Error("채점 요청 실패");
        }

        const data = await res.json();

        const resultList =
            data.results ||
            data.resultDtos ||
            data.scoreResults ||
            data.scoredAnswers ||
            data.answers ||
            [];

        resultList.forEach((r) => {
          const card = document.querySelector(
              `.problem.card.item[data-sentence-id="${r.sentenceId}"]`
          );
          if (!card) return;

          const scoreMini = card.querySelector(".score-mini");
          const feedbackEl = card.querySelector(".feedback");

          if (scoreMini) {
            const score = r.score ?? "-";
            scoreMini.textContent = `${score} / 10`;
          }

          if (feedbackEl) {
            const fb = r.feedback || "";
            const correct = r.correctExample || "";
            let html = fb ? `피드백: ${fb}` : "피드백: -";
            if (correct) {
              html += `<br><span class="correct-example">예시: ${correct}</span>`;
            }
            feedbackEl.innerHTML = html;
          }
        });

        const total =
            data.totalScore ??
            data.total ??
            (typeof data.sum === "number" ? data.sum : null);
        const avg =
            data.averageScore ??
            data.avgScore ??
            data.avg ??
            (typeof data.mean === "number" ? data.mean : null);

        if (totalScoreEl && total != null) {
          totalScoreEl.textContent = `${total} / 100`;
        }
        if (avgScoreEl && avg != null) {
          avgScoreEl.textContent = `${avg} 점`;
        }

        alert("채점이 완료되었습니다.\n틀린 문장은 오답노트에 자동으로 저장됩니다.");
      } catch (e) {
        console.error(e);
        alert("채점 요청 중 오류가 발생했습니다.");
      }
    });
  }
});

