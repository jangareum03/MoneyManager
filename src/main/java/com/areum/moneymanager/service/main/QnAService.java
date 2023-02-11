package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

public interface QnAService {

    //Q&A 전체 개수
    int countAll() throws SQLException;

    //Q&A 검색조건에 따른 개수 조회
    int countSearch( ReqServiceDto.QnASearch search ) throws SQLException;

    //Q&A 리스트
    List<ResServiceDto.QnA> findQAList( ResServiceDto.Page pageInfo, int pageIndex ) throws SQLException;

    //검색조건에 따른 Q&A 리스트
    List<ResServiceDto.QnA> findSearchList( ReqServiceDto.QnASearch search, ResServiceDto.Page pageInfo, int pageIndex ) throws SQLException;

    //Q&A 번호 일치여부 확인
    int isSameQnA( String id, String mid ) throws SQLException;

}
