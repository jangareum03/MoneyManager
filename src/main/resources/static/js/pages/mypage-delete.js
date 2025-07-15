const status = {
    password : false,
    cause : false,
    etc : false,
    agree : false
}

document.addEventListener('DOMContentLoaded', () => {
    initInputPassword();
    initInputCheckbox();
    initSelect();
    initButton();
});



//----------[ ▼ 비밀번호창을 초기화합니다. ]----------
function initInputPassword() {
    const password = document.querySelector('.form__input--password');
    if( password ) {
        password.addEventListener( 'blur', handlerBlur );
    }
}


//----------[ ▼ 체크박스를 초기화합니다. ]----------
function initInputCheckbox() {
    const agreeBox = document.querySelector('.form__input--checkbox');
    if( agreeBox ) {
        agreeBox.addEventListener( 'click', handlerClick );
    }
}



//----------[ ▼ select 를 초기화합니다. ]----------
function initSelect() {
    const select = document.querySelector('.form__select');
    if( select ) {
        select.addEventListener( 'change', handlerChange );
    }
}



//----------[ ▼ 버튼을 초기화합니다. ]----------
function initButton() {
    const buttons = document.querySelectorAll('.button');
    buttons.forEach( button => {
        button.addEventListener( 'click', handlerClick );
    });
}



//----------[ ▼ 요소를 클릭할 때 이벤트를 설정합니다. ]----------
async function handlerClick( event ) {
    const target = event.currentTarget;

    //이용약관 체크박스를 클릭할 때
    if( target.closest('.form__field--agreement') ) {
        if( target.checked ) {
            status.agree = true;
        }else {
            status.agree = false;
        }
    }

    //탈퇴버튼을 클릭할 때
    if( target.classList.contains('button--danger') ) {
        const valid = Object.values(status).every(s => s === true);

        if( !valid ) {
            alert('회원탈퇴에 필요한 정보를 입력하지 않았습니다. 다시 확인해주세요.');
            return;
        }else {
            if( confirm('정말로 탈퇴하시겠습니까?') ) {
                const data = {
                    id : document.querySelector('.form__field--id span').textContent,
                    password : document.querySelector('.form__field--password .form__input--password').value,
                    code : document.querySelector('.form__field--cause .form__select').value,
                    cause : document.querySelector('.form__field--cause .form__input--text') ? document.querySelector('.form__field--cause .form__input--text').value : null
                };

                const memberDelete = await fetchMemberDelete( data );

                alert( memberDelete.message );
                if( memberDelete.success ) {
                    setTimeout( () => {
                        location.href = '/';
                    }, 100);
                }
            }
        }
    }
}


//----------[ ▼ 요소를 변경할 때 이벤트를 설정합니다. ]---------
async function handlerChange( event ) {
    const target = event.currentTarget;
    let container;

    //탈퇴사유 선택박스를 변경할 때
    container = target.closest('.form__field--cause');
    if( container ) {
        //기타사항 입력사항 삭제
        container.querySelector('.form__input--text')?.remove();

        //탈퇴사유 미선택인 경우
        if( target.value === '00' ) {
            status.cause = false;
            status.etc = false;

            return;
        }else if( target.value === '05' ) {
            //탈퇴사유 기타 선택한 경우
            const input = createTextInput({ parent: container, classList: ['form__input', 'form__input--text'], placeHolder: '탈퇴 이유를 입력해주세요.' });
            input.setAttribute('maxlength', 200);
            input.addEventListener( 'blur', handlerBlur );

            status.cause = true;
            status.etc = false;
        }else {
            status.cause = true;
            status.etc = true;
        }
    }
}



//----------[ ▼ 요소에 포커스가 해지될 때 이벤트를 설정합니다. ]----------
async function handlerBlur( event ) {
    const target = event.currentTarget;
    let container;

    //비밀번호 입력창에서 포커스가 벗어날 때
    container = target.closest('.form__field--password');
    if( container ) {
        status.password = false;

        //에러메시지 삭제
        removeMessage();

        const checkPassword = await validPassword( target );
        if( checkPassword.success ) {
            status.password = true;
        }else {
            createSpan({ parent: container, classList: ['message--error'], text: checkPassword.message });
            status.password = false;
        }
    }

    //기타사항 입력창에서 포커스가 벗어날 때
    container = target.closest('.form__field--cause');
    if( container ) {
        //에러메시지 삭제
        removeMessage();

        if( !target.value ) {
            status.etc = false;
        }else {
            status.etc = true;
        }
    }
}