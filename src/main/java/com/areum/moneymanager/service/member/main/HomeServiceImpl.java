package com.areum.moneymanager.service.member.main;

import com.areum.moneymanager.dao.AttendanceDao;
import com.areum.moneymanager.dao.AttendanceDaoImpl;
import com.areum.moneymanager.dto.ReqHomeDto;
import com.areum.moneymanager.dto.ResHomeDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;


import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class HomeServiceImpl implements HomeService {

    private final AttendanceDao attendanceDao;
    private final Logger logger = LogManager.getLogger(HomeServiceImpl.class);


    public HomeServiceImpl( AttendanceDaoImpl attendanceDao) {
        this.attendanceDao = attendanceDao;
    }

    private static final int POINT = 5;

    @Override
    public void addPoint( String mid ) throws Exception {
        attendanceDao.updatePoint(mid, POINT);
    }

    @Override
    public List<ResHomeDto.AttendCheck> confirmAttend(String mid, int year, int month, int lastDate ) throws SQLException {
        ReqHomeDto.AttendCheck date;
        if( month < 10 ) {
            date = ReqHomeDto.AttendCheck.builder().mid(mid).startDate(""+year+ "0" + month+"01").endDate(""+year+"0"+month+lastDate).build();
        }else{
            date = ReqHomeDto.AttendCheck.builder().mid(mid).startDate(""+year+month+"01").endDate(""+year+month+lastDate).build();
        }


        return new ResHomeDto().toResAttendDto(attendanceDao.selectAttendDateList(date));
    }

    @Override
    public int toAttend( String mid ) throws Exception {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return attendanceDao.insertAttend( mid, today );
    }

}
