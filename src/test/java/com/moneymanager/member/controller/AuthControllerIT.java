package com.moneymanager.member.controller;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.service.generator.HashGenerator;
import com.moneymanager.member.service.generator.UuidGenerator;
import com.moneymanager.redis.service.PasswordResetService;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller<br>
 * 파일이름       : AuthControllerIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 8<br>
 * 설명              : AuthController 클래스 요청을 검증하는 통합 테스트 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 * 		<thead>
 * 		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 * 		 	  	<td>날짜</td>
 * 		 	  	<td>작성자</td>
 * 		 	  	<td>변경내용</td>
 * 		 	</tr>
 * 		</thead>
 * 		<tbody>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 9. 8</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class AuthControllerIT extends IntegrationTest {

    @Autowired
    private UuidGenerator tokenGenerator;

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private PasswordResetService passwordResetService;

    @Nested
    @DisplayName("로그인 화면 요청할 때")
    class LoginView {

        @BeforeEach
        void setup() {
            Member member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .buildWithEncodePassword(passwordEncoder.encode(MemberTestData.DEFAULT_PASSWORD));

            insertMember(member);
        }
        
        @Test
        @DisplayName("로그인 실패하면 에러 메시지를 반환한다.")
        void rejectsRequest_whenLoginFails() throws Exception {
        	//given
            String username = MemberTestData.DEFAULT_USERNAME;
            String password = "noPass123";
        	
        	//when
            MvcResult result = mockMvc.perform(
                    post("/api/auth/login")
                            .param("username", username)
                            .param("password", password)
            )
                    .andReturn();

            MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
            Assertions.assertNotNull(session);

            //redirect된 URL로 재요청
            mockMvc.perform(
                    get("/login")
                            .session(session)
            )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("loginError", "member.login.failed"));
        }
        
    }


    @Nested
    @DisplayName("아이디 찾기 화면 요청할 때")
    class FindIdView {
        @Test
        @DisplayName("아이디 찾기 화면을 반환한다.")
        void returnsFindIdViewData_whenRequestIsValid() throws Exception {
        	//when
            mockMvc.perform(
                    get("/account/find-id")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML_VALUE))
                    .andExpect(view().name("/member/recovery_id"));
        }
    }


    @Nested
    @DisplayName("비밀번호 초기화 화면 요청할 때")
    class ResetPwdView {

        private final String URL = "/password/reset";
        
        @Test
        @DisplayName("token이 존재하면 회원 초기화 페이지로 이동한다.")
        void returnsResetView_whenTokenExists() throws Exception {
        	//given
            String token = tokenGenerator.generate();
            String hashToken = hashGenerator.sha256(token);

            passwordResetService.saveToken(hashToken);
        	
        	//when
            mockMvc.perform(
                    get(URL)
                            .param("token", token)
            )
                    .andExpect(status().isOk())
                    .andExpect(view().name("/member/password_reset"));
        }
        
        @Test
        @DisplayName("token이 존재하지 않으면 로그인 페이지로 리디렉션 한다.")
        void returnsLoginRedirect_whenTokenDoesNotExist() throws Exception {
            //given
            String token = tokenGenerator.generate();

            //when
            mockMvc.perform(
                            get(URL)
                                    .param("token", token)
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/auth/login"));
        }

    }

}