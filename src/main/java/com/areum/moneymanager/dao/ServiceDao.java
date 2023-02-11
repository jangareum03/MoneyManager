package com.areum.moneymanager.dao;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.entity.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ServiceDao {

    //가계부 삭제
    void deleteAccountBook( String mid, String deleteQuery ) throws SQLException;

    //가계부 등록
    void insertAccountBook( AccountBook accountBook, String mid ) throws SQLException;

    //가계부 카테고리 내역 조회
    List<ResServiceDto.ListAccount> selectAccountByCategory( String mid, String startDate, String endDate, String category ) throws SQLException;

    //가계부 제목 내역 조회
    List<ResServiceDto.ListAccount> selectAccountByTitle( String mid, String startDate, String endDate, String title ) throws SQLException;

    //특정 가계부 조회
    AccountBook selectAccountOneById( String mid, Long id ) throws SQLException;

    //가계부 수입/지출 내역 조회
    List<ResServiceDto.ListAccount> selectAccountByParentCategory( String mid, String startDate, String endDate, String code ) throws SQLException;

    //가계부 전체내역 조회
    List<ResServiceDto.ListAccount> selectAllAccount( String mid, String startDate, String endDate ) throws SQLException;

    //카테고리명 조회
    List<Category> selectAllCategory( String code ) throws SQLException;

    //공지사항 전체개수 조회
    Integer selectAllNotice() throws SQLException;

    //전체 공지사항 리스트
    List<Notice> selectAllNotice( int start, int end ) throws SQLException;

    //Q&A 전체개수 조회
    Integer selectAllQuestion() throws SQLException;

    //Q&A 리스트 조회
    List<Question> selectAllQuestion( int start, int end ) throws SQLException;

    //대카테고리 조회
    List<Category> selectCategory() throws SQLException;

    //중&소카테고리 조회
    List<Category> selectCategory( String code ) throws SQLException;

    //월 전체 그래프
    List<AccountBook> selectGraphByMonth( String mid, String date ) throws SQLException;

    //주 전체 그래프
    List<ResServiceDto.WeekChart> selectGraphByWeek( String mid, String date ) throws SQLException;

    //년 전체 그래프
    List<ResServiceDto.YearChart> selectGraphByYear( String mid, ReqServiceDto.AccountSearch search ) throws SQLException;

    //가계부 수입/지출가격 조회
    Integer selectAccountPrice( String mid, String startDate, String endDate, String code ) throws SQLException;

    //Q&A 답변 조회
    Answer selectAnswer( String id ) throws SQLException;

    //최신 가계부 번호 조회
    Long selectId( String mid ) throws SQLException;

    //특정 공지사항
    Notice selectNoticeById( String id ) throws SQLException;

    //Q&A 등록자 확인
    String selectQnAMemberId( String id ) throws SQLException;

    //Q&A 검색조건에 따른 개수 조회
    Integer selectQuestionBySearch( String sql ) throws SQLException;

    //특정 Q&A 조회
    Question selectQuestionById( String id ) throws SQLException;

    //Q&A 검색 조건에 따른 조회
    List<Question> selectQuestionBySearch( String sql, int start, int end ) throws SQLException;

    //가계부 수정
    void updateAccountBook( AccountBook accountBook, String mid ) throws SQLException;

    //조회수 증가
    void updateReadCount( String id ) throws SQLException;


}
