//검색영역 선택 동작
function onChangeMode( mode ) {
    let option = document.getElementsByClassName('search__option');

    //화면 안보이게
    for( i=0; i<option.length; i++ ) {
        option[i].style.display = 'none';
    }

    if( mode.value == 'all' ) {
        document.getElementsByClassName('search__form')[0].submit();
    }else if( mode.value == 'inEx' ) {
        option[0].style.display = 'inline-flex';

        let radioOption = document.getElementsByName('option');
        for( i=0; i<radioOption.length; i++ ) {
            radioOption[i].checked = false;
        }
    }else if( mode.value == 'inCategory' ) {
        option[1].style.display = 'inline-flex';
    }else if( mode.value == 'exCategory' ) {
        option[2].style.display = 'inline-flex';
    }else if( mode.value == 'title' ) {
        option[3].style.display = 'inline-flex';
    }else{
        option[4].style.display = 'inline-flex';
    }
}
//카테고리 선택 시 하위 카테고리 표시
function changeCategory( category ) {
    let mode_name = 'inMode';
    let label_name = 'inLabel';

    if( category.value.substr(0, 2) == '02' ) {
        mode_name = 'exMode';
        label_name = 'exLabel';
    }

    let mode = document.getElementsByClassName(mode_name);
    checkBoxDisabled( mode );

    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/accounts/category', true);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send('code=' + category.value);

    xhr.onload = function() {
        if( xhr.status == 200 ) {
            let result = JSON.parse(xhr.response);

            for( i=0; i<mode.length; i++ ){
                if( i < result.length ) {
                    mode[i].value = result[i].code;
                    document.querySelector('label[for="' + label_name + i + '"]').innerText = result[i].name;
                    document.querySelector('label[for="' + label_name + i + '"]').style.display = 'inline';
                }else{
                    mode[i].style.display = 'none';
                    document.querySelector('label[for="' + label_name + i + '"]').style.display = 'none';
                }
            }
        }else{
            alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
        }
    }
}
//카테고리 체크박스 모두해제
function checkBoxDisabled( category ){
    for( i=0; i<category.length; i++ ) {
        if(category[i].checked){
            category[i].checked = false;
        }
    }
}
//Array를 JSON객체로 변환
function arrToJson() {
    let check = document.getElementsByName('id');
    let index = 0;
    let arr = [];

    for(i=0; i<check.length; i++) {
        if( check[i].checked == true ) {
            arr[index] = check[i].value;
            index++;
        }
    }

    return arr;
}
//내역 삭제 시 체크박스 확인
function deleteCheck(){
    let check = document.getElementsByName('id');
    let isCheck = true;

    for(i=0; i<check.length; i++) {
        if( check[i].checked == true ) {
            isCheck = false;
        }
    }

    if( isCheck ) {
        alert('삭제할 내역을 선택해주세요.');
        return false;
    }else{
        if( confirm("삭제하시겠습니까?") ) {
            document.getElementsByClassName('account__form')[0].submit();
        }else{
            return false;
        }
    }
}
//팝업창 띄우기
function win_open( page, name ){
    window.open(page, name, "width=1000, height=600, left=500, top=200, toolbar=no, scrollbars=no, location=no, fullscreen=yes");
}
//기간 날짜 확인
function periodCheck(){
    let start = document.getElementById('start').value;
    let end = document.getElementById('end').value;

    if( start == '' && end == '' ) {
        alert('검색할 날짜를 입력해주세요.');
        document.getElementById('start').focus();
        return false;
    }else if( start == '' && end != '' ) {
        alert('시작날짜를 입력해주세요.');
        document.getElementById('start').focus();
        return false;
    }else if( start != '' && end == '' ) {
        alert('종료날짜를 입력해주세요.');
        document.getElementById('end').focus();
        return false;
    }

    document.getElementsByClassName('search__form')[0].submit();
}