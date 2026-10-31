package com.moneymanager.member.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : MemberRepository<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-08-11<br>
 * 역할             : 회원 테이블(MEMBER) 데이터를 조작
 * </p>
 */
@Repository
public class MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public MemberRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Integer existsByUsername(String username) {
        String sql = """
                SELECT
                    CASE
                        WHEN EXISTS(
                            SELECT 1
                            FROM member
                            WHERE username = ?
                        )
                        THEN 1
                        ELSE 0
                    END AS isExist
                FROM dual
                """;

        return jdbcTemplate.queryForObject(sql, Integer.class, username);
    }

}