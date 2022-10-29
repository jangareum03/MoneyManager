package com.areum.moneymanager.service.member;


import com.areum.moneymanager.dto.ReqMemberDto;

public interface MemberService {

    //아이디중복확인
    int idCheck( String id );

    //회원가입
    void joinMember( ReqMemberDto.Join joinDto, String mid ) throws Exception;

    //로그인
    int loginCheck( ReqMemberDto.Login loginDto );

    //회원번호 생성
    String makeMemberId( String id );

    //닉네임중복확인
    int nickNameCheck( String nickName );


}
