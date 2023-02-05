package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class Notice {
    //공지사항 번호
    private String id;
    //등록자
    private String adminId;
    //공지사항 유형
    private char type;
    //공지사항 제목
    private String title;
    //공지사항 내용
    private String content;
    //등록일
    private Date regDate;
    //수정일
    private Date modifiedDate;
    //조회수
    private int readCnt;
    //우선순위
    private int rank;
}
