package com.areum.moneymanager.service;


public interface MemberService {

    //아이디중복확인
    int idCheck(String id);

    //회원번호 생성
    String makeMemberId(String id);

    //닉네임중복확인
    int nickNameCheck(String nickName);


}
