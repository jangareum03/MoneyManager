package com.areum.moneymanager.dao;

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
    List<AccountBook> selectGraphByMonth( String mid ) throws SQLException;

    //수입 카테고리 조회
    List<Category> selectIncomeCategory() throws SQLException;

    //월 전체내역 조회
    List<ResServiceDto.detailMonth> selectMonthAccount( String mid, String startDate, String endDate ) throws SQLException;

    //월 수입/지출 내역 조회
    List<ResServiceDto.detailMonth> selectMonthByParentCategory( String mid, String startDate, String endDate, String code ) throws SQLException;

    //월 카테고리 조회
    List<ResServiceDto.detailMonth> selectMonthByCategory( String mid, String startDate, String endDate, String category ) throws SQLException;

    //월 제목 조회
    List<ResServiceDto.detailMonth> selectMonthByTitle( String mid, String startDate, String endDate, String title ) throws SQLException;

    //월 수입/지출가격 조회
    Integer selectMonthPrice( String mid, String startDate, String endDate, String code ) throws SQLException;

    //월 전체 가격 조회
    Integer selectMonthTotalPrice( String mid, String startDate, String endDate ) throws SQLException;

    //최상단 카테고리 조회
    List<Category> selectParentCategory() throws SQLException;

}
