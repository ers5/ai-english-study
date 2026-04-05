import { scoreOne } from "../api.js";

document.addEventListener("DOMContentLoaded", () => {
  const listEl = document.querySelector(".list");
  const loadBtn = document.querySelector(".btn.secondary");   // "문장 불러오기"
  const goMypageBtn = document.querySelector(".btn.ghost");   // 마이페이지로
  const backFab = document.querySelector(".back-fab");        // 메뉴로 플로팅 버튼

  // ----------------------------------------------------
  // 1. 마이페이지 / 메뉴 이동
  // ----------------------------------------------------
  if (goMypageBtn) {
    goMypageBtn.addEventListener("click", (e) => {
      e.preventDefault();
      window.location.href = "../mypage/mypage.html";
    });
  }

  if (backFab) {
    backFab.addEventListener("click", (e) => {
      e.preventDefault();
      window.location.href = "../menu/index.html";
    });
  }

  // ----------------------------------------------------
  // 2. 오답 목록 불러오기
  // ----------------------------------------------------
  async function loadWrongNotes() {
    try {
      const res = await fetch("/api/study/wrong-notes", {
        method: "GET",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(`오답 조회 실패: ${res.status} ${text}`);
      }

      const notes = await res.json();
      renderWrongNotes(Array.isArray(notes) ? notes : []);
    } catch (err) {
      console.error(err);
      alert("오답 목록을 불러오는 중 오류가 발생했습니다.");
    }
  }

  // ----------------------------------------------------
  // 3. 오답 목록 렌더링
  // ----------------------------------------------------
  function renderWrongNotes(notes) {
    if (!listEl) return;

    listEl.innerHTML = "";

    if (!notes.length) {
      listEl.innerHTML = `<p class="empty">저장된 오답이 없습니다.</p>`;
      return;
    }

    notes.forEach((note, index) => {
      const article = document.createElement("article");
      article.className = "note card";
      article.dataset.sentenceId = note.sentenceId;

      article.innerHTML = `
        <div class="head">
          <div class="head-left">
            <span class="pill blue">#${index + 1}</span>
          </div>
          <div class="head-right">
            <a href="#" class="btn link score-btn">채점하기</a>
          </div>
        </div>

        <h2 class="en">${note.englishText}</h2>

        <label class="label">나의 해석</label>
        <textarea class="ta">${note.userTranslation ?? ""}</textarea>

        <div class="foot">
          <div class="score-block">
            <span class="score-text">
              ${note.feedback ?? "아직 채점 전"}
            </span>
          </div>
        </div>
      `;

      listEl.appendChild(article);
    });

    attachNoteEvents();
  }

  // ----------------------------------------------------
  // 4. 개별 채점 기능
  // ----------------------------------------------------
  function attachNoteEvents() {
    const notes = document.querySelectorAll(".note");

    notes.forEach(noteEl => {
      const sentenceId = Number(noteEl.dataset.sentenceId);
      const textarea = noteEl.querySelector(".ta");
      const scoreBtn = noteEl.querySelector(".score-btn");
      const scoreText = noteEl.querySelector(".score-text");

      if (!scoreBtn || !textarea || !scoreText) return;

      scoreBtn.addEventListener("click", async (e) => {
        e.preventDefault();

        const userTranslation = textarea.value.trim();
        if (!userTranslation) {
          alert("해석을 입력해주세요.");
          return;
        }

        try {
          // 🚀 api.js 의 scoreOne() 사용
          const data = await scoreOne(sentenceId, userTranslation);
          const score = data.score ?? null;

          // 점수가 8점을 "넘기면" → 9 이상이면 정답 처리
          if (score !== null && score > 8) {
            alert("정답입니다! 오답노트에서 제거됩니다.");
            noteEl.remove();   // 화면에서 카드 제거
            return;
          }

          // 그 외(8점 이하)는 피드백만 갱신
          scoreText.innerHTML = `
            ${data.feedback ?? "채점 완료"}
            ${data.correctExample ? `<br><span class="correct-example">예시: ${data.correctExample}</span>` : ""}
          `;
        } catch (err) {
          console.error(err);
          alert("채점 중 오류가 발생했습니다.");
        }
      });
    });
  }


  // ----------------------------------------------------
  // 5. 버튼 클릭 시 오답 불러오기
  // ----------------------------------------------------
  if (loadBtn) {
    loadBtn.addEventListener("click", (e) => {
      e.preventDefault();
      loadWrongNotes();
    });
  }
});
