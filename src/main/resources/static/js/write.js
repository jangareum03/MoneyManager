function openMapPop(){
    let option = 'top=320, left=660, width=800, height=500, status=no, menubar=no, toolbar=no, resizable=no';
    window.open('/write/map', '팝업', option);
}

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

function changeCategory( categoryL ){
    let category = document.getElementById('category');

    category.value = categoryL.value;
}

function checkPrice( target ){
    const pattern = /[^0-9]/g;

    if( pattern.test(target.value) ) {
        target.value = target.value.replace(pattern, '');
    }
}

function writeCheck() {
    let wTitle = document.getElementsByClassName('wTitle')[0].value;
    let wPrice = document.getElementsByClassName('wPrice')[0].value;
    let categoryL = document.getElementById('categoryL').value;
    let categoryM = document.getElementById('categoryM').value;

    if( categoryL == '00' ) {
        alert('카테고리를 선택해주세요.');
        return false;
    }else if( wTitle.replace(/ /g, '').length == 0 ) {
        alert('제목을 작성해주세요.');
        return false;
    }else if( wPrice.length == 0 ) {
        alert('금액을 입력해주세요.');
        return false;
    }
}