package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Builder
@Getter
public class MemberInfo {
    private String memberId;                   //회원번호(PK)
    private String id;                                  //회원아이디
    private String password;                    //회원비밀번호
    private String name;                            //회원이름
    private String nickName;                    //회원닉네임
    private char gender;                            //회원성별
    private String email;                             //회원 이메일
    private String profile;                         //회원 프로필
    private int point;                                  //회원포인트
    private int checkCnt;                           //연속출석일자
    private Date regDate;                           //회원가입일
    private Date resignDate;                      //회원탈퇴일
    private Date lastLoginDate;                 //마지막접속일

}
