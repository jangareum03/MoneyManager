package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

import java.sql.Date;
import java.sql.Timestamp;


@Builder
@Getter
public class Attendance {
    /* 출석번호(PK) */
    private Long id;
    /* 회원번호(FK: member_id) */
    private Member member;
    /* 출석체크날짜 */
    private Date attendanceDate;
}
