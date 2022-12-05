package com.areum.moneymanager.dao;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.entity.AccountBook;
import com.areum.moneymanager.entity.Category;

import java.sql.SQLException;
import java.util.List;

public interface ServiceDao {

    //가계부 삭제
    void deleteAccountBook( String mid, String deleteQuery ) throws SQLException;

    //가계부 등록
    void insertAccountBook(AccountBook accountBook, String mid ) throws SQLException;

    //지출 카테고리 조회
    List<Category> selectExpenditureCategory() throws SQLException;

    //월 전체 그래프
    List<AccountBook> selectGraphByMonth( String mid, String date ) throws SQLException;

    //년 전체 그래프
    List<ResServiceDto.YearChar> selectGraphByYear( String mid, ReqServiceDto.AccountSearch search ) throws SQLException;

    //수입 카테고리 조회
    List<Category> selectIncomeCategory() throws SQLException;

    //가계부 전체내역 조회
    List<ResServiceDto.DetailList> selectAllAccount( String mid, String startDate, String endDate ) throws SQLException;

    //가계부 수입/지출 내역 조회
    List<ResServiceDto.DetailList> selectAccountByParentCategory( String mid, String startDate, String endDate, String code ) throws SQLException;

    //가계부 카테고리 내역 조회
    List<ResServiceDto.DetailList> selectAccountByCategory( String mid, String startDate, String endDate, String category ) throws SQLException;

    //가계부 제목 내역 조회
    List<ResServiceDto.DetailList> selectAccountByTitle( String mid, String startDate, String endDate, String title ) throws SQLException;

    //가계부 수입/지출가격 조회
    Integer selectAccountPrice( String mid, String startDate, String endDate, String code ) throws SQLException;

    //최상단 카테고리 조회
    List<Category> selectParentCategory() throws SQLException;

}
