
document.addEventListener("DOMContentLoaded", () => {

  // ---------------------------------------------------
  // 1. 오답노트 페이지로 이동
  // ---------------------------------------------------
  const wrongNoteBtn = document.querySelector(".btn.ghost");
  if (wrongNoteBtn) {
    wrongNoteBtn.addEventListener("click", () => {
      window.location.href = "../mypage/wrongnote.html";
    });
  }

  // ---------------------------------------------------
  // 2. 메뉴로 돌아가기 (FAB 버튼)
  // ---------------------------------------------------
  const backFab = document.querySelector(".back-fab");
  if (backFab) {
    backFab.addEventListener("click", (e) => {
      e.preventDefault();
      window.location.href = "../menu/index.html";
    });
  }

  // ---------------------------------------------------
  // 3. 스프링 REST API에서 통계정보 가져오기
  // ---------------------------------------------------
  fetch("http://localhost:8080/api/stats")   // 스프링 서버 주소
    .then(res => res.json())
    .then(data => {
      const numbers = document.querySelectorAll(".stat-number");
      if (numbers.length >= 4) {
        numbers[0].textContent = data.streakDays;
        numbers[1].textContent = data.todaySolved;
        numbers[2].textContent = data.totalSolved;
        numbers[3].textContent = data.totalWrong;
      }

      const barFill = document.querySelector(".bar-fill");
      const barMeta = document.querySelector(".bar-meta span");
      if (barFill && barMeta) {
        barFill.style.width = data.accuracyRate + "%";
        barMeta.textContent = data.accuracyRate;
      }
    })
    .catch(err => console.error("데이터 불러오기 실패:", err));

});
