package com.areum.moneymanager.dao;

import com.areum.moneymanager.entity.MemberInfo;

import java.sql.SQLException;

public interface MemberDao {

    //회원정보 추가
    void insertMember( MemberInfo memberInfo ) throws SQLException;

    //아이디 갯수
    Integer selectCountById( String id ) throws SQLException;

    //닉네임 갯수
    Integer selectCountByNickName( String nickName ) throws SQLException;

    //이메일 찾기
    MemberInfo selectEmail( String name, String id ) throws SQLException;

    //아이디와 마지막 접속일 찾기
    MemberInfo selectId(String name, String email ) throws SQLException;

    //회원번호 찾기
    String selectMid( String mid ) throws SQLException;

    //회원번호 찾기
    String selectMid( String id, String password ) throws SQLException;

    //비밀번호 찾기
    String selectPwd( String id ) throws SQLException;

    //비밀번호 변경
    void updatePwd( String id, String password ) throws SQLException;
}
