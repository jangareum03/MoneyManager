package com.moneymanager.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * <p>
 * 패키지이름    : com.moneymanager.support<br>
 * 파일이름       : RepositoryIntegrationTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-08-14<br>
 * 역할             : Repository 통합 테스트 클래스
 * </p>
 */
@SpringBootTest
@ActiveProfiles("dev")
public abstract class RepositoryIntegrationTest {

	@Autowired
	protected JdbcTemplate jdbcTemplate;

}