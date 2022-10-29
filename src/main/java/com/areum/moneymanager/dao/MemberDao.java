package com.areum.moneymanager.dao;

import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.MemberInfo;

public interface MemberDao {

    //회원정보 추가
    void insertMember( Member member, MemberInfo memberInfo, String mid ) throws Exception;

    //아이디 갯수
    Integer selectCountById( String id );

    //닉네임 갯수
    Integer selectCountByNickName( String nickName );

    //회원번호 찾기
    String selectMid( String mid );

    //비밀번호 찾기
    String selectPwd( String id );

}
