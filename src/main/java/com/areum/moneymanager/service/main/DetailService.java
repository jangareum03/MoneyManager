package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.entity.Category;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DetailService {

    //월 기준으로 가계부 조회
    Map<String, Object> getAccountBookByMonth( String mid, String mode, ReqServiceDto.AccountSearch search ) throws SQLException;

    //주 기준으로 가계부 조회
    Map<String, Object> getAccountBookByWeek( String mid, String mode, ReqServiceDto.AccountSearch search ) throws SQLException;

    //년 기준으로 가계부 조회
    Map<String ,Object> getAccountBookByYear( String mid, String mode, ReqServiceDto.AccountSearch search ) throws SQLException;

    //가계부 카테고리 조회
    Map<String, List<ResServiceDto.Category>> getAccountCategory( ) throws SQLException;

    //가계부 선택한 소카테고리 조회
    List<ResServiceDto.Category> getAccountCategory( String code ) throws SQLException;

    //가계부 삭제
    void deleteAccountBook( String mid, ReqServiceDto.DeleteAccount deleteAccount ) throws SQLException;

    //날짜 만들기
    List<String> makeDate( ReqServiceDto.AccountSearch search );

    //주에 해당하는 시작 및 종료날짜 만들기
    List<String> makeDateByWeek( ReqServiceDto.AccountSearch search );

    //카테고리 리스트 만들기
    String[] makeCategoryList( String[] category, int size );

    //JSON 객체 생성
    JSONObject makeJsonObject( String mid, String type, ReqServiceDto.AccountSearch search ) throws SQLException;

}
