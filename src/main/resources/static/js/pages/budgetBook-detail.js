document.addEventListener('DOMContentLoaded', () => {
    initButton();
    initIcon();
    initImage();
});



//----------[ ▼ 버튼을 초기화합니다. ]----------
function initButton() {
    const buttons = document.querySelectorAll('.button');

    buttons.forEach( (button) => {
        button.addEventListener( 'click', handlerClick );
    });
}



//----------[ ▼ 아이콘을 초기화합니다. ]----------
function initIcon() {
    const icons = document.querySelectorAll('.icon');

    if( icons ) {
        icons.forEach( (icon) => {
            icon.addEventListener( 'click', handlerClick );
        });
    }
}



//----------[ ▼ 이미지를 초기화합니다. ]----------
function initImage() {
    const images = document.querySelectorAll('.detail-additional__photo--filled');
    images.forEach( image => {
        image.addEventListener( 'click', handlerClick );
    });
}



//----------[ ▼ 요소를 클릭할 때 이벤트를 설정합니다. ]---------
async function handlerClick( event ) {
    const target = event.currentTarget;

    //뒤로가기 아이콘을 클릭할 때
    if( target.classList.contains('icon--back') ) {
        goToPage('/budgetBooks/list/month');
    }


    //이미지를 클릭할 때
    if( target.closest('.detail-additional__photo-item') ) {
        renderLargeImage( target.src );
    }


    //수정버튼 클릭할 때
    const mode = target.dataset.mode
    if(  mode === 'edit' ) {
        const id = getBudgetId();

        goToPage(`/budgetBooks/${id}?mode=${mode}`);
    }


    //삭제버튼 클릭할 때
    if( mode=== 'delete' && confirm('정말로 삭제하시겠습니까?') ) {
        const id = getBudgetId()

        const apiResult = await fetchBudgetDelete( [id] );

        alert(apiResult.message);
        if( apiResult.success ) {
            history.back();
        }
    }
}



//----------[ ▼ 현재 주소에서 가계부 번호를 가져옵니다. ]----------
function getBudgetId() {
    const pathParts = window.location.pathname.split('/');

    return pathParts[ pathParts.length - 1 ];
}



//----------[ ▼ 이미지를 클릭하면 큰 이미지로 보여줍니다. ]----------
function renderLargeImage( url ) {
    const container = document.querySelector('.page-budget-detail');

    const imageBox = createDiv( { parent: container, classList: ['detail-photo__viewer'] } );
    const imageText = createSpan( {parent: imageBox, classList: ['detail-photo__viewer-guide'], text: '아무곳이나 클릭하면 이미지가 없어집니다.'} );
    const boxImage = createImg( {parent: imageBox, src: url, classList: ['detail-photo__viewer-image']} );

    imageBox.addEventListener( 'click', () => imageBox.remove() );
}