package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dto.ResMemberDto;

import java.util.List;

public interface HomeService {

    //출석완료 리스트
    List<ResMemberDto.AttendCheck> completeAttend(String mid, int year, int month, int lastDate ) throws Exception;

    //출석하기
    int doAttend( String mid ) throws Exception;

    //출석체크 포인트 얻기
    void getPoint ( String mid ) throws Exception;

}
