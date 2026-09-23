package com.moneymanager.member.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneymanager.member.domain.dto.request.MemberWithdrawalRequest;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.enums.MemberStatus;
import com.moneymanager.member.domain.enums.WithdrawalReason;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller.api<br>
 * 파일이름       : MemberApiControllerIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 23<br>
 * 설명              : MemberApiController 통합 테스트 클래스
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
 * 		 	  <td>26. 9. 23</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Transactional
class MemberApiControllerIT extends IntegrationTest {

    private static final String BASE_URL = "/api/members";
    private static final String WITHDRAWAL_URL = BASE_URL + "/withdrawal";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Member member;


    @BeforeEach
    void setUp() {
        member = MemberTestFixture
                .builder()
                .password(passwordEncoder.encode(MemberTestData.DEFAULT_PASSWORD))
                .build();

        insertMember(member);
    }

    @Nested
    @DisplayName("회원탈퇴 요청하면")
    class Withdrawal {
        
        @Test
        @DisplayName("탈퇴 성공하면  응답을 반환한다.")
        void returnsOk_whenWithdrawalSucceeds() throws Exception {
            //given
            MemberWithdrawalRequest request = new MemberWithdrawalRequest(
                    MemberTestData.DEFAULT_PASSWORD,
                    WithdrawalReason.SERVICE_DISSATISFACTION.getMessageKey().toLowerCase(),
                    null
            );

        	//when
            mockMvc.perform(
                    delete(WITHDRAWAL_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
                            .cookie(accessTokenCookie(member.getId()))
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.messageKey").value("member.withdrawal.success"))
                    .andExpect(jsonPath("$.next").value("/login"));
        	
        	//then
            Member after = memberRepository.findById(member.getId());

        	assertThat(after.getStatus()).isSameAs(MemberStatus.DELETED);
            assertThat(after.getDeletedAt()).isNotNull();
        }
        
        @Test
        @DisplayName("탈퇴 실패하면 실패 응답을 반환한다.")
        void returnsBadRequest_whenWithdrawalFails() throws Exception {
        	//given
            MemberWithdrawalRequest request = new MemberWithdrawalRequest(
                    MemberTestData.DEFAULT_PASSWORD,
                    WithdrawalReason.OTHER.getMessageKey().toLowerCase(),
                    null
            );
        	
        	//when
            mockMvc.perform(
                    delete(WITHDRAWAL_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
                            .cookie(accessTokenCookie(member.getId()))
            )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("member.withdrawal.other"));

            //then
            Member after = memberRepository.findById(member.getId());

            assertThat(after.getStatus()).isSameAs(MemberStatus.ACTIVE);
            assertThat(after.getDeletedAt()).isNull();
        }

    }

}