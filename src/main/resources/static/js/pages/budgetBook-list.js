const MAX_MEMO = 30;
const selectedBoxes = new Set();

document.addEventListener('DOMContentLoaded', () => {
    initButton();
    initIcon();
    initCard();
    truncateMemos();
    loadGoogleChart();
});



//----------[ ▼ 버튼을 초기화합니다. ]----------
function initButton() {
    //검색 메뉴인 경우
    const menus = document.querySelectorAll('.budget-menu__item');
    menus.forEach( menu => {
        menu.addEventListener( 'click', handlerClick );
    });

    //버튼인 경우
    const buttons = document.querySelectorAll('.button');
    buttons.forEach( button => {
        button.addEventListener( 'click', handlerClick );
    });
}



//----------[ ▼ 아이콘을 초기화합니다. ]----------
function initIcon() {
    const icons = document.querySelectorAll('.icon');
    icons.forEach( icon => {
        icon.addEventListener( 'click', handlerClick );
    });
}



//----------[ ▼ 가계부 카드 클릭 이벤트를 등록합니다. ]----------
function initCard() {
    const cards = document.querySelectorAll('.budget-card');
    cards.forEach( card => {
        card.addEventListener( 'click', handlerClick );
    });
}


//----------[ ▼ 요소를 클릭할 때 이벤트를 설정합니다. ]---------
function handlerClick( event ) {
    const target = event.currentTarget;
    let container;

    //캘린더 아이콘을 클릭할 때
    if( target.classList.contains('icon--calendar') ) {
        container = document.querySelector('.budget-date');
        container.replaceChildren();

        const today = new Date();

        const yearSelect = getYearSelect( today.getFullYear() );         //년도 선택박스 생성
        yearSelect.addEventListener( 'change', handlerChange );

        decorateSelect( container, yearSelect );
    }


    //메뉴를 클릭할 때
    if( target.closest('.budget-menu__filter') ) {
        container = document.querySelector('.budget-menu');
        const mode = target.dataset.mode;
        if( !mode ) return;

        //메뉴 활성화
        updateActiveTab( container, mode );

        //기존 검색메뉴 삭제 후 생성
        container.querySelector('.budget-menu__bottom')?.remove();
        const searchBox = document.createElement('section');
        searchBox.classList.add('budget-menu__bottom');

        container.appendChild(searchBox);
        renderSearchMenu( searchBox, mode );
    }


    //검색 버튼을 클릭할 때
    container = target.closest('.budget-menu__bottom');
    if( container ) {
        //메모 검색할 경우
        if( container.querySelector('input[type="text"][name="memo"]') ) {
            renderSearchResults('memo');
        }

        //기간 검색할 경우
        if( container.querySelector('input[type="text"]').classList.contains('hasDatepicker') ) {
            renderSearchResults('period');
        }
    }


    //가계부 카드를 클릭할 때
    if( target.closest('.budget-list__cards') ) {
        if( !target.querySelector('.empty') ) {
            const isDelMode = document.querySelector('.budget-menu__action--delete');

            if( !isDelMode ) {
                const id = target.querySelector('.budget-card__icon').dataset.pk;
                goToPage(`/budgetBooks/${id}`);
            }
        }
    }


    //삭제 버튼 클릭할 때
    if( target.classList.contains('button--delete') ) {
        container = target.closest('.budget-menu__action');

        const isDelMode = container.classList.contains('budget-menu__action--delete');
        container.classList.toggle( 'budget-menu__action--delete', !isDelMode );

        toggleDeleteMode( !isDelMode, container );
    }


    //삭제 체크박스를 클릭할 때
    container = target.closest('.budget-card');
    if( container ) {
        //클릭 이벤트가 카드 클릭으로 넘어가지 않도록 설정
        event.stopPropagation();

        const id = target.parentNode.dataset.pk;
        const card = container;

        if( selectedBoxes.has(id) ) {
            selectedBoxes.delete(id);
            card.classList.remove('checked');
        }else {
            selectedBoxes.add(id);
            card.classList.add('checked');
        }

        updateDeleteText();
        playShakeAnimation(card);
    }


    //플로팅 버튼 클릭할 때
    if( target.classList.contains('button--fab') ) {
        goToPage('/budgetBook/write');
    }
}



//----------[ ▼ 요소를 변경할 때 이벤트를 설정합니다. ]---------
async function handlerChange( event ) {
    const target = event.currentTarget;
    let container;

    //날짜 선택박스를 변경할 때
    container = target.closest('.budget-date');
    if( container ) {
        //현재 날짜 범위 알기
        const dateType = container.dataset.type;
        let url = `/budgetBooks/list/${dateType}`;

        //년 선택박스 변경할 때
        if( isYearSelect(target) ) {
            const year = target.value;

            if( dateType === 'year' ) {
                goToPage(`${url}?year=${year}`);
                return;
            }

            removeNextSiblings( target.parentNode );

            const monthSelect = getMonthSelect( year );        //월 선택박스 생성
            monthSelect.addEventListener( 'change', handlerChange );

            decorateSelect( container, monthSelect );
        }

        //월 선택박스 변경할 때
        if( isMonthSelect(target) ) {
            const year = container.querySelector('.form__select--year')?.value;
            const month = target.value;

            if( dateType === 'month' ) {
                goToPage(`${url}?year=${year}&month=${month}`);
                return;
            }

            removeNextSiblings( target.parentNode );

            const weekSelect = getWeekSelect( year, month );        //주 선택박스 생성
            weekSelect.addEventListener( 'change', handlerChange );

            decorateSelect( container, weekSelect );
        }


        //주 선택박스 변경할 때
        if( isWeekSelect(target) ) {
            const year = container.querySelector('.form__select--year')?.value;
            const month = container.querySelector('.form__select--month')?.value;
            const week = target.value;

            if( !year || !month ) return;

            goToPage(`${url}?year=${year}&month=${month}&week=${week}`)
        }
    }


    //검색 영역 선택박스를 변경할 때
    container = document.querySelector('.budget-menu__bottom');
    if( container ) {
        //최상위 카테고리 변경할 때
        if( target.classList.contains('form__select--top') ) {
            removeNextSiblings( target );

            if( target.value === '' ) return;

           createMiddleSelect( container, target.value );
        }


        //중간 카테고리 변경할 때
        if( target.classList.contains('form__select--middle') ) {
            removeNextSiblings( target );

            if( target.value === '' ) return;

            createCategoryBox( container, target.value );
        }
    }
}


//----------[ ▼ 코드값에 따른 카테고리 체크박스를 추가합니다. ]----------
async function createCategoryBox( container, code ) {
    const apiData = await fetchCategoryInfo( code );

    const categoryBox = createDiv({ parent: container });
    apiData.forEach( ({ code, name }) => {
        const label = createLabel({ parent: categoryBox });

        const checkbox = createCheckbox({
           parent: label,
           value: code,
           name: 'category',
           classList: ['form__input', 'form__input--checkbox'],
           label: name
         });

         checkbox.addEventListener( 'click',  () => { renderSearchResults( 'category' ) } );
    });
}



//----------[ ▼ 코드값에 따른 카테고리 선택박스를 추가합니다. ]----------
async function createMiddleSelect( container, code ) {
    const apiData = await fetchCategoryInfo( code );

    const middleSelect = createSelect({ parent: container, name: 'middle', classList: ['form__select', 'form__select--middle'], options: apiData, initOption: true });
    middleSelect.addEventListener( 'change', handlerChange );
}



//----------[ ▼ 내역 메모 내용을 자릅니다. ]----------
function truncateMemos() {
    const cards = document.querySelectorAll('.budget-card');
    cards.forEach( card => {
        const memo = card.querySelector('.budget-card__memo');

        if( memo ) {
            const value = memo.textContent;
            memo.textContent = changeMemo( value, MAX_MEMO );
        }
    });
}



//----------[ ▼ 메모 길이가 maxLength를 넘으면 줄임말로 변경합니다.  ]----------
function changeMemo( memo, maxLength ) {
    if( memo ) {
        return memo.length > maxLength ? memo.slice(0, maxLength) + '···' : memo;
    }

    return '';
}



//----------[ ▼ 구글 차트를 불러옵니다. ]----------
function loadGoogleChart() {
    google.charts.load('current', { packages: ['corechart', 'bar'] });
    google.charts.setOnLoadCallback(loadChartData);
}



//----------[ ▼ 차트 데이터를 불러와서 차트를 보여줍니다. ]----------
async function loadChartData() {
    const apiData = await fetchGoogleChart();
    drawChart( apiData );
}



//----------[ ▼ 구글차트를 페이지에 그립니다. ]----------
function drawChart(chartData) {
    let data = new google.visualization.arrayToDataTable(chartData);
    let view = new google.visualization.DataView(data);

    if (data.getColumnProperty(2, 'role') === "style") {
        view.setColumns([
            0,
            1,
            { calc: "stringify", sourceColumn: 1, type: "string", role: "annotation" },
            2
        ]);
    } else { // 이중 Y차트 사용 시
        view.setColumns([
            0,
            1,
            { calc: "stringify", sourceColumn: 1, type: "string", role: "annotation" },
            2,
            { calc: "stringify", sourceColumn: 2, type: "string", role: "annotation" }
        ]);
    }

    let options = {
        title: '',
        legend: { position: 'none' },
        series: {
            0: { color: "#1B5E20" },
            1: { color: "#B71C1C" }
        },
    }

    let chart = new google.visualization.ColumnChart(document.querySelector('.budget-graph__content'));
    chart.draw(view, options);
}



//----------[ ▼ 년도 선택박스를 생성 후 반환됩니다. ]---------
function getYearSelect( currentYear ) {
    const urlDate = parseDateFromUrl();
    const checkedYear = (urlDate.urlYear == null) ? currentYear : urlDate.urlYear;
    const YEAR_RANGE = 5;

    const options = [];
    for( let year = currentYear; (currentYear - YEAR_RANGE) <= year; year-- ) {
        options.push({
            code: String(year),
            name: String(year) + '년',
            defaultChecked: (checkedYear == year)
        });
    }

    return createSelect({ options: options, name: 'year', classList: ['form__select', 'form__select--year'] });
}



//----------[ ▼ 월 선택박스를 생성 후 반환됩니다. ]---------
function getMonthSelect( selectedYear ) {
    const today = new Date();

    //월의 최대값 변경하기
    let maxMonth = 12;
    if( selectedYear == today.getFullYear() ) {
        maxMonth = Number(today.getMonth() + 1);
    }

    const options = [];
    for( let month = 1; month <= maxMonth; month++ ) {
        options.push({
            code: String(month),
            name: String(month) + '월'
        });
    }

    return createSelect({ options: options, name: 'month', classList: ['form__select', 'form__select--month'] });
}



//----------[ ▼ 주 선택박스를 생성 후 반환됩니다. ]---------
function getWeekSelect( year, month ) {
    const maxWeek = getMaxWeekByMonth( year, month );

    const options = [];
    for( let week=1; week <= maxWeek; week ++ ) {
        options.push({
            code: String(week),
            name: String(week) + '주'
        });
    }

    return createSelect({ options: options, name: 'week', classList: ['form__select', 'form__select--week'] });
}



//----------[ ▼ 년과 월 값에 따른 월의 최대 주를 반환합니다. ]----------
function getMaxWeekByMonth( year, month ) {
    const firstDay = new Date(year, month -1, 1);
    const lastDay = new Date(year, month, 0);

    const firstDayOfWeek = firstDay.getDay();
    const totalDays = lastDay.getDate();

    const totalSlots = firstDayOfWeek + totalDays;
    const weekCount = Math.ceil(totalSlots / 7);

    return weekCount;
}



//----------[ ▼ 선택한 탭의 메뉴로 활성화합니다. ]---------
function updateActiveTab( container, mode ) {
    const tabs = container.querySelectorAll('.budget-menu__item');

    tabs.forEach( tab => {
        const isActive = tab.dataset.mode === mode;
        tab.classList.toggle( 'budget-menu__item--active', isActive );
    });
}



//----------[ ▼ 수입&지출 검색메뉴를 표시합니다. ]---------
async function renderInOutCategories( container, mode ) {
    const apiData = await fetchCategoryInfo();

    //라디오 박스 생성
    const radioBox = createDiv({ parent: container, classList: ['budget-menu__search'] });

    apiData.forEach( ({code, name}) => {
        const label = createLabel({ parent: radioBox });
        const radio = createRadio({ parent: label, name: 'inout', value: code, classList: ['form__input', 'form__input--radio'], label: name });
        radio.addEventListener( 'click', () => renderSearchResults( mode ) );
    });
}



//----------[ ▼ 하위 카테고리 메뉴를 표시합니다. ]---------
async function renderCategoryHierarchy( container, mode ) {
    const top = await fetchCategoryInfo();

    const topSelect = createSelect({ parent: container, name: 'top', classList: ['form__select', 'form__select--top'], options: top, initOption: true });
    topSelect.addEventListener( 'change', handlerChange );
}



//----------[ ▼ 메모 메뉴를 표시합니다. ]----------
function renderMemoSearch( container, mode ) {
    const label = createLabel({ parent: container });

    const input = createTextInput({ parent: label, name: 'memo', classList: ['form__input', 'form__input--text'], placeHolder: '메모 내용을 검색해주세요.' } );

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



//----------[ ▼ 메뉴에 따른 내역을 조회합니다. ]---------
async function renderSearchResults( mode ) {
    const data = getSearchData( mode );
    console.log(data);
    const {price, info} = await fetchBudgetSearch( data );

    updateSummaryStats(price);

    const container = document.querySelector('.budget-list');
    if( !container ) return;

    container.replaceChildren();    //자식요소 초기화

    const isEmpty = !Object.keys(info).length;
    container.classList.toggle( 'empty', isEmpty );
    container.classList.toggle( 'filled', !isEmpty );

    isEmpty ? renderEmptyResult( container ) : renderFilledResult( container, info );
}



//----------[ ▼ 검색에 필요한 데이터를 가져옵니다. ]---------
function getSearchData( mode ) {
    const title = document.querySelector('.budget-date');

    const keywords = getKeywords( mode );
    if( keywords.length === 0 || (mode === 'period' && keywords.length != 2) ) {
        mode = 'all';
    }

    return {
        mode: mode,
        keywords: keywords,
        date: {...getDate( title.textContent ), type: title.dataset.type}
    }
}



//----------[ ▼ 검색어를 배열로 반환합니다. ]----------
function getKeywords( mode ) {
    let keywords = [];

    const container = document.querySelector('.budget-menu__bottom');
    switch( mode ) {
        case 'inout' :
            const category = container.querySelector('input[type="radio"][name="inout"]:checked');
            if( category ) {
                keywords.push(category.value);
            }
            break;
        case 'category' :
            const codes = container.querySelectorAll('input[type="checkbox"][name="category"]:checked');

            codes.forEach( code => keywords.push( code.value ));
            break;
        case 'memo' :
            const memo = document.querySelector('input[type="text"][name="memo"]');
            if( memo  && memo.value.trim() !== '') {
                keywords.push(memo.value.trim());
            }
            break;
        case 'period' :
            const start = document.querySelector('#startDate');
            const end = document.querySelector('#endDate');

            if( start.value && end.value ) {
                keywords.push(start.value, end.value);
            }
    }

    return keywords;
}



//----------[ ▼ 내역조회 범위에 날짜를 반환합니다. ]----------
function getDate( title ) {
    const reg = /(?<year>\d{4})년(?: (?<month>\d{2})월)?(?: (?<week>\d{1}주))?/;
    const date = title.match(reg);

    if( date && date.groups ) {
        const { year, month, week } = date.groups;

        return {
            year : parseInt(year),
            month : month ? parseInt(month) : null,
            week : week ? parseInt(week) : null,
            day : null
        };
    }

    return null;
}



//----------[ ▼ 조회한 내역의 통계 금액으로 변경합니다. ]----------
function updateSummaryStats( price ) {
    const container = document.querySelector('.budget-stats');
    if( !container ) return;

    //전체금액
    container.querySelector('.budget-stats__total dd').textContent = price.total ? formatNumber(price.total) + '원' : '0원';

    //수입금액
    container.querySelector('.budget-stats__income dd').textContent = price.income ? formatNumber(price.income) + '원' : '0원';

    //지출금액
    container.querySelector('.budget-stats__outlay dd').textContent = price.outlay ? formatNumber(price.outlay) + '원' : '0원';
}



//----------[ ▼ 조회한 내역 리스트가 없으면 안내 문구를 표시합니다. ]----------
function renderEmptyResult( container ) {
    createSpan( {parent: container, text: '아직 등록된 내역이 없어요.'} );
    createSVG({
        parent: container,
        link: 'http://www.w3.org/2000/svg',
        viewBox: '0 0 51 51',
        paths: [
            {d: 'M25.625 6.74967C23.1628 6.74967 20.7246 7.23466 18.4497 8.17693C16.1749 9.11921 14.1079 10.5003 12.3668 12.2414C10.6257 13.9825 9.24457 16.0495 8.3023 18.3244C7.36002 20.5992 6.87504 23.0374 6.87504 25.4997C6.87504 27.962 7.36002 30.4001 8.3023 32.675C9.24457 34.9498 10.6257 37.0168 12.3668 38.7579C14.1079 40.499 16.1749 41.8801 18.4497 42.8224C20.7246 43.7647 23.1628 44.2497 25.625 44.2497C30.5978 44.2497 35.367 42.2742 38.8833 38.7579C42.3996 35.2416 44.375 30.4725 44.375 25.4997C44.375 20.5269 42.3996 15.7577 38.8833 12.2414C35.367 8.72511 30.5978 6.74967 25.625 6.74967ZM2.70837 25.4997C2.70837 12.8434 12.9688 2.58301 25.625 2.58301C38.2813 2.58301 48.5417 12.8434 48.5417 25.4997C48.5417 38.1559 38.2813 48.4163 25.625 48.4163C12.9688 48.4163 2.70837 38.1559 2.70837 25.4997ZM21.9438 21.0538L14.7271 25.2205L12.6438 21.6122L19.8605 17.4455L21.9438 21.0538ZM31.3938 17.4455L38.6105 21.6122L36.5271 25.2205L29.3105 21.0538L31.3938 17.4455ZM16.6021 32.7893C17.5168 31.2058 18.8322 29.8911 20.416 28.9772C21.9999 28.0633 23.7964 27.5825 25.625 27.583C27.4535 27.5831 29.2497 28.0644 30.8332 28.9786C32.4167 29.8929 33.7316 31.2078 34.6459 32.7913L35.6896 34.5934L32.0834 36.6788L31.0417 34.8768C30.4931 33.9257 29.7036 33.1359 28.7527 32.5869C27.8018 32.038 26.723 31.7492 25.625 31.7497C24.5273 31.749 23.4487 32.0374 22.4978 32.586C21.5469 33.1346 20.7573 33.924 20.2084 34.8747L19.1667 36.6788L15.5605 34.5934L16.6021 32.7893Z' }
        ],
        classList: ['icon', 'icon--sad']
    });
}



//----------[ ▼ 조회한 내역 리스트를 표시합니다. ]----------
function renderFilledResult( container, result ) {
    Object.entries(result).forEach( ([key, cards]) => {
        const filledBox = createDiv({ parent: container });

        //제목
        const title = document.createElement('h2');
        title.classList.add('budget-list__title');
        title.textContent = key;

        filledBox.appendChild(title);

        //카드
        for( let i=0; i < cards.length; i += 3 ) {
            const ul = document.createElement('ul');
            ul.classList.add('budget-list__cards');

            const cardBundle = cards.slice( i, i+3 );
            cardBundle.forEach( card => {
                ul.appendChild( card ? createCard( card ) : createEmptyCard() );
            });

            for( let j = cardBundle.length; j < 3; j++ ) {
                ul.appendChild( createEmptyCard() );
            }

            filledBox.appendChild(ul);
        }

        container.appendChild(filledBox);
    });

    initCard();
}



//----------[ ▼ 빈 내역카드를 추가 후 반환합니다. ]---------
function createEmptyCard() {
    const card = document.createElement('li');
    card.classList.add('budget-card');

    //아이콘 생성
    createDiv({ parent: card, classList: ['budget-card__type'] });

    //카테고리
    createSpan({ parent: card, classList: ['budget-card__category'] });

    //금액
    createSpan({ parent: card, classList: ['budget-card__price'] });

    //메모
    createP({ parent: card, classList: ['budget-card__memo'] });

    return card;
}



//----------[ ▼ 내역카드에 내용을 추가 후 반환합니다. ]---------
function createCard( item ) {
    const card = createEmptyCard();

    //내역 유형에 따른 클래스 추가
    if( item.code == null ) {
        card.classList.add('empty');
    }else {
        card.classList.add( item.code.startsWith('01') ? 'income' : 'outlay' );
    }


    //삭제 아이콘 추가
    const iconBox = createDiv({ parent: card, classList: ['budget-card__icon'] });
    iconBox.setAttribute( 'data-pk', item.id );

    createSVG({
        parent: iconBox,
        link: 'http://www.w3.org/2000/svg',
        viewBox: '0 0 8 9' ,
        paths: [
            {d: 'M1.08333 0.25C0.86232 0.25 0.650358 0.337797 0.494078 0.494078C0.337797 0.650358 0.25 0.86232 0.25 1.08333V6.91667C0.25 7.13768 0.337797 7.34964 0.494078 7.50592C0.650358 7.6622 0.86232 7.75 1.08333 7.75H6.91667C7.13768 7.75 7.34964 7.6622 7.50592 7.50592C7.6622 7.34964 7.75 7.13768 7.75 6.91667V1.08333C7.75 0.86232 7.6622 0.650358 7.50592 0.494078C7.34964 0.337797 7.13768 0.25 6.91667 0.25H1.08333ZM6.0625 3.08167C6.14068 3.00354 6.18463 2.89755 6.18467 2.78702C6.18471 2.67649 6.14084 2.57048 6.06271 2.49229C5.98458 2.41411 5.87859 2.37016 5.76806 2.37012C5.65753 2.37008 5.55152 2.41395 5.47333 2.49208L3.41083 4.55458L2.52708 3.67083C2.4884 3.63212 2.44247 3.60141 2.39191 3.58045C2.34136 3.55948 2.28717 3.54868 2.23244 3.54867C2.12191 3.54863 2.01589 3.5925 1.93771 3.67063C1.85952 3.74875 1.81558 3.85474 1.81554 3.96527C1.8155 4.0758 1.85937 4.18182 1.9375 4.26L3.08667 5.40917C3.12923 5.45175 3.17977 5.48553 3.23539 5.50858C3.29101 5.53162 3.35063 5.54349 3.41083 5.54349C3.47104 5.54349 3.53066 5.53162 3.58628 5.50858C3.6419 5.48553 3.69244 5.45175 3.735 5.40917L6.0625 3.08167Z', fillRule: 'evenodd', clipRule: 'evenodd' }
        ],
        classList: ['icon', 'icon--box']
    });

    card.querySelector('.budget-card__category').textContent = item.name;
    card.querySelector('.budget-card__price').textContent = formatNumber(item.price) + '원';
    card.querySelector('.budget-card__memo').textContent = changeMemo( item.memo, MAX_MEMO );

    //이벤트 할당
    card.addEventListener('click', handlerClick);

    return card;
}



//----------[ ▼ 삭제모드로  됩니다. ]----------
function toggleDeleteMode( on, container ) {
    const budgetBox = document.querySelector('.budget-list');

    if( on ) {
        activateDeleteMode( container, budgetBox );
    }else {
        deactivateDeleteMode( container, budgetBox );
    }
}



//----------[ ▼ 삭제모드를 활성화 합니다. ]----------
function activateDeleteMode( container, box ) {
    box.classList.add('budget-list--delete');

    box .querySelectorAll('.budget-card:not(.empty)').forEach( (card) => {
        card.addEventListener( 'click', handlerClick );
    });

    //휴지통 아이콘 제거
    container.querySelector('.icon--basket')?.remove();

    //삭제문구 추가
    container.querySelector('.button--delete')?.remove();
    const delButton = createSpan({ parent: container, classList: ['delete__count']});
    delButton.addEventListener( 'click', () => confirmAndDelete() );

    updateDeleteText();

    // 취소 버튼 추가
    const cancelButton = createSpan({
        parent: container,
        classList: ['button', 'button--cancel'],
        text: ' [취소]'
    });
    cancelButton.addEventListener('click', () => toggleDeleteMode(false, container));
}



//----------[ ▼ 삭제모드를 비활성화 합니다. ]----------
function deactivateDeleteMode( container, box ) {
    box.classList.remove('budget-list--delete');
    container.classList.remove('budget-menu__action--delete');

    // 삭제 문구 & 취소 버튼 제거
    container.querySelector('.delete__count')?.remove();
    container.querySelector('.budget-menu__action .button--cancel')?.remove();

    // 휴지통 아이콘 복원
    const delButton = createButton({
        parent: container,
        type: 'button',
        classList: ['button', 'button--delete']
    });

    createSVG({
        parent: delButton,
        link: 'http://www.w3.org/2000/svg',
        viewBox: '0 0 10 10',
        paths: [
            { d: 'M6.875 0.9375V1.5625H9.0625C9.14538 1.5625 9.22487 1.59542 9.28347 1.65403C9.34208 1.71263 9.375 1.79212 9.375 1.875C9.375 1.95788 9.34208 2.03737 9.28347 2.09597C9.22487 2.15458 9.14538 2.1875 9.0625 2.1875H8.72625L8.19313 8.85C8.16798 9.16325 8.02578 9.45553 7.79483 9.66865C7.56388 9.88176 7.26113 10.0001 6.94687 10H3.05313C2.73887 10.0001 2.43612 9.88176 2.20517 9.66865C1.97422 9.45553 1.83202 9.16325 1.80687 8.85L1.27375 2.1875H0.9375C0.85462 2.1875 0.775134 2.15458 0.716529 2.09597C0.657924 2.03737 0.625 1.95788 0.625 1.875C0.625 1.79212 0.657924 1.71263 0.716529 1.65403C0.775134 1.59542 0.85462 1.5625 0.9375 1.5625H3.125V0.9375C3.125 0.68886 3.22377 0.450403 3.39959 0.274587C3.5754 0.098772 3.81386 0 4.0625 0L5.9375 0C6.18614 0 6.4246 0.098772 6.60041 0.274587C6.77623 0.450403 6.875 0.68886 6.875 0.9375ZM3.75 0.9375V1.5625H6.25V0.9375C6.25 0.85462 6.21708 0.775134 6.15847 0.716529C6.09987 0.657924 6.02038 0.625 5.9375 0.625H4.0625C3.97962 0.625 3.90013 0.657924 3.84153 0.716529C3.78292 0.775134 3.75 0.85462 3.75 0.9375ZM2.8125 3.14312L3.125 8.45563C3.12667 8.49715 3.1366 8.53793 3.15422 8.57557C3.17184 8.61321 3.1968 8.64696 3.22762 8.67484C3.25844 8.70272 3.29452 8.72417 3.33373 8.73794C3.37295 8.75171 3.41452 8.75751 3.456 8.75502C3.49749 8.75253 3.53806 8.74178 3.57534 8.72341C3.61263 8.70505 3.64587 8.67943 3.67313 8.64806C3.70039 8.61668 3.72112 8.58019 3.73411 8.54071C3.74709 8.50123 3.75207 8.45955 3.74875 8.41813L3.43625 3.10562C3.43458 3.0641 3.42465 3.02332 3.40703 2.98568C3.38941 2.94804 3.36445 2.91429 3.33363 2.88641C3.30281 2.85853 3.26673 2.83708 3.22752 2.82331C3.1883 2.80954 3.14673 2.80374 3.10525 2.80623C3.06376 2.80872 3.02319 2.81947 2.98591 2.83784C2.94862 2.8562 2.91538 2.88182 2.88812 2.91319C2.86086 2.94456 2.84013 2.98106 2.82714 3.02054C2.81416 3.06002 2.80918 3.1017 2.8125 3.14312ZM6.89375 2.81312C6.81104 2.80834 6.72982 2.8366 6.66794 2.89169C6.60606 2.94677 6.56858 3.02417 6.56375 3.10688L6.25125 8.41937C6.24857 8.50085 6.27783 8.58015 6.33279 8.64035C6.38775 8.70056 6.46407 8.7369 6.54545 8.74163C6.62683 8.74636 6.70684 8.7191 6.76841 8.66567C6.82998 8.61224 6.86823 8.53686 6.875 8.45563L7.1875 3.14312C7.19228 3.06042 7.16402 2.9792 7.10894 2.91731C7.05386 2.85543 6.97645 2.81796 6.89375 2.81312ZM5 2.8125C4.91712 2.8125 4.83763 2.84542 4.77903 2.90403C4.72042 2.96263 4.6875 3.04212 4.6875 3.125V8.4375C4.6875 8.52038 4.72042 8.59987 4.77903 8.65847C4.83763 8.71708 4.91712 8.75 5 8.75C5.08288 8.75 5.16237 8.71708 5.22097 8.65847C5.27958 8.59987 5.3125 8.52038 5.3125 8.4375V3.125C5.3125 3.04212 5.27958 2.96263 5.22097 2.90403C5.16237 2.84542 5.08288 2.8125 5 2.8125Z' }
        ],
        classList: ['icon', 'icon--basket']
    });

    delButton.addEventListener( 'click', handlerClick );
}



//----------[ ▼ 내역 삭제를 재확인 후 삭제를 진행합니다.  ]----------
async function confirmAndDelete() {
    const count = document.querySelectorAll('.checked').length;

    if( count === 0 ) {
        alert('삭제할 내역을 선택해주세요.');
        return;
    }

    if (!confirm(`${count}개를 삭제하시겠습니까?`)) return;

    const apiData = await fetchBudgetDelete( selectedBoxes );

    alert(apiData.message);
    if( apiData.success ) {
        window.location.reload();
    }
}



//----------[ ▼ 체크박스를 선택한 개수만큼 문구를 변경합니다. ]----------
function updateDeleteText() {
    const count = document.querySelectorAll('.checked').length;
    const text = document.querySelector('.delete__count');

    if( count === 0 ) {
        text.textContent = '0개 삭제';
    }else {
        text.textContent = `${count}개 삭제`;
    }
}



//----------[ ▼ 년 선택박스인지 확인합니다. ]---------
function isYearSelect( target ) {
    return target.classList.contains('form__select--year');
}



//----------[ ▼ 월 선택박스인지 확인합니다. ]---------
function isMonthSelect( target ) {
    return target.classList.contains('form__select--month');
}



//----------[ ▼ 주 선택박스인지 확인합니다. ]---------
function isWeekSelect( target ) {
    return target.classList.contains('form__select--week');
}



//----------[ ▼ 선택박스에 이미지를 추가 후 반환됩니다. ]---------
function decorateSelect( container, select ) {
    const label = createLabel({ classList: ['budget-select__label'] });
    label.appendChild(select);

    const span = createSpan( {parent:label, classList: ['budget-select__icon'] } );
    const arrowImg = loadImage({ parent: span, url: '/image/default/arrow-down.svg' });

    container.appendChild(label);
}



//----------[ ▼ 선택박스를 삭제합니다. ]---------
function removeNextSiblings( target ) {
    //다른 형제요소 삭제
    let nextBox = target.nextElementSibling;

    while( nextBox ) {
        const next = nextBox.nextElementSibling;
        nextBox.remove();

        nextBox = next;
    }
}



//----------[ ▼ 내역카드를 선택하면 애니메이션 동작이 나타납니다.  ]----------
function playShakeAnimation( card ) {
    card.classList.remove('shake-animation');
    void card.offsetWidth;
    card.classList.add('shake-animation');

    card.addEventListener( 'animationend', () => {
        card.classList.remove('shake-animation');
    }, {once: true});
}



//----------[ ▼ 흔들기 애니메이션을 정의합니다.  ]----------
(function addShakeAnimationStyle() {
    const style = document.createElement('style');

    style.textContent = `
        @keyframes shake {
            0% { transform: translateX(0); }
            25% { transform: translateX(-5px); }
            50% { transform: translateX(5px); }
            75% { transform: translateX(-5px); }
            100% { transform: translateX(0); }
        }
        .shake-animation {
            animation: shake 0.3s;
        }`;
    document.head.appendChild(style);
})();