package com.areum.moneymanager.service.member.main;

import com.areum.moneymanager.dao.AccountBookDao;
import com.areum.moneymanager.dao.AccountBookDaoImpl;
import com.areum.moneymanager.dto.ReqAccountBookDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class WriteServiceImpl implements WriteService {

    private final AccountBookDao accountBookDao;


    @Autowired
    public WriteServiceImpl( AccountBookDaoImpl accountBookDao ) {
        this.accountBookDao = accountBookDao;
    }

    @Override
    public void addAccountBook( ReqAccountBookDto.Write write, String mid ) throws Exception {
        accountBookDao.insertAccountBook(write.toEntity(), mid);
    }

}
