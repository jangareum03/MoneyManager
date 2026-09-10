package com.moneymanager.global.security;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.repository.SideBarRedisRepository;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.security<br>
 * 파일이름       : CustomAuthSuccessHandlerIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 8<br>
 * 설명              : CustomAuthSuccessHandler 클래스 로직을 검증하는 통합 테스트 클래스
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
class CustomAuthSuccessHandlerIT extends IntegrationTest {

    @Autowired
    SideBarRedisRepository redisRepository;

    @Test
    @DisplayName("로그인 성공하면 토큰 및 사이드바 저장되고 home화면으로 이동한다.")
    void processAuthentication_whenSucceeds() throws Exception {
    	//given: 회원 정보가 저징되어 있다.
        Member member = MemberTestFixture.builder()
                .withMemberInfo(MemberInfoTestFixture.builder())
                .buildWithEncodePassword(passwordEncoder.encode(MemberTestData.DEFAULT_PASSWORD));

        insertMember(member);

    	//when
        mockMvc.perform(
                post("/api/auth/login")
                        .param("username", member.getUsername())
                        .param("password", MemberTestData.DEFAULT_PASSWORD)
        )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().httpOnly("accessToken", true))
                .andExpect(cookie().path("accessToken", "/"));

        //then
        assertThat(redisRepository.getProfile(member.getMemberNumber())).isNotEmpty();
        assertThat(redisRepository.getNickname(member.getMemberNumber())).isNotEmpty();
    }

}