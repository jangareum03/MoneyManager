document.addEventListener('DOMContentLoaded', () => {
    initLink();
});



//----------[ ▼ 링크를 초기화합니다.. ]----------
function initLink() {
    const links = document.querySelectorAll('.index-link');
    links.forEach( link => {
        link.addEventListener( 'click', handlerClick );
    });
}



//----------[ ▼ 요소를 클릭할 때 이벤트를 설정합니다. ]----------
function handlerClick( event ) {
    const target = event.currentTarget;

    //설정 메뉴의 링크 클릭할 때
    if( target.classList.contains('index-link--setting') ) {
        alert('아직 준비중인 기능입니다.');
    }
}