package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Builder
@Getter
public class Member {
    /* 회원번호(PK) */
    private String id;
    /* 회원유형 */
    private String type;
    /* 회원상태 */
    private String status;
    /* 회원권한 */
    private String role;
    /* 아이디 */
    private String userName;
    /* 비밀번호 */
    private String password;
    /* 이름 */
    private String name;
    /* 생년월일 */
    private String birthDate;
    /* 닉네임 */
    private String nickName;
    /* 이메일 */
    private String email;
    /* 가입일 */
    private Timestamp createdAt;
    /* 탈퇴일 */
    private Timestamp deletedAt;
}
