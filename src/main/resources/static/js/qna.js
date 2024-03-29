//검색 메뉴 선택
function changeSearch( type ) {
    let search = document.getElementsByClassName('search-area')[1];
    let option = document.getElementsByClassName('search-box');
    let input = document.getElementsByClassName('search__input_text');

    for( i=0; i< type.options.length - 1; i++ ) {
        input[i].removeAttribute('name');
        option[i].className = 'search-box box__display_none';
    }

    if( type.value == 'all' ) {
        location.href = '/qna';
    }else{
        search.style.display = 'inline';

        let index;
        if( type.value == 'title' ) {
            index = 0;
            input[index].setAttribute('name', 'keyword');
            option[index].className = 'search-box box__display_block';
        }else if( type.value == 'writer' ) {
            index = 1;
            input[index].setAttribute('name', 'keyword');
            option[index].className = 'search-box box__display_block';
        }else {
            index = 2;
            input[index].setAttribute('name', 'startDate');
            input[index+1].setAttribute('name', 'endDate');
            option[index].className = 'search-box box__display_block';
        }
    }
}

//검색어 확인
function checkSearch() {
    let type = document.getElementsByClassName('search__select')[0].value;
    let keyword = document.getElementsByClassName('search__input_text');
    let form = document.getElementsByClassName('search-form')[0];

    if( type == 'title' ) {
        if( keyword[0].value.replace(/ /g, '') == '' ) {
            alert('검색어를 입력해주세요.');

            keyword[0].value = null;
            keyword[0].focus();
            return false;
        }else{
            form.submit();
        }
    }else if( type == 'writer' ) {
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
    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/qna/checkMember', true);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send('id=' + id);

    xhr.onload = function(){
        if( xhr.status == 200 ) {
            if( xhr.response == 1 ) {
                location.href = '/qna/detail/' + id;
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
        document.getElementsByClassName('main__msg')[0].innerHTML = '제목을 입력해주세요.';
        isTitle = false;
    }else{
        document.getElementsByClassName('main__msg')[0].innerHTML = '';
        isTitle = true;
    }
}

//내용 확인
let isContent = false;
function checkContent( target ) {
    if( target.value.length == 0 ) {
        document.getElementsByClassName('main__msg')[1].innerHTML = '내용을 입력해주세요.';
        isContent =false;
    }else{
        document.getElementsByClassName('main__msg')[1].innerHTML = '';
        isContent = true;
    }
}

//비밀글 여부 확인
function clickBox(){
    let checkbox = document.getElementsByClassName('main__input_checkbox')[0];
    let boxValue = document.getElementsByClassName('main__input_hidden')[0];

    if( checkbox.checked ) {
        boxValue.value = 'y';
    }else{
        boxValue.value = 'n';
    }
}

//Q&A 작성 조건 확인
function checkWrite() {
    if( isTitle && isContent ) {
        document.getElementsByClassName('main__form')[0].submit();
    }else if( !isTitle ) {
        document.getElementsByClassName('main__input')[0].focus();
    }else{
        document.getElementsByClassName('main__textarea')[0].focus();
    }
}