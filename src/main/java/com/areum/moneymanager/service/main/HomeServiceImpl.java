package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.MemberDao;
import com.areum.moneymanager.dao.MemberDaoImpl;
import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;


import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class HomeServiceImpl implements HomeService {

    private final MemberDao memberDao;
    private final Logger logger = LogManager.getLogger(HomeServiceImpl.class);

    public HomeServiceImpl( MemberDaoImpl memberDao) {
        this.memberDao = memberDao;
    }

    private static final int POINT = 5;

    @Override
    public List<ResMemberDto.AttendCheck> completeAttend(String mid, int year, int month, int lastDate ) throws SQLException {
        ReqMemberDto.AttendCheck date;
        if( month < 10 ) {
            date = ReqMemberDto.AttendCheck.builder().mid(mid).startDate(""+year+ "0" + month+"01").endDate(""+year+"0"+month+lastDate).build();
        }else{
            date = ReqMemberDto.AttendCheck.builder().mid(mid).startDate(""+year+month+"01").endDate(""+year+month+lastDate).build();
        }


        return new ResMemberDto().toResAttendDto(memberDao.selectAttendDateList(date));
    }

    @Override
    public int doAttend( String mid ) throws Exception {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return memberDao.insertAttend( mid, today );
    }

    @Override
    public void getPoint( String mid ) throws Exception {
        memberDao.updatePoint(mid, POINT);
    }



}
