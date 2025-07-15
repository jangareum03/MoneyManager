const imgSrc = 'image/attendance/teddy.png';

document.addEventListener( 'DOMContentLoaded', () => {
    loadAttendanceData();
    initButton();
    initToday();
});



//----------[ ▼ 로그인한 회원의 완료된 출석정보를 가져옵니다. ]----------
async function loadAttendanceData() {
    const date = getDateFromUrl();

    const resJson = await fetchAttendance( date.year, date.month );

    renderCompletedIcon( resJson.dates );
}



//----------[ ▼ 버튼을 초기화합니다. ]----------
function initButton() {
    const buttons = document.querySelectorAll('.button');
    buttons.forEach( (button) => {
        button.addEventListener( 'click', handlerClick );
    });
}



//----------[ ▼ 오늘일자를 초기화합니다. ]----------
function initToday() {
    const todayCell = document.querySelector('.table__cell--today');
    if( todayCell ) {
        todayCell.addEventListener( 'click', handlerClick );
    }
}



//----------[ ▼ 요소를 클릭할 때 이벤트를 설정합니다. ]----------
async function handlerClick( event ) {
    const target = event.currentTarget;

    //이전달 이동 버튼 클릭할 때
    if( target.classList.contains('button--prev') ) {
        goToPrevMonth();
    }

    //다음달 이동 버튼 클릭할 때
    if( target.classList.contains('button--next') ) {
        goToNextMonth();
    }

    //달력의 오늘일짜를 클릭할 때
    if( target.closest('.table__body') ) {
        //출석 완료된 상태
        const isCompleted = target.querySelector('.table__cell--icon');
        if( isCompleted ) return;

        const today = new Date();
        const data = await fetchAttendToday( today.getFullYear(), today.getMonth() + 1, today.getDate() );

        alert( data.message );

        if( data.success ) {
            renderTodayIcon( today.getDate() );
        }
    }
}



//----------[ ▼ 출석 완료된 날짜영역에만 아이콘을 표시합니다. ]----------
function renderCompletedIcon( dates ) {
    //이번달에 출석을 한번도 진행하지 않은 상태
    if( !dates || dates.length === 0 ) return;

    //달의 일자를 map 저장
    const dateToIndex = new Map();

    const cells = document.querySelectorAll('.table--home .table__cell--day');
    cells.forEach( (cell, index) => {
        const text = cell.querySelector('span')?.textContent.trim();
        dateToIndex.set( text, index );
    });

    //출석 완료된 일자에 이미지 보이기
    dates.forEach( date => {
        const index = dateToIndex.get(date);

        if( index !== undefined ) {
            createImg({ parent: cells[index], src: imgSrc, alt: '출석 완료 이미지', classList: ['table__cell--icon'] });
        }
    });
}



//----------[ ▼ 출석 완료된 오늘날짜에 아이콘을 표시합니다. ]----------
function renderTodayIcon( day ) {
    const cells = document.querySelectorAll('.table__cell--day');
    cells.forEach( cell => {
        if( cell.querySelector('span').textContent.trim() == day ) {
            createImg({ parent: cell, src: imgSrc, alt: '출석 완료 이미지', classList: ['table__cell--icon'] });
        }
    });
}



//----------[ ▼ 출석달력에 필요한 년과 월을 얻습니다. ]----------
function getDateFromUrl() {
    const { urlYear, urlMonth } = parseDateFromUrl();
    const { isPass, year, month } = validYearMonth(urlYear, urlMonth);

    if( !isPass ) {
        alert('잘못된 날짜 입력으로 현재 날짜로 조회합니다.');
    }

    return { year: year, month: month };
}



//----------[ ▼ 이전달로 이동합니다. ]----------
function goToPrevMonth() {
    const { year, month } = getDateFromUrl();
    let prevYear = year, prevMonth = month;

    if( prevMonth <= 1 ) {
        prevYear = year - 1;
        prevMonth = 12;
    }else {
        prevMonth = month - 1;
    }

    goToPage(`/attendance?year=${prevYear}&month=${prevMonth}`);
}



//----------[ ▼ 다음달로 이동합니다. ]----------
function goToNextMonth() {
    const { year, month } = getDateFromUrl();
    let nextYear = year, nextMonth = month;

    if( nextMonth >= 12 ) {
        nextYear = year + 1;
        nextMonth = 1;
    }else {
        nextMonth = month + 1;
    }

    goToPage(`/attendance?year=${nextYear}&month=${nextMonth}`);
}