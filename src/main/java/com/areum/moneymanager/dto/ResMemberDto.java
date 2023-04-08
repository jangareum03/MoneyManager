package com.areum.moneymanager.dto;

import com.areum.moneymanager.entity.Attendance;
import com.areum.moneymanager.entity.MemberInfo;
import com.areum.moneymanager.entity.UpdateHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
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
    @Builder
    public static class FindId{
        private String id;
        private Date lastDate;
        private Date resignDate;

        public static FindId toDTO( MemberInfo memberInfo ) {
            return FindId.builder().id( memberInfo.getId() ).lastDate( memberInfo.getLastLoginDate() ).resignDate( memberInfo.getResignDate() ).build();
        }

    }

    //비밀번호 찾기
    @Getter
    @Builder
    public static class FindPwd {
        private String email;
        private Date resignDate;

        public static FindPwd toDTO( MemberInfo memberInfo ) {
            return  FindPwd.builder().email( memberInfo.getEmail() ).resignDate( memberInfo.getResignDate() ).build();
        }
    }

    //회원 찾기
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
        private Date lastLogin;
        private int totalCount;
        private String email;

        public static Member toDto( MemberInfo memberInfo ) {
            return Member.builder().id(memberInfo.getId()).name(memberInfo.getName()).nickName(memberInfo.getNickName())
                    .gender(memberInfo.getGender()).profile(memberInfo.getProfile()).joinDate(memberInfo.getRegDate()).lastLogin(memberInfo.getLastLoginDate())
                    .totalCount(memberInfo.getCheckCnt()).email(memberInfo.getEmail()).build();
        }
    }

    @Getter
    @Builder
    public static class MoveDate {
        private int year;
        private int month;
        private int date;
        private int maxDate;
    }

    //프로필 수정내역
    @Getter
    @Builder
    public static class ProfileHistory {
        private char success;
        private String year;
        private String month;
        private String date;
        private String time;
        private String profile;

        public static ProfileHistory toDto( UpdateHistory updateHistory ) {
            return ProfileHistory.builder().success(updateHistory.getSuccess())
                    .year( String.valueOf(updateHistory.getDatetime().toLocalDate().getYear()) ).month( String.valueOf(updateHistory.getDatetime().toLocalDate().getMonthValue()) ).date( String.valueOf(updateHistory.getDatetime().toLocalDate().getDayOfMonth()) )
                    .time( String.valueOf(updateHistory.getDatetime().getTime()) ).profile( updateHistory.getAfInfo() ).build();
        }
    }

}
