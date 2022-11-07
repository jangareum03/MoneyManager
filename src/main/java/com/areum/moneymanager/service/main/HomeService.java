package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dto.ReqHomeDto;
import com.areum.moneymanager.dto.ResHomeDto;

import java.sql.SQLException;
import java.util.List;

public interface HomeService {

    //출석확인
    List<ResHomeDto.AttendCheck> confirmAttend(String mid, int year, int month, int lastDate ) throws SQLException;

}
