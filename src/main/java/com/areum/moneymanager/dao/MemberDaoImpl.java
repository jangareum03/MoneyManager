package com.areum.moneymanager.dao;

import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.MemberInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class MemberDaoImpl implements MemberDao {

    private final JdbcTemplate jdbcTemplate;

    public MemberDaoImpl( DataSource dataSource ) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void insertMember( Member member, MemberInfo memberInfo, String mid ) {
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
    public String selectMid( String mid ) {
        return jdbcTemplate.queryForObject(
                "SELECT mid FROM tb_member WHERE mid LIKE '%' || ? || '%'",
                String.class,
                mid
        );
    }
}
