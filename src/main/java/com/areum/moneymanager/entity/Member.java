package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.sql.Date;

@Builder
@Getter
@ToString
public class Member {
    private String id;
    private char type;
    private Date reg_date;
    private char resign;
    private Date resign_date;
    private char lock;
}
