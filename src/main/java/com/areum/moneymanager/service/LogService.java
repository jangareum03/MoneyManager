package com.areum.moneymanager.service;

import com.areum.moneymanager.dao.LogDao;
import com.areum.moneymanager.dao.LogDaoImpl;
import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.entity.LoginHistory;
import com.areum.moneymanager.entity.UpdateHistory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@Service
public class LogService {

    private final LogDao logDao;

    public LogService( LogDaoImpl logDao ) {
        this.logDao = logDao;
    }

    //로그인 로그 등록
    public void memberLogIn( ReqMemberDto.LoginLog loginLog, HttpServletRequest request ) throws SQLException {
        String ip, browser, cause = null;

        if( loginLog != null ) {
            ip = getIp(request);
            browser = getBrowser(request);

            LoginHistory loginHistory = loginLog.toEntity( ip, browser );
            logDao.insertLogin( loginHistory );
        }else{
            throw new RuntimeException("로그 객체 존재하지 않음");
        }
    }

    //회원정보 수정 로그 등록
    public void updateMember(String mid, UpdateHistory updateHistory, int result, String sql ) throws SQLException {

        if( result == 1 ) {
            sql = "INSERT INTO tb_update_history(id, member_id, success, datetime, type, bf_info, af_info) VALUES(seq_updateHistory.NEXTVAL, ?, ?, SYSDATE, ?, ?, ?)";
            updateHistory.updateSuccess('y');
        }

        logDao.insertUpdateHistory( mid, updateHistory, sql );
    }


    private String getIp( HttpServletRequest request ) {
        String ip = request.getHeader("X-Forwarded-For");

        if( ip == null ) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if( ip == null ) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if( ip == null ) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if( ip == null ) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if( ip == null ) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    private String getBrowser( HttpServletRequest request ) {
        String agent = request.getHeader("User-Agent");
        String browser = "null";

        if( agent != null ) {
            if (agent.contains("Trident")) {
                browser = "MSIE";
            }else if( agent.contains("Chrome") ) {
                browser = "Chrome";
            }else if( agent.contains("Opera") ) {
                browser = "Opera";
            }else if( agent.contains("Safari") ) {
                browser = "Safari";
            }else if(  agent.contains("FireFox")) {
                browser = "FireFox";
            }
        }

        return browser;
    }

}
