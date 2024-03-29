package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.ServiceDao;
import com.areum.moneymanager.dao.ServiceDaoImpl;
import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class WriteServiceImpl implements WriteService {

    private final ServiceDao serviceDao;


    @Autowired
    public WriteServiceImpl( ServiceDaoImpl serviceDao ) {
        this.serviceDao = serviceDao;
    }

    @Override
    public List<ResServiceDto.Category> getCategory() throws Exception {
        return ResServiceDto.Category.entityToDto(serviceDao.selectCategory());
    }

    @Override
    public List<ResServiceDto.Category> getCategory( String code ) throws Exception {
        return ResServiceDto.Category.entityToDto(serviceDao.selectCategory( code ));
    }

    @Override
    public void writeAccountBook(ReqServiceDto.Write write, String mid ) throws Exception {
        serviceDao.insertAccountBook(write.toEntity(), mid);
    }

}
