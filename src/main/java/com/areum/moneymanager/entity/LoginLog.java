package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Builder
@Getter
public class LoginLog {
    /* 로그번호(PK) */
    private Long id;
    /* 회원번호(FK) */
    private String memberId;
    /* 회원 아이디 */
    private String userName;
    /* 로그인 유형 */
    private String type;
    /* 성공여부 */
    private char success;
    /* 브라우저 */
    private String browser;
    /* 접속 IP */
    private String ip;
    /* 실패사유 */
    private String failureReason;
    /* 접속날짜 */
    private Timestamp accessAt;
}
