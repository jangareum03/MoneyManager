package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dto.ReqServiceDto;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public interface DetailService {

    //월 기준으로 가계부 조회
    Map<String, Object> accountBookByMonth( String mid, String mode, ReqServiceDto.AccountSearch search ) throws Exception;

    //주 기준으로 가계부 조회
    Map<String, Object> accountBookByWeek( String mid, String mode, ReqServiceDto.AccountSearch search ) throws Exception;

    //년 기준으로 가계부 조회
    Map<String ,Object> accountBookByYear( String mid, String mode, ReqServiceDto.AccountSearch search ) throws Exception;

    //가계부 삭제
    void deleteAccountBook( String mid, ReqServiceDto.DeleteAccount deleteAccount ) throws Exception;

    //JSON 객체 생성
    JSONObject getJsonObject( String mid, String type, ReqServiceDto.AccountSearch search ) throws Exception;

    //날짜 만들기
    List<String> makeDate( ReqServiceDto.AccountSearch search ) throws Exception;

    //주에 해당하는 시작 및 종료날짜 만들기
    List<String> makeDateByWeek( ReqServiceDto.AccountSearch search ) throws Exception;

    //카테고리 리스트 만들기
    String[] makeCategoryList( String[] category, int size ) throws Exception;




}
