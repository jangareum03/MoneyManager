//----------[ ▼ input text 생성 후 반환합니다. ]----------
function createTextInput( { parent, id, name, classList = [], value, placeHolder } ) {
    const input = document.createElement('input');
    input.type = 'text';

    if (id) input.id = id;
    if (name) input.name = name;
    if( value ) input.value = value;
    if( placeHolder ) input.placeholder = placeHolder;
    classList.forEach(c => input.classList.add(c));


    if (parent) {
        parent.appendChild(input);
    }

    return input;
}



//----------[ input hidden 생성 후 반환합니다. ]----------
function createHiddenInput( { parent, id, name, classList = [], value } ) {
    const input = document.createElement('input');
    input.type = 'hidden';


    if( id ) input.id = id;
    if( name ) input.name = name;
    if( value ) input.value= value;

    classList.forEach( c => input.classList.add(c) );

    if( parent ) {
        parent.appendChild(input);
    }

    return input;
}



//----------[ ▼ input text 생성 후 반환합니다. ]----------
function createPasswordInput( { parent, id, name, classList = [], value, placeHolder } ) {
    const input = document.createElement('input');
    input.type = 'password';

    if (id) input.id = id;
    if (name) input.name = name;
    if( value ) input.value = value;
    if( placeHolder ) input.placeholder = placeHolder;
    classList.forEach(c => input.classList.add(c));


    if (parent) {
        parent.appendChild(input);
    }

    return input;
}



//----------[ radio 생성 ]----------
function createRadio( { parent, id, name, value, classList = [], label } ) {
    const radio = document.createElement('input');
    radio.type = 'radio';

     if (id) radio.id = id;
    if (name) radio.name = name;
    if (value) radio.value = value;
    classList.forEach(c => radio.classList.add(c));

    if (parent) {
        parent.appendChild(radio);
        if (label) parent.appendChild(document.createTextNode(' ' + label));
    }

    return radio;
}



//----------[ checkbox 생성 ]----------
function createCheckbox( { id, name, value, classList = [], label, parent } ) {
    const radio = document.createElement('input');
    radio.type = 'checkbox';

     if (id) radio.id = id;
    if (name) radio.name = name;
    if (value) radio.value = value;
    classList.forEach(c => radio.classList.add(c));

    if (parent) {
        parent.appendChild(radio);
        if (label) parent.appendChild(document.createTextNode(' ' + label));
    }

    return radio;
}



//----------[ file 생성 ]----------
function createFile({ parent, accept, name, classList }) {
    const file = document.createElement('input');
    file.type = 'file';

    if( accept ) file.accept = accept;
    if( name ) file.name = name;

    classList.forEach( c => file.classList.add(c) );

    if( parent ) {
        parent.appendChild(file);
    }

    return file;
}



//----------[ ▼ form 생성 후 반환합니다. ]----------
function createForm({ parent, method = 'GET', action, classList = [] }) {
    const form = document.createElement('form');

    form.method = method;
    form.action = action;

    return form;
}



//----------[ label 생성 ]----------
function createLabel({ parent, classList= [], forName } ) {
    const label = document.createElement('label');

    classList.forEach( c => label.classList.add(c));

    if( forName ) label.setAttribute('for', forName);

    if( parent ) {
        parent.appendChild(label);
    }

    return label;
}


//----------[ button 생성 ]----------
function createButton( {parent, type, id, name, classList = [], text} ) {
    const newButton = document.createElement('button');
    newButton.type = type;


    if (id) newButton.id = id;
    if (name) newButton.name = name;
    if( text ) newButton.textContent = text;
    classList.forEach(c => newButton.classList.add(c));

    if (parent) {
        parent.appendChild(newButton);
    }

    return newButton;
}

//----------[ select 생성 ]----------
function createSelect( { parent, name, id, classList = [], options, initOption = false } ) {
    const newSelect = document.createElement('select');

    //기본 옵션 설정
    if( initOption === true ) {
        const defaultOption = document.createElement('option');

        defaultOption.value = '';
        defaultOption.textContent = '-- 선택 --';
        defaultOption.disabled = true;
        defaultOption.selected = true;

        newSelect.appendChild(defaultOption);
    }

    if( name ) newSelect.name = name;
    if( id ) newSelect.id = id;
    classList.forEach(c => newSelect.classList.add(c));

    options.forEach( ({ code, name, defaultChecked }) => {
        const op = document.createElement('option');

        op.value = code;
        op.textContent = name;

        if( defaultChecked ) {
            op.selected = true;
        }
        newSelect.appendChild(op);
    });

    if (parent) {
        parent.appendChild(newSelect);
    }

    return newSelect;
}




//----------[ span 생성 ]----------
function createSpan( {parent, id, classList= [], text} ) {
    const newSpan = document.createElement('span');
    newSpan.textContent = text;

    if( id ) newSpan.id = id;
    classList.forEach(c => newSpan.classList.add(c));

    if( parent ) {
        parent.appendChild(newSpan);
    }

    return newSpan;
}



//----------[ p 생성 ]----------
function createP( {parent, id, classList= [], text} ) {
    const newP = document.createElement('p');
    newP.textContent = text;

    if( id ) newP.id = id;
    classList.forEach(c => newP.classList.add(c));

    if( parent ) {
        parent.appendChild(newP);
    }

    return newP;
}


//----------[ div 생성 ]----------
function createDiv( { id, classList = [], parent } ) {
    const newDiv = document.createElement('div');

    if( id ) newDiv.id = id;
    classList.forEach(c => newDiv.classList.add(c));

    if( parent ) {
        parent.appendChild(newDiv);
    }

    return newDiv;
}


//----------[ svg 생성 ]----------
function createSVG( { parent, link, viewBox, paths=[], classList = [] } ) {
    const svg = document.createElementNS( link, 'svg' );
    svg.setAttribute('viewBox', viewBox);

    classList.forEach( c => svg.classList.add(c) );

    paths.forEach( ({ d, fill, fillRule, clipRule, strokeLinejoin, strokeLinecap }) => {
        const path = document.createElementNS( link, 'path' );
        path.setAttribute('d', d);

        if( fill ) path.setAttribute('fill', fill);
        if( fillRule ) path.setAttribute('fill-rule', fillRule);
        if( clipRule ) path.setAttribute('clip-rule', clipRule);
        if( strokeLinejoin ) path.setAttribute('strokeLinejoin', strokeLinejoin);
        if( strokeLinejoin ) path.setAttribute('strokeLinecap', strokeLinecap);

        svg.appendChild(path);
    });

    if( parent ) {
        parent.appendChild(svg);
    }

    return svg;
}



//----------[ ▼ img 태그를 생성 합니다. ]----------
function createImg( {parent, src, id, classList = [], alt } ) {
    const img = document.createElement('img');

    if (id) img.id = id;
    if( src ) img.src = src;
    if (alt) img.alt = alt;

    classList.forEach(c => img.classList.add(c));

    if( parent ) {
        parent.appendChild( img );
    }

    return img;
}


//----------[ 이미지 삽입 ]----------
function loadImage({ parent, url, classList = [] }) {
    return fetch( url )
           .then( response => response.text() )
           .then( data => {
               parent.innerHTML = data;
           });
}


//----------[ 요소 삭제 ]----------
function removeElements( classList = [] ) {
    classList.forEach( c => { document.querySelectorAll(`.${c}`).forEach( el => el.remove() ) });
}



//----------[ ▼ 클래스이름을 지웁니다. ]----------
function removeClassName( classList = [] ) {
    classList.forEach( className => {
        document.querySelectorAll(`.${className}`).forEach( e => {
            e.classList.remove(className);
        });
    });
}



//----------[ ▼ url 페이지로 이동합니다. ]----------
function goToPage( url ) {
    window.location.assign(url);
}



//----------[ ▼ 입력창의 에러 테두리를 삭제합니다. ]----------
function removeErrorBorder() {
    const borders = document.querySelectorAll('.form__input--error');

    borders.forEach( border => {
        border.classList.remove('form__input--error');
    });
}



//----------[ ▼ 메시지를 페이지에  표시합니다. ]----------
function renderMessage( container, errorData ) {
    let className = 'message--success';
    if( !errorData.success ) {
        className = 'message--error';
    }

    createSpan({ parent: container, classList: [className], text: errorData.message });
}



//----------[ ▼ 메시지를 제거합니다. ]----------
function removeMessage( success = true, error = true ) {
    //성공 메세지 제거
    if( success ) {
        const successMessages = document.querySelectorAll('.message--success');

        if( successMessages ) {
            successMessages.forEach( (message) => {
                message.remove();
            });
        }
    }

    //실패 메세지 제거
    if( error ) {
        const errorMessages = document.querySelectorAll('.message--error');
        if( errorMessages ) {
            errorMessages.forEach( (message) => {
                message.remove();
            })
        }
    }
}



//----------[ ▼ 인증코드 인증시간 타이머를 생성합니다. ]----------
const MAX_MINUTE = 1;
const EmailTimer = ( () => {
    let minute = 1;
    let second = 0;
    let clock = null;

    function startTimer( timer ) {

        clock = setInterval(() => {
            if( minute === 0 && second === 0 ) {  //시간 초과한 경우
                EmailTimer.reset();

                const event = new CustomEvent("authTimerEnd");
                window.dispatchEvent(event);

                return;
            }

            if( second === 0 ) {
                if( minute > 0 ) {
                    minute--;
                    second = 59;
                }
            }else {
                second--;
            }

            timer.textContent = `0${minute} : ${second < 10 ? '0' + second : second}`;
        }, 1000);
    }

    return {
        start: startTimer,
        getMinute: () => minute,
        getSecond: () => second,
        reset: () => { minute = MAX_MINUTE; second = 0; clearInterval(clock); }    };
})();



//----------[ ▼ 검증을 완료하지 않은 요소에 에러 테두리를 표시 합니다. ]----------
function showErrorBorder() {
    const inputs = document.querySelectorAll('.form__input');

    inputs.forEach( input => {
        const name = input.name;
        const isValid = status[name];

        if( isValid ) {
            input.classList.remove('form__input--error');
        }else {
            input.classList.add('form__input--error');
        }
    });
}



//----------[ ▼ Url에서 날짜값을 얻습니다. ]----------
function parseDateFromUrl() {
    const params = new URLSearchParams(window.location.search);

    return {
        urlYear: Number( params.get('year') ),
        urlMonth: Number( params.get('month') ),
        urlWeek: Number( params.get('week') )
    };
}



//----------[ ▼ 선택한 탭의 검색메뉴를 표시합니다. ]---------
function renderSearchMenu( container, mode ) {
    switch( mode ) {
        case 'inout': renderInOutCategories( container, mode ); break;
        case 'category': renderCategoryHierarchy( container ); break;
        case 'memo': renderMemoSearch( container ); break;
        case 'period': renderPeriodSearch( container ); break;
        case 'title': renderTitleSearch( container ); break;
        case 'writer': renderWriterSearch( container ); break;
        case 'answer': renderAnswerSearch( container ); break;
        default:
            renderSearchResults();
    }
}



//----------[ ▼ 금액을 '#,###' 형식으로 지정합니다. ]----------
function formatNumber( num ) {
    return Number(num).toLocaleString('ko-KR');
}