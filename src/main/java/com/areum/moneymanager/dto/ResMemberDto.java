package com.areum.moneymanager.dto;

import com.areum.moneymanager.entity.Attendance;
import com.areum.moneymanager.entity.MemberInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ResMemberDto {

    //출석체크 확인
    @Getter
    @Builder
    public static class AttendCheck{
        private String date;
    }

    public List<AttendCheck> toResAttendDto(List<Attendance> list ) {
        List<ResMemberDto.AttendCheck> attendCheckList =  new ArrayList<>(list.size());

        for( Attendance attendance : list ) {
            attendCheckList.add( attendanceToAttendCheck(attendance) );
        }

        return attendCheckList;
    }

    private ResMemberDto.AttendCheck attendanceToAttendCheck(Attendance attendance) {
        if( attendance == null ) {
            return null;
        }

        return ResMemberDto.AttendCheck.builder().date(attendance.getCheckDate().toString().substring(8)).build();
    }

    //아이디 찾기
    @Getter
    @AllArgsConstructor
    public static class FindId{
        private String id;
        private Timestamp date;
    }

    public static FindId FindIdResponse( MemberInfo memberInfo ) {
        return new FindId( memberInfo.getId(), Timestamp.valueOf(memberInfo.getLastLoginDate().toString()) );
    }

    @Getter
    @AllArgsConstructor
    public static class FindPwd {
        private String email;
    }
    public static FindPwd FindPwdResponse(MemberInfo memberInfo ){
        return new FindPwd( memberInfo.getEmail() );
    }

    @Getter
    @Builder
    public static class Member {
        private String id;
        private char type;
        private String name;
        private String nickName;
        private char gender;
        private String profile;
        private Date joinDate;
        private int totalCount;
        private String email;
    }

    @Getter
    @Builder
    public static class MoveDate {
        private int year;
        private int month;
        private int date;
        private int maxDate;
    }



}
