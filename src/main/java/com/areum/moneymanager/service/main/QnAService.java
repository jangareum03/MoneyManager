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

    //Q&A 답변 찾기
    ResServiceDto.Answer findAnswer( String id, String title ) throws SQLException;

    //특정 질문 찾기
    ResServiceDto.QnADetail findQnADetail( String id ) throws SQLException;

    //Q&A 리스트
    List<ResServiceDto.QnA> findQnAList( ResServiceDto.Page pageInfo, int pageIndex ) throws SQLException;

    //검색조건에 따른 Q&A 리스트
    List<ResServiceDto.QnA> findSearchList( ReqServiceDto.QnASearch search, ResServiceDto.Page pageInfo, int pageIndex ) throws SQLException;

    //Q&A 번호 일치여부 확인
    int isSameQnA( String id, String mid ) throws SQLException;

    //질문 등록
    void registerQnA( ReqServiceDto.Question question, String mid ) throws SQLException;
}
