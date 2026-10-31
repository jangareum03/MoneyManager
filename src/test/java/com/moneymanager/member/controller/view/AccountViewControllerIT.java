package com.moneymanager.member.controller.view;

import com.moneymanager.support.ControllerIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller.view<br>
 * 파일이름       : AccountViewControllerIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-11-01<br>
 * 역할             : AccountViewController 통합 테스트
 * </p>
 */
@DisplayName("AccountViewController 테스트")
class AccountViewControllerIT extends ControllerIntegrationTest {

    @Nested
    @DisplayName("회원가입 화면 요청 메서드는")
    class SignupView {
        
        @Test
        @DisplayName("200 코드와 함께 페이지를 반환한다.")
        void returns200AndView() throws Exception {
        	//when
            mockMvc.perform(
                    get("/signup")
            )
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("bodyClass", "signup-page"));
        }
        
    }

}