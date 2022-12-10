package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;

import java.util.List;

public interface WriteService {

    //대카테고리 얻기
    List<ResServiceDto.Category> getCategory() throws Exception;

    //중&소 카테고리 얻기
    List<ResServiceDto.Category> getCategory( String code ) throws Exception;

    //가계부 등록
    void writeAccountBook(ReqServiceDto.Write write, String mid ) throws Exception;

}
