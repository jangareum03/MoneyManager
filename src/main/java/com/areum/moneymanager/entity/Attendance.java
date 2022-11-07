package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Builder
@Getter
public class Attendance {
    private Long id;                        //출석번호(PK)
    private String memberId;         //회원번호(FK)
    private Date checkDate;         //출석체크날짜
}
