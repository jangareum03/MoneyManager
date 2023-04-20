let isChange = true;
let isPwd, isNewPwd, isRePwd = false;
let msg = document.getElementsByClassName('msg__error');

//변경할 정보 화면보이기
function changeUser(type) {
    let beforeInfo = document.getElementsByClassName('myInfo__div');
    let afterInfo = document.getElementsByClassName('myInfo__form');

    if( isChange ) {
        switch( type ) {
            case 'name' :
                beforeInfo[0].style.display = 'none';
                afterInfo[0].style.display = 'block';
                isChange = false;

                break;
            case 'gender' :
                beforeInfo[1].style.display = 'none';
                afterInfo[1].style.display = 'block';
                isChange = false;

                break;
            case 'email' :
                beforeInfo[2].style.display = 'none';
                afterInfo[2].style.display = 'block';
                isChange = false;

                break;
            case 'profile' :
                let input = document.createElement('input');

                input.type = 'file';
                input.accept = '.gif, .jpg, .png';
                input.name = 'profile';
                document.getElementsByClassName('profile__img')[1].appendChild(input);
                input.click();

                input.onchange = function() {
                    if( this.files && this.files[0] ) {
                        let reader = new FileReader();
                        reader.onload = function(e) {
                            document.getElementsByClassName('profile__img')[1].src = e.target.result;
                            document.getElementsByClassName('profile__img')[1].style.display = 'block'
                            document.getElementsByClassName('profile')[0].className = 'user__profile';
                            isChange = false;
                        };

                        reader.readAsDataURL(this.files[0]);
                    }

                    document.getElementsByClassName('profile__btn')[0].style.display = 'none';
                    document.getElementsByClassName('profile__svg')[0].style.display = 'none';
                    document.getElementsByClassName('profile__btn')[1].style.display = 'block';
                }


                break;
            }
    }
}
//이름값 체크
function checkName( ){
    let name = document.getElementsByClassName('myInfo__name');
    const pattern = /[^a-zA-Zㄱ-ㅎ가-힣]/g;

    if( name[1].value.length == 0 ) {
        msg[0].innerHTML = '이름을 입력해주세요.';
        return false;
    }else if( name[1].value.length < 2 || pattern.test(name[1].value) ) {
        msg[0].innerHTML = '2글자 이상의 영문자와 한글만 입력해야 합니다.';
        return false;
    }else if( name[1].value == name[0].innerHTML ) {
        msg[0].innerHTML = '기존이름과 동일합니다. 다른 이름으로 입력해주세요.';
        return false;
    }else{
        document.getElementsByClassName('myInfo__form')[0].submit();
    }
}
//이메일값 체크
function checkEmail() {
    let email = document.getElementsByClassName('myInfo__email');
    const pattern = /^([\w\.\_\-])*[a-zA-Z0-9]+([\w\.\_\-])*([a-zA-Z0-9])+([\w\.\_\-])+@([a-zA-Z0-9]+\.)+[a-zA-Z0-9]{2,8}$/;

    if( email[1].value.length == 0 ) {
        msg[1].innerHTML = '이메일을 입력해주세요.';
        return false;
    }else if( !pattern.test(email[1].value) ) {
        msg[1].innerHTML = '이메일 형식에 맞게 입력해야 합니다.';
        return false;
    }else if(email[0].innerHTML == email[1].value) {
        msg[1].innerHTML ='기존 이메일과 동일합니다. 다른 이메일로 입력해주세요.';
        return false;
    }else{
        let xhr = new XMLHttpRequest();
        xhr.open('POST', '/members/join/send-email', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send('email=' + email[1].value);

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                msg[1].style.color = getComputedStyle( document.documentElement ).getPropertyValue("--color-basic-success");
                msg[1].innerHTML = '작성한 이메일로 인증코드 전송했습니다.';

                document.getElementsByClassName('myInfo__div')[4].style.display = 'inline-flex';
                document.getElementsByClassName('email__btn')[0].className = 'email__btn display__none';
                document.getElementsByClassName('email__btn')[1].className = 'email__btn';
                emailTimer();
            }else{
                alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }
}
//타이머
let clock;
function emailTimer() {
    const Timer = document.getElementsByClassName('msg__timer')[0];

    let time =  300;
    let min = "";
    let sec = "";

    Timer.style.margin = 'auto 30px';
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

            document.getElementsByClassName('email__btn')[0].className = 'email__btn';
            document.getElementsByClassName('email__btn')[1].className = 'email__btn display__none';
            document.getElementsByClassName('myInfo__div')[4].style.display = 'none';

            msg[1].style.color = getComputedStyle( document.documentElement ).getPropertyValue("--color-basic-error");
            msg[1].innerHTML = '시간 초과되었습니다. 다시 전송해주세요.';
        }
    }, 1000);
}
//인증코드값 체크
function emailCodeCheck( code ) {
    let email = document.getElementsByClassName('myInfo__email')[1].value;
    let time = document.getElementsByClassName('msg__timer')[0];

    if( code.value.length == 6 ) {
        let xhr = new XMLHttpRequest();

        xhr.open('POST', '/members/join/check-email-code', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send('email=' + email +'&code=' + code.value +'&time=' + time.innerHTML );

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                if( xhr.response == 'yes' ){
                    msg[1].style.color = getComputedStyle( document.documentElement ).getPropertyValue("--color-basic-success");
                    msg[1].innerHTML = '이메일 인증 완료했습니다.';

                    clearInterval(clock);
                    document.getElementsByClassName('myInfo__div')[4].style.display = 'none';
                    time.style.display = 'none';
                }else if( xhr.response == 'no' ){
                    msg[1].style.color = getComputedStyle( document.documentElement ).getPropertyValue("--color-basic-error");
                    msg[1].innerHTML =' 인증코드가 일치하지 않습니다. 다시 확인해주세요.';
                }
            }else{
                alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }
}
//팝업창 표시
function openPopup( type ) {
    let option = "top=200, left=430, width=1200, height=650, status=no, menubar=no, toolbar=no, resizable=no";
    if( type == 'pwdChange' ) {
        window.open('/popup/password', '비밀번호 변경', option);
    }else if( type == 'memberDelete' ) {
        window.open('/popup/member', '회원탈퇴', option);
    }
}
//현재 비밀번호 일치 확인
function checkPwd( pwd, index ) {
    const pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$!%*#^])[A-Za-z\d$!%*#^]{8,20}$/;

    isPwd = false;

    if( pwd.value.length == 0 ) {
        msg[index].innerHTML = '비밀번호를 입력해주세요.';
        return false;
    }else if( !pattern.test(pwd.value) ) {
        msg[index].innerHTML = '8 ~ 20자리의 영문자, 숫자, 특수문자(!, ^, *, #, %)의 조합으로 입력해야 합니다.';
        return false;
    }else{
        let xhr = new XMLHttpRequest();
        xhr.open('POST', '/members/password', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send('password=' + pwd.value);

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                if( xhr.response == 1 ) {
                    msg[index].innerHTML = '';
                    isPwd = true;
                }else {
                    msg[index].innerHTML = '현재 비밀번호와 다르게 입력했습니다.';
                }
            }else{
                alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }
}
//새 비밀번호 확인
function checkNewPwd( newPwd ) {
    const pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$!%*#^])[A-Za-z\d$!%*#^]{8,20}$/;

    if( newPwd.value.length == 0 ) {
        msg[1].innerHTML = '비밀번호를 입력해주세요.';
        return false;
    } else if( !pattern.test(newPwd.value) ) {
        msg[1].innerHTML = '8 ~ 20자리의 영문자, 숫자, 특수문자(!, ^, *, #, %)의 조합으로 입력해야 합니다.';
        return false;
    }else {
        let xhr = new XMLHttpRequest();
        xhr.open('POST', '/members/password', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send('password=' + newPwd.value);

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                if( xhr.response == 1 ) {
                    msg[1].innerHTML = '현재 비밀번호와 동일합니다. 다른 비밀번호를 입력해주세요.';
                }else{
                    msg[1].innerHTML = '';
                    isNewPwd = true;
                }
            }else{
                alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }
}
//새 비밀번호 재확인
function checkReNewPwd( rePwd ) {
    let newPwd = document.getElementsByClassName('input__password')[1];
    const pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$!%*#^])[A-Za-z\d$!%*#^]{8,20}$/;

    if( rePwd.value.length == 0 ) {
        msg[2].innerHTML = '비밀번호을 입력해주세요.';
        return false;
    }else if( !pattern.test(rePwd.value) ) {
        msg[2].innerHTML = '8 ~ 20자리의 영문자, 숫자, 특수문자(!, ^, *, #, %)의 조합으로 입력해야 합니다.';
        return false;
    }else {
        if(  !checkNewPwd(newPwd) && newPwd.value != rePwd.value ) {
            msg[2].innerHTML = '새 비밀번호와 일치하지 않습니다. 다시 확인해주세요.';
        }else{
            msg[2].innerHTML = '';
            isRePwd = true;
        }
    }
}
//비밀번호 변경
function changePwd(){
    const pwd = document.getElementsByClassName('input__password');

    if( isPwd && isNewPwd && isRePwd ) {
        let xhr = new XMLHttpRequest();
        xhr.open('PUT', '/members/password', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send('password=' + pwd[1].value);

        xhr.onload = function(){
            if( xhr.status == 200 ) {
                if( xhr.response == 1 ) {
                    alert('변경한 비밀번호로 로그인해주세요.');

                    setTimeout(function(){
                        window.close();
                        opener.location.href = '/';
                    }, 100);
                }else{
                    alert('일시적인 문제로 비밀번호가 변경되지 않았습니다. 잠시 후 다시 시도해주세요.');
                }
            }else{
                alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }else{
        if( !isPwd ) {
            checkPwd(pwd[0], 0);
            pwd[0].focus();
        }else if( !isNewPwd ) {
            checkNewPwd(pwd[1]);
            pwd[1].focus();
        }else{
            checkReNewPwd(pwd[2]);
            pwd[2].focus();
        }
    }
}
//탈퇴 사유 선택
function changeOption( option ) {
    let optionBecause = document.getElementsByClassName('input__text')[0];
    let delete__items = document.getElementsByClassName('delete__items')[0];

    if( option.value == '05' ) {
        optionBecause.style.display = 'block';
        optionBecause.disabled = false;
    }else{
        optionBecause.style.display = 'none';
        optionBecause.disabled = true;
    }

    msg[0].innerHTML = '';
}
//회원탈퇴
function deleteMember() {
    let option = document.getElementsByClassName('delete__select')[0];
    let input = document.getElementsByClassName('input__text');

    if( option.value == '00' ) {
        msg[0].innerHTML = '탈퇴 사유를 선택해주세요.';
    }else if( option.value == '05' && input[0].value == '' ) {
        msg[0].innerHTML = '기타 사항을 입력해주세요.';
        input[0].focus();
    }else {
        msg[0].innerHTML = '';

        const id = document.getElementsByClassName('delete__info')[0].value;
        const pwd = document.getElementsByClassName('input__password')[0].value;

        if( isPwd ) {
            let xhr = new XMLHttpRequest();

            xhr.open('DELETE', '/members', true);
            xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
            xhr.send('id=' + id + '&password=' + pwd + '&code=' + option.value + '&cause=' + input[0].value);

            xhr.onload = function() {
                if( xhr.status == 200 ) {
                console.log(xhr.response);
                    if( xhr.response == 1 ) {
                        alert('탈퇴가 완료되었습니다. 그동안 저희 서비스를 이용해주셔서 감사합니다');

                        setTimeout(function() {
                            window.close();
                            opener.location.href = '/';
                        })
                    }else{
                        alert('일시적인 문제로 탈퇴가 되지 않았습니다.. 잠시 후 다시 시도해주세요.');
                    }
                }else{
                    alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
                }
            }
        }else{
            msg[1].innerHTML = '비밀번호를 확인해주세요.';
        }
    }
}