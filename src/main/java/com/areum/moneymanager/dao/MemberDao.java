package com.areum.moneymanager.dao;

public interface MemberDao {

    //아이디 갯수
    Integer selectCountById(String id);

    //닉네임 갯수
    Integer selectCountByNickName(String nickName);

    //회원번호 찾기
    String selectMid(String mid);
}
