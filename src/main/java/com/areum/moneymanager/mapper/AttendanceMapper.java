package com.areum.moneymanager.mapper;

import com.areum.moneymanager.dto.ReqHomeDto;
import com.areum.moneymanager.dto.ResHomeDto;
import com.areum.moneymanager.entity.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    //DTO -> Entity
    @Mapping(source = "mid", target = "memberId")
    Attendance toEntity( ReqHomeDto.AttendCheck dto );

    //Entity -> DTO
    default List<ResHomeDto.AttendCheck> toResAttendDto(List<Attendance> attendanceList){
        List<ResHomeDto.AttendCheck> attendCheckList = new ArrayList<>( attendanceList.size() );
        for( Attendance attendance : attendanceList ) {
            attendCheckList.add( attendanceToAttendCheck(attendance) );
        }

        return attendCheckList;
    }

    default ResHomeDto.AttendCheck attendanceToAttendCheck(Attendance attendance ) {
        if( attendance == null ) {
            return null;
        }
        String date = attendance.getCheckDate().toString();

        return ResHomeDto.AttendCheck.builder().date(date.substring(8)).build();
    }
}
