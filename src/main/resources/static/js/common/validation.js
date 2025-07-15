//----------[ ▼ 아이디 유효성을 확인합니다. ]----------
function validIdFormat( target ) {
    let value = target.value.trim();
    if( !value ) value = '';

    return fetchIdValid( value );
}



//----------[ ▼ 아이디 유효성 + 중복성을 확인합니다. ]----------
function validId( target ) {
    let value = target.value.trim();
    if( !value ) value = '';

    return fetchIdCheck( value );
}



//----------[ ▼ 비밀번호 입력값을 확인합니다. ]----------
function validPasswordFormat( target ) {
    let value = target.value.trim();
    if( !value ) value = '';

    return fetchPasswordValid( value );
}



//----------[ ▼ 비밀번호 유효성 + 중복성을 확인합니다. ]----------
function validPassword( target ) {
    let value = target.value.trim();
    if( !value ) value = '';

    return fetchPasswordCheck( value );
}



//----------[ ▼ 이름 입력값을 확인합니다. ]----------
function validName( target ) {
    let value = target.value.trim();
    if( !value ) value = '';

    return fetchNameCheck( value );
}



//----------[ ▼ 생년월일 입력값을 확인합니다. ]----------
function validBirth( target ) {
    let value = target.value.trim();
    if( !value ) value = '';

    return fetchBirthCheck( value );
}



//----------[ ▼ 닉네임 입력값을 확인합니다. ]----------
function validNickname( target ) {
    let value = target.value.trim();
    if( !value ) value = '';

    return fetchNickNameCheck( value );
}



//----------[ ▼ 이메일 입력값을 확인합니다. ]----------
function validEmail( target ) {
    let value = target.value.trim();
    if( !value ) value = '';

    return fetchEmailCheck( value );
}



//----------[ ▼ 이메일 인증코드값을 확인합니다. ]----------
function validEmailCode( fields ) {
    if( !fields ){
        return { success: false, message: '이메일 인증을 할 수 없습니다.' };
    }

    return fetchEmailCodeCheck( fields );
}



//----------[ ▼ 년과 월의 값의 유효성을 검사합니다. ]----------
function validYearMonth( year, month ) {
    const isYear = Number.isInteger(year) && 1900 <= year && year <= 2100;
    const isMonth = Number.isInteger(month) && 1 <= month && month <= 12;

    const now = new Date();

    return {
        isPass : isYear && isMonth,
        year : isYear ? year : now.getFullYear(),
        month : isMonth ? month : now.getMonth() + 1
    };
}
