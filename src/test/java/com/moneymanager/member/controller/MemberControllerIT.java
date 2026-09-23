package com.moneymanager.member.controller;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller<br>
 * 파일이름       : MemberControllerIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 22<br>
 * 설명              : MemberController 통합 테스트
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
 * 		 	  <td>26. 9. 22</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class MemberControllerIT extends IntegrationTest {

    private static final String BASE_URL = "/members";
    private static final String WITHDRAWAL_URL = BASE_URL + "/mypage/withdrawal";

    Member member;

    @BeforeEach
    void setUp() {
        member = MemberTestFixture.builder().build();

        insertMember(member);
    }

    @Nested
    @DisplayName("회원탈퇴 페이지를 요청하면")
    class GetWithdrawalPage {
        
        @Test
        @DisplayName("회원탈퇴 페이지를 반환한다.")
        void returnsMemberWithdrawalView_whenRequested() throws Exception {
        	//when
            mockMvc.perform(
                    get(WITHDRAWAL_URL)
                            .cookie(accessTokenCookie(member.getId()))
            )
                    .andExpect(status().isOk())
                    .andExpect(view().name("member/mypage_delete"));
        }
        
        @Test
        @DisplayName("회원 아이디를 Model에 저장한다.")
        void addsUsernameToModel_whenAccessingWithdrawalPage() throws Exception {
        	//when
            mockMvc.perform(
                    get(WITHDRAWAL_URL)
                            .cookie(accessTokenCookie(member.getId()))
            )
                    .andExpect(model().attribute("username", member.getUsername()));
        }

        @Test
        @DisplayName("탈퇴 사유가 화면에 표시한다.")
        void addsWithdrawalReasons_whenAccessingWithdrawalPage() throws Exception {
        	//when
            mockMvc.perform(
                    get(WITHDRAWAL_URL)
                            .cookie(accessTokenCookie(member.getId()))
            )
                    .andExpect(content().string(containsString("서비스 이용하지 않음")))
                    .andExpect(content().string(containsString("기타")));
        }

    }

}