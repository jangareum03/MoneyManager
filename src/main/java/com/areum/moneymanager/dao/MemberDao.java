package com.areum.moneymanager.dao;

import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.MemberInfo;

import java.util.List;

public interface MemberDao {

    //회원정보 추가
    void insertMember( Member member, MemberInfo memberInfo, String mid ) throws Exception;

    //아이디 갯수
    Integer selectCountById( String id );

    //닉네임 갯수
    Integer selectCountByNickName( String nickName );

    //아이디와 마지막 접속일 찾기
    ResMemberDto.FindId selectId(String name, String email );

    //회원번호 찾기
    String selectMid( String mid );

    //비밀번호 찾기
    String selectPwd( String id );

}
