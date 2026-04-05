// src/main/resources/static/board/detail.js
document.addEventListener("DOMContentLoaded", () => {
  const params = new URLSearchParams(window.location.search);
  const type = params.get("type") || "free";
  const id = params.get("id");

  if (!id) {
    alert("잘못된 접근입니다.");
    window.location.href = "board.html";
    return;
  }

  const titleEl = document.querySelector(".detail-title");
  const writerEl = document.querySelector(".detail-writer");
  const dateEl = document.querySelector(".detail-date");
  const contentEl = document.querySelector(".detail-content");
  const replyListEl = document.querySelector(".reply-list");
  const replyInput = document.querySelector(".reply-input");
  const replyBtn = document.querySelector(".reply-submit");

  // 상세 조회
  async function loadDetail() {
    try {
      const res = await fetch(`/api/boards/${id}`, {
        method: "GET",
        credentials: "include",
        headers: {
          Accept: "application/json",
        },
      });

      if (!res.ok) {
        throw new Error("상세 조회 실패");
      }

      const data = await res.json(); // BoardDetailResponse

      titleEl.textContent = data.title;
      writerEl.textContent = `작성자: ${data.authorName}`;
      dateEl.textContent = "날짜: —"; // createdAt 안 내려오므로 일단 고정
      contentEl.textContent = data.content;

      renderReplies(data.replies || []);
    } catch (err) {
      console.error(err);
      alert("글을 불러올 수 없습니다.");
      window.location.href = `board.html?tab=${type}`;
    }
  }

  // 댓글 렌더링
  function renderReplies(list) {
    replyListEl.innerHTML = "";

    if (!list.length) {
      const empty = document.createElement("p");
      empty.className = "reply-empty";
      empty.textContent = "아직 댓글이 없습니다.";
      replyListEl.appendChild(empty);
      return;
    }

    list.forEach((r) => {
      const item = document.createElement("div");
      item.className = "reply-item";

      const meta = document.createElement("div");
      meta.className = "reply-meta";
      meta.textContent = r.authorName;

      const content = document.createElement("div");
      content.className = "reply-content";
      content.textContent = r.content;

      item.appendChild(meta);
      item.appendChild(content);
      replyListEl.appendChild(item);
    });
  }

  // 댓글 등록
  replyBtn.addEventListener("click", async () => {
    const text = replyInput.value.trim();
    if (!text) {
      alert("댓글을 입력해주세요.");
      return;
    }

    try {
      const res = await fetch(`/api/boards/${id}/replies`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ content: text }), // ReplyCreateRequest.content
      });

      if (res.status === 401) {
        alert("로그인이 필요합니다.");
        window.location.href = "/index.html";
        return;
      }

      if (!res.ok) {
        throw new Error("댓글 등록 실패");
      }

      replyInput.value = "";
      await loadDetail(); // 댓글 다시 로드
    } catch (err) {
      console.error(err);
      alert(err.message || "댓글 등록 중 오류가 발생했습니다.");
    }
  });

  // 최초 로딩
  loadDetail();
});
