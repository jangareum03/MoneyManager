package com.areum.moneymanager.dao;

import com.areum.moneymanager.entity.AccountBook;

import java.sql.SQLException;
import java.util.List;

public interface AccountBookDao {

    //가계부 등록
    void insertAccountBook(AccountBook accountBook, String mid ) throws SQLException;

    //월 조회
    List<AccountBook> selectAccountByMonth( String mid ) throws SQLException;

}
