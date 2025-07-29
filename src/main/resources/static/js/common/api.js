//----------[ ▼ 회원의 출석정보를 리스트로 가져옵니다. ]----------
function fetchAttendance( year, month ) {
    return fetch(`/api/attendance?year=${year}&month=${month}`)
                .then( response => response.json() );
}



//----------[ ▼ 오늘날짜로 출석을 진행합니다. ]----------
function fetchAttendToday( year, month, day ) {
    return fetch('k', {
       method: 'POST',
       headers: {
           'Content-Type' : 'application/json'
       },
       body : JSON.stringify({
           year : year,
           month: month,
           day : day
       })
   })
   .then( response => response.json() );
}



//----------[ ▼ 생년월일값의 유효성을 확인합니다. ]----------
function fetchBirthCheck( value ) {
    return  fetch('/api/members/validate/birth', {
         method: 'POST',
         headers: { 'Content-Type' : 'application/json' },
         body: JSON.stringify({ birthDate : value })
     })
     .then( response => response.json() );
}



//----------[ ▼ 선택한 가계부를 삭제합니다. ]----------
function fetchBudgetDelete( data ) {
    return fetch('/api/budgetBook', {
        method: 'DELETE',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify( Array.from(data) )
    })
    .then( response => response.json() );
}



//----------[ ▼ 검색어에 해당하는 가계부를 가져옵니다. ]----------
function fetchBudgetSearch( data ) {
    return fetch('/api/budgetBook/search', {
        method: 'PATCH',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify( data )
    })
    .then( response => response.json() );
}



//----------[ ▼ 특정 가계부를 수정합니다. ]----------
function fetchBudgetUpdate( id, data ) {
    return fetch(`/api/budgetBook/${id}`, {
        method: 'PUT',
        body: data
    })
    .then( response => response.json() );
}



//----------[ ▼ 카테고리 정보를 가져옵니다. ]----------
function fetchCategoryInfo( code ) {
    return fetch('/api/budgetBook/category', {
        method: 'POST',
        headers: { 'Content-Type' : 'text/plain' },
        body: code
    })
    .then( response => response.json() );
}



//----------[ ▼ 이메일값의 유효성을 확인합니다. ]----------
function fetchEmailCheck( value ) {
    return fetch('/api/members/validate/email', {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify({ email: value })
    })
    .then( response => response.json() );
}



//----------[ ▼ 이메일 인증을 진행합니다. ]----------
function fetchEmailCodeCheck( data ) {
    return fetch('/api/members/check/email-code', {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify( data )
    })
    .then( response => response.json() );
}



//----------[ ▼ 이메일 인증코드를 전송합니다. ]----------
function fetchEmailCodeSend( value ) {
    return fetch('/api/members/check/email', {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify({ email: value })
    })
    .then( response => response.json() );
}



//----------[ ▼ 구글 차트를 가져옵니다. ]----------
function fetchGoogleChart() {
    return fetch('/api/budgetBook/charts').then( response => response.json() );
}



//----------[ ▼ 아이디값의 유효성을 확인합니다. ]----------
function fetchIdValid( value ) {
    return fetch('/api/members/validate/id', {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify({ id: value })
    })
    .then( response => response.json() );
}



//----------[ ▼ 아이디값의 유효성 및 중복을 확인합니다. ]----------
function fetchIdCheck( value ) {
    return fetch('/api/members/check/id', {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify({ id: value })
    })
    .then( response => response.json() );
}



//----------[ ▼ 문의사항을 삭제합니다. ]----------
function fetchInquiryDelete( id ) {
    return fetch(`/api/support/${id}`, {
        method: 'DELETE'
    })
    .then( response => response.json() );
}



//----------[ ▼ 검색어에 해당하는 문의사항을 가져옵니다. ]----------
function fetchInquirySearch( data ) {
    return fetch('/api/support/search', {
        method: 'PUT',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify( data )
    })
    .then( response => response.json() );
}



//----------[ ▼ 년과 월의 마지막 일을 가져옵니다. ]----------
function fetchLastDay( year, month ) {
    return fetch('/api/budgetBook/lastDay', {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify({ year: year, month: month })
    });
}



//----------[ ▼ 이름값의 유효성을 확인합니다. ]----------
function fetchNameCheck( value ) {
    return  fetch('/api/members/validate/name', {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify({ name : value })
    })
    .then( response => response.json() );
}



//----------[ ▼ 닉네임 값의 유효성 및 중복을 확인합니다. ]----------
function fetchNickNameCheck( value ) {
    return fetch('/api/members/check/nickname', {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify({ nickName: value })
    })
    .then( response => response.json() );
}



//----------[ ▼ 비밀번호값의 유효성을 확인합니다. ]----------
function fetchPasswordValid( value ) {
     return fetch('/api/members/validate/password', {
         method: 'POST',
         headers: { 'Content-Type' : 'application/json' },
         body: JSON.stringify({ password: value })
     })
     .then( response => response.json() );
}



//----------[ ▼ 비밀번호값의 유효성 및 중복을 확인합니다. ]----------
function fetchPasswordCheck( value ) {
    return fetch('/api/members/check/password', {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify({ password: value })
    })
    .then( response => response.json() );
}



//----------[ ▼ 비밀번호를 변경합니다. ]----------
function fetchPasswordUpdate( value ) {
    return fetch('/api/member/password', {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify({ password : value })
    })
    .then( response => response.json() );
}



//----------[ ▼ 닉네임 값의 유효성 및 중복을 확인합니다. ]----------
function fetchNickNameCheck( value ) {
    return fetch('/api/members/check/nickname', {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify({ nickName: value })
    })
    .then( response => response.json() );
}



//----------[ ▼ 회원 정보를 변경합니다. ]----------
function fetchMemberUpdate( mode, data ) {
    return fetch('/api/member', {
        method: 'PATCH',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify({ [mode] : data })
    })
    .then( response => response.json() );
}



//----------[ ▼ 회원을 탈퇴합니다. ]----------
function fetchMemberDelete( data ) {
    return fetch('/api/member', {
        method: 'DELETE',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify( data )
    })
    .then( response => response.json() );
}



//----------[ ▼ 회원의 프로필 이미지를 변경합니다. ]----------
function fetchProfileUpdate( data ) {
    return fetch('/api/member/profile', {
        method: 'POST',
        body: data
    })
    .then( response => response.json() );
}



//----------[ ▼ 작성자가 맞는지 확인합니다. ]----------
function fetchWriterCheck( id ) {
    return fetch(`/api/support/${id}/writer`)
    .then( response => response.json() );
}