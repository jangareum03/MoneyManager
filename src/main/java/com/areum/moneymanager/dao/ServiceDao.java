package com.areum.moneymanager.dao;

import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.entity.AccountBook;

import java.sql.SQLException;
import java.util.List;

public interface ServiceDao {

    //가계부 등록
    void insertAccountBook(AccountBook accountBook, String mid ) throws SQLException;

    //월 전체내역 조회
    List<ResServiceDto.detailMonth> selectAllAccountByMonth( String mid ) throws SQLException;

    //월 총합가격 조회
    List<ResServiceDto.detailMonth> selectAllPriceByMonth( String mid ) throws SQLException;

    //그래프 월 조회
    List<AccountBook> selectGraphByMonth( String mid ) throws SQLException;



}
