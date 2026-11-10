//========================================
// 회원가입
//========================================
const canSignup = {
    id: false,
    pwd: false,
    name: false,
    email: false
};

let lastUsername = null;
let usernameController = null;

document.addEventListener("DOMContentLoaded", () => {
    //입력란 이벤트
    document.addEventListener("focusout", onInputFocusOut);
    document.addEventListener("input", onInputChange);
});

//----------[ ▼ 입력란 포커스 아웃 이벤트를 연결합니다. ]----------
async function onInputFocusOut(event) {
    const input = event.target;

    if(!input.matches(".signup-page__input")) {
        return;
    }

    switch (input.id) {
        case "username":
            await onUsernameBlur(event);
            break;
    }
}

//----------[ ▼ 입력란 값 변경 이벤트를 연결합니다. ]----------
function onInputChange(event) {
    const input = event.target;

    if(!input.matches(".signup-page__input")) {
        return;
    }

    switch (input.id) {
        case "username":
            hideFieldMessage(input.closest('.signup-page__field'));

            onUsernameChange(event);
            break;
    }
}

//----------[ ▼ 아이디 입력란 포커스 아웃 이벤트를 연결합니다. ]----------
async function onUsernameBlur(event) {
    const input = event.target;
    const value = input.value.trim();
    const field = input.closest('.signup-page__field');

    //아이디 미입력
    if(value === "") {
        lastUsername = null;

        showFieldMessage(field, Message.page("usernameRequired"));
        return;
    }

    //기존값과 동일하면 종료
    if(value === lastUsername) {
        return;
    }
    
    await checkDuplicateUsername(value, field, input);
}

//----------[ ▼ 아이디 입력란 값 변경 이벤트를 연결합니다. ]----------
function onUsernameChange() {
    canSignup.id = false;
    lastUsername = null;

    //진행 중인 중복 확인 요청 취소
    usernameController?.abort();
    usernameController = null;
}

//----------[ ▼ 아이디 중복 확인 API를 호출합니다. ]----------
async function checkDuplicateUsername(username, field, input) {
    usernameController?.abort();

    const controller = new AbortController();
    usernameController = controller;

    const params = new URLSearchParams({username});

    try{
        const response = await fetch(
            `/api/accounts/username-availability?${params}`,
            {
                headers: {
                    'Accept': 'application/json'
                },
                signal: controller.signal
            }
        );

        const json = await response.json();

        //오래된 요청이면 무시
        if(usernameController !== controller || input.value.trim() !== username) {
            return;
        }

        if(response.ok && json.success === true) {
            const type = json.data.available ? "success" : "error";

            showFieldMessage(field, json.message, type);

            lastUsername = username;

            if(type === "success") {
                canSignup.id = true;
            }

            return;
        }

        if(response.status === 400 && json.success === false) {
            showFieldMessage(field, json.message);
        }
    }catch(error) {
        if(error.name === "AbortError") {
            return;
        }

        showAlert(Message.page('networkError'), field);
    }finally {
        if(usernameController === controller) {
            usernameController = null;
        }
    }
}

//----------[ ▼ 메시지를 보여줍니다. ]----------
function showFieldMessage(field, text, type="error") {
    MessageUI.show(field, text, type);

    setInputState(field, type);
}

//----------[ ▼ 메시지를 숨깁니다. ]----------
function hideFieldMessage(field) {
    MessageUI.hide(field);
    clearInputState(field);
}

//----------[ ▼ 알럿을 보여줍니다. ]----------
function showAlert(text, field) {
    alert(text);
    clearInputState(field);

    ClassUtil.add(
        field.querySelector('.signup-page__input'),
        'signup-page__input--error',
    );
}

//----------[ ▼ 입력란 상태를 설정합니다. ]----------
function setInputState(field, type) {
    const inputs = field.querySelectorAll('.signup-page__input');

    inputs.forEach(input => {
        ClassUtil.remove(input, 'signup-page__input--error');
        ClassUtil.remove(input, 'signup-page__input--success');

        ClassUtil.add(input, `signup-page__input--${type}`);
    });
}

//----------[ ▼ 입력란 상태를 초기화합니다. ]----------
function clearInputState(field) {
    const inputs = field.querySelectorAll('.signup-page__input');

    inputs.forEach(input => {
        ClassUtil.remove(input, 'signup-page__input--error');
        ClassUtil.remove(input, 'signup-page__input--success');
    });
}