package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class Admin {
    //관리자번호
    private String id;
    //아이디
    private String account;
    //비밀번호
    private String password;
    //이름
    private String name;
    //닉네임
    private String nickname;
    //전화번호
    private String phone;
    //이메일
    private String email;
    //팀
    private String team;
    //직위
    private char position;
    //그룹번호
    private String groupId;
    //가입일
    private Date regDate;
    //답변등록수
    private int answerCnt;
}
