package com.areum.moneymanager.dto;

import com.areum.moneymanager.entity.Attendance;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public class ResHomeDto {

    //출석체크 확인
    @Getter
    @Builder
    public static class AttendCheck{
        private String date;
    }

    public List<AttendCheck> toResAttendDto( List<Attendance> list ) {
        List<AttendCheck> attendCheckList =  new ArrayList<>(list.size());

        for( Attendance attendance : list ) {
            attendCheckList.add( attendanceToAttendCheck(attendance) );
        }

        return attendCheckList;
    }

    private AttendCheck attendanceToAttendCheck(Attendance attendance) {
        if( attendance == null ) {
            return null;
        }

        return AttendCheck.builder().date(attendance.getCheckDate().toString().substring(8)).build();
    }
}
