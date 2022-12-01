function onChangeMode( input ) {
    let mode = input.value;
    let inout = document.getElementsByClassName('menu')[0];
    let title = document.getElementsByClassName('menu')[1];
    let inCategory = document.getElementsByClassName('menu')[2];
    let outCategory = document.getElementsByClassName('menu')[3];

    inout.style.display = 'none';
    title.style.display = 'none';
    inCategory.style.display = 'none';
    outCategory.style.display = 'none';

    if( mode == 'all' ) {
        document.getElementById('menu').submit();
    }else if( mode == 'inout' ) {
        inout.style.display = 'block';
    }else if( mode == 'title' ) {
        title.style.display = 'block';
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



