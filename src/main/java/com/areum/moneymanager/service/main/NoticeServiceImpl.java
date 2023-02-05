package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.ServiceDao;
import com.areum.moneymanager.dao.ServiceDaoImpl;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.entity.Notice;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final ServiceDao serviceDao;

    public NoticeServiceImpl( ServiceDaoImpl serviceDao ) {
        this.serviceDao = serviceDao;
    }

    @Override
    public int countAll() throws SQLException {
        return serviceDao.selectAllNotice();
    }

    @Override
    public List<ResServiceDto.Notice> findNoticeList( ResServiceDto.Page pageInfo, int pageIndex ) throws SQLException {
        int end = pageInfo.getPostCount() * pageIndex;
        int start = end - ( pageInfo.getPostCount() - 1 );

        List<Notice> noticeList = serviceDao.selectNoticeByPage( start, end );

        return ResServiceDto.Notice.toDTO( noticeList, changeType( noticeList ), pageInfo, (pageIndex - 1) );
    }

    private Map<Character, String> changeType( List<Notice> noticeList ) {
        Map<Character, String> resultMap = new HashMap<>(noticeList.size());

        for( Notice notice : noticeList ) {
            String type = null;
            String color = null;

            switch ( notice.getType() ) {
                case 'N' :
                    type = "<span>[일반]</span>";
                    break;
                case 'E' :
                    color = "#274A97";
                    type = "<span style='color: " + color + "'>[이벤트]</span>";
                    break;
                case 'P' :
                    color = "#EA3B20";
                    type = "<span style='color: " + color + "'>[긴급]</span>";
                    break;
            }

            resultMap.put(notice.getType(), type);
        }

        return resultMap;
    }
}
