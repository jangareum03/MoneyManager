package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;

import java.util.List;
import java.util.Map;

public interface WriteService {

    //카테고리 얻기
    Map<String, List<ResServiceDto.category>> getCategory() throws Exception;

    //지출 카테고리 얻기
    List<ResServiceDto.category> getExpenditureCategory() throws Exception;

    //수입 카테고리 얻기
    List<ResServiceDto.category> getIncomeCategory() throws Exception;

    //가계부 등록
    void writeAccountBook(ReqServiceDto.Write write, String mid ) throws Exception;

}
