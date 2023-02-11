package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Builder
@Getter
public class Answer {
    //답변번호
    private String id;
    //질문번호
    private String questionId;
    //등록자
    private String adminId;
    //답변 내용
    private String content;
    //등록날짜
    private Date regDate;
    //수정날짜
    private Date modifiedDate;
    //관리자정보
    private Admin admin;
}
