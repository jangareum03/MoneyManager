package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.ServiceDao;
import com.areum.moneymanager.dao.ServiceDaoImpl;
import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.entity.Question;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

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
    public ResServiceDto.Answer findAnswer( String id, String title ) throws SQLException {
        return ResServiceDto.Answer.toDTO( serviceDao.selectAnswer(id), title );
    }

    @Override
    public ResServiceDto.QnADetail findQnADetail(String id) throws SQLException {
        return ResServiceDto.QnADetail.toDTO( serviceDao.selectQuestionById( id ) );
    }

    @Override
    public List<ResServiceDto.QnA> findQnAList( ResServiceDto.Page pageInfo, int pageIndex ) throws SQLException {
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

    @Override
    public void registerQnA( ReqServiceDto.Question question, String mid ) throws SQLException {
        String id = serviceDao.selectLastId( mid );
        serviceDao.insertQnA( question.toEntity(), makeQuestionId(id, mid), mid );
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

    private String makeQuestionId( String id, String mid ) {
        String result;
        if( Objects.isNull(id)) {
            result = mid + "_001";
        }else {
            int next = Integer.parseInt( id.substring(9) );
            String num = String.format( "%03d", ++next );
            result = mid + "_" + num;
        }

        return result;
    }
}
