package com.moneymanager.member.controller.api;


import com.moneymanager.support.ControllerIntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller.api<br>
 * 파일이름       : AccountApiControllerIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-10-09<br>
 * 역할             : AccountApiController 통합 테스트
 * </p>
 */
@Transactional
@DisplayName("AccountApiController 테스트")
class AccountApiControllerIT extends ControllerIntegrationTest {

    private final String BASE_URI = "/api/accounts";

    @Nested
    @DisplayName("아이디 중복 확인 메서드는")
     class AvailabilityUsername {

        final String URI = BASE_URI + "/username-availability";

        @Test
        @DisplayName("미가입된 아이디는 200 상태코드와 사용 가능 응답을 반환한다.")
        void returns200_whenUsernameDoesNotExist() throws Exception {
        	//given
            String username = "user123";
        	
        	//when
            mockMvc.perform(
                    get(URI)
                            .param("username", username)
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("사용 가능한 아이디입니다."))
                    .andExpect(jsonPath("$.data.available").value(true));
        }

        @Test
        @DisplayName("가입된 아이디는 200 상태코드와 사용 불가 응답을 반환한다.")
        void returns200_whenUsernameExists() throws Exception {
        	//given
            String username = MemberTestData.DEFAULT_USERNAME;

        	//when
            mockMvc.perform(
                    get(URI)
                            .param("username", username)
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("이미 사용 중인 아이디입니다."))
                    .andExpect(jsonPath("$.data.available").value(false));
        }
        
        @Test
        @DisplayName("유효하지 않은 아이디는 400 상태코드를 반환한다.")
        void returns400_whenUsernameIsInvalid() throws Exception {
        	//given
            String username = "use";
        	
        	//when
            mockMvc.perform(
                    get(URI)
                            .param("username", username)
            )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("아이디는 4~15자로 입력해주세요."))
                    .andExpect(jsonPath("$.data").value(nullValue()));
        }

    }

}