package com.areum.moneymanager.service.member;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;

import java.sql.Date;
import java.sql.SQLException;

public interface MemberService {

    //날짜 기본양식으로 변경
    String changeBasicFormatByDate( Date date ) throws SQLException;

    //성별 양식 변경
    String changeFormatByGender( char gender ) throws SQLException;

    //마지막 접속일 양식 변경
    String changeFormatByLastLogin( Date lastLogin ) throws SQLException;

    //비밀번호 수정
    void changePwd(ReqMemberDto.FindPwd findPwdDto, String newPwd ) throws SQLException;

    //회원탈퇴
    void deleteMember( String mid, ReqMemberDto.Delete delete ) throws SQLException;

    //아이디 찾기
    ResMemberDto.FindId findId(ReqMemberDto.FindId findIdDto ) throws SQLException;
    String findId( String mid ) throws SQLException;

    //특정회원 찾기
    ResMemberDto.Member findMember( String mid ) throws SQLException;
    ResMemberDto.AuthMember findAuthMember( String id );

    //회원번호 찾기
    String findMid( String id, String password ) throws SQLException;

    //비밀번호 찾기
    ResMemberDto.FindPwd findPwd( ReqMemberDto.FindPwd findPwdDto ) throws SQLException;

    int findPwd( String mid, String password ) throws SQLException;

    //회원유형 찾기
    String findType( String mid ) throws SQLException;

    //프로필 수정내역 찾기
    ResMemberDto.ProfileHistory findUpdateHistory( String mid, char type ) throws SQLException;

    //아이디중복확인
    int idCheck( String id ) throws SQLException;

    //회원 확인
    boolean isMember( String id );

    //회원가입
    void joinMember( ReqMemberDto.Join joinDto ) throws SQLException;

    //로그인
    int loginCheck( String id, String password ) throws SQLException;

    //회원번호 생성
    String makeMemberId( String id ) throws SQLException;

    //회원정보 수정
    void modifyMember( String mid, ReqMemberDto.Update update ) throws SQLException;

    //닉네임중복확인
    int nickNameCheck( String nickName ) throws SQLException;

    //탈퇴회원 복구
    void recoverMember( String mid, ReqMemberDto.Login login ) throws SQLException;

}
