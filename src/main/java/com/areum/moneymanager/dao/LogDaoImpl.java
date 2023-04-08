package com.areum.moneymanager.dao;

import com.areum.moneymanager.entity.LoginHistory;
import com.areum.moneymanager.entity.UpdateHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class LogDaoImpl implements LogDao {

    private final JdbcTemplate jdbcTemplate;

    public LogDaoImpl( DataSource dataSource ) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void insertLogin( LoginHistory loginHistory  ) throws SQLException {
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pstmt = con.prepareStatement(
                                "INSERT INTO tb_login_history VALUES(seq_login_history.NEXTVAL, ?, ?, ?, SYSDATE, ? ,?)");

                        pstmt.setString(1, loginHistory.getLogin_id());
                        pstmt.setString(2, loginHistory.getBrowser());
                        pstmt.setString(3, loginHistory.getIp());
                        pstmt.setString(4, String.valueOf(loginHistory.getSuccess()));
                        pstmt.setString(5, loginHistory.getCause());

                        return pstmt;
                    }
                }
        );
    }

    @Override
    public void insertUpdateHistory( String mid, UpdateHistory updateHistory, String sql ) throws SQLException {
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement pstmt = con.prepareStatement(
                        sql
                );

                pstmt.setString(1, mid);
                pstmt.setString(2, Character.toString(updateHistory.getSuccess()));
                pstmt.setString(3, Character.toString(updateHistory.getType()));
                pstmt.setString(4, updateHistory.getBfInfo());
                pstmt.setString(5, updateHistory.getAfInfo());
                if( updateHistory.getDeleteType() != null ) {
                    pstmt.setString(6, updateHistory.getDeleteType());
                    pstmt.setString(7, updateHistory.getDeleteCause());
                }

                return pstmt;
            }
        });
    }
}
