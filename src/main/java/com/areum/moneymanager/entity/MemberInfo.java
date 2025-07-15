package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;
import java.sql.Timestamp;


@Builder
@Getter
public class MemberInfo {
    /* 회원번호(PK) */
    private Member member;
    /* 성별 */
    private Character gender;
    /* 프로필 */
    private String profile;
    /* 포인트 */
    private Long point;
    /* 연속출석일자 */
    private Long consecutiveDays;
    /* 등록 가능한 이미지 수 */
    private int imageLimit;
    /* 마지막 접속일 */
    private Timestamp loginAt;
}
