//변경할 정보 화면보이기
function changeUser(type) {
    let mode = document.getElementById('changeType');

    if( mode.innerHTML == 'n' ) {
        switch( type ) {
            case 'name' :
                document.getElementById('name').style.display = 'none';
                document.getElementById('nameUpdate').style.display = 'block';
                mode.innerHTML = 'y';

                break;
            case 'gender' :
                document.getElementById('gender').style.display = 'none';
                document.getElementById('genderUpdate').style.display = 'block';
                mode.innerHTML = 'y';

                break;
            case 'email' :
                document.getElementById('email').style.display = 'none';
                document.getElementById('emailUpdate').style.display = 'block';
                mode.innerHTML = 'y';

                break;
            case 'profile' :
                let input = document.createElement('input');

                input.type = 'file';
                input.accept = '.gif, .jpg, .png';
                input.name = 'profile';
                document.getElementById('profileImage').appendChild(input);
                input.click();

                input.onchange = function() {
                    if( this.files && this.files[0] ) {
                        let reader = new FileReader();
                        reader.onload = function(e) {
                            document.getElementById('profileImage').src = e.target.result;
                            mode.innerHTML = 'y';
                        };

                        reader.readAsDataURL(this.files[0]);
                    }

                    document.getElementsByClassName('btn-change')[0].style.display = 'none';
                    document.getElementById('profileBtn').style.display = 'block';
                    document.getElementById('profileBtn').style.margin = '0 auto';
                }


                break;
            }
    }
}

//이름값 체크
function checkName( ){
    let originName = document.getElementById('originName').innerHTML;
    let name = document.getElementsByName('name')[0].value;
    let msg = document.getElementsByClassName('msg')[0];
    const pattern = /[^a-zA-Zㄱ-ㅎ가-힣]/g;

    if( name.length == 0 ) {
        msg.innerHTML = '이름을 입력해주세요.';
        return false;
    }else if( name.length < 3 || pattern.test(name) ) {
        msg.innerHTML = '3글자 이상의 영문자와 한글만 입력해야 합니다.';
        return false;
    }else if( name == originName ) {
        msg.innerHTML = '기존이름과 동일합니다. 다른 이름으로 입력해주세요.';
        return false;
    }else{
        document.getElementById('nameUpdate').submit();
    }
}

//이메일값 체크
function checkEmail() {
    let originEmail = document.getElementById('originEmail').innerHTML;
    let email = document.getElementsByName('email')[0].value;
    let msg = document.getElementsByClassName('msg')[1];
    const pattern = /^([\w\.\_\-])*[a-zA-Z0-9]+([\w\.\_\-])*([a-zA-Z0-9])+([\w\.\_\-])+@([a-zA-Z0-9]+\.)+[a-zA-Z0-9]{2,8}$/;

    if( email.length == 0 ) {
        msg.innerHTML = '이메일을 입력해주세요.';
        return false;
    }else if( !pattern.test(email) ) {
        msg.innerHTML = '이메일 형식에 맞게 입력해야 합니다.';
        return false;
    }else if(originEmail == email) {
        msg.innerHTML ='기존 이메일과 동일합니다. 다른 이메일로 입력해주세요.';
        return false;
    }else{
        let xhr = new XMLHttpRequest();
        xhr.open('POST', '/join/sendEmail', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send('email=' + email);

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                msg.style.color = '#2A8D2A';
                msg.innerHTML = '작성한 이메일로 인증코드 전송했습니다.';

                document.getElementById('inputEmailCode').style.display = 'block';
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
    const Timer = document.getElementById('Timer');

    let time = 300000;
    let min = 5;
    let sec = 60;

    Timer.style.color = '#D71E1E';
    Timer.innerHTML = '0' + min + ' : 00';

    time = time - 1000;
    min = time / (60*1000);

    clock = setInterval(function() {
        if( sec > 0 ) {
            sec = sec - 1;
            if( sec < 10 ) {
                Timer.innerHTML = '0' + Math.floor(min) + ' : 0' + sec;
            }else{
                Timer.innerHTML = '0' + Math.floor(min) + ' : ' + sec;
            }
        }

        if(  sec === 0) {
            sec = 60;
            Timer.innerHTML = '0' + Math.floor(min) + ' : 00';
        }
    }, 1000);
}

//인증코드값 체크
function emailCodeCheck() {
    let email = document.getElementsByName('email')[0].value;
    let code = document.getElementById('inputEmailCode').value;
    let msg = document.getElementsByClassName('msg')[1];
    let time = document.getElementById('Timer').innerHTML;

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
                    document.getElementById('inputEmailCode').style.display = 'none';
                    document.getElementById('emailBtn1').style.display = 'none';
                    document.getElementById('Timer').style.display = 'none';
                    document.getElementById('emailBtn2').style.display = 'block';
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

//팝업창 표시
function openPopup( type ) {
    let option = "top=200, left=430, width=1200, height=650, status=no, menubar=no, toolbar=no, resizable=no";
    if( type == 'pwdChange' ) {
        window.open('/changePwd', '비밀번호 변경', option);
    }else if( type == 'memberDelete' ) {
        window.open('/deleteMember', '회원탈퇴', option);
    }
}


let isPwd = false;
let isNewPwd = false;
let isRePwd = false;

//현재 비밀번호 일치 확인
function checkPwd( pwd, index ) {
    let msg = document.getElementsByClassName('main__msg')[index];
    const pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$!%*#^])[A-Za-z\d$!%*#^]{8,20}$/;

    if( pwd.value.length == 0 ) {
        msg.innerHTML = '비밀번호를 입력해주세요.';
        return false;
    }else if( !pattern.test(pwd.value) ) {
        msg.innerHTML = '8 ~ 20자리의 영문자, 숫자, 특수문자(!, ^, *, #, %)의 조합으로 입력해야 합니다.';
        return false;
    }else{
        let xhr = new XMLHttpRequest();
        xhr.open('POST', '/findPwd', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send('password=' + pwd.value);

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                if( xhr.response == 1 ) {
                    msg.innerHTML = '';
                    isPwd = true;
                }else {
                    msg.innerHTML = '현재 비밀번호와 다르게 입력했습니다.';
                }
            }else{
                alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }
}

//새 비밀번호 확인
function checkNewPwd( newPwd ) {
    let pwd = document.getElementsByClassName('main__input')[0].value;
    let msg = document.getElementsByClassName('main__msg')[1];
    const pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$!%*#^])[A-Za-z\d$!%*#^]{8,20}$/;

    if( newPwd.value.length == 0 ) {
        msg.innerHTML = '비밀번호를 입력해주세요.';
        return false;
    } else if( !pattern.test(newPwd.value) ) {
        msg.innerHTML = '8 ~ 20자리의 영문자, 숫자, 특수문자(!, ^, *, #, %)의 조합으로 입력해야 합니다.';
        return false;
    }else {
        let xhr = new XMLHttpRequest();
        xhr.open('POST', '/findPwd', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send('password=' + newPwd.value);

        xhr.onload = function() {
            if( xhr.status == 200 ) {
                if( xhr.response == 1 ) {
                    msg.innerHTML = '현재 비밀번호와 동일합니다. 다른 비밀번호를 입력해주세요.';
                }else{
                    msg.innerHTML = '';
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
    let newPwd = document.getElementsByClassName('main__input')[1];
    let msg = document.getElementsByClassName('main__msg')[2];
    const pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$!%*#^])[A-Za-z\d$!%*#^]{8,20}$/;

    if( rePwd.value.length == 0 ) {
        msg.innerHTML = '비밀번호을 입력해주세요.';
        return false;
    }else if( !pattern.test(rePwd.value) ) {
        msg.innerHTML = '8 ~ 20자리의 영문자, 숫자, 특수문자(!, ^, *, #, %)의 조합으로 입력해야 합니다.';
        return false;
    }else {
        if(  !checkNewPwd(newPwd) && newPwd.value != rePwd.value ) {
            msg.innerHTML = '새 비밀번호와 일치하지 않습니다. 다시 확인해주세요.';
        }else{
            msg.innerHTML = '';
            isRePwd = true;
        }
    }
}

//비밀번호 변경
function changePwd(){
    console.log('들어옴');
    const pwd = document.getElementsByClassName('main__input');

    if( isPwd && isNewPwd && isRePwd ) {
        let xhr = new XMLHttpRequest();
        xhr.open('POST', '/updatePwd', true);
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
function changeOption( target ) {
    let optionBecause = document.getElementsByClassName('main__input');

    if( target.value == '05' ) {
        optionBecause[0].style.display = 'inline-block';
        optionBecause[0].disabled = false;
    }else{
        optionBecause[0].style.display = 'none';
        optionBecause[0].disabled = true;
    }

    document.getElementsByClassName('main__msg')[0].innerHTML = '';
}

//회원탈퇴
function deleteMember() {
    let option = document.getElementsByClassName('main__select')[0];
    let inputText = document.getElementsByClassName('main__input');
    let msg = document.getElementsByClassName('main__msg');

    if( option.value == '00' ) {
        msg[0].innerHTML = '탈퇴 사유를 선택해주세요.';
    }else if( option.value == '05' && inputText[0].value == '' ) {
        msg[0].innerHTML = '기타 사항을 입력해주세요.';
        inputText[0].focus();
    }else {
        msg[0].innerHTML = '';

        const id = document.getElementById('id').value;
        const pwd = document.getElementById('pwd').value;

        if( isPwd ) {
            let xhr = new XMLHttpRequest();

            xhr.open('POST', '/deleteMember', true);
            xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
            xhr.send('id=' + id + '&password=' + pwd + '&code=' + option.value + '&cause=' + inputText[0].value);

            xhr.onload = function() {
                if( xhr.status == 200 ) {
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