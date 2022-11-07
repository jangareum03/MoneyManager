package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dto.ResHomeDto;

import java.util.List;

public interface HomeService {

    //출석완료 리스트
    List<ResHomeDto.AttendCheck> confirmAttend(String mid, int year, int month, int lastDate ) throws Exception;

    //출석하기
    int toAttend( String mid ) throws Exception;

}
