package com.areum.moneymanager.dao;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.entity.Attendance;
import com.areum.moneymanager.entity.MemberInfo;

import java.sql.SQLException;
import java.util.List;

public interface MemberDao {

    //출석하기
    int insertAttend( String mid, String today ) throws SQLException;

    //회원정보 추가
    void insertMember( MemberInfo memberInfo ) throws SQLException;

    //출석날짜 리스트 출력
    List<Attendance> selectAttendDateList(ReqMemberDto.AttendCheck date ) throws SQLException;

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

    //출석 포인트 수정
    void updatePoint( String mid, int point ) throws SQLException;

    //비밀번호 변경
    void updatePwd( String id, String password ) throws SQLException;

}
