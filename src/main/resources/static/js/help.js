//이름 검사
function nameCheck() {
    let name = document.getElementById('input_name').value;
    let msg = document.getElementById('nameMsg');
    let result = document.getElementById('nameResult').value;
    const pattern = /[^a-zA-Zㄱ-ㅎ가-힣]/g;

    msg.style.color = '#D71E1E';
    result.value = 'n';

    if( name == '' ) {
        msg.innerHTML = '';
    }else if( name.length < 3 || pattern.test(name) ) {
        msg.innerHTML = '3글자 이상의 영문자와 한글만 입력해야 합니다.';
    }else{
        result.value = 'y';
        msg.innerHTML = '';
    }
}

//이메일 검사
function emailCheck() {
    let email = document.getElementById('input_email').value;
    let msg = document.getElementById('emailMsg');
    let result = document.getElementById('emailResult').value;
    const pattern = /^([\w\.\_\-])*[a-zA-Z0-9]+([\w\.\_\-])*([a-zA-Z0-9])+([\w\.\_\-])+@([a-zA-Z0-9]+\.)+[a-zA-Z0-9]{2,8}$/;

    msg.style.color = '#D71E1E';
    result.value = 'n';

    if( email == '' ) {
        msg.innerHTML = '';
    }else if( !pattern.test(email) ) {
        msg.innerHTML = '이메일 형식에 맞게 입력해야 합니다.';
    }else{
        result.value = 'y';
    }
}

//아이디값 체크
function idCheck() {
    let id = document.getElementById('input_id').value;
    let msg = document.getElementById('idMsg');
    let result = document.getElementById('idResult').value;
    const pattern = /[^a-zA-Z0-9]/g;

    msg.style.color = '#D71E1E';
    result.value = 'n';

    if( id == '' ) {
        msg.innerHTML = '';
    }else if( id.length < 5 || pattern.test(id) ) {
        msg.innerHTML = '5 ~ 20자리의 영문자와 숫자만 입력해야 합니다.';
    }else{
        msg.innerHTML = '';
        result.value = 'y';
    }
}