//위치 아이콘 선택 시 동작
function openMapPop(){
    let option = 'top=320, left=660, width=800, height=500, status=no, menubar=no, toolbar=no, resizable=no';
    window.open('/write/map', '팝업', option);
}
//고정 or 변동 선택 시 동작
function showOption(){
    let fix = document.getElementsByClassName('input__radio__large')[0];
    let option = document.getElementsByClassName('input__radio__middle');

    if( fix.checked ) {
        document.getElementsByClassName('update__fix')[1].style.display = 'inline-flex';
    }else{
        document.getElementsByClassName('update__fix')[1].style.display = 'none';
        for( let i=0; i<option.length; i++ ) {
            option[i].checked = false;
        }
    }
}
//카테고리 선택 시 하위카테고리 표시
function changeCategory( option ){
    let category = document.getElementsByClassName('update__category')[0];
    let middle = document.getElementsByClassName('select__category__middle')[0];
    let small = document.getElementsByClassName('select__category__small')[0];

    //기존 카테고리 삭제
    const subCategory = deleteCategory( option.value, middle, small );

    //선택한 카테고리에 맞게 새로 생성
    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/write/category', true);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send('code=' + option.value);

    xhr.onload = function() {
        if( xhr.status == 200 ) {
            let result = JSON.parse(xhr.response);

            if( result.length == 0 ) {
                small.removeAttribute('name', 'category');
                middle.setAttribute('name', 'category');
            }else {
                if( subCategory == 'M' ) {
                    for( i=0; i<result.length; i++ ) {
                        const new_option = document.createElement('option');
                        new_option.value = result[i].code;
                        new_option.text = result[i].name;

                        middle.appendChild(new_option);
                    }
                }else {
                    const new_select = document.createElement('select');

                    for( i=0; i<result.length; i++ ) {
                        const new_option = document.createElement('option');
                        new_option.value = result[i].code;
                        new_option.text = result[i].name;

                        new_select.appendChild(new_option);
                    }

                    new_select.setAttribute('class', 'select__category__small');
                    new_select.setAttribute('name', 'category');

                    category.appendChild(new_select);
                }
            }
        }else{
            alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
        }
    }
}
//하위 카테고리 삭제
function deleteCategory( category, middle, small ) {
    if( category.substr(2, 2) == '00' ) {
        if( middle != null ) {
            const size = middle.length;

            for( i=0; i < size; i++ ) {
                middle.remove(0);
            }
        }
        if( small != null ) {
            small.remove();
        }

        return "M";
    }else {
        if( small != null ) {
            small.remove();
        }

        return "S";
    }
}
//가격에 숫자만 입력
function checkPrice( target ){
    const pattern = /[^0-9]/g;

    if( pattern.test(target.value) ) {
        target.value = target.value.replace(pattern, '');
    }
}
//위치 아이콘 선택 시 동작
function openMapPop(){
    let option = 'top=320, left=660, width=800, height=500, status=no, menubar=no, toolbar=no, resizable=no';
    window.open('/write/map', '팝업', option);
}
//위치 지우기
function removeLocation() {
    document.getElementById('title').value = '';
    document.getElementById('road').value = '';
    document.getElementById('add').value = '';

    document.getElementsByClassName('update__location__search')[0].style.display = 'inline-flex';
    document.getElementsByClassName('update__location__result')[0].classList.replace('display__inline-flex', 'display__none');
}
//위치 아이콘 선택 시 동작
function openMapPop(){
    let option = 'top=320, left=660, width=800, height=500, status=no, menubar=no, toolbar=no, resizable=no';
    window.open('/api/kakao-map', '팝업', option);
}
//등록 시 필수값 체크
function updateCheck() {
    let title = document.getElementsByName('title')[0];
    let price = document.getElementsByName('price')[0];


    if( title.value == '' ) {
        alert('제목을 입력해주세요.');
        title.focus();
        return false;
    }else if( price.value == '' ) {
        alert('금액을 입력해주세요.');
        price.focus();
        return false;
    }

    document.getElementsByClassName('update__form')[0].submit();
}