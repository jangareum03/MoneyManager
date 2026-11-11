//========================================
// 회원가입
//========================================
//회원가입 가능 여부
const canSignup = {
    id: false,
    pwd: false,
    name: false,
    email: false
};

//비밀번호 입력 상태
const passwordState = {
    valid: false,
    confirm: false
};

const passwordRegex = /^(?:(?=.*[A-Za-z])(?=.*[0-9])|(?=.*[A-Za-z])(?=.*[!#%^*]))[A-Za-z0-9!#%^*]{8,20}$/;

let lastUsername = null;
let usernameController = null;


//----------[ ▼ 초기화 및 이벤트를 연결합니다. ]----------
document.addEventListener("DOMContentLoaded", () => {
    //입력란 이벤트
    document.addEventListener("focusout", handleInputFocusOut);
    document.addEventListener("input", handleInputChange);
});

//----------[ ▼ 공통적인 입력란의 포커스 아웃 이벤트를 연결합니다. ]----------
async function handleInputFocusOut(event) {
    const input = event.target;

    if(!input.matches(".signup-page__input")) {
        return;
    }

    switch (input.id) {
        case "username":
            await onUsernameBlur(event);
            break;
        case "password":
            onPasswordBlur(event);
            break;
        case "re-password":
            onPasswordConfirmBlur(event);
            break;
    }
}

//----------[ ▼ 공통적인 입력란의 변경 이벤트를 연결합니다. ]----------
function handleInputChange(event) {
    const input = event.target;

    if(!input.matches(".signup-page__input")) {
        return;
    }

    switch (input.id) {
        case "username":
            hideFieldMessage(input.closest('.signup-page__field'));

            onUsernameChange(event);
            break;
        case "password":
        case "re-password":
            canSignup.pwd = false;

            hideFieldMessage(input.closest('.signup-page__field'));
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

//----------[ ▼ 비밀번호 입력란 포커스 아웃 이벤트를 연결합니다. ]----------
function onPasswordBlur(event) {
    const input = event.target;
    const field = input.closest('.signup-page__field');

    const message = validatePassword(input.value);

    passwordState.valid = message === null;

    if(message) {
        showFieldMessage(field, message);
        return;
    }

    hideFieldMessage(field);
}

//----------[ ▼ 비밀번호 확인 입력란 포커스 아웃 이벤트를 연결합니다. ]----------
function onPasswordConfirmBlur(event) {
    //비밀번호 검증 실패 상태
    if(!passwordState.valid) {
        onPasswordBlur({target: document.getElementById('password')});
        return;
    }

    const input = event.target;
    const field = input.closest('.signup-page__field');

    //비밀번호 일치여부
    const isMatch = validatePasswordConfirm();
    if(isMatch) {
        showFieldMessage(field, Message.page('passwordMatch'), 'success');
    }else {
        showFieldMessage(field, Message.page('passwordMismatch'));
    }
}

//----------[ ▼ 비밀번호 형식을 검사합니다. ]----------
function validatePassword(password) {
    if(password === "") {
        return Message.page("passwordRequired");
    }

    if(!isValidPasswordLength(password)) {
        return Message.page("passwordLength");
    }

    if(!isValidPasswordFormat(password)) {
        return Message.page("passwordFormat");
    }

    return null;
}

//----------[ ▼ 비밀번호 길이를 검사합니다. ]----------
function isValidPasswordLength(password) {
    return password.length >= 8 && password.length <= 20;
}

//----------[ ▼ 비밀번호 형식을 검사합니다. ]----------
function isValidPasswordFormat(password) {
    return passwordRegex.test(password);
}

//----------[ ▼ 비밀번호 일치 여부를 확인합니다. ]----------
function validatePasswordConfirm() {
    const password = document.getElementById("password").value;
    const confirm = document.getElementById("re-password").value;

    canSignup.pwd = password === confirm;

    return canSignup.pwd;
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