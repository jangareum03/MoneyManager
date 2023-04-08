package com.areum.moneymanager.dao;

import com.areum.moneymanager.entity.LoginHistory;
import com.areum.moneymanager.entity.UpdateHistory;

import java.sql.SQLException;

public interface LogDao {

    //로그인 성공 로그 등록
    void insertLogin( LoginHistory loginHistory ) throws SQLException;

    //회원정보 수정내역 추가
    void insertUpdateHistory(String mid, UpdateHistory updateHistory, String sql ) throws SQLException;

}
