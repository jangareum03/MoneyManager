package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Builder
@Getter
public class Answer {
    /* 답변번호(PK) */
    private String id;
    /* 질문번호(FK: question_id) */
    private Question question;
    /* 등록자(FK : admin_id) */
    private Admin admin;
    /* 답변 내용 */
    private String content;
    /* 등록일 */
    private Date createdDate;
    /* 수정일 */
    private Date updatedDate;
}
