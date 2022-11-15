package com.areum.moneymanager.service.member;


import com.areum.moneymanager.dto.ReqMemberInfoDto;
import com.areum.moneymanager.dto.ResMemberInfoDto;

import java.sql.SQLException;

public interface MemberService {

    //비밀번호 수정
    void changePwd(ReqMemberInfoDto.FindPwd findPwdDto, String newPwd ) throws SQLException;

    //아이디 찾기
    ResMemberInfoDto.FindId findId(ReqMemberInfoDto.FindId findIdDto ) throws SQLException;

    //회원번호 찾기
    String findMid( ReqMemberInfoDto.Login loginDto ) throws SQLException;

    //비밀번호 찾기
    ResMemberInfoDto.FindPwd findPwd(ReqMemberInfoDto.FindPwd findPwdDto ) throws SQLException;

    //아이디중복확인
    int idCheck( String id ) throws SQLException;

    //회원가입
    void joinMember( ReqMemberInfoDto.Join joinDto ) throws SQLException;

    //로그인
    int loginCheck( ReqMemberInfoDto.Login loginDto ) throws SQLException;

    //회원번호 생성
    String makeMemberId( String id ) throws SQLException;

    //닉네임중복확인
    int nickNameCheck( String nickName ) throws SQLException;

}
