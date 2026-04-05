// src/main/resources/static/board/board.js
document.addEventListener("DOMContentLoaded", () => {
  /* -----------------------------------
     탭 전환
  ----------------------------------- */
  const tabButtons = document.querySelectorAll(".tab");
  const boardSections = document.querySelectorAll(".board");

  tabButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
      const target = btn.dataset.target; // "free" | "question"
      setActiveTab(target);
    });
  });

  function setActiveTab(target) {
    tabButtons.forEach((b) => {
      b.classList.toggle("active", b.dataset.target === target);
    });

    boardSections.forEach((sec) => {
      sec.style.display = sec.id === target ? "block" : "none";
    });

    loadBoard(target);
  }

  /* -----------------------------------
     메뉴 버튼 (메뉴 페이지로 이동)
  ----------------------------------- */
  const menuBtn = document.getElementById("menu-btn");
  if (menuBtn) {
    menuBtn.addEventListener("click", (e) => {
      e.preventDefault();
      window.location.href = "../menu/index.html";
    });
  }

  /* -----------------------------------
     글쓰기 버튼 -> write.html?type=...
  ----------------------------------- */
  const writeButtons = document.querySelectorAll(".board-head .btn.primary");
  writeButtons.forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();
      const boardType = btn.closest("section").id; // free | question
      window.location.href = `write.html?type=${boardType}`;
    });
  });

  /* -----------------------------------
     게시글 목록 불러오기
     /api/boards?type=FREE | QUESTION
  ----------------------------------- */
  async function loadBoard(tabType) {
    // tabType: "free" | "question"
    const enumType = tabType === "question" ? "QUESTION" : "FREE";
    const tbody = document.querySelector(
        `#${tabType} .board-table tbody`
    );

    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="4">불러오는 중...</td></tr>`;

    try {
      const res = await fetch(`/api/boards?type=${enumType}`, {
        method: "GET",
        credentials: "include",
        headers: {
          Accept: "application/json",
        },
      });

      if (!res.ok) {
        throw new Error("목록 조회 실패");
      }

      const list = await res.json(); // BoardSummaryResponse[]

      tbody.innerHTML = "";

      if (!list || list.length === 0) {
        const row = document.createElement("tr");
        const cell = document.createElement("td");
        cell.colSpan = 4;
        cell.textContent = "게시글이 없습니다.";
        row.appendChild(cell);
        tbody.appendChild(row);
        return;
      }

      list.forEach((post) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${post.id}</td>
          <td>${post.title}</td>
          <td>${post.authorName}</td>
          <td>-</td>
        `;
        tbody.appendChild(tr);
      });

      // 행 클릭 시 상세 페이지로 이동
      tbody.addEventListener("click", (e) => {
        const row = e.target.closest("tr");
        if (!row) return;
        const postId = row.children[0].textContent;
        window.location.href = `detail.html?type=${tabType}&id=${postId}`;
      });
    } catch (err) {
      console.error(err);
      tbody.innerHTML = `<tr><td colspan="4">목록을 가져오지 못했습니다.</td></tr>`;
    }
  }

  /* -----------------------------------
     첫 로딩: URL ?tab=free|question 처리
  ----------------------------------- */
  const params = new URLSearchParams(window.location.search);
  const initialTab = params.get("tab") || "free";
  setActiveTab(initialTab);
});

