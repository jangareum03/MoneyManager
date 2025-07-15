package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Getter
@Builder
public class Notice {
    /* 공지사항 번호(PK) */
    private String id;
    /* 등록자(FK: admin_id) */
    private Admin admin;
    /* 유형 */
    private char type;
    /* 상태 */
    private String status;
    /* 제목 */
    private String title;
    /* 내용 */
    private String content;
    /* 등록일 */
    private Date createdDate;
    /* 수정일 */
    private Date updatedDate;
    /* 조회수 */
    private Long viewCount;
    /* 우선순위 */
    private int rank;
}
