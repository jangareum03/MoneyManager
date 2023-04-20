//검색 메뉴 선택
function changeSearch( type ) {
    let option = document.getElementsByClassName('search__option');
    let text = document.getElementsByClassName('input__text');

    for( i=0; i< type.options.length - 1; i++ ) {
        text[i].removeAttribute('name');
        option[i].style.display = 'none';
    }

    if( type.value == 'all' ) {
        location.href = '/qna';
    }else{
        if( type.value == 'title' ) {
            text[0].setAttribute('name', 'keyword');
            option[0].style.display = 'block';
        }else if( type.value == 'writer' ) {
            text[1].setAttribute('name', 'keyword');
            option[1].style.display = 'block';
        }else {
            text[2].setAttribute('name', 'startDate');
            text[3].setAttribute('name', 'endDate');
            option[2].style.display = 'block';
        }
    }
}
//검색어 확인
function checkSearch() {
    let select = document.getElementsByName('type')[0].value;
    let keyword = document.getElementsByClassName('input__text');
    let form = document.getElementsByClassName('search__form')[0];

    if( select == 'title' ) {
        if( keyword[0].value.replace(/ /g, '') == '' ) {
            alert('검색어를 입력해주세요.');

            keyword[0].value = null;
            keyword[0].focus();
            return false;
        }else{
            form.submit();
        }
    }else if( select == 'writer' ) {
        if( keyword[1].value.replace(/ /g, '') == '' ) {
            alert('검색어를 입력해주세요.');

            keyword[1].value = null;
            keyword[1].focus();
            return false;
        }else{
            form.submit();
        }
    }else {
        if( keyword[2].value == '' ) {
            alert('시작일을 선택해주세요.');

            keyword[2].focus();
            return false;
        }else if( keyword[3].value == '' ) {
            alert('종료일을 선택해주세요.');

            keyword[3].focus();
            return false;
        }else {
            form.submit();
        }
    }
}
//비밀글 확인 후 이동
function checkMember( id ) {
    console.log(id);

    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/qna/checkMember', true);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send('id=' + id);

    xhr.onload = function(){
        if( xhr.status == 200 ) {
            if( xhr.response == 1 ) {
                location.href = '/qna/' + id;
            }else{
                alert('비밀글이여서 볼 수 없습니다.');
            }
        }else{
            alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
        }
    }
}
//제목 확인
let isTitle = false;
function checkTitle( target ) {
    if( target.value.length == 0 ) {
        document.getElementsByClassName('msg__error')[0].innerHTML = '제목을 입력해주세요.';
        isTitle = false;
    }else{
        document.getElementsByClassName('msg__error')[0].innerHTML = '';
        isTitle = true;
    }
}
//내용 확인
let isContent = false;
function checkContent( target ) {
    if( target.value.length == 0 ) {
        document.getElementsByClassName('msg__error')[1].innerHTML = '내용을 입력해주세요.';
        isContent =false;
    }else{
        document.getElementsByClassName('msg__error')[1].innerHTML = '';
        isContent = true;
    }
}
//비밀글 여부 확인
function clickBox(){
    let box = document.getElementsByClassName('input__checkbox')[0];
    let value = document.getElementsByClassName('input__hidden')[0];

    if( box.checked ) {
        value.setAttribute('value', 'n');
    }else {
        value.setAttribute('value', 'y');
    }
}
//Q&A 작성 조건 확인
function checkWrite() {
    let textarea = document.getElementsByClassName('textarea')[0];

    //textarea에서 줄바꿈 처리
    textarea.value = textarea.value.replaceAll(/(\n|\r|\n)/g, '<br>');

    if( isTitle && isContent ) {
        document.getElementsByClassName('write__form')[0].submit();
    }else if( !isTitle ) {
        alert('제목을 입력해주세요.');
        document.getElementsByClassName('input__text')[0].focus();
    }else{
        alert('내용을 입력해주세요.');
        textarea.focus();
    }
}