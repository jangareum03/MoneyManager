package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.ServiceDao;
import com.areum.moneymanager.dao.ServiceDaoImpl;
import com.areum.moneymanager.dto.ReqServiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class WriteServiceImpl implements WriteService {

    private final ServiceDao accountBookDao;


    @Autowired
    public WriteServiceImpl( ServiceDaoImpl accountBookDao ) {
        this.accountBookDao = accountBookDao;
    }

    @Override
    public void addAccountBook(ReqServiceDto.Write write, String mid ) throws Exception {
        accountBookDao.insertAccountBook(write.toEntity(), mid);
    }

}
