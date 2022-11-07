package com.areum.moneymanager.dao;

import com.areum.moneymanager.dto.ReqHomeDto;
import com.areum.moneymanager.entity.Attendance;

import java.sql.SQLException;
import java.util.List;

public interface AttendanceDao {

    //출석하기
    int insertAttend( String mid, String today ) throws SQLException;

    //출석날짜 리스트 출력
    List<Attendance> selectAttendDateList( ReqHomeDto.AttendCheck date ) throws SQLException;

    //출석 포인트 수정
    void updatePoint( String mid, int point ) throws SQLException;

}
