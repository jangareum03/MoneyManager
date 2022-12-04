//내역 검색영역 선택 동작
function onChangeMode( input ) {
    let mode = input.value;
    let inout = document.getElementsByClassName('menu')[0];
    let title = document.getElementsByClassName('menu')[1];
    let period = document.getElementsByClassName('menu')[2];
    let inCategory = document.getElementsByClassName('menu')[3];
    let outCategory = document.getElementsByClassName('menu')[4];

    inout.style.display = 'none';
    title.style.display = 'none';
    period.style.display = 'none';
    inCategory.style.display = 'none';
    outCategory.style.display = 'none';

    if( mode == 'all' ) {
        document.getElementById('menu').submit();
    }else if( mode == 'inout' ) {
        inout.style.display = 'block';
    }else if( mode == 'title' ) {
        title.style.display = 'block';
    }else if( mode == 'period' ){
        period.style.display = 'block';
    }else if( mode == 'inCategory' ) {
        inCategory.style.display = 'block';

        let category = document.getElementsByClassName('exMode');
        checkBoxDisabled(category);
    }else if( mode == 'outCategory' ) {
        outCategory.style.display = 'block';

        let category = document.getElementsByClassName('inMode');
        checkBoxDisabled(category);
    }
}

//카테고리 체크박스 해제
function checkBoxDisabled( category ){
    for( i=0; i<category.length; i++ ) {
        if(category[i].checked){
            category[i].checked = false;
        }
    }
}

//내역 삭제 시 체크박스 확인
function deleteCheck(){
    let check = document.getElementsByName('id');
    let isCheck = true;

    for(i=0; i<check.length; i++) {
        if( check[i].checked == true ) {
            isCheck = false;
            break;
        }
    }

    if( isCheck ) {
        alert('삭제할 내역을 선택해주세요.');
        return false;
    }else{
        let frm = document.getElementById('table_form');
        frm.submit();
    }
}

//팝업창 띄우기
function win_open( page, name ){
    window.open(page, name, "width=1000, height=600, left=500, top=200, toolbar=no, scrollbars=no, location=no, fullscreen=yes");
}

//날짜 숫자만 입력
function dateCheck( target ){
    const pattern = /[^0-9]/g;

    if( pattern.test(target.value) ){
        target.value = target.value.replace(pattern, '');
    }
}

//기간 시작날짜와 종료날짜 입력 확인
function periodCheck(){
    let start = document.getElementById('start').value;
    let end = document.getElementById('end').value;

    if( start == '' && end == '' ) {
        alert('검색할 날짜를 입력해주세요.');
        return false;
    }else if( start == '' && end != '' ) {
        alert('시작날짜를 입력해주세요.');
        return false;
    }else if( start.length < 8 || end.length <8 ) {
        alert('아래와 같은 형식으로 입력해주세요.\n(형식: 20220101)');
        return false;
    }else if( start != '' && end == '' ) {
        alert('종료날짜를 입력해주세요.');
        return false;
    }

    document.getElementById('menu').submit();
}