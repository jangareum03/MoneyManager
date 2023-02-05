package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dto.ResServiceDto;

import java.sql.SQLException;
import java.util.List;

public interface NoticeService {

    //공지사항 전체 개수
    int countAll() throws SQLException;

    //공지사항 리스트
    List<ResServiceDto.Notice> findNoticeList( ResServiceDto.Page pageInfo, int pageIndex ) throws SQLException;

}
