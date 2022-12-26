//위치 아이콘 선택 시 동작
function openMapPop(){
    let option = 'top=320, left=660, width=800, height=500, status=no, menubar=no, toolbar=no, resizable=no';
    window.open('/write/map', '팝업', option);
}

//고정 or 변동 선택 시 동작
function showOption(){
    let fix = document.getElementById('fix');
    let cycle = document.getElementsByClassName('fixCycle');

    if( fix.checked ) {
        document.getElementById('fixOption').style.display = 'block';
    }else{
        document.getElementById('fixOption').style.display = 'none';
        for( let i=0; i<cycle.length; i++ ) {
            cycle[i].checked = false;
        }
    }
}

//카테고리 선택 시 하위카테고리 표시
function changeCategory( category ){
    let categoryArea = document.getElementById('categoryArea');
    let categoryL = document.getElementById('categoryL');
    let categoryM = document.getElementById('categoryM');

    if( categoryM != null ) {
        categoryM.remove();
    }

    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/write/category', true);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send('code=' + category.value);

    xhr.onload = function() {
        if( xhr.status == 200 ) {
            const new_select = document.createElement('select');
            let result = JSON.parse(xhr.response);

            if( result.length == 0 ) {
                categoryL.setAttribute('name', 'category');
                return false;
            }else{
                for( i=0; i<result.length; i++ ) {
                    const new_option = document.createElement('option');
                    new_option.value = result[i].code;
                    new_option.text = result[i].name;

                    new_select.appendChild(new_option);
                }

                new_select.setAttribute('class', 'wCategory');
                new_select.setAttribute('id', 'categoryM');
                new_select.setAttribute('name', 'category');
                new_select.style.display = 'block';
                new_select.style.width = '80px';

                categoryArea.appendChild(new_select);
                categoryL.removeAttribute('name');
            }
        }else{
            alert('통신 실패했습니다. 잠시 후 다시 시도해주세요.');
        }
    }
}

//가격에 숫자만 입력
function checkPrice( target ){
    const pattern = /[^0-9]/g;

    if( pattern.test(target.value) ) {
        target.value = target.value.replace(pattern, '');
    }
}

//위치 지우기
function removeLocation() {
    document.getElementById('mapName').value = '';
    document.getElementById('mapRoad').value = '';
    document.getElementById('mapAddr').value = '';

    document.getElementById('location').style.display = 'none';
    document.getElementById('mapInput').style.display = 'block';
}


//등록 시 필수값 체크
function writeCheck() {
    let categoryL = document.getElementById('categoryL');
    let title = document.getElementsByName('title')[0];
    let price = document.getElementsByName('price')[0];
    let priceType = document.getElementsByName('priceType')[0];

    if( categoryL.value == '00' ) {
        alert('카테고리를 선택해주세요.');
        category.focus();
        return false;
    }else if( title.value == '' ) {
        alert('제목을 입력해주세요.');
        title.focus();
        return false;
    }else if( price.value == '' ) {
        alert('금액을 입력해주세요.');
        price.focus();
        return false;
    }else if( priceType.value == '' ) {
        alert('금액 타입을 선택해주세요.');
        priceType.focus();
        return false;
    }

    return true;
}