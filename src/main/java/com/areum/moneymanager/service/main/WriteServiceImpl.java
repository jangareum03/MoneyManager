package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.ServiceDao;
import com.areum.moneymanager.dao.ServiceDaoImpl;
import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class WriteServiceImpl implements WriteService {

    private final ServiceDao accountBookDao;


    @Autowired
    public WriteServiceImpl( ServiceDaoImpl accountBookDao ) {
        this.accountBookDao = accountBookDao;
    }

    @Override
    public Map<String, List<ResServiceDto.category>> getCategory() throws Exception {
        Map<String, List<ResServiceDto.category>> resultMap = new HashMap<>();

        resultMap.put("income", ResServiceDto.category.toResIncomeCategory( accountBookDao.selectIncomeCategory() ));
        resultMap.put("expend", ResServiceDto.category.toResIncomeCategory( accountBookDao.selectExpenditureCategory() ));
        resultMap.put("parent", ResServiceDto.category.toResIncomeCategory( accountBookDao.selectParentCategory() ));

        return resultMap;
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
