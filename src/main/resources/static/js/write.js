//고정 or 변동 선택 시 동작
function showOption(){
    let fix = document.getElementsByClassName('input__radio__large')[0];
    let option = document.getElementsByClassName('input__radio__middle');

    if( fix.checked ) {
        document.getElementsByClassName('write__fix')[1].classList.replace('display__none', 'display__inline-flex');
    }else{
        document.getElementsByClassName('write__fix')[1].classList.replace('display__inline-flex', 'display__none');
        for( let i=0; i<option.length; i++ ) {
            option[i].checked = false;
        }
    }
}
function writeSubmit() {
    let code = document.getElementsByClassName('input__radio')[0];

    if( code.checked ) {
        location
    }
}
//카테고리 선택 시 하위카테고리 표시
function changeCategory( option ){
    let category = document.getElementsByClassName('write__item__category')[0];
    let large = document.getElementsByClassName('select__category__large')[0];
    let middle = document.getElementsByClassName('select__category__middle')[0];

    if( middle != null ) {
        middle.remove();
    }

    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/accounts/write/category', true);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send('code=' + option.value);

    xhr.onload = function() {
        if( xhr.status == 200 ) {
            const new_select = document.createElement('select');
            let result = JSON.parse(xhr.response);

            if( result.length == 0 ) {
                large.setAttribute('name', 'category');
            }else{
                for( i=0; i<result.length; i++ ) {
                    const new_option = document.createElement('option');
                    new_option.value = result[i].code;
                    new_option.text = result[i].name;

                    new_select.appendChild(new_option);
                }

                new_select.setAttribute('class', 'select__category__middle');
                new_select.setAttribute('name', 'category');
                new_select.style.display = 'block';
                new_select.style.width = '80px';

                category.appendChild(new_select);
                large.removeAttribute('name');
            }
        }else{
            alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
        }
    }
}
//제목 확인
let isTitle = false;
function checkTitle( title ) {
    const pattern = /[`~!@#$%^&*|\\\'\";:\\/?]/gi;

    if( pattern.test( title.value ) ) {
        isTitle = false;
    }else{
        isTitle = true;
    }
}
//가격에 숫자만 입력
function checkPrice( price ){
    const pattern = /[^0-9]/g;

    if( pattern.test(price.value) ) {
        price.value = price.value.replace(pattern, '');
    }
}
//위치 아이콘 선택 시 동작
function openMapPop(){
    let option = 'top=320, left=660, width=800, height=500, status=no, menubar=no, toolbar=no, resizable=no';
    window.open('/api/kakao-map', '위치', option);
}
//위치 지우기
function removeLocation() {
    document.getElementById('titleV').value = '';
    document.getElementById('roadV').value = '';
    document.getElementById('addV').value = '';

    document.getElementsByClassName('write__location__search')[0].style.display = 'inline-flex';
    document.getElementsByClassName('write__location__result')[0].classList.replace('display__inline-flex', 'display__none');
}
//등록 시 필수값 체크
function writeCheck() {
    let category = document.getElementsByClassName('select__category__large')[0];
    let input = document.getElementsByClassName('input__text');

    if( category.value == '00' ) {
        alert('카테고리를 선택해주세요.');
        category.focus();
        return false;
    }else if( input[0].value.length == 0 ){
        alert('제목을 입력해주세요.');
        input[0].focus();
        return false;
    }else if( !isTitle ) {
        alert('제목에는 특수문자를 제외한 문자를 입력해주세요.');
        input[0].focus();
        return false;
    }else if( input[1].value == '' ) {
        alert('금액을 입력해주세요.');
        input[1].focus();
        return false;
    }

    document.getElementsByClassName('write__form')[0].submit();
    return true;
}