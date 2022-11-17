package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dto.ReqServiceDto;

public interface WriteService {

    //가계부 등록
    void addAccountBook(ReqServiceDto.Write write, String mid ) throws Exception;

}
