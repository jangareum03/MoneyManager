/*
    이름                          |                                 설명
    ---------------------------------------------------
    nameCheck             |         이름 형식 확인
    emailCheck             |         이메일 형식 확인
    idCheck                   |         아이디 형식 확인
    helpId                      |         필요한 정보 확인 후 아이디 찾기 실행
    helpPwd                  |         필요한 정보 확인 후 비밀번호 변경 실행
*/


let msg = document.getElementsByClassName('msg__error');
let isName, isEmail, isId = false;

function nameCheck( name ) {
    const pattern = /[^a-zA-Zㄱ-ㅎ가-힣]/g;

    isName = false;
    if( name.value == '' ) {
        msg[0].innerHTML = '';
    }else if( name.value.length < 2 || pattern.test(name.value) ) {
        msg[0].innerHTML = '2글자 이상의 영문자와 한글만 입력해야 합니다.';
    }else{
        msg[0].innerHTML = '';
        isName = true;
    }
}

function emailCheck( email ) {
    const pattern = /^([\w\.\_\-])*[a-zA-Z0-9]+([\w\.\_\-])*([a-zA-Z0-9])+([\w\.\_\-])+@([a-zA-Z0-9]+\.)+[a-zA-Z0-9]{2,8}$/;

    isEmail =false;
    if( email.value == '' ) {
        msg[1].innerHTML = '';
    }else if( !pattern.test(email.value) ) {
        msg[1].innerHTML = '이메일 형식에 맞게 입력해야 합니다.';
    }else{
        isEmail = true;
    }
}

function idCheck( id ) {
    const pattern = /[^a-zA-Z0-9]/g;

    isId = false;
    if( id.value == '' ) {
        msg[1].innerHTML = '';
    }else if( id.value.length < 5 || pattern.test(id.value) ) {
        msg[1].innerHTML = '5 ~ 20자리의 영문자와 숫자만 입력해야 합니다.';
    }else{
        msg[1].innerHTML = '';
        isId = true;
    }
}

function helpId() {
    let name = document.getElementsByClassName('input')[0];
    let email = document.getElementsByClassName('input')[1];

    if( !isName ) {
        alert('이름을 확인해주세요.');
        name.focus();
        return false;
    }else if( !isEmail ) {
        alert('이메일을 확인해주세요.');
        email.focus();
        return false;
    }else{
        return true;
    }
}

function helpPwd() {
    let name = document.getElementsByClassName('input')[0];
    let id = document.getElementsByClassName('input')[1];

    if( !isName ) {
        alert('이름을 확인해주세요.');
        name.focus();
        return false;
    }else if( !isId ) {
        alert("아이디를 확인해주세요.");
        id.focus();
        return false;
    }else {
        return true;
    }
}