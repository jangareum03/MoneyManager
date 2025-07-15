const status = {
    id: false,
    password: false,
    rePassword: false,
    name: false,
    birth: false,
    nickName: false,
    email: false,
    agree:false
};

window.addEventListener( 'authTimerEnd', () => {
    const code = document.querySelector('.form__input-box--code');
    if( code ) {
        toggleEmailSend(true);
    }
});

document.addEventListener('DOMContentLoaded', () => {
    initInputText();
    initInputPassword();
    initInputCheckbox();
    initButton();
});



//----------[ ▼ 입력창을 초기화합니다. ]----------
function initInputText() {
    const idInput = document.querySelector('input[type="text"][name="id"]');
    if( idInput ) {
        idInput.addEventListener( 'blur', handlerBlur );
        idInput.addEventListener( 'focus', handlerFocus );
    }

    const nameInput = document.querySelector('input[type="text"][name="name"]');
    if( nameInput ) {
        nameInput.addEventListener( 'blur', handlerBlur );
        nameInput.addEventListener( 'focus', handlerFocus );
    }

    const birthInput = document.querySelector('input[type="text"][name="birth"]');
    if( birthInput ) {
        birthInput.addEventListener( 'blur', handlerBlur );
        birthInput.addEventListener( 'focus', handlerFocus );
    }

    const nickNameInput = document.querySelector('input[type="text"][name="nickName"]');
    if( nickNameInput ) {
        nickNameInput.addEventListener( 'blur', handlerBlur );
        nickNameInput.addEventListener( 'focus', handlerFocus );
    }

    const emailInput = document.querySelector('input[type="text"][name="email"]');
    if( emailInput ) {
        emailInput.addEventListener( 'focus', handlerFocus );
    }
}



//----------[ ▼ 비밀번호창을 초기화합니다. ]----------
function initInputPassword() {
    const passwordInput = document.querySelector('input[type="password"][name="password"]');
    if( passwordInput ) {
        passwordInput.addEventListener( 'blur', handlerBlur );
        passwordInput.addEventListener( 'focus', handlerFocus );
    }

    const rePasswordInput = document.querySelector('input[type="password"][id="signup-rePassword"]');
    if( rePasswordInput ) {
        rePasswordInput.addEventListener( 'blur', handlerBlur );
        rePasswordInput.addEventListener( 'focus', handlerFocus );
    }
}



//----------[ ▼ 체크박스를 초기화합니다. ]----------
function initInputCheckbox() {
    const agreeBox = document.querySelector('.form__input--checkbox');
    if( agreeBox ) {
        agreeBox.addEventListener( 'click', handlerClick );
    }
}



//----------[ ▼ 버튼을 초기화합니다. ]----------
function initButton() {
    //이메일 전송 버튼
    const emailButton = document.querySelector('.form__input-box--email .button');
    if( emailButton ) {
        emailButton.addEventListener( 'click', handlerClick );
    }

    //회원가입 버튼
    const signupButton = document.querySelector('.button--submit');
    if( signupButton ) {
        signupButton.addEventListener( 'click', handlerClick );
    }

    //취소버튼
    const cancelButton = document.querySelector('.button--cancel');
    if( cancelButton ) {
        cancelButton.addEventListener( 'click', handlerClick );
    }
}



//----------[ ▼ 요소에 포커스가 될 때 이벤트를 설정합니다. ]----------
function handlerFocus( event ) {
    const target = event.currentTarget;

    //모든 has-error 클래스 제거
    removeClassName( ["has-error"] );

    removeErrorBorder();
    removeMessage( false, true );


    //아이디 입력창에서 포커스 될 때
    if( target.closest('.form__field--id') ) {
        target.closest('.form__field--id').classList.remove('has-success');
        target.closest('.form__field--id').querySelector('.message--success')?.remove();
    }

    //닉네임 입력창에서 포커스 될 때
    if( target.closest('.form__field--nickname') ) {
        target.closest('.form__field--nickname').classList.remove('has-success');
        target.closest('.form__field--nickname').querySelector('.message--success')?.remove();
    }
}



//----------[ ▼ 요소에 포커스가 해지될 때 이벤트를 설정합니다. ]----------
async function handlerBlur( event ) {
    const target = event.currentTarget;
    let container, data;

    //아이디 입력창에서 포커스가 벗어났을 때
    if( target.closest('.form__field--id') ) {
        container = target.closest('.form__field--id');

        data = await validId( target );
        renderMessage( target.parentNode, data );

        status.id = data.success;

        if( status.id ) {
            target.classList.add('form__input--success');
            container.classList.add('has-success');
        }else {
            target.classList.add('form__input--error');
            container.classList.add('has-error');
        }
    }


    //비밀번호 입력창에서 포커스가 벗어났을 때
    if( target.closest('.form__field--password') ) {
        container = target.closest('.form__field--password');

        data = await validPasswordFormat( target );
        renderMessage( target.parentNode, data );

        status.password = data.success;

        if( !status.password ) {
            target.classList.add('form__input--error');
            container.classList.add('has-error');
        }
    }


    //비밀번호 확인 입력창에서 포커스가 벗어났을 때
    if( target.closest('.form__field--rePassword') ) {
        const passwordContainer = document.querySelector('.form__field--password');
        const passwordValue = passwordContainer.querySelector('input[type="password"][name="password"]').value.trim();

        //비밀번호가 검증이 안된 경우
        if( !status.password ) {
            passwordContainer.classList.add('has-error');
            passwordContainer.querySelector('.form__input--password').classList.add('form__input--error');

            //기존 에러메시지 삭제
            if( passwordContainer.classList.contains('message--error') ) passwordContainer.classList.contains('message--error').remove();

            renderMessage( passwordContainer, {success: false, message: '비밀번호를 먼저 확인해주세요.'} );
            return;
        }


        data = await validPasswordFormat( target );
        renderMessage( target.parentNode, data );

        status.rePassword = data.success;

        container = target.closest('.form__field--rePassword');
        target.classList.add('form__input--error');
        container.classList.add('has-error');

        if( !status.rePassword ) {
            renderMessage( container, data );
        }else if( passwordValue !== target.value.trim() ) {
            renderMessage( container, {success: false, message: '비밀번호와 일치하지 않습니다.'} );

            status.rePassword = false;
        }else {
            target.classList.remove('form__input--success');
        }
    }


    //이름 입력창에서 포커스가 벗어났을 때
    if( target.closest('.form__field--name') ) {
        data = await validName( target );
        renderMessage( target.parentNode, data );

        status.name = data.success;

        if( !status.name ) {
            target.classList.add('form__input--error');
            target.closest('.form__field--name').classList.add('has-error');
        }
    }


    //생년월일 입력창에서 포커스 벗어났을 때
    if( target.closest('.form__field--birth') ) {
        data = await validBirth( target );
        renderMessage( target.parentNode, data );

        status.birth = data.success;

        if( !status.birth ) {
            target.classList.add('form__input--error');
            target.closest('.form__field--birth').classList.add('has-error');
        }
    }


    //닉네임 입력창에서 포커스 벗어났을 때
    if( target.closest('.form__field--nickname') ) {
        container = target.closest('.form__field--nickname');
        data = await validNickname( target );
        renderMessage( target.parentNode, data );

        status.nickName = data.success;

        if( status.nickName ) {
            target.classList.add('form__input--success');
            container.classList.add('has-success');
        }else {
            target.classList.add('form__input--error');
            container.classList.add('has-error');
        }
    }


    //인증코드 입력창에서 포커스 벗어났을 때
    if( target.closest('.form__input-box--code') ) {
        container = target.closest('.form__input-container');

        //타이머 종료될 때
        if( EmailTimer.getMinute() === 0 && EmailTimer.getSecond() === 0 ) {
            toggleEmailSend(true);

            return;
        }

        const fields = getValuesByEmailCode( container );
        data = await validEmailCode( fields );

        status.email = data.success;

        if( status.email ) {
            container = document.querySelector('.form__field--email');

            if( !container.classList.contains('has-success') ) container.classList.add('has-success');

            toggleEmailSend(true);
            renderMessage( container, data );
        }else {
            target.classList.add('form__input--error');
            target.closest('.form__field--email').classList.add('has-error');

            renderMessage( container, data );
        }
    }
}



//----------[ ▼ 요소를 클릭할 때 이벤트를 설정합니다. ]----------
async function handlerClick( event ) {
    const target = event.currentTarget;

    //이메일 전송 버튼을 클릭할 때
    if( target.closest('.form__input-box--email') ) {
        const container = target.closest('.form__field--email');
        const sendButton = target;
        const emailInput = document.querySelector('.form__input-box--email .form__input');

        //에러 UI 삭제
        removeClassName( ["has-error"] );
        removeErrorBorder();
        removeMessage( false, true );


        //이메일 형식이 맞지 않을 때
        let data = await validEmail(emailInput);
        if( !data.success ) {
            emailInput.classList.add('form__input--error');
            container.classList.add('has-error');

            renderMessage( container, data );

            status.email = false;
            return;
        }

        data = await fetchEmailCodeSend( emailInput.value.trim() );
        if( data.success ) {
            //입력창과 버튼 비활성화(중복 전송 방지)
            toggleEmailSend(false);
        }else {
            renderMessage({ container, data });
        }
    }


    //이용약관 체크박스를 클릭할 때
    if( target.closest('.form__field--agree') ) {
        if( target.checked ) {
            status.agree = true;
        }else {
            status.agree = false;
        }
    }


    //회원가입 버튼을 클릭할 때
    if( target.classList.contains('button--submit') ) {
        const valid = Object.values(status).every(s => s === true);

       if( !valid ) {
           alert('회원가입에 필요한 정보를 입력하지 않았습니다. 다시 확인해주세요!');

           showErrorBorder();
           return;
       }

       document.querySelector('.form').submit();
    }


    //취소버튼을 클릭할 때
    if( target.classList.contains('button--cancel') ) {
        if(confirm('가입을 취소하시겠습니까?')) {
           goToPage('/');
       }
    }
}



//----------[ ▼ 인증코드를 입력할 input 생성 후 페이지에 보여줍니다. ]----------
function renderEmailCodeInput() {
    //인증코드 입력창 생성
    const codeBox = createDiv({ classList: ['form__input-box', 'form__input-box--code'] });
    const codeInput = createTextInput({ parent: codeBox, classList: ['form__input', 'form__input--text'], placeHolder: '인증코드를 입력해주세요.' });
    codeInput.setAttribute('maxlength', 6);

    //이벤트 할당
    codeInput.addEventListener( 'focus', handlerFocus );
    codeInput.addEventListener( 'blur', handlerBlur );

    //타이머 생성 및 시작
    const timer = createSpan({ parent: codeBox, classList: ['timer'] });
    EmailTimer.reset();
    EmailTimer.start( timer );

    return codeBox;
}



//----------[ ▼ 인증코드 인증에 필요한 데이터값을 가져옵니다. ]----------
function getValuesByEmailCode( container ) {
    //이메일
    let emailValue = container.querySelector('input[type="text"][name="email"]')?.value.trim();
    if( !emailValue ) emailValue = '';

    let emailCode = container.querySelector('.form__input-box--code input')?.value;
    if( !emailCode ) emailCode = '';

    return {
        email : emailValue,
        code: emailCode,
        time: {
            minute: EmailTimer.getMinute(),
            second: EmailTimer.getSecond()
        }
    }
}



//----------[ ▼ 이메일 입력 및 전송버튼을 활성화/비활성화로 변경 합니다. ]----------
function toggleEmailSend( isSend = false  ) {
    const container = document.querySelector('.form__field--email .form__input-container');
    const sendButton = container.querySelector('.form__input-box--email .button');
    const emailInput = container.querySelector('.form__input-box--email .form__input');

    //활성화 변경
    if( isSend ) {
        document.querySelector('.form__field--email').classList.remove('form__field--code');

        sendButton.disabled = false;
        emailInput.disabled = false;

        sendButton.classList.remove('button--disabled');

        //인증코드 입력창 삭제
        document.querySelector('.form__input-box--code')?.remove();
    }else {
        document.querySelector('.form__field--email').classList.add('form__field--code');

        sendButton.disabled = true;
        emailInput.disabled = true;

        sendButton.classList.add('button--disabled');

        //인증코드 입력창 생성
        container.appendChild( renderEmailCodeInput() );
    }
}



//----------[ ▼ 검증을 완료하지 않은 요소에 에러 테두리를 표시 합니다. ]----------
function showErrorBorder() {
    const inputs = document.querySelectorAll('.form__input');

    inputs.forEach( input => {
        const name = input.name;
        const isValid = status[name];

        if( isValid ) {
            input.classList.remove('form__input--error');
        }else {
            input.classList.add('form__input--error');
        }
    });
}