// src/main/resources/static/board/write.js
document.addEventListener("DOMContentLoaded", () => {
  // URL에서 type=free | question
  const params = new URLSearchParams(window.location.search);
  const type = params.get("type") || "free";
  const enumType = type === "question" ? "QUESTION" : "FREE";

  const titleEl = document.querySelector(".write-title");
  const titleInput = document.querySelector(".title-input");
  const contentInput = document.querySelector(".content-input");
  const submitBtn = document.querySelector(".write-submit");
  const cancelBtn = document.querySelector(".write-cancel");

  if (titleEl) {
    titleEl.textContent =
        type === "free" ? "자유게시판 글쓰기" : "질문게시판 글쓰기";
  }

  // 등록 버튼
  submitBtn.addEventListener("click", async (e) => {
    e.preventDefault();

    const title = titleInput.value.trim();
    const content = contentInput.value.trim();

    if (!title || !content) {
      alert("제목과 내용을 모두 입력해주세요.");
      return;
    }

    const payload = {
      title,
      content,
      type: enumType, // BoardCreateRequest.type (BoardType)
    };

    try {
      const res = await fetch("/api/boards", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(payload),
      });

      if (res.status === 401) {
        alert("로그인이 필요합니다. 다시 로그인해주세요.");
        window.location.href = "/index.html";
        return;
      }

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "글 등록 실패");
      }

      await res.json(); // 응답 내용은 지금은 안 써도 됨

      alert("글이 등록되었습니다.");
      window.location.href = `board.html?tab=${type}`;
    } catch (err) {
      console.error(err);
      alert(err.message || "등록 중 오류가 발생했습니다.");
    }
  });

  // 취소 버튼
  cancelBtn.addEventListener("click", (e) => {
    e.preventDefault();
    history.back();
  });
});

