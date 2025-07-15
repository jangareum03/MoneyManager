package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.Date;

@Builder
@Getter
public class Admin {
    /* 관리자번호(PK) */
    private String id;
    /* 관리자 역할 */
    private String role;
    /* 아이디 */
    private String adminName;
    /* 비밀번호 */
    private String password;
    /* 이름 */
    private String name;
    /* 닉네임 */
    private String nickName;
    /* 전화번호 */
    private String phone;
    /* 이메일 */
    private String email;
    /* 가입일 */
    private Timestamp createdAt;
    /* 답변등록수 */
    private Long answerCount;
}
