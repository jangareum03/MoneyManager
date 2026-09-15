package com.moneymanager.member.controller;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller<br>
 * 파일이름       : MemberControllerIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 16<br>
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
 * 		 	  <td>26. 9. 16</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class MemberControllerIT extends IntegrationTest {

    Member member;

    @BeforeEach
    void setUp() {
        member = MemberTestFixture.builder()
                .withMemberInfo(MemberInfoTestFixture.builder())
                .build();

        insertMember(member);
    }

    @Test
    @DisplayName("로그인한 회원의 정보로 마이페이지로 이동한다.")
    void returnsMember_whenMemberExists() throws Exception {
    	//when
        mockMvc.perform(
                get("/members/mypage")
                        .cookie(accessTokenCookie(member.getMemberNumber()))
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("member"))
                .andExpect(view().name("member/mypage_info"));

    }

}