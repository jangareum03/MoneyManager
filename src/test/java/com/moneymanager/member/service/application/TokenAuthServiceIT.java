package com.moneymanager.member.service.application;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : TokenAuthServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 10<br>
 * 설명              : TokenService 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 9. 10</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class TokenAuthServiceIT extends IntegrationTest {

    @Nested
    @DisplayName("인증 정보 설정할 때")
    class Authenticate {
        
        @Test
        @DisplayName("정상적인 토큰이면 인증객체를 설정 후 200을 반환한다.")
        void returnsAuthentication_whenTokenIsValid() throws Exception {
        	//given
            Member member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                            .build();

            insertMember(member);
        	
        	//when
            mockMvc.perform(
                    get("/ledgers/histories")
                            .cookie(accessTokenCookie(member.getMemberNumber()))
            )
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("유효하지 않은 토큰이면 401을 반환한다.")
        void throwsException_whenTokenIsInvalid() throws Exception {
        	//given
            String token = "no-token";

            Member member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .build();

            insertMember(member);
        	
        	//when
            mockMvc.perform(
                    get("/ledgers/histories")
                            .cookie(new Cookie("accessToken", token))
            )
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("INVALID_ACCESS_TOKEN"));
        }
        
    }


    @Nested
    @DisplayName("토큰 처음 발급할 때")
    class IssueToken {

        @Test
        @DisplayName("회원가입한 회원으로 토큰을 발급한다.")
        void createsToken_whenUserExists() throws Exception {
            //given
            Member member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .buildWithEncodePassword(passwordEncoder.encode(MemberTestData.DEFAULT_PASSWORD));

            insertMember(member);

            mockMvc.perform(
                    post("/api/auth/login")
                            .param("username",member.getUsername())
                            .param("password", MemberTestData.DEFAULT_PASSWORD)
            )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/home"));
        }

    }

}