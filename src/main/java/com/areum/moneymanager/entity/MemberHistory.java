package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;
import java.sql.Timestamp;

@Getter
@Builder
public class MemberHistory {
    /* 수정내역번호(PK) */
    private Long id;
    /* 회원번호(FK: member_id) */
    private Member member;
    /* 성공여부 */
    private char success;
    /* 수정유형 */
    private String type;
    /* 수정항목 */
    private String item;
    /* 기존정보 */
    private String beforeInfo;
    /* 변경정보 */
    private String afterInfo;
    /* 실패사유 */
    private String failureReason;
    /* 변경날짜 */
    private Timestamp updatedAt;
}
