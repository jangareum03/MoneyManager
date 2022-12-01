//아이디값 체크
function idCheck() {
    let id = document.getElementById('inputId').value;
    let msg = document.getElementById('idMsg');
    let result = document.getElementById('idResult');
    const pattern = /[^a-zA-Z0-9]/g;

    msg.style.color = '#D71E1E';
    result.value = 'n';

    if( id == '' ) {
        msg.innerHTML = '';
    }else if( id.length < 5 || pattern.test(id) ) {
        msg.innerHTML = '5 ~ 20자리의 영문자와 숫자만 입력해야 합니다.';
    }else{
        let xhr = new XMLHttpRequest();

        xhr.open('POST', '/join/idCheck', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send("id=" + id);

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                if( JSON.parse(xhr.response) == 0 ) {
                    msg.style.color = '#2A8D2A';
                    msg.innerHTML = '사용 가능한 아이디입니다.';
                    result.value = 'y';
                }else{
                    msg.innerHTML = '이미 사용중인 아이디입니다.';
                }
            }else{
                alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }
}


//비밀번호값 체크
function pwdCheck() {
    let pwd = document.getElementById('inputPwd').value;
    let msg = document.getElementById('pwdMsg');
    let result = document.getElementById('pwdResult');
    const pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$!%*#^])[A-Za-z\d$!%*#^]{8,20}$/;

    msg.style.color ='#D71E1E';
    result.value = 'n';

    if( pattern.test(pwd) ) {
        msg.innerHTML = '';
        result.value = 'y';
    }else if( pwd == '' ) {
        msg.innerHTML = '';
    }else{
        msg.innerHTML = '8 ~ 20자리의 영문자, 숫자, 특수문자(!, ^, *, #, %)의 조합으로 입력해야 합니다.';
    }
}

//비밀번호일치 체크
function rePwdCheck(){
    let pwd = document.getElementById('inputPwd').value;
    let rePwd = document.getElementById('inputRePwd').value;
    let msg = document.getElementById('rePwdMsg');
    let result = document.getElementById('rePwdResult');
    const pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$!%*#^])[A-Za-z\d$!%*#^]{8,20}$/;

    msg.style.color = '#D71E1E';
    result.value = 'n';

    if( rePwd == '' || (pattern.test(rePwd) && pwd == rePwd) ) {
        msg.innerHTML = '';

        if( pwd == rePwd ) {
            result.value = 'y';
        }
    }else{
        msg.innerHTML = '8 ~ 20자리의 영문자, 숫자, 특수문자(!, ^, *, #, %)의 조합으로 입력해야 합니다';

        if( pwd != '' && (pwd != rePwd) ) {
            msg.innerHTML = '비밀번호와 일치하지 않습니다.';
        }else if( pwd == '' ) {
            msg.innerHTML = '비밀번호를 먼저 입력해주세요.';
        }
    }
}

//이름값 체크
function nameCheck() {
    let name = document.getElementById('inputName').value;
    let msg = document.getElementById('nameMsg');
    let result = document.getElementById('nameResult');
    const pattern = /[^a-zA-Zㄱ-ㅎ가-힣]/g;

    msg.style.color = '#D71E1E';
    result.value = 'n';

    if( name == '' ) {
        msg.innerHTML = '';
    }else if( name.length < 3 || pattern.test(name) ) {
        msg.innerHTML = '3글자 이상의 영문자와 한글만 입력해야 합니다.';
    }else{
        msg.innerHTML = '';
        result.value = 'y';
    }
}

//닉네임값 체크
function nickNameCheck() {
    let nickName = document.getElementById('inputNickName').value;
    let msg = document.getElementById('nickNameMsg');
    let result = document.getElementById('nickNameResult');
    const pattern = /[^a-zA-Z0-9ㄱ-ㅎ가-힣]/g;

    msg.style.color = '#D71E1E';
    result.value = 'n';

    if( nickName == '' ) {
        msg.innerHTML = '';
    }else if( nickName.length < 2 || pattern.test(nickName) ) {
        msg.innerHTML = '2 ~ 10자리의 한글, 영문자, 숫자만 입력해야 합니다.';
    }else{
        const xhr = new XMLHttpRequest();
        xhr.open('POST', '/join/nickNameCheck', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send('nickName=' + nickName);

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                if( xhr.response == 0 ) {
                    msg.style.color = '#2A8D2A';
                    msg.innerHTML = '사용 가능한 닉네임입니다.';
                    result.value = 'y';
                }else{
                    msg.innerHTML = '이미 사용중인 닉네임입니다.';
                }
            }else{
                alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }
}

//이메일값 체크 후 코드 전송
function sendCode(){
    let email = document.getElementById('inputEmail').value;
    let msg = document.getElementById('emailMsg');
    const pattern = /^([\w\.\_\-])*[a-zA-Z0-9]+([\w\.\_\-])*([a-zA-Z0-9])+([\w\.\_\-])+@([a-zA-Z0-9]+\.)+[a-zA-Z0-9]{2,8}$/;

    msg.style.color = '#D71E1E';

    if( pattern.test(email) ) {
        document.getElementById('inputEmailCode').type= 'text';

        let xhr = new XMLHttpRequest();
        xhr.open('POST', '/join/sendEmail', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send('email=' + email);

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                msg.style.color = '#2A8D2A';
                msg.innerHTML = '작성한 이메일로 인증코드 전송했습니다.';

                document.getElementById('inputEmailCode').disabled = false;
                emailTimer();
            }else{
                alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }else if( email == '' )  {
        msg.innerHTML = '이메일을 입력해주세요.';
    }else{
        msg.innerHTML = '이메일 형식에 맞게 입력해야 합니다.';
    }
}

//타이머
let clock;
function emailTimer() {
    const Timer = document.getElementById('Timer');

        let time = 300000;
        let min = 5;
        let sec = 60;

        Timer.style.color = '#D71E1E';
        Timer.innerHTML ="0" + min + " : 00";

        time = time - 1000;
        min = time / (60*1000);

        clock = setInterval(function() {
            if( sec > 0 ) {
                    sec = sec - 1;
                    if( sec < 10) {
                        Timer.innerHTML = "0" + Math.floor(min) + " : 0" + sec;
                    }else{
                        Timer.innerHTML = "0" + Math.floor(min) + " : " + sec;
                    }
                }

                if( sec === 0 ) {
                    sec = 60;
                    Timer.innerHTML = "0" + Math.floor(min) + " : 00";
                }
        }, 1000);
}

//인증코드값 체크
function emailCodeCheck() {
    let email = document.getElementById('inputEmail').value;
    let code = document.getElementById('inputEmailCode').value;
    let msg = document.getElementById('emailMsg');
    let time = document.getElementById('Timer').innerHTML;

    document.getElementById('emailResult').value = 'n';

    if( code.length == 6 ) {
        let xhr = new XMLHttpRequest();

        xhr.open('POST', '/join/emailCodeCheck', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send('email=' + email +'&code=' + code +'&time=' + time);

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                if( xhr.response == 'yes' ){
                    msg.style.color = '#2A8D2A';
                    msg.innerHTML = '이메일 인증 완료했습니다.';

                    clearInterval(clock);
                    document.getElementById('inputEmailCode').disabled = true;
                    document.getElementById('emailResult').value = 'y';
                }else if( xhr.response == 'no' ){
                    msg.style.color = '#D71E1E';
                    msg.innerHTML =' 인증코드가 일치하지 않습니다. 다시 확인해주세요.';
                }else{
                    msg.style.color = '#D71E1E';
                    msg.innerHTML = '시간 초과되었습니다. 다시 전송해주세요.';
                }
            }else{
                alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }
}

//회원가입 체크
function joinCheck() {
    let id = document.getElementById('idResult').value;
    let pwd = document.getElementById('pwdResult').value;
    let rePwd = document.getElementById('rePwdResult').value;
    let name = document.getElementById('nameResult').value;
    let nickName = document.getElementById('nickNameResult').value;
    let email = document.getElementById('emailResult').value;
    let agree = document.getElementById('inputAgree').checked;

    if(  id == 'n' ){
        alert('이름을 확인해주세요.');
        document.getElementById('inputId').focus();
        return false;
    }else if( pwd == 'n' ) {
        alert('비밀번호를 확인해주세요.');
        document.getElementById('inputPwd').focus();
        return false;
    }else if( rePwd == 'n' ) {
        alert('비밀번호를 재확인해주세요.');
        document.getElementById('inputRePwd').focus();
        return false;
    }else if( name == 'n' ) {
        alert('이름을 확인해주세요.');
        document.getElementById('inputName').focus();
        return false;
    }else if( nickName == 'n' ) {
        alert('닉네임을 확인해주세요.');
        document.getElementById('inputNickName').focus();
        return false;
    }else if( email == 'n' ) {
        alert('이메일을 확인해주세요.');
        document.getElementById('inputEmail').focus();
        return false;
    }else if( !agree ) {
        alert('이용약관을 동의해주세요.');
        return false;
    }

    return true;
}