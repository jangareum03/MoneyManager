package com.moneymanager.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * <p>
 * 패키지이름    : com.moneymanager<br>
 * 파일이름       : ControllerIntegrationTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-10.-02<br>
 * 역할             : 컨트롤러 통합 테스트 클래스
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public abstract class ControllerIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

}