package com.areum.moneymanager.dao;

import com.areum.moneymanager.entity.AccountBook;

import java.sql.SQLException;

public interface AccountBookDao {

    //가계부 등록
    void insertAccountBook(AccountBook accountBook, String mid ) throws SQLException;

}
