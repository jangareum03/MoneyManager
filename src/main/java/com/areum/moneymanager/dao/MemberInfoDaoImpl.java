package com.areum.moneymanager.dao;

import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.MemberInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MemberDaoImpl implements MemberDao {

    private final JdbcTemplate jdbcTemplate;
    private final Logger LOGGER = LogManager.getLogger(MemberDaoImpl.class);

    public MemberDaoImpl( DataSource dataSource ) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void insertMember( MemberInfo memberInfo, String mid ) {
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement( Connection con ) throws SQLException {
                PreparedStatement pstmt = con.prepareStatement(
                        "INSERT ALL INTO tb_member(mid, type) VALUES(?,'A') "
                                + "INTO tb_member_info(member_mid, id, password, name, nickname, gender, email) VALUES(?, ?, ?, ?, ?, ?, ?)"
                                + "SELECT * FROM dual");
                pstmt.setString(1, mid);
                pstmt.setString(2, mid);
                pstmt.setString(3, memberInfo.getId());
                pstmt.setString(4, memberInfo.getPassword());
                pstmt.setString(5, memberInfo.getName());
                pstmt.setString(6, memberInfo.getNickName());
                pstmt.setString(7, Character.toString(memberInfo.getGender()));
                pstmt.setString(8, memberInfo.getEmail());

                return pstmt;
            }
        });
    }

    @Override
    public Integer selectCountById( String id ) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(id) FROM tb_member_info WHERE id=?",
                Integer.class,
                id
        );

    }

    @Override
    public Integer selectCountByNickName( String nickName ) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(nickname) FROM tb_member_info WHERE nickname=?",
                Integer.class,
                nickName
        );
    }

    @Override
    public ResMemberDto.FindPwd selectEmail( String name, String id ) {
        try{
            return jdbcTemplate.queryForObject(
                    "SELECT email FROM tb_member_info WHERE name=? AND id=?",
                    ResMemberDto.FindPwd.class,
                    name, id
            );
        }catch (EmptyResultDataAccessException e) {
            LOGGER.error("{} 발생! {}", e.getClass(), e.getStackTrace());
            return null;
        }
    }

    @Override
    public ResMemberDto.FindId selectId(String name, String email) {
        List<ResMemberDto.FindId> member = jdbcTemplate.query(
                "SELECT tmi.id, tll.login_date" +
                        " FROM tb_member_info tmi, tb_login_log tll" +
                        " WHERE tll.MEMBER_MID = tmi.MEMBER_MID" +
                        " AND tmi.NAME = ? AND tmi.EMAIL = ?" +
                        "ORDER BY tll.LOGIN_DATE DESC",
                new RowMapper<ResMemberDto.FindId>() {
                    @Override
                    public ResMemberDto.FindId mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new ResMemberDto.FindId(rs.getString(1), rs.getTimestamp(2));
                    }
                },
                name, email
        );
        return member.isEmpty() ? null : member.get(0);
    }


    @Override
    public String selectMid( String mid ) {
        return jdbcTemplate.queryForObject(
                "SELECT mid FROM tb_member WHERE mid LIKE '%' || ? || '%'",
                String.class,
                mid
        );
    }

    @Override
    public String selectMid(String id, String password) {
        return jdbcTemplate.queryForObject(
                "SELECT member_mid FROM tb_member_info WHERE id=? AND password=?",
                String.class,
                id, password
        );
    }

    @Override
    public String selectPwd( String id) {
        try{
            return jdbcTemplate.queryForObject(
                    "SELECT password FROM tb_member_info WHERE id=?",
                    String.class,
                    id
            );
        }catch (EmptyResultDataAccessException e){
            return "0";
        }
    }

    @Override
    public void updatePwd( String id, String password ) {
        jdbcTemplate.update(
                "UPDATE tb_member_info SET password=? WHERE id=?",
                password, id
        );
    }

}
