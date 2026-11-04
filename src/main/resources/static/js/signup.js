//========================================
// 회원가입
//========================================
let lastUsername, usernameController;

document.addEventListener("DOMContentLoaded", () => {
    const usernameInput = document.querySelector('#username');

    usernameInput.addEventListener("focus", () => hideMessage(usernameInput.parentElement));
    usernameInput.addEventListener("blur", onUsernameInput);
});

async function onUsernameInput(event) {
    const input = event.target;
    const value = input.value.trim();
    const field = input.closest(".signup-page__field");

    //아이디 미입력
    if (value === "") {
        showMessage(field, Message.page('usernameRequired'));
        lastUsername = null;

        return;
    }

    //기존값과 동일하면 종료
    if(lastUsername === value) {
        return;
    }

    //기존 메시지 제거
    hideMessage(field);

    //아이디 중복 확인
    await checkDuplicateUsername(value, field);
}

//----------[ ▼ 아이디 중복 API를 호출합니다. ]----------
async function checkDuplicateUsername(username, field) {
    //이전 요청 취소
    usernameController?.abort();

    usernameController = new AbortController();
    const params = new URLSearchParams({username});

    try{
        const response = await fetch(
            `/api/accounts/username-availability?${params}`,
            {
                headers: {
                    'Accept': 'application/json'
                },
                signal: usernameController.signal
            }
        );

        const json = await response.json();

        if(response.ok && json.success === true) {
            const type = json.data.available ? "success" : "error";

            showMessage(field, json.message, type);
            return;
        }

        if(response.status === 400 && json.success === false) {
            showMessage(field, json.message);
        }
    }catch(error) {
        if(error.name === "AbortError") {
            return;
        }

        showAlert(Message.page('networkError'), field);
    }
}

//----------[ ▼ 메시지를 생성합니다. ]----------
function createMessage(text, type = "error") {
    const message = document.createElement("p");

    message.classList.add("message", `message--${type}`);
    message.textContent = text;

    return message;
}

//----------[ ▼ 메시지를 보여줍니다. ]----------
function showMessage(parent, text, type = "error") {
    hideMessage(parent);

    const message = createMessage(text, type);
    parent.appendChild(message);

    showClassName(parent, type);
    
    return message;
}

//----------[ ▼ 메시지를 숨깁니다. ]----------
function hideMessage(parent) {
    parent.querySelectorAll(".message").forEach(
        message => {
            message.remove();
        }
    );

    hideClassName(parent);
}

//----------[ ▼ 알럿을 보여줍니다. ]----------
function showAlert(text, parent) {
    alert(text);

    showClassName(parent);
}

//----------[ ▼ 상태 클래스를 추가합니다. ]---------
function showClassName(parent, type = "error") {
    parent.querySelectorAll('.signup-page__input').forEach(
        input => {
            input.classList.add(`signup-page__input--${type}`);
        }
    )
}

//----------[ ▼ 상태 클래스를 제거합니다. ]----------
function hideClassName(parent) {
    parent.querySelectorAll('.signup-page__input').forEach(
        input => {
            input.classList.remove('signup-page__input--error');
        }
    )
}