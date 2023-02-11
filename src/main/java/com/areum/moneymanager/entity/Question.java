package com.areum.moneymanager.entity;


import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Builder
@Getter
public class Question {
    //Q&A번호
    private String id;
    //등록자
    private String memberId;
    //제목
    private String title;
    //내용
    private String content;
    //공개여부
    private char open;
    //답변여부
    private char answer;
    //등록일
    private Date regDate;
    //수정일
    private Date modifiedDate;
    //회원정보
    private MemberInfo memberInfo;
}
