document.addEventListener('DOMContentLoaded', () => {
    initInputText();
    initInputRadio();
    initInputFile();
    initSelect();
    initButton();
    initLocationPopup();
});



//----------[ ▼ 입력창을 초기화합니다. ]----------
function initInputText() {
    const priceInput = document.querySelector('input[name="price"]');
    if( priceInput ) {
        priceInput.addEventListener( 'keyup', handlerKeyUp );
    }
}



//----------[ ▼ 라디오 입력을 초기화합니다. ]----------
function initInputRadio() {
    const radioInputs = document.querySelectorAll('.form__input--radio');
    radioInputs.forEach( radio => {
        radio.addEventListener( 'change', handlerChange );
    });
}



//----------[ ▼ 파일 입력을 초기화합니다. ]----------
function initInputFile() {
    const fileInputs= document.querySelectorAll('.form__input--file');
    fileInputs.forEach( file => {
        file.addEventListener( 'change', handlerChange );
    });
}



//----------[ ▼ select 를 초기화합니다. ]----------
function initSelect() {
    const categorySelects = document.querySelectorAll('.form__select');
    categorySelects.forEach(select => {
        select.addEventListener( 'change', handlerChange );
    });
}



//----------[ ▼ 버튼을 초기화합니다. ]----------
function initButton() {
    const buttons = document.querySelectorAll('.button');
    buttons.forEach( button => {
        button.addEventListener( 'click', handlerClick );
    })
}



//----------[ ▼ 위치영역을 초기화합니다. ]----------
function initLocationPopup() {
    const locationBox = document.querySelector('.form__input-box--empty');
    if( locationBox ) {
        locationBox.addEventListener( 'click', handlerClick );
    }
}



//----------[ ▼ 요소를 클릭할 때 이벤트를 설정합니다. ]----------
async function handlerClick( event ) {
    const target = event.currentTarget;

    //취소버튼 클릭할 때
    if( target.classList.contains('button--cancel') ) {
        if( confirm('정말로 취소하시겠습니까?') ) {
            const id = getBudgetId();

            goToPage(`/budgetBooks/${id}`);
        }
    }


    //저장버튼 클릭할 때
    if( target.classList.contains('button--default') ) {
        event.preventDefault();

        //에러 메세지 삭제
        removeMessage();

        const status = validEssentialValue( document.querySelector('.budget-body .form') );
        const isValid = Object.values( status ).every( s => s === true );

        if( !isValid ) {
            alert('가계부 작성에 필요한 필수정보를 입력하지 않았습니다. 다시 확인해주세요!');
            return;
        }

        const formData = getFormData();
        const id = getBudgetId();

        const apiResult = await fetchBudgetUpdate( id, formData );

        alert(apiResult.message);
        if( apiResult.success ) {
            goToPage(`/budgetBooks/${id}`);
        }
    }


    //이미지 등록된 상태에서 삭제버튼 클릭할 때
    const photoBox = target.closest('.form__label');
    if( photoBox ) resetPhotoUpload( photoBox );


    //위치영역 클릭할 때
    const locationContainer = target.closest('.form__input-container--location');
    if( locationContainer ) {
        if( target.classList.contains('icon--del') ) {
            resetLocation( locationContainer );
        }else {
            openMapPopup();
        }
    }
}



//----------[ ▼ 요소를 변경할 때 이벤트를 설정합니다. ]----------
function handlerChange( event ) {
    const target= event.currentTarget;
    let container;

    //고정&변동 라디오 버튼 변경할때
    if( target.closest('.form__input-box--main') ) {
        container = document.querySelector('.form__input-container--fix');
        const optionMenu = container.querySelector('.form__input-box--option');

        if( target.value === 'y' && !optionMenu ) {
            renderOptionMenu( container );
        }else if( target.value === 'n' && optionMenu ){
            optionMenu.remove(); //옵션 메뉴 삭제
        }
    }

    //중간 카테고리 선택박스를 변경할 때
    container = target.closest('.form__input-box--category');
    if( container ) {
        renderSubSelect( container );
    }

    //사진을 변경할 때
    container = target.closest('.form__input-box--photo');
    if( container ) {
        const fileInput = target;

        if( fileInput.files && fileInput.files[0] ) {
            const fileBox = target.closest('.form__label');

            if( !fileBox ) return;

            fileBox.querySelector('.icon--enabled')?.remove();
            fileBox.querySelector('.photo__preview')?.remove();
            fileBox.querySelector('.button')?.remove();

            //이미지 미리보기
            previewPhoto( fileInput );
        }
    }
}



//----------[ ▼ 요소에 글자를 입력할 때 이벤트를 설정합니다. ]----------
function handlerKeyUp( event ) {
    const target = event.currentTarget;
    let container;

    //금액 입력창에서 글자를 입력할 때
    container = target.closest('.form__input-box--main');
    if( container ) {
        const pattern = /[^0-9]/g;
        const input = container.querySelector('input[type="text"]');

        if( pattern.test( input.value ) ) {
            input.value = input.value.replace(pattern, '');
        }
    }
}



//----------[ ▼ 고정 선택 시에 옵션메뉴가 표시됩니다. ]----------
function renderOptionMenu( container ) {
    if( !container ) return;

    const optionBox = document.createElement('section');
    optionBox.classList.add('form__input-box', 'form__input-box--option');
    container.appendChild(optionBox);

    const options = [
        { label: '일년', value: 'y' },
        { label: '한달', value: 'm', defaultChecked: true },
        { label: '일주일', value: 'w' }
    ];

    options.forEach( ({ label, value, defaultChecked }) => {
        const optionLabel = createLabel({ parent: optionBox, classList: ['form__label'] });

        const optionRadio = createRadio({ parent: optionLabel, name: 'cycle', classList: ['form__input', 'form__input--radio'], value: value, label: label });
        if( defaultChecked ) optionRadio.checked = true;
    });
}



//----------[ ▼ 하위 카테고리가 표시됩니다. ]----------
async function renderSubSelect( container ) {
    const middleSelect = container.querySelector('.form__select--middle');
    const subSelect = container.querySelector('.form__select--sub');

    if( subSelect )  subSelect.remove();    //기존 하위 카테고리 삭제
    if( !middleSelect || !middleSelect.value ) return;

    const categoryList = await fetchCategoryInfo( middleSelect.value );

    if( !categoryList.length ) {
        middleSelect.setAttribute('name', 'category');
        return;
    }

    const newSelect = createSelect({ parent:container, name: 'category', classList: ['form__select', 'form__select--sub'], options: categoryList, initOption: true } );
    middleSelect.removeAttribute('name');
}



//----------[ ▼ 선택한 파일로 미리보기 이미지가 표시됩니다. ]----------
function previewPhoto( fileInput ) {
    const file = fileInput.files[0];

    createImg({ parent: fileInput.parentNode, src: URL.createObjectURL(file), alt: '사진 미리보기', classList: ['photo__preview'] });
    const delButton =
                    createButton({ parent: fileInput.parentNode, type: 'button', classList: ['button', 'button--nano', 'button--danger'], text: '삭제' });

    delButton.addEventListener('click', handlerClick);
}



//----------[ ▼ 사진이 초기 상태로 표시됩니다. ]----------
function resetPhotoUpload( container ) {
    if( !container ) return;

    //이미지 및 버튼 삭제
    container.querySelector('.photo__preview')?.remove();
    container.querySelector('.button')?.remove();

    //input 파일 초기화
    const inputFile = container.querySelector('.form__input--file');
    if( inputFile ) {
        inputFile.value = '';
        inputFile.disabled = true;
    }

    //기존 아이콘 생성
    createSVG({
        parent: container,
        link: 'http://www.w3.org/2000/svg',
        viewBox: '0 0 27 27',
        classList: ['icon', 'icon--enabled'],
        paths: [
            { d: 'M24.1667 1H3.33333C2.18274 1 1.25 1.93274 1.25 3.08333V23.9167C1.25 25.0673 2.18274 26 3.33333 26H24.1667C25.3173 26 26.25 25.0673 26.25 23.9167V3.08333C26.25 1.93274 25.3173 1 24.1667 1Z', strokeLinejoin: 'round' },
            { d: 'M13.75 7.94434V19.0554M8.19446 13.4999H19.3056', strokeLinejoin: 'round', strokeLinecap: 'round' }
        ]
    });

    setTimeout( () => {
        inputFile.disabled = false;
    }, 100);
}



//----------[ ▼ 위치정보를 검색할 수 있는 지도 팝업이 열립니다. ]----------
function openMapPopup() {
    const option = 'top=320, left=660, width=800, height=500, status=no, menubar=no, toolbar=no, resizable=no';
    const popup = window.open('/budgetBook/write/map', '팝업', option);

    const checkPopupClosed = setInterval( () => {
        if( popup.closed ) {
            clearInterval(checkPopupClosed);

            //요소 추가
            const container = document.querySelector('.form__input-container--location');

            if( container.querySelector('.form__input-box--filled') && !container.querySelector('.icon--del') ) {
                const delIcon = createSVG({
                        parent: container,
                        link: 'http://www.w3.org/2000/svg',
                        viewBox: '0 0 24 24',
                        classList: ['icon', 'icon--del'],
                        paths: [
                            { d: 'M3.121 17.85a3 3 0 0 1 0-4.243l7.071-7.072l8.486 8.486l-3.243 3.242H20a1 1 0 1 1 0 2H6.778a3 3 0 0 1-2.121-.878L3.12 17.849zm16.97-4.243l1.415-1.415a3 3 0 0 0 0-4.242l-4.243-4.243a3 3 0 0 0-4.242 0l-1.414 1.414z', fillRule: 'evenodd', clipRule: 'evenodd', fill: 'currentColor' }
                        ]
                });
                delIcon.addEventListener( 'click', handlerClick );
            }
        }
    }, 300);
}



//----------[ ▼ 위치정보를 초기 상태로 표시합니다. ]----------
function resetLocation( container ) {
    const locationBox = container.querySelector('.form__input-box--filled');

    container.querySelector('.icon--del')?.remove();
    locationBox.replaceChildren();

    locationBox.classList.remove('form__input-box--filled');
    locationBox.classList.add('form__input-box--empty');

    createImg({ parent: locationBox, src: '/image/location.png', alt: '위치 아이콘' });
    createSpan({ parent: locationBox, text: '위치를 입력해주세요.' });
}




//----------[ ▼ 현재 주소에서 가계부 번호를 가져옵니다. ]----------
function getBudgetId() {
    const pathParts = window.location.pathname.split('/');

    return pathParts[2];
}



//----------[ ▼ 가계부 필수정보 입력여부를 확인합니다. ]----------
function validEssentialValue( container ) {
    const status = { category:  false, price: false };

    //카테고리 확인
    const categorySelect = container.querySelector('select[name="category"]');
    if( !categorySelect || categorySelect.value === '' ) {
        status.category = false;

        renderMessage(
            container.querySelector('.form__field--category .form__label--essential') ,
            { success: false, message: '카테고리를 선택해주세요.' }
        );
    }else {
        status.category = true;
    }

    //가격 확인
    const priceInput = container.querySelector('input[name="price"]');
    if( !priceInput.value || priceInput.value === 0  ) {
        status.price = false;

        renderMessage(
                container.querySelector('.form__field--price .form__label--essential'),
                { success: false, message: '금액을 입력해주세요.' }
        );
    }else{
        status.price = true;
    }


    return status;
}



//----------[ ▼ 전송할 데이터를 반환합니다. ]----------
function getFormData() {
    const form = new FormData();

    const data = {
        date : document.querySelector('.budget-header__title').textContent,
        fix : {
            option: document.querySelector('input[type="radio"][name="option"]:checked').value,
            cycle : document.querySelector('input[type="radio"][name="cycle"]:checked')?.value || ''
        },
        category : document.querySelector('select[name="category"]').value,
        memo : document.querySelector('textarea[name="memo"]')?.value.trim() || '',
        price : document.querySelector('input[type="text"][name="price"]')?.value || 0,
        paymentType : document.querySelector('input[type="radio"][name="paymentType"]:checked')?.value || 'none',
        map: {
            name: document.querySelector('.location__name')?.textContent || '',
            roadAddress : document.querySelector('.location__road')?.textContent || '',
            address : document.querySelector('.location__add')?.textContent || ''
        }
    };

    form.append( 'update', new Blob([JSON.stringify(data)], {type: 'application/json'}) );

    //파일 추가
    const images = document.querySelectorAll('.form__input-box--photo .form__input--file');
    for( let i=0; i < images.length; i++ ) {
        const file = images[i].files[0];

        if( file ) {
            form.append('image', file);
        }
    }

    return form;
}



