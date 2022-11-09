function changeMonth(){
    let yearList = document.getElementById('selectYear').value;
    let monthList = document.getElementById('selectMonth').value;

    const today = new Date();
    if( yearList == today.getFullYear() ) {
        for( let i=today.getMonth()+1; i<=12; i++ ) {
            monthList.options[i].setAttribute('disabled', true);
        }
    }
}

function changeDate(){

}