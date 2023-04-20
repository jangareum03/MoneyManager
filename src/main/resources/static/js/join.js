/*
    이름                          |                                 설명
    ---------------------------------------------------
    idCheck                   |         입력한 아이디로 회원가입 가능여부 확인
    pwdCheck               |         입력한 비밀번호로 회원가입 가능여부 확인
    rePwdCheck           |         입력한 비밀번호 재확인
    nameCheck            |         이름 형식 및 길이 확인
    sendCode               |         이메일 형식 확인 및 인증코드 보내기
    emailTimer             |         인증코드 입력 가능시간 확인
    emailCodeCheck    |         보낸 인증코드와 일치하게 입력했는지 확인
    joinCheck              |         미입력한 정보 확인 및 회원가입 진행
*/

let msg = document.getElementsByClassName('msg__error');
let isId, isPwd, isRePwd, isName, isNickName, isEmail = false;

function idCheck( id ) {
    const pattern = /[^a-zA-Z0-9]/g;

    isId = false;
    if( id.value == '' ) {
        msg[0].innerHTML = '';
    }else if( id.value.length < 5 || pattern.test(id.value) ) {
        msg[0].innerHTML = '5 ~ 20자리의 영문자와 숫자만 입력해야 합니다.';
    }else{
        let xhr = new XMLHttpRequest();

        xhr.open('POST', '/members/join/check-id', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send("id=" + id.value);

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                if( xhr.response == 0 ) {
                    msg[0].style.color = getComputedStyle( document.documentElement ).getPropertyValue("--color-basic-success");
                    msg[0].innerHTML = '사용 가능한 아이디입니다.';
                    isId = true;
                }else{
                    msg[0].innerHTML = '이미 사용중인 아이디입니다.';
                }
            }else{
                alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }
}

function pwdCheck( pwd ) {
    const pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$!%*#^])[A-Za-z\d$!%*#^]{8,20}$/;

    isPwd = false;
    if( pattern.test(pwd.value) ) {
        msg[1].innerHTML = '';
        isPwd = true;
    }else if( pwd.value == '' ) {
        msg[1].innerHTML = '';
    }else{
        msg[1].innerHTML = '8 ~ 20자리의 영문자, 숫자, 특수문자(!, ^, *, #, %)의 조합으로 입력해야 합니다.';
    }
}

function rePwdCheck( rePwd ){
    let pwd = document.getElementsByClassName('input')[1].value;
    const pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$!%*#^])[A-Za-z\d$!%*#^]{8,20}$/;

    isRePwd = false;
    if( rePwd.value == '' || (pattern.test(rePwd.value) && pwd == rePwd.value) ) {
        msg[2].innerHTML = '';

        if( pwd == rePwd.value ) {
            isRePwd = true;
        }
    }else{
        msg[2].innerHTML = '8 ~ 20자리의 영문자, 숫자, 특수문자(!, ^, *, #, %)의 조합으로 입력해야 합니다';

        if( pwd != '' && (pwd != rePwd.value) ) {
            msg[2].innerHTML = '비밀번호와 일치하지 않습니다.';
        }else if( pwd == '' ) {
            msg[2].innerHTML = '비밀번호를 먼저 입력해주세요.';
        }
    }
}

function nameCheck( name ) {
    const pattern = /[^a-zA-Zㄱ-ㅎ가-힣]/g;

    isName = false;
    if( name.value == '' ) {
        msg[3].innerHTML = '';
    }else if( name.value.length < 2 || pattern.test(name.value) ) {
        msg[3].innerHTML = '2글자 이상의 영문자와 한글만 입력해야 합니다.';
    }else{
        msg[3].innerHTML = '';
        isName = true;
    }
}

function nickNameCheck( nickName ) {
    const pattern = /[^a-zA-Z0-9ㄱ-ㅎ가-힣]/g;
    msg[4].style.color = getComputedStyle( document.documentElement ).getPropertyValue("--color-basic-error");

    isNickName = false;
    if( nickName.value == '' ) {
        msg[4].innerHTML = '';
    }else if( nickName.value.length < 2 || pattern.test(nickName.value) ) {
        msg[4].innerHTML = '2 ~ 10자리의 한글, 영문자, 숫자만 입력해야 합니다.';
    }else{
        const xhr = new XMLHttpRequest();
        xhr.open('POST', '/members/join/check-nickname', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send('nickName=' + nickName.value);

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                if( xhr.response == 0 ) {
                    msg[4].style.color = getComputedStyle( document.documentElement ).getPropertyValue("--color-basic-success");
                    msg[4].innerHTML = '사용 가능한 닉네임입니다.';
                    isNickName = true;
                }else{
                    msg[4].innerHTML = '이미 사용중인 닉네임입니다.';
                }
            }else{
                alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }
}

function sendCode(){
    let email = document.getElementsByClassName('input__email')[0].value;
    const pattern = /^([\w\.\_\-])*[a-zA-Z0-9]+([\w\.\_\-])*([a-zA-Z0-9])+([\w\.\_\-])+@([a-zA-Z0-9]+\.)+[a-zA-Z0-9]{2,8}$/;

    document.getElementsByClassName('join__item')[6].style.display = 'none';

    if( pattern.test(email) ) {
        let xhr = new XMLHttpRequest();
        xhr.open('POST', '/members/join/send-email', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send('email=' + email);

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                if( xhr.response == 1 ) {
                    msg[6].style.color = getComputedStyle( document.documentElement ).getPropertyValue("--color-basic-success");
                    msg[6].innerHTML = '작성한 이메일로 인증코드 전송했습니다.';
                    msg[5].innerHTML = '';

                    document.getElementsByClassName('join__item')[6].style.display = 'block';
                    emailTimer();
                }
            }else{
                alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }else if( email == '' )  {
        msg[5].innerHTML = '이메일을 입력해주세요.';
    }else{
        msg[5].innerHTML = '이메일 형식에 맞게 입력해야 합니다.';
    }
}

let clock;
function emailTimer() {
    const Timer = document.getElementsByClassName('msg__timer')[0];

        let time =  300;
        let min = "";
        let sec = "";

        clock = setInterval(function() {
            min = parseInt( time/60 );
            sec = time%60;

            if( sec < 10 ) {
                Timer.innerHTML = min + ': 0' + sec;
            }else{
                Timer.innerHTML = min + ': ' + sec;
            }
            time--;

            if( time < 0 ) {
                clearInterval(clock);
                document.getElementsByClassName('input')[6].disabled = true;
                msg[6].style.color = getComputedStyle( document.documentElement ).getPropertyValue("--main-color-error");
                msg[6].innerHTML = '시간 초과되었습니다. 다시 전송해주세요.';
            }
        }, 1000);
}

function emailCodeCheck( code ) {
    let email = document.getElementsByClassName('input__email')[0].value;
    let time = document.getElementsByClassName('msg__timer')[0].innerHTML;

    isEmail = false;
    if( code.value.length == 6 ) {
        let xhr = new XMLHttpRequest();

        xhr.open('POST', '/members/join/check-email-code', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send('email=' + email +'&code=' + code.value +'&time=' + time);

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                if( xhr.response == 'yes' ){
                    msg[5].style.color = getComputedStyle( document.documentElement ).getPropertyValue("--color-basic-success");
                    msg[5].innerHTML = '이메일 인증 완료했습니다.';
                    msg[6].innerHTML = '';

                    clearInterval(clock);
                    document.getElementsByClassName('join__item')[6].style.display = 'none';
                    isEmail = true;
                }else if( xhr.response == 'no' ){
                    msg[6].style.color = getComputedStyle( document.documentElement ).getPropertyValue("--color-basic-error");
                    msg[6].innerHTML =' 인증코드가 일치하지 않습니다. 다시 확인해주세요.';
                }
            }else{
                alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }
}

function joinCheck() {
    let agree = document.getElementsByClassName('checkbox')[0].checked;

    if( !isId ) {
        alert('아이디를 확인해주세요.');
        document.getElementsByClassName('input')[0].focus();
        return false;
    }else if( !isPwd ) {
        alert('비밀번호를 확인해주세요.');
        document.getElementsByClassName('input')[1].focus();
        return false;
    }else if( !isRePwd ) {
        alert('비밀번호를 재확인해주세요.');
        document.getElementsByClassName('input')[2].focus();
        return false;
    }else if( !isName ) {
        alert('이름을 확인해주세요.');
        document.getElementsByClassName('input')[3].focus();
        return false;
    }else if( !isNickName ) {
        alert('닉네임을 확인해주세요.');
        document.getElementsByClassName('input')[4].focus();
        return false;
    }else if( !isEmail ) {
        alert('이메일을 확인해주세요.');
        document.getElementsByClassName('input')[5].focus();
        return false;
    }else if( !agree ) {
        alert('이용약관을 동의해주세요.');
        return false;
    }else{
        document.getElementsByClassName('join__form')[0].submit();
    }
}