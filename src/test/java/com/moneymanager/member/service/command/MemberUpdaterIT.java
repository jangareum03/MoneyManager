package com.moneymanager.member.service.command;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberHistory;
import com.moneymanager.member.domain.enums.MemberStatus;
import com.moneymanager.member.repository.MemberHistoryRepository;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.command<br>
 * 파일이름       : MemberUpdaterIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 23<br>
 * 설명              : MemberUpdater 통합 테스트
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
class MemberUpdaterIT extends IntegrationTest {

    @Autowired
    private MemberUpdater target;

    @SpyBean
    private MemberHistoryRepository historyRepository;

    @Nested
    @DisplayName("회원 탈퇴 상태로 변경할 때")
    class ChangeWithdrawn {

        Member member;

        @BeforeEach
        void setUp() {
            member = MemberTestFixture.builder().build();

            insertMember(member);
        }

        @AfterEach
        void tearDown() {
            jdbcTemplate.update(
                    "DELETE FROM member"
            );
        }

        @Test
        @DisplayName("내역 저장에 실패하면 롤백한다.")
        void rollback_whenHistorySaveFails() {
            //given
            doThrow(new RuntimeException("내역 저장 실패"))
                    .when(historyRepository)
                    .insertHistory(any(MemberHistory.class));

            //when
            assertThatThrownBy(() -> target.changeToWithdrawn(member))
                    .isInstanceOf(RuntimeException.class);

            //then
            Member result = memberRepository.findById(member.getId());

            assertThat(result.getStatus()).isSameAs(MemberStatus.ACTIVE);
            assertThat(result.getDeletedAt()).isNull();

            assertThat(
                    jdbcTemplate.queryForObject(
                            "SELECT count(*) FROM member_history WHERE member_id = ?",
                            Integer.class,
                            member.getId()
                    )
            ).isZero();
        }

    }

}