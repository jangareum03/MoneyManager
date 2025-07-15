const status = {
    name: false,
    birth: false,
    id: false
};

document.addEventListener('DOMContentLoaded', () => {
    initMenuTab();
    initInputText();
    initButton();
});



//----------[ ▼ 서브탭을 초기화합니다. ]----------
function initMenuTab() {
    const tabs = document.querySelectorAll('.recovery-menu__item');

    tabs.forEach( (tab) => {
        tab.addEventListener( 'click', handlerClick );
    });
}



//----------[ ▼ 입력창을 초기화합니다. ]----------
function initInputText() {
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

    const idInput = document.querySelector('input[type="text"][name="id"]');
    if( idInput ) {
        idInput.addEventListener( 'blur', handlerBlur );
        idInput.addEventListener( 'focus', handlerFocus );
    }
}



//----------[ ▼ 버튼을 초기화합니다. ]----------
function initButton() {
    const buttons = document.querySelectorAll('.button');
    if( buttons ) {
        buttons.forEach( button => {
            button.addEventListener( 'click', handlerClick );
        })
    }
}



//----------[ ▼ 요소를 클릭할 때 이벤트를 설정합니다. ]----------
function handlerClick( event ) {
    const target = event.currentTarget;

    //계정찾기 페이지의 서브탭 클릭했을 때
    if( target.matches('.recovery-menu__item') ) {
        const menu = target.dataset.menu;
        let url = '/recovery/id';

        if( menu === 'password' ) url = '/recovery/password';
        goToPage( url );
    }

    //계정찾기의 찾기 버튼을 클릭했을 때
    if( target.classList.contains('button--submit') ) {
        event.preventDefault();

        const formId = document.querySelector('[data-form="id"]')
        const formPassword = document.querySelector('[data-form="password"]')

        if( formId && status.name && status.birth ) {
            formId.submit();
            return;
        }

        if( formPassword && status.name && status.id ) {
            formPassword.submit();
            return;
        }

        alert('입력한 정보를 확인 후 다시 진행해주세요.');
        showErrorBorder();
    }

    //계정찾기의 로그인 버튼을 클릭할 때
     if( target.classList.contains('button--login') ) {
        goToPage('/');
     }

     //계정찾기의 회원가입 버튼을 클릭할 때
     if( target.classList.contains('button--signup') ) {
        goToPage('/signup')
     }
}



//----------[ ▼ 요소에 포커스가 될 때 이벤트를 설정합니다. ]----------
function handlerFocus( event ) {
    const target = event.currentTarget;

    //모든 has-error 클래스 제거
    removeClassName( ["has-error"] );

    removeErrorBorder();
    removeMessage( true, true );
}



//----------[ ▼ 요소에 포커스가 해지될 때 이벤트를 설정합니다. ]----------
async function handlerBlur( event ) {
    const target = event.currentTarget;
    let data;

    //이름 입력창에서 포커스가 벗어났을 때
    if( target.closest('.form__field--name') ) {
        data = await validName( target );

        status.name = data.success;

        if( !status.name ) {
            target.classList.add('form__input--error');
            target.closest('.form__field--name').classList.add('has-error');

            renderMessage( target.parentNode, data );
        }
    }


    //생년월일 입력창에서 포커스가 벗어났을 때
    if( target.closest('.form__field--birth') ) {
        data = await validBirth( target );

        status.birth = data.success;

        if( !status.birth ) {
            target.classList.add('form__input--error');
            target.closest('.form__field--birth').classList.add('has-error');

            renderMessage( target.parentNode, data );
        }
    }


    //아이디 입력창에서 포커스가 벗어났을 때
    if( target.closest('.form__field--id') ) {
        data = await validIdFormat( target );

        status.id = data.success;

        if( !status.id ) {
            target.classList.add('form__input--error');
            target.closest('.form__field--id').classList.add('has-error');

            renderMessage( target.parentNode, data );
        }
    }
}