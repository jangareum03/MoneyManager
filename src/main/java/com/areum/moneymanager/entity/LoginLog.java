package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Builder
@Getter
public class LoginLog {
    private Long id;
    private String member_mid;
    private String browser;
    private String ip;
    private Date login_date;
    private char success;
    private String cause;
}
