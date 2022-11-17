package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.ServiceDao;
import com.areum.moneymanager.dao.ServiceDaoImpl;
import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class WriteServiceImpl implements WriteService {

    private final ServiceDao accountBookDao;


    @Autowired
    public WriteServiceImpl( ServiceDaoImpl accountBookDao ) {
        this.accountBookDao = accountBookDao;
    }

    @Override
    public List<ResServiceDto.category> getExpenditureCategory() throws Exception {
        return ResServiceDto.category.toResIncomeCategory( accountBookDao.selectExpenditureCategory() );
    }

    @Override
    public List<ResServiceDto.category> getIncomeCategory() throws Exception {
        return ResServiceDto.category.toResIncomeCategory( accountBookDao.selectIncomeCategory() );
    }

    @Override
    public void writeAccountBook(ReqServiceDto.Write write, String mid ) throws Exception {
        accountBookDao.insertAccountBook(write.toEntity(), mid);
    }

}
