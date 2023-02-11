package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.ServiceDao;
import com.areum.moneymanager.dao.ServiceDaoImpl;
import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@Service
public class QnAServiceImpl implements QnAService {

    private final ServiceDao serviceDao;

    public QnAServiceImpl( ServiceDaoImpl serviceDao ){
        this.serviceDao = serviceDao;
    }


    @Override
    public int countAll() throws SQLException {
        return serviceDao.selectAllQuestion();
    }

    @Override
    public int countSearch( ReqServiceDto.QnASearch search ) throws SQLException {
        String sql = makeSQL( search );

        return serviceDao.selectQuestionBySearch( sql );
    }

    @Override
    public List<ResServiceDto.QnA> findQAList( ResServiceDto.Page pageInfo, int pageIndex ) throws SQLException {
        int end = pageInfo.getPostCount() * pageIndex;
        int start = end - (pageInfo.getPostCount() - 1);

        return ResServiceDto.QnA.toDTO( serviceDao.selectAllQuestion( start, end ), pageInfo, (pageIndex - 1) );
    }

    @Override
    public List<ResServiceDto.QnA> findSearchList( ReqServiceDto.QnASearch search, ResServiceDto.Page pageInfo, int pageIndex ) throws SQLException {
        int end = pageInfo.getPostCount() * pageIndex;
        int start = end - (pageInfo.getPostCount() - 1);
        String sql = makeSQL( search );

        return ResServiceDto.QnA.toDTO( serviceDao.selectQuestionBySearch( sql, start, end ), pageInfo, (pageIndex -1) );
    }

    @Override
    public int isSameQnA( String id, String mid ) throws SQLException {
        String writer = serviceDao.selectQnAMemberId( id );

        return mid.equals(writer) ? 1 : 0;
    }

    private String makeSQL( ReqServiceDto.QnASearch search ) {
        String result = null;

        switch ( search.getType() ) {
            case "title":
                result = "WHERE title LIKE '%' || '" + search.getKeyword() + "' || '%' ";
                break;
            case "writer":
                result = "WHERE member_id = (SELECT member_id FROM tb_member_info WHERE nickname LIKE '%' || '" + search.getKeyword() + "' || '%') ";
                break;
            case "period":
                result = "WHERE regdate BETWEEN TO_DATE(" + search.getStartDate() + "00, 'YYYYMMDDHH24') AND TO_DATE(" + search.getEndDate() + "235959, 'YYYYMMDDHH24MISS') ";
                break;
        }

        return result;
    }
}
