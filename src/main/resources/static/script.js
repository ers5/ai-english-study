// 공통 POST 호출 유틸
async function postJson(url, data) {
  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',          // 🔹 JSESSIONID(세션) 쿠키 유지
    body: JSON.stringify(data),
  })

  let body = null
  try {
    body = await res.json()
  } catch (e) {
    // JSON 아닌 응답일 수도 있으니 무시
  }

  if (!res.ok) {
    const msg = body && body.message ? body.message : `요청 실패 (${res.status})`
    throw new Error(msg)
  }
  return body
}

document.addEventListener('DOMContentLoaded', () => {
  // 🔹 로그인 처리
  const loginForm = document.getElementById('loginForm')
  if (loginForm) {
    loginForm.addEventListener('submit', async (event) => {
      event.preventDefault()

      const id = document.getElementById('uid').value.trim()
      const pw = document.getElementById('upw').value.trim()

      if (!id || !pw) {
        alert('아이디와 비밀번호를 입력해주세요.')
        return
      }

      try {
        // ⚠️ 백엔드 LoginRequest 필드 이름에 맞게 수정 필요할 수도 있음
        await postJson('/api/auth/login', {
          username: id,          // 백엔드에서 username/ password 로 받는다고 가정
          password: pw,
        })

        // 로그인 성공 → 메인 메뉴로 이동
        window.location.href = '/menu/index.html'
      } catch (e) {
        console.error(e)
        alert(e.message || '로그인에 실패했습니다.')
      }
    })
  }

  // 🔹 회원가입 처리
  const signupForm = document.getElementById('signupForm')
  if (signupForm) {
    signupForm.addEventListener('submit', async (event) => {
      event.preventDefault()

      const id = document.getElementById('uid').value.trim()
      const pw = document.getElementById('upw').value.trim()
      const nick = document.getElementById('unick').value.trim()

      if (!id || !pw || !nick) {
        alert('모든 값을 입력해주세요.')
        return
      }

      try {   // 👈 여기 ; 말고 { !
        // ⚠ 백엔드 SignupRequest 필드 이름에 맞게 수정 필요할 수도 있음
        await postJson('/api/auth/signup', {
          username: id,
          password: pw,
          nickname: nick,
        })

        alert('회원가입이 완료되었습니다. 이제 로그인해주세요!')
        window.location.href = '/index.html'
      } catch (e) {
        console.error(e)
        alert(e.message || '회원가입에 실패했습니다.')
      }
    })
  }})

