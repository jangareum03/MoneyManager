package com.areum.moneymanager.entity;


import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Builder
@Getter
public class Question {
    /* 질문번호(PK) */
    private Long id;
    /* 회원번호(FK: member_id) */
    private Member member;
    /* 제목 */
    private String title;
    /* 내용 */
    private String content;
    /* 공개여부 */
    private char open;
    /* 답변여부 */
    private char answer;
    /* 등록일 */
    private Timestamp createdDate;
    /* 수정일 */
    private Timestamp updatedDate;
}
