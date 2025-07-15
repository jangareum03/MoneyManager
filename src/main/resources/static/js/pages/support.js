document.addEventListener('DOMContentLoaded', () => {
    initMenuTab();
    initSelect();
    initLink();
    initIcon();
    initInputCheckbox();
    initButton();
})



//----------[ ▼ 서브탭을 초기화합니다. ]----------
function initMenuTab() {
    const tabs = document.querySelectorAll('.support-menu__item');

    tabs.forEach( (tab) => {
        tab.addEventListener( 'click', handlerClick );
    });
}



//----------[ ▼ 선택박스를 초기화합니다. ]----------
function initSelect() {
    const selectBox = document.querySelector('.form__select');
    if( selectBox ) {
        selectBox.addEventListener( 'change', handlerChange );
    }
}



//----------[ ▼ 링크를 초기화합니다. ]----------
function initLink() {
    const links = document.querySelectorAll('.table__link');
    links.forEach( link => {
        link.addEventListener( 'click', handlerClick );
    });
}


//----------[ ▼ 아이콘을 초기화합니다. ]----------
function initIcon() {
    const icons = document.querySelectorAll('.icon');
    icons.forEach( icon => {
        icon.addEventListener( 'click', handlerClick );
    });
}



//----------[ ▼ 버튼을 초기화합니다. ]----------
function initButton() {
    //페이지네이션 버튼
    const pageButtons = document.querySelectorAll('.pagination__button');
    pageButtons.forEach( button => {
        button.addEventListener( 'click', handlerClick );
    });

    const buttons = document.querySelectorAll('.button');
    buttons.forEach( button => {
        button.addEventListener( 'click', handlerClick );
    });
}



//----------[ ▼ 체크박스를 초기화합니다. ]----------
function initInputCheckbox() {
    const lockBox = document.querySelector('.form__input--checkbox');
    if( lockBox ) {
        lockBox.addEventListener( 'change', handlerChange );
    }
}



//----------[ ▼ 요소를 클릭할 때 이벤트를 설정합니다. ]----------
function handlerClick( event ) {
    const type = getCurrentPageType();
    const target = event.currentTarget;
    let container;

    //서브탭을 클릭할 때
    if( target.closest('.support-menu__list') ) {
        const menu = target.dataset.menu;
        const url = `/support/${menu}`;

        goToPage(url);
    }


    //검색영역의 버튼 및 라디오 버튼을 클릭할 때
    container = target.closest('.support-tab__search');
    if( container ) {
        //제목을 검색할 경우
        if( container.querySelector('input[type="text"][name="title"]') ) {
            renderSearchResults('title');
        }

        //작성자를 검색할 경우
        if( container.querySelector('input[type="text"][name="writer"]') ) {
            renderSearchResults('writer');
        }

        //기간 검색할 경우
        if( container.querySelector('input[type="text"]')?.classList.contains('hasDatepicker') ) {
            renderSearchResults('period');
        }

        //답변상태 검색할 경우
        if( container.querySelector('input[type="radio"][name="answer"]') ) {
            renderSearchResults('answer');
        }
    }

    //제목을 클릭할 때
    container = target.closest('.table__cell--title');
    if( container ) {
        const id = target.dataset.id;

        //비공개 글을 클릭한 경우
        if( target.classList.contains('table__link--locked') ) {
            if( !isWriter() ) {
                alert('비밀글이여서 접근이 불가능합니다.');
                return;
            }
        }

       goToPage(`/support/${type}/${id}`);
    }

    //뒤로가기 아이콘을 클릭할 때
    if( target.classList.contains('icon--back') ) {
        goToPage('/support/inquiry');
    }

    //페이지네이션 버튼을 클릭할 때
    container = target.closest('.pagination');
    if( container ) {
        const type = getCurrentPageType();
        let number;

        //이전 버튼
        if( target.classList.contains('pagination__button--prev') ) {
            number = target.dataset.start;
            number =- 1;
        }

        //번호버튼
        if( target.classList.contains('pagination__button--number') ) {
            number = target.dataset.page;
        }

        //다음버튼
        if( target.classList.contains('pagination__button--next') ) {
            number = target.dataset.end;
            number =+ 1;
        }
        number = Number(number);

        goToPage(`/support/${type}?num=${number}`);
    }

    //등록하기 버튼 클릭할 때
    if( target.closest('.support__actions') ) {
        goToPage('/support/inquiry/write');
    }

    //공지사항 상세 화면에서 버튼 클릭할 때
    if( target.closest('.inquiry__actions') ) {
        //이전 버튼을 클릭할 때
        if( target.dataset.mode === 'prev' ) {
            goToPage('/support/inquiry');
        }

        //수정 버튼을 클릭할 때
        if( target.dataset.mode === 'edit' ) {
            const id = window.location.pathname.split('/')[3];
            goToPage(`/support/inquiry/edit/${id}`);
        }

        //삭제 버튼을 클릭할 때
        if( target.dataset.mode === 'delete' ) {
            const id = window.location.pathname.split("/")[3];

            confirmAndDelete( id );
        }
    }

    //공지사항 작성&수정 화면에서 버튼 클릭할 때
    if( target.closest('.form__actions') ) {
        //이전 버튼 클릭할 때
        if( target.dataset.mode === 'prev' && confirm('정말로 작성을 취소하시겠습니까?') ) {
            goToPage('/support/inquiry');
        }

        //취소 버튼 클릭할 때
        if( target.dataset.mode === 'cancel' && confirm('정말로 수정을 취소하시겠습니까?') ) {
            const id = window.location.pathname.split('/')[4];
            goToPage(`/support/inquiry/${id}`);
        }

        //작성 및 저장 버튼 클릭할 때
        if( target.dataset.mode === 'submit' || target.dataset.mode === 'save' ) {
            event.preventDefault();

            removeMessage();

            const form = document.querySelector('.form');

            const status = validValue( form );
            const isValid = Object.values( status ).every( s => s === true );

            if( !isValid ) {
                alert('문의사항 등록에 필요한 정보를 입력하지 않았습니다. 다시 확인해주세요.');
                return;
            }

            form.submit();
        }
    }

    //문의사항 상세 화면에서 버튼 클릭할 때
    if( target.closest('.notice__actions') ) {
        //이전 버튼을 클릭할 때
        if( target.dataset.mode === 'prev' ) {
            goToPage('/support/notice');
        }
    }
}



//----------[ ▼ 요소를 변경할 때 이벤트를 설정합니다. ]----------
function handlerChange( event ) {
    const target = event.currentTarget;
    let container;

    //검색 선택박스를 변경할 때
    if( target.closest('.support-tab__filter') ) {
        container = document.querySelector('.support-tab');
        const mode = target.value;
        if( !mode ) return;

        //기존 검색메뉴 삭제 후 생성
        let searchBox = container.querySelector('.support-tab__search');
        if( searchBox ) searchBox.remove();

        searchBox = createDiv({ parent: container, classList: ['support-tab__search'] });

        renderSearchMenu( searchBox, mode );
    }

    //비밀글 여부 체크박스 변경할 때
    if( target.closest('.form__field--lock') ) {
        if( target.checked ) {
            target.value = false;
        }else {
            target.value = true;
        }
    }
}



//----------[ ▼ 제목 메뉴를 표시합니다. ]----------
function renderTitleSearch( container, mode ) {
    const label = createLabel({ parent: container });

    const input = createTextInput({ parent: label, name: 'title', classList: ['form__input', 'form__input--text'], placeHolder: '제목을 검색해주세요.' } );

    const button = createButton({ parent: container, type: 'button', classList: ['button', 'button--nano', 'button--default'], text: '검색' });
    button.addEventListener( 'click', handlerClick );
}



//----------[ ▼ 작성자 메뉴를 표시합니다. ]----------
function renderWriterSearch( container, mode ) {
    const label = createLabel({ parent: container });

    const input = createTextInput({ parent: label, name: 'writer', classList: ['form__input', 'form__input--text'], placeHolder: '작성자를 검색해주세요.' } );

    const button = createButton({ parent: container, type: 'button', classList: ['button', 'button--nano', 'button--default'], text: '검색' });
    button.addEventListener( 'click', handlerClick );
}



//----------[ ▼ 기간 메뉴를 표시합니다. ]----------
function renderPeriodSearch( container, mode ) {
    //시작 날짜 입력창
    const startLabel = createLabel({ parent: container });
    const startInput = createTextInput({ parent:startLabel, id: 'startDate', classList: ['form__input', 'form__input--text'], placeHolder: '시작날짜를 선택해주세요.' });
    startInput.setAttribute('autocomplete', 'off');

    startInput.addEventListener( 'focus', () => {
        $(startInput).datepicker( getDatePickerOptions({
            onClose : function( selectedDate ) {
                $("#endDate").datepicker("option", "minDate", selectedDate);
            }
        })).datepicker("show");
    }, {once: true});

    createSpan( {parent: container, text: '-'} );

    //종료 날짜 입력창
    const endLabel = createLabel({ parent: container });
    const endInput = createTextInput({ parent: container, id: 'endDate', classList: ['form__input', 'form__input--text'], placeHolder: '종료날짜를 선택해주세요.' });
    endInput.setAttribute('autocomplete', 'off');

    endInput.addEventListener( 'focus', () => {
        $(endInput).datepicker( getDatePickerOptions() ).datepicker("show");
    }, {once: true});

    //검색 버튼
    const button = createButton({ parent: container, type: 'button', classList: ['button', 'button--nano', 'button--default'], text: '검색' });
    button.addEventListener( 'click', handlerClick );
}



//----------[ ▼ 답변여부를 검색할 수 있는 라디오버튼을 표시합니다. ]----------
function renderAnswerSearch( container, mode ) {
    //답변이 안달린 상태
    const beforeLabel = createLabel({ parent: container, classList: [] });
    createRadio({ parent: beforeLabel, label: '답변준비', classList: ['form__input', 'form__input--radio'], name: 'answer', value: 'N' })
        .addEventListener( 'click', handlerClick );


    //답변이 달린 상태
    const afterLabel = createLabel({ parent: container, classList: [] });
    createRadio({ parent: afterLabel, label: '답변완료', classList: ['form__input', 'form__input--radio'], name: 'answer', value: 'Y' })
        .addEventListener( 'click', handlerClick );
}



//----------[ ▼ 메뉴에 따른 내역을 조회합니다. ]---------
async function renderSearchResults( mode ) {
    const data = getSearchData( mode );
    const { inquiries, search, page, resultText } = await fetchInquirySearch( data );

    const container = document.querySelector('.support-list .table__body');
    if( !container ) return;

    container.replaceChildren();        //자식요소 초기화

   const isEmpty = inquiries.length === 0;
   document.querySelector('.support-list .table').classList.toggle( 'empty', isEmpty );
   document.querySelector('.support-list .table').classList.toggle( 'filled', !isEmpty );

   const size = 5;
   isEmpty ? renderEmptyResult( container, size, resultText ) : renderFilledResult( container, size, {inquiries, page} );
   renderPagination( page );
}



//----------[ ▼ 조회한 내역 리스트가 없으면 안내 문구를 표시합니다. ]----------
function renderEmptyResult( container, tdCount, text ) {
    const tr = document.createElement('tr');
    tr.classList.add('table__row', 'table__row--empty');

    const td = document.createElement('td');
    td.setAttribute('colspan', tdCount);
    td.innerHTML = text;

    tr.appendChild(td);
    container.appendChild(tr);
}



//----------[ ▼ 조회한 내역 리스트를 표시합니다. ]----------
function renderFilledResult( container, tdCount, data ) {
    const page = data.page;
    const inquiries = data.inquiries;

    let tr;

    for( let i=0; i<10; i++ ) {
        tr = createEmptyRow( tdCount );
        container.appendChild( tr );

        if( i < inquiries.length ) {
            fillRow( tr, i+1, inquiries[i], page );
        }else {
            tr.classList.add('table__row--empty');
        }
    }
}



//----------[ ▼ 빈 행을 반환합니다. ]----------
function createEmptyRow( size ) {
    const classNames = ['table__cell--num', 'table__cell--title', 'table__cell--date', 'table__cell--writer', 'table__cell--answer'];
    const tr = document.createElement('tr');
    tr.classList.add('table__row');

    for( let i=0; i<size; i++ ) {
        const td = document.createElement('td');
        td.classList.add( 'table__cell', classNames[i] );

        tr.appendChild(td);
    }

    return tr;
}



//----------[ ▼ 행에 내용을 추가 후 반환합니다. ]----------
function fillRow( tr, index, item, page ) {
    //번호
    tr.querySelector('.table__cell--num').textContent = getRowNumber( index, page );

    //제목
    const title = tr.querySelector('.table__cell--title');

    //링크 추가
    const link = document.createElement('a');
    link.classList.add('table__link');
    link.textContent = item.title;
    link.setAttribute('data-id', item.id);
    if( !item.open ) {
        link.classList.add('table__link--locked');
    }

    link.addEventListener('click', handlerClick );
    title.appendChild(link);

    //날짜
    tr.querySelector('.table__cell--date').textContent = item.date;

    //작성자
    tr.querySelector('.table__cell--writer').textContent = item.writer;

    //답변
    const answer = tr.querySelector('.table__cell--answer');

    const span = document.createElement('span');
    span.classList.add('table__status');
     span.textContent = item.answer.text;

     if( item.answer.code === 'Y' ) {
        span.classList.add('completed');
     }else {
        span.classList.add('waiting');
     }

     answer.appendChild(span);
}



//----------[ ▼ 조회한 내역 리스트의 페이지네이션을 표시합니다. ]----------
function renderPagination( page ) {
    const container = document.querySelector('.support-footer');
    if( !container ) return;

    container.replaceChildren();        //자식요소 초기화

    const paginationBox = createDiv({ parent: container, classList: ['pagination'] });

    //이전 버튼
    if( page.prev ) {
        const prevButton = document.createElement('button');
        prevButton.innerHTML = '&ltrif;';
        prevButton.setAttribute('data-start', page.start);
        prevButton.classList.add('pagination__button', 'pagination__button--prev');

        prevButton.addEventListener( 'click', handlerClick );

        paginationBox.appendChild(prevButton);
    }

    //번호 버튼
    for( let i=page.start; i<=page.end; i++ ) {
        const numButton = document.createElement('button');
        numButton.textContent = i;
        numButton.setAttribute('data-page', i);
        numButton.classList.add('pagination__button', 'pagination__button--number');

        if( i === page.page ) {
            numButton.classList.add('pagination__button--selected');
        }else {
            numButton.addEventListener( 'click', handlerClick );
        }

        paginationBox.appendChild(numButton);
    }

    //다음버튼
    if( page.next ) {
        const nextButton = document.createElement('button');
        nextButton.innerHTML = '&rtrif;';
        nextButton.setAttribute('data-end', page.end);
        nextButton.classList.add('pagination__button', 'pagination__button--next');

        nextButton.addEventListener( 'click', handlerClick );

        paginationBox.appendChild(nextButton);
    }
}



//----------[ ▼ DatePicker 옵션을 얻습니다. ]----------
function getDatePickerOptions( closeOptions = {} ) {
    return Object.assign({
        changeMonth: "true",
        changeYear: "true",
        showMonthAfterYear : "true",
        yearRange: "c-5:c",
        yearSuffix: "년",
        dateFormat: "yymmdd",
        dayNames: ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"],
        dayNamesMin: ["일", "월", "화", "수", "목", "금", "토"],
        monthNames: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        monthNamesShort: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"],
        nextText: "다음달",
        prevText: "이전달",
        maxDate: 0
    }, closeOptions );
}



//----------[ ▼ 번호를 계산하여 반환합니다. ]----------
function getRowNumber( index, page ) {
    return (page.page - 1) * page.size + index;
}



//----------[ ▼ 검색에 필요한 데이터를 가져옵니다. ]---------
function getSearchData( mode ) {
    const keywords = getKeywords( mode );
    if( keywords.length === 0 || (mode === 'period' && keywords.length != 2) ) {
        mode = 'all';
    }

    return {
        pagination: { page: null, size: null },
        search: { mode: mode, keyword: keywords }
    };
}



//----------[ ▼ 검색어를 배열로 반환합니다. ]----------
function getKeywords( mode ) {
    let keywords = [];

    switch( mode ) {
        case 'title' :
            const title = document.querySelector('input[type="text"][name="title"]');
            if( title && title.value.trim() !== '' ) {
                keywords.push( title.value.trim() );
            }
            break;
        case 'writer' :
            const writer = document.querySelector('input[type="text"][name="writer"]');
            if( writer && writer.value.trim() !== '' ) {
                keywords.push( writer.value.trim() );
            }
            break;
        case 'period' :
            const start = document.querySelector('#startDate');
            const end = document.querySelector('#endDate');

            if( start.value && end.value ) {
                keywords.push( start.value, end.value );
            }
            break;
        case 'answer' :
            const answer = document.querySelector('input[type="radio"][name="answer"]:checked');
            if( answer ) {
                keywords.push( answer.value );
            }
            break;
    }

    return keywords;
}



//----------[ ▼ 현재 페이지 메뉴 유형을 반환합니다. ]----------
function getCurrentPageType() {
    const menus = document.querySelectorAll('.support-menu__item');

    for( const menu of menus ) {
        if( menu.classList.contains('support-menu__item--selected') ) {
            return menu.dataset.menu;
        }
    }

    return null;
}



//----------[ ▼ 문의사항을 삭제합니다. ]----------
async function confirmAndDelete( id ) {
    if( confirm('정말로 삭제하시겠습니까?') ) {
        const apiResult = await fetchInquiryDelete( id );

        alert( apiResult.message );
        if( apiResult.success ) {
            goToPage('/support/inquiry');
        }
    }
}


//----------[ ▼ 작성자를 확인 후 결과를 반환합니다. ]----------
async function isWriter() {
    const result = await fetchWriterCheck(id);

    return result;
}



//----------[ ▼ 문의사항 입력정보의 유효성을 검사합니다. ]----------
function validValue( container ) {
    const status = { title: false, content: false };

    //제목 확인
    const titleInput = container.querySelector('input[type="text"][name="title"]');
    if( !titleInput && !titleInput.value || titleInput.value.trim() === '' ) {
        status.title = false;

        renderMessage( container.querySelector('.form__field--title'), {success: false, message: '제목을 입력해주세요.'} );
    }else {
        status.title = true;
    }

    //내용 확인
    const contentInput = container.querySelector('.form__textarea');
    if( !contentInput || !contentInput.value || contentInput.value.trim() === '' ) {
        status.content = false;

        renderMessage( container.querySelector('.form__field--content'), {success: false, message: '내용을 입력해주세요.'} );
    }else {
        status.content = true;
    }

    return status;
}