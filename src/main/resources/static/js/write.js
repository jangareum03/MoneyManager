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

function checkPrice( target ){
    const pattern = /[^0-9]/g;

    if( pattern.test(target.value) ) {
        target.value = target.value.replace(pattern, '');
    }
}