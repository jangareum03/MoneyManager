package com.areum.moneymanager.service.member;


import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;

import java.sql.SQLException;

public interface MemberService {

    //비밀번호 수정
    void changePwd(ReqMemberDto.FindPwd findPwdDto, String newPwd ) throws SQLException;

    //아이디 찾기
    ResMemberDto.FindId findId(ReqMemberDto.FindId findIdDto ) throws SQLException;

    //회원번호 찾기
    String findMid( ReqMemberDto.Login loginDto ) throws SQLException;

    //비밀번호 찾기
    ResMemberDto.FindPwd findPwd(ReqMemberDto.FindPwd findPwdDto ) throws SQLException;

    //아이디중복확인
    int idCheck( String id ) throws SQLException;

    //회원가입
    void joinMember( ReqMemberDto.Join joinDto ) throws SQLException;

    //로그인
    int loginCheck( ReqMemberDto.Login loginDto ) throws SQLException;

    //회원번호 생성
    String makeMemberId( String id ) throws SQLException;

    //닉네임중복확인
    int nickNameCheck( String nickName ) throws SQLException;

}
