package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dto.ReqServiceDto;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public interface DetailService {

    //월 기준으로 가계부 조회
    public Map<String, Object> accountBookByMonth(String mid, String mode, ReqServiceDto.MonthSearch monthSearch) throws Exception;

    //JSON 객체 생성
    public JSONObject getJsonObject(String mid) throws Exception;

    //날짜 만들기
    public List<String> makeDate(ReqServiceDto.MonthSearch monthSearch) throws Exception;

    //카테고리 리스트 만들기
    public String[] makeCategoryList( String[] category, int size ) throws Exception;




}
