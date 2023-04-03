package com.areum.moneymanager.dao;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.entity.Attendance;
import com.areum.moneymanager.entity.MemberInfo;
import com.areum.moneymanager.entity.Question;
import com.areum.moneymanager.entity.UpdateHistory;

import java.sql.SQLException;
import java.util.List;

public interface MemberDao {

    //특정회원의 출석 추가
    int insertAttend( String mid, String today ) throws SQLException;

    //회원 추가
    void insertMember( MemberInfo memberInfo ) throws SQLException;

    //회원정보 수정내역 추가
    void insertUpdateHistory( String mid, UpdateHistory updateHistory, String sql ) throws SQLException;

    //출석날짜 리스트 출력
    List<Attendance> selectAttendDateList(ReqMemberDto.AttendCheck date ) throws SQLException;

    //아이디 갯수 찾기
    Integer selectCountById( String id ) throws SQLException;

    Integer selectCountById( String id, String sql ) throws SQLException;

    //닉네임 갯수 찾기
    Integer selectCountByNickName( String nickName ) throws SQLException;

    //이메일 찾기
    MemberInfo selectEmail( String name, String id ) throws SQLException;

    //아이디와 마지막 접속일 찾기
    MemberInfo selectId(String name, String email ) throws SQLException;

    //특정회원 조회
    MemberInfo selectMemberByMid( String mid ) throws SQLException;

    //회원번호 찾기
    String selectMid( String mid ) throws SQLException;

    String selectMid( String id, String password ) throws SQLException;

    //비밀번호 찾기
    String selectPwd( String id );

    String selectPwdByMid( String mid ) throws SQLException;

    //탈퇴한 계정 찾기
    Integer selectResignMember( MemberInfo memberInfo ) throws SQLException;

    //회원유형 찾기
    String selectType( String mid ) throws SQLException;

    //회원정보 수정 리스트 최신데이터 조회
    UpdateHistory selectUpdateHistoryByMid( String mid, char type ) throws SQLException;

    //회원정보 변경
    int updateMemberInfo( String mid, String sql ) throws SQLException;

    //회원탈퇴 및 복구
    int updateResignMember( MemberInfo memberInfo, String table, String sql ) throws SQLException;

    //출석 포인트 수정
    void updatePoint( String mid, int point ) throws SQLException;

    //비밀번호 변경
    void updatePwd( String id, String password ) throws SQLException;

}
