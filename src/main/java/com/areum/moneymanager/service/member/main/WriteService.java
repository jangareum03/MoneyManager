package com.areum.moneymanager.service.member.main;

import com.areum.moneymanager.dto.ReqAccountBookDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

public interface WriteService {

    //가계부 등록
    void addAccountBook( ReqAccountBookDto.Write write, String mid ) throws Exception;

}
