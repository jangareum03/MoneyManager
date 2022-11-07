package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.AttendanceDao;
import com.areum.moneymanager.dao.AttendanceDaoImpl;
import com.areum.moneymanager.dto.ReqHomeDto;
import com.areum.moneymanager.dto.ResHomeDto;
import com.areum.moneymanager.mapper.AttendanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;


@Service
public class HomeServiceImpl implements HomeService {

    private final AttendanceDao attendanceDao;
    private final AttendanceMapper attendanceMapper;


    @Autowired
    public HomeServiceImpl( AttendanceDaoImpl attendanceDao, AttendanceMapper attendanceMapper ) {
        this.attendanceDao = attendanceDao;
        this.attendanceMapper = attendanceMapper;
    }

    @Override
    public List<ResHomeDto.AttendCheck> confirmAttend(String mid, int year, int month, int lastDate ) throws SQLException {
        ReqHomeDto.AttendCheck date;
        if( month < 10 ) {
            date = ReqHomeDto.AttendCheck.builder().mid(mid).startDate(""+year+ "0" + month+"01").endDate(""+year+"0"+month+lastDate).build();
        }else{
            date = ReqHomeDto.AttendCheck.builder().mid(mid).startDate(""+year+month+"01").endDate(""+year+month+lastDate).build();
        }


        return attendanceMapper.toResAttendDto(attendanceDao.selectAttendDateList(date));
    }

}
