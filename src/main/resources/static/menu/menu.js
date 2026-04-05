// 메뉴 페이지 이동 컨트롤
import { logout } from "../api.js";

document.addEventListener("DOMContentLoaded", () => {

  // 🔥 로그아웃 버튼
  const logoutBtn = document.querySelector("#logout-btn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", async () => {
      if (!confirm("로그아웃 하시겠습니까?")) return;

      try {
        await logout(); // API 호출 (세션 삭제)
        alert("로그아웃되었습니다.");

        // 로그아웃 후 이동할 페이지
        window.location.href = "../index.html";
      } catch (err) {
        console.error(err);
        alert("로그아웃 중 오류가 발생했습니다.");
      }
    });
  }

  // 마이페이지 이동
  const mypageBtn = document.querySelector(".menu-card[data-target='mypage']");
  if (mypageBtn) {
    mypageBtn.addEventListener("click", () => {
      window.location.href = "../mypage/mypage.html";
    });
  }

  // 게시판 이동
  const boardBtn = document.querySelector(".menu-card[data-target='board']");
  if (boardBtn) {
    boardBtn.addEventListener("click", () => {
      window.location.href = "../board/board.html";
    });
  }

  // 공부하기 이동
  const learnBtn = document.querySelector(".menu-card[data-target='learn']");
  if (learnBtn) {
    learnBtn.addEventListener("click", () => {
      window.location.href = "../learn/learn.html";
    });
  }
});
