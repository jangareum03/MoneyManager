//검색영역 선택 동작
function onChangeMode( input ) {
    let mode = input.value;
    let inout = document.getElementsByClassName('menu')[0];
    let inCategory = document.getElementsByClassName('menu')[1];
    let exCategory = document.getElementsByClassName('menu')[2];
    let title = document.getElementsByClassName('menu')[3];
    let period = document.getElementsByClassName('menu')[4];

    inout.style.display = 'none';
    inCategory.style.display = 'none';
    exCategory.style.display = 'none';
    title.style.display = 'none';
    period.style.display = 'none';

    if( mode == 'all' ) {
        document.getElementById('menu').submit();
    }else if( mode == 'inout' ) {
        inout.style.display = 'block';

        let radioOption = document.getElementsByName('option');
        for( i=0; i<radioOption.length; i++ ) {
            if( radioOption[i].checked ) {
                radioOption[i].checked = false;
            }
        }
    }else if( mode == 'inCategory' ) {
        inCategory.style.display = 'block';
    }else if( mode == 'exCategory' ) {
        exCategory.style.display = 'block';
    }else if( mode == 'title' ) {
        title.style.display = 'block';
    }else{
        period.style.display = 'block';
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
    xhr.open('POST', '/accountCategory', true);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send('code=' + category.value);

    xhr.onload = function() {
        if( xhr.status == 200 ) {
            let result = JSON.parse(xhr.response);

            for( i=0; i<mode.length; i++ ){
                if( i < result.length ) {
                    mode[i].value = result[i].code;
                    mode[i].style.display = 'inline';
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
            document.getElementById('table_list').submit();
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

    document.getElementById('menu').submit();
}