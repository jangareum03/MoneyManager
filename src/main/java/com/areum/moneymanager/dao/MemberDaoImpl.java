package com.areum.moneymanager.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class MemberDaoImpl implements MemberDao {

    private final JdbcTemplate jdbcTemplate;

    public MemberDaoImpl( DataSource dataSource ) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Integer selectCountById(String id) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(id) FROM tb_member_info WHERE id=?",
                Integer.class,
                id
        );

    }

    @Override
    public Integer selectCountByNickName(String nickName) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(nickname) FROM tb_member_info WHERE nickname=?",
                Integer.class,
                nickName
        );
    }

    @Override
    public String selectMid(String mid) {
        return jdbcTemplate.queryForObject(
                "SELECT mid FROM tb_member WHERE mid LIKE '%' || ? || '%'",
                String.class,
                mid
        );
    }
}
