const status = {
    profile: false,
    name: false,
    gender: false,
    email: false,
    password: false
};

document.addEventListener('DOMContentLoaded', () => {
    initImage();
    initIcon();
});



window.addEventListener( 'authTimerEnd', () => {
    document.querySelector('.info__input-box--code').remove();

    //에러메시지 변경
    removeMessage();

    const container = document.querySelector('.info-item__email--edit dd');
    createSpan({ parent: container, classList: ['message--error'], text: '시간 초과되었습니다. 다시 진행해주세요.' });

    document.querySelector('.info__input-box--email .form__input--text').disabled = false;
    document.querySelector('.info__input-box--email .button--default').disabled = false;
    document.querySelector('.info__input-box--email .button--disabled').classList.remove('button--disabled');

    status.email = false;
});



//----------[ ▼ 현재 수정모드인지 확인합니다. ]----------
function isEditMode( target ) {
    const statusSelector = {
        profile: '.info-profile',
        name: '.info-item__name',
        gender: '.info-item__gender',
        email: '.info-item__email',
        password: '.info-item__password'
    };

    if( !event ) return Object.values(status).some(s => s === true);

    //현재 수정 중인 요소
    const editElement = Object.entries(status).find( ([key, value]) => value == true );
    if( !editElement ) return false;

    const [key] = editElement;
    const selectedElement = statusSelector[key];
    if( !selectedElement ) return true;

    const editContainer = target.closest(selectedElement);

    return editContainer ? false : true;
}



//----------[ ▼ 이미지를 초기화합니다. ]----------
function initImage() {
    const image = document.querySelector('.info-profile__image');

    if( image ) {
        image.addEventListener('click', handlerClick );
    }
}



//----------[ ▼ 아이콘을 초기화합니다. ]----------
function initIcon() {
    const icons = document.querySelectorAll('.icon--edit');
    icons.forEach( icon => {
        icon.addEventListener( 'click', handlerClick );
    })
}



//----------[ ▼ 요소를 클릭할 때 이벤트를 설정합니다. ]----------
function handlerClick( event ) {
    const target = event.currentTarget;
    const container =  target.parentNode;
    let box;

    //현재 수정중이면 다른 요소 수정 불가
    if( isEditMode( target ) ) {
        alert('수정 중인 항목이 있습니다. 저장 후 다시 시도해주세요.');
        return;
    }

    //프로필 이미지를 클릭할 때
    if( container.classList.contains('info-profile') ) {
        renderImageFile(container);
        return;
    }

    //이름 수정 아이콘 클릭할 때
    box = container.closest('.info-item__name');
    if( box ) {
        box.classList.add('info-item__name--edit');
        renderName();
    }

    //성별 수정 아이콘 클릭할 때
    box = container.closest('.info-item__gender');
    if( box ) {
        box.classList.add('info-item__gender--edit');
        renderGender();
    }

    //이메일 수정 아이콘 클릭할 때
    box = container.closest('.info-item__email');
    if( box ) {
        box.classList.add('info-item__email--edit');
        renderEmail();
    }

    //이메일 수정 아이콘 클릭할 때
    box = container.closest('.info-item__password');
    if( box ) {
        box.classList.add('info-item__password--edit');
        renderCurrentPassword();
    }
}



//----------[ ▼ 이미지 저장할 수 있는 파일과 버튼을 생성합니다. ]----------
function renderImageFile( container ) {
    const originalInputFile = container.querySelector('.info-profile__image');
    const originalFileSrc = originalInputFile.src;

    //input file 중복방지 위해 확인
    let inputFile = container.querySelector('.form__input--file');
    if( !inputFile ) {
        inputFile = createFile({ parent: container, accept: '.gif, .jpg, .png', classList: ['form__input', 'form__input--file'] });
        inputFile.addEventListener('change', previewImage );

        //버튼 생성
        const buttonDiv = document.createElement('div');
        const submitButton = createButton({ parent: buttonDiv, type: 'button', classList: ['button', 'button--nano', 'button--default'], text: '저장' });
        const cancelButton = createButton({ parent: buttonDiv, type: 'button', classList: ['button', 'button--nano', 'button--cancel'], text: '취소' });

        //기존이미지 확인 후 삭제버튼 추가
        if( !isDefaultProfile( originalFileSrc ) ) {
            const trashIcon = createSVG({ parent: container, link: 'http://www.w3.org/2000/svg', viewBox: '0 0 16 16', paths: [{ d: 'M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5m-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5M4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06m6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528M8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5' }], classList: ['icon', 'icon--del'] });
            trashIcon.addEventListener('click', changeDefaultProfile );
        }

        //이벤트 설정
        submitButton.addEventListener('click', () => { updateInfo('profile', container) });
        cancelButton.addEventListener('click', () => { renderResetImage(container, originalFileSrc) });

        container.appendChild(buttonDiv);
    }

    status.profile = true;
    container.classList.add('info-profile--edit');

    inputFile.click();
}



//----------[ ▼ 기본 프로필인지 확인합니다. ]----------
function isDefaultProfile( originalFileSrc ) {
    if( originalFileSrc.indexOf('/default/') === -1 ) {
        return false;
    }

    return true;
}



//----------[ ▼ 기본 프로필로 변경합니다. ]----------
function changeDefaultProfile() {
    const image = document.querySelector('.info-profile__image');
    image.src = '/image/default/profile.png';

    document.querySelector('.info-profile .form__input--file').src = '';
}



//----------[ ▼ 선택한 사진으로 미리보기 합니다. ]----------
function previewImage( event ) {
    const inputFile = event.target;

    if( inputFile.files && inputFile.files[0] ) {
        const file = inputFile.files[0];

        //새로운 이미지로 변경
        const image = inputFile.parentNode.querySelector('.info-profile__image');
        if( image ) {
            image.src = URL.createObjectURL(file);
        }
    }
}



//----------[ ▼ 프로필정보를 초기화합니다. ]----------
function renderResetImage( container, src ) {
    //이미지를 원래대로 변경
    const img = container.querySelector('.info-profile__image');
    img.src = src;

    //클래스명 및 버튼 삭제
    removeProfileButton();
    status.profile = false;
}



//----------[ ▼ 프로필정보에서 버튼을 삭제합니다. ]----------
function removeProfileButton() {
    const container = document.querySelector('.info-profile');

    container?.classList.remove('info-profile--edit');
    container?.querySelector('.form__input--file')?.remove();
    container?.querySelector('.icon--del')?.remove();
    container?.querySelector('div')?.remove();
}




//----------[ ▼ 이름을 수정할 수 있는 입력박스와 버튼을 생성합니다.  ]----------
function renderName() {
    const container = document.querySelector('.info-item__name dd');
    status.name = true;

    //기존 자식태그 삭제
    const value = container.querySelector('span').textContent;
    container.replaceChildren();

    //input box 생성
    const inputBox = createDiv({ classList: ['info__input-box'] });

    //input 생성
    createTextInput({ parent: inputBox, classList: ['form__input', 'form__input--text'], value: value, placeHolder: '이름을 입력해주세요.' });

    //버튼 생성
    const buttonDiv = document.createElement('div');
    const submitButton = createButton({ parent: buttonDiv, type: 'button', classList: ['button', 'button--small', 'button--default'], text: '저장' });
    const cancelButton = createButton({ parent: buttonDiv, type: 'button', classList: ['button', 'button--small', 'button--cancel'], text: '취소' });

    //이벤트 설정
    cancelButton.addEventListener('click', () => { renderResetValue( container, value, 'name' ) });
    submitButton.addEventListener('click', () => { checkNameValue( container, value ) });
    inputBox.appendChild(buttonDiv);

    container.appendChild(inputBox);
}



//----------[ ▼ 입력한 값을 검증합니다.  ]----------
async function checkNameValue( container, beforeValue ) {
    let after = container.querySelector('.form__input--text');
    const mode = 'name';

    container.querySelector('.message--error')?.remove();

    if( beforeValue === after.value ) {
        createSpan({ parent: container, classList: ['message--error'], text: '기존이름과 동일합니다. 다른 이름으로 입력해주세요.' });
        return;
    }

    const apiResult = await validName( after );
    if( apiResult.success ) {
        updateInfo( mode, container );
    }else {
        createSpan({ parent: container, classList: ['message--error'], text: apiResult.message });
    }
}



//----------[ ▼ 성별을 수정할 수 있는 라디오버튼과 버튼을 생성합니다.  ]----------
function renderGender( ) {
    const container = document.querySelector('.info-item__gender dd');
    status.gender = true;

    //기존 자식요소 삭제
    const value = container.querySelector('span').textContent;
    container.replaceChildren();

    //radio box 생성
    const radioBox = createDiv({ classList: ['info__input-box'] })

    //라디오버튼 생성
    const options = [
        {label: '선택없음', value: 'N'},
        {label: '남자', value: 'M'},
        {label: '여자', value: 'F'}
    ];

    options.forEach( option => {
        const label = createLabel({ parent: radioBox, classList: [] });
        const radio = createRadio({ parent: label, name: 'gender', classList: ['form__input', 'form__input--radio'], label: option.label, value: option.value });

        //기존 성별값과 동일한 라디오 버튼 선택되게 설정
        if( label.textContent.trim() === value.trim() ) {
            radio.checked = true;
        }
    });

    //버튼 생성
    const buttonDiv = document.createElement('div');
    const submitButton = createButton({ parent: buttonDiv, type: 'button', classList: ['button', 'button--small', 'button--default'], text: '저장' });
    const cancelButton = createButton({ parent: buttonDiv, type: 'button', classList: ['button', 'button--small', 'button--cancel'], text: '취소' });

    //이벤트 설정
    cancelButton.addEventListener('click', () => { renderResetValue( container, value, 'gender' ) });
    submitButton.addEventListener('click', () => { updateInfo( 'gender', container ) });
    radioBox.appendChild(buttonDiv);

    container.appendChild(radioBox);
}



//----------[ ▼ 이메일을 수정할 수 있는 입력박스와 버튼을 생성합니다.  ]----------
function renderEmail() {
    const container = document.querySelector('.info-item__email dd');
    status.email = true;

    //기존 자식요소 삭제
    const value = container.querySelector('span').textContent;
    container.replaceChildren();

    //input box 생성
    const inputBox = createDiv({ classList: ['info__input-box', 'info__input-box--email'] });

    //input 생성
    createTextInput({ parent: inputBox, classList: ['form__input', 'form__input--text'], value: value, placeHolder: '이메일을 입력해주세요.' });

    //버튼 생성
    const buttonDiv = document.createElement('div');
    const submitButton = createButton({ parent: buttonDiv, type: 'button', classList: ['button', 'button--small', 'button--default'], text: '전송' });
    const cancelButton = createButton({ parent: buttonDiv, type: 'button', classList: ['button', 'button--small', 'button--cancel'], text: '취소' });

    //이벤트 설정
    cancelButton.addEventListener('click', () => { renderResetValue( container, value, 'email' ) });
    submitButton.addEventListener('click', () => { checkEmailValue( container, value ) });
    inputBox.appendChild(buttonDiv);

    container.appendChild(inputBox);
}



//----------[ ▼ 이메일 값을 검증합니다.  ]----------
async function checkEmailValue( container, beforeValue ) {
    let after = document.querySelector('.info__input-box--email .form__input--text');
    const mode = 'email';

    container.querySelector('.message--error')?.remove();

    if( beforeValue === after.value ) {
        createSpan({ parent: container, classList: ['message--error'], text: '기존 이메일과 동일합니다. 다른 이메일을 입력해주세요.' });
        return;
    }

    const emailCheck = await validEmail( after );
    if( emailCheck.success ) {
        sendEmailCode( container, mode, after.value );
    }else {
        createSpan({ parent: container, classList: ['message--error'], text: emailCheck.message });
    }
}



//----------[ ▼ 이메일로 인증코드를 전송합니다. ]----------
async function sendEmailCode( container, mode, email ) {
    const sendEmail = await fetchEmailCodeSend( email );

    if( sendEmail.success ) {
        createSpan({ parent: container, classList: ['message--success'], text: sendEmail.message });
        renderEmailCode(container);
    }else {
        createSpan({ parent: container, classList: ['message--error'], text: sendEmail.message });
    }
}



//----------[ ▼ 인증코드를 입력할 수 있는 입력박스와 버튼을 생성합니다.  ]----------
function renderEmailCode( container ) {
    //이메일 입력창과 전송버튼 비활성화
    container.querySelector('.info__input-box--email .form__input--text').disabled = true;
    container.querySelector('.info__input-box--email .button--default').disabled = true;
    container.querySelector('.info__input-box--email .button--default').classList.add('button--disabled');

    //input box 생성
    const inputBox = createDiv({ classList: ['info__input-box', 'info__input-box--code'] });

    //input 생성
    const codeInput = createTextInput({ parent: inputBox, classList: ['form__input', 'form__input--text'], placeHolder: '인증번호 6자리를 입력해주세요.' });
    codeInput.addEventListener( 'blur', () => { checkEmailCode(container) });

    //타이머 생성 및 시작
    const timer = createSpan({ parent: inputBox, classList: ['timer'] });
    EmailTimer.reset();
    EmailTimer.start( timer );

    container.appendChild(inputBox);
}



//----------[ ▼ 인증코드를 확인합니다. ]----------
async function checkEmailCode( container ) {
    const emailValue = document.querySelector('.info__input-box--email .form__input--text').value;
    const codeValue = document.querySelector('.info__input-box--code .form__input--text').value;

    //기존 에러메시지 삭제
    removeMessage( false, true );

    //전송할 데이터
    const data = {
        email: emailValue,
        code: codeValue,
        time: {
            minute: EmailTimer.getMinute(),
            second: EmailTimer.getSecond()
        }
    };

    const codeCheck = await validEmailCode( data );
    if( codeCheck.success ) {
        updateInfo( 'email', container );
    }else {
        removeMessage();
        createSpan({ parent: container, classList: ['message--error'], text: codeCheck.message });
    }
}



//----------[ ▼ 현재 비밀번호를 입력할 수 있는 입력박스와 버튼을 생성합니다.  ]----------
function renderCurrentPassword() {
    const container = document.querySelector('.info-item__password dd');
    status.password = true;

    //기존 자식태그 삭제
    container.replaceChildren();

    //input box 생성
    const inputBox = createDiv({ classList: ['info__input-box'] });

    //input 생성
    createPasswordInput({ parent: inputBox, classList: ['form__input', 'form__input--password'], placeHolder: '현재 비밀번호를 입력해주세요.' });

    //버튼 생성
    const buttonDiv = document.createElement('div');
    const submitButton = createButton({ parent: buttonDiv, type: 'button', classList: ['button', 'button--small', 'button--default'], text: '확인' });
        const cancelButton = createButton({ parent: buttonDiv, type: 'button', classList: ['button', 'button--small', 'button--cancel'], text: '취소' });

    //이벤트 설정
    cancelButton.addEventListener('click', () => { renderResetValue( container, '**********', 'password' ) });
    submitButton.addEventListener('click', () => { checkCurrentPasswordValue( container ) });
    inputBox.appendChild(buttonDiv);

    container.appendChild(inputBox);
}



//----------[ ▼ 입력한 현재 비밀번호 값을 검증합니다.  ]----------
async function checkCurrentPasswordValue( container ) {
    let password = container.querySelector('.form__input--password');
    const mode = 'password';

    //에러메시지 삭제
    container.querySelector('.message--error')?.remove();

    const checkPassword = await validPassword( password );
    if( checkPassword.success ) {
        renderNewPassword( container, mode );
    }else {
        createSpan({ parent: container, classList: ['message--error'], text: checkPassword.message });
    }
}



//----------[ ▼ 새 비밀번호를 입력할 수 있는 입력박스와 버튼을 생성합니다.  ]----------
function renderNewPassword( container, mode ) {
    //하위 자식요소 삭제
    container.replaceChildren();

    //새비밀번호 input box 생성
    const newInputBox = createDiv({ classList: ['info__input-box', 'info__input-box--password'] });

    //새 비밀번호 input과 button 생성
    createPasswordInput({  parent: newInputBox, classList: ['form__input', 'form__input--password'], placeHolder: '새 비밀번호를 입력해주세요.'});

    const buttonDiv = document.createElement('div');
    const submitButton = createButton({ parent: buttonDiv, type: 'button', classList: ['button', 'button--small', 'button--default'], text: '저장' });
    const cancelButton = createButton({ parent: buttonDiv, type: 'button', classList: ['button', 'button--small', 'button--cancel'], text: '취소' });

    //이벤트 설정
    cancelButton.addEventListener('click', () => { renderResetValue( container, '**********', 'password' ) });
    submitButton.addEventListener('click', () => { checkNewPasswordValue( container, mode ) });
    newInputBox.appendChild(buttonDiv);

    //새 비밀번호 재확인 input box 생성
    const reInputBox = createDiv({ classList: ['info__input-box', 'info__input-box--rePassword'] });

    //새 비밀번호 재확인 input 생성
    createPasswordInput({  parent: reInputBox, classList: ['form__input', 'form__input--password'], placeHolder: '새 비밀번호를 재입력해주세요.'});

    container.appendChild(newInputBox);
    container.appendChild(reInputBox);
}



//----------[ ▼ 입력한 새 비밀번호 값을 검증합니다.  ]----------
async function checkNewPasswordValue( container, mode ) {
    //에러메시지 삭제
    removeMessage( false, true );

    const newPassword = container.querySelector('.info__input-box--password .form__input--password');

    const validPassword = await validPasswordFormat( newPassword );
    if( validPassword.success ) {
        checkReNewPasswordValue( container, mode );
    }else {
        createSpan({ parent: container, classList: ['message--error'], text: validPassword.message});

        newPassword.value = (newPassword.value === ' ') ? '' : newPassword.value;

        newPassword.classList.add('form__input--error');
        newPassword.focus();
    }
}



//----------[ ▼ 입력한 새 비밀번호 값을 다시 검증합니다.  ]----------
function checkReNewPasswordValue( container, mode ) {
    //에러메시지 삭제
    removeMessage( false, true );

    const newPassword = container.querySelector('.info__input-box--password .form__input--password');
    const reNewPassword = container.querySelector('.info__input-box--rePassword .form__input--password');

    if( !newPassword.value ) {
        createSpan({ parent: container, classList: ['message--error'], text: '새 비밀번호를 먼저 입력해주세요.'});
        newPassword.classList.add('form__input--error');

        newPassword.focus();
        return;
    }else if( !reNewPassword.value ) {
        createSpan({ parent: container, classList: ['message--error'], text: '새 비밀번호를 다시 한번 입력해주세요.'});
        reNewPassword.classList.add('form__input--error');
        newPassword.classList.remove('form__input--error');

        reNewPassword.focus();
        return;
    }else if( newPassword.value != reNewPassword.value ) {
        createSpan({ parent: container, classList: ['message--error'], text: '새 비밀번호와 일치하지 않습니다. 다시 확인해주세요.'});
        reNewPassword.classList.add('form__input--error');
        newPassword.classList.remove('form__input--error');

        reNewPassword.focus();
        return;
    }

    removeMessage( false, true );
    updateInfo( mode, container );
}



//----------[ ▼ 변경된 정보로 회원정보를 변경합니다. ]----------
async function updateInfo( mode, container ) {
    switch( mode ) {
        case 'profile' :
            const inputFile = container.querySelector('.form__input--file');
            const formData = new FormData();

            if( !inputFile || !(inputFile.files && inputFile.files[0]) ) {
                formData.append( 'isReset', true );
                formData.append( mode, null );
            }else {
                formData.append( 'isReset', false );
                formData.append(mode, inputFile.files[0]);
            }

            const changeImage = await fetchProfileUpdate( formData );
            if( !changeImage.success ) {
                //변경 불가능한 경우
                alert( changeImage.message );
            }else {
                //변경된 이미지로 변경
                const image = document.querySelector('.info-profile__image');
                image.src = changeImage.message;

                //사이드바 프로필 이미지 변경
                const sideImage = document.querySelector('.sidebar-profile__image');
                sideImage.src = changeImage.message;

                //클래스명 및 버튼 삭제
                removeProfileButton( container );
            }

            status.profile = false;
        break;
        case 'name' :
            const inputName = container.querySelector('.form__input--text');

            const changeName = await fetchMemberUpdate( mode, inputName.value );
            if( !changeName.success ) {
                alert( changeName.message );
            }else {
                renderResetValue( container, changeName.message, mode );
            }

            status.name = false;
        break;
        case 'gender' :
            const inputRadio = container.querySelector('.form__input--radio:checked');

            const changeGender = await fetchMemberUpdate( mode, inputRadio.value );
            if( !changeGender.success ) {
                alert( changeGender.message );
            }else {
                renderResetValue( container, changeGender.message, mode );
            }

            status.gender= false;
        break;
        case 'email' :
            const inputEmail = container.querySelector('.form__input--text');

            const changeEmail = await fetchMemberUpdate( mode, inputEmail.value );
            if( !changeEmail.success ) {
                alert( changeEmail.message );
            }else {
                renderResetValue( container, changeEmail.message, mode );
            }

            status.email = false;
        break;
        case 'password' :
            const inputPassword = container.querySelector('.form__input--password');

            const changePassword = await fetchPasswordUpdate( inputPassword.value );
            if( changePassword.success ) {
                alert(changePassword.message);

                setTimeout( () => {
                    goToPage('/');
                })
            }else {
                createSpan({ parent: container, classList: ['message--error'], text: changePassword.message});
            }

        status.password = false;
        break;
    }
}



//----------[ ▼ 정보리스트 영역을 초기화합니다. ]----------
function renderResetValue( container, value, mode ) {
    //클래스 삭제
    if( mode === 'name' ) document.querySelector('.info-item__name')?.classList.remove('info-item__name--edit');
    if( mode === 'gender' ) document.querySelector('.info-item__gender')?.classList.remove('info-item__gender--edit');
    if( mode === 'email' ) document.querySelector('.info-item__email')?.classList.remove('info-item__email--edit');
    if( mode === 'password' ) document.querySelector('.info-item__password')?.classList.remove('info-item__password--edit');

    //입력창 및 버튼 삭제
    container.replaceChildren();

    for( let key in status ) {
        status[key] = false;
    }

    createSpan({ parent: container, text: value });
    container.appendChild( createEditButton( mode ) );
}



//----------[ ▼ 수정버튼을 생성 후 반환합니다. ]----------
function createEditButton( mode ) {
    const button = createButton({ type: 'button', classList: ['button'] });

    const icon = createSVG({ parent: button, link: 'http://www.w3.org/2000/svg', viewBox: '0 0 10 11', paths: [{ d: 'M9.83752 2.74552C10.0542 2.52888 10.0542 2.16782 9.83752 1.9623L8.5377 0.662477C8.33218 0.445841 7.97112 0.445841 7.75448 0.662477L6.7324 1.679L8.81544 3.76205M0 8.41696V10.5H2.08304L8.22664 4.35085L6.14359 2.26781L0 8.41696Z' }], classList: ['icon', 'icon--edit'] });
    icon.addEventListener('click', handlerClick );

    return button;
}