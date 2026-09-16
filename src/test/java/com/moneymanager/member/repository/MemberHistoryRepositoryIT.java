package com.moneymanager.member.repository;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberHistory;
import com.moneymanager.member.domain.enums.HistoryType;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : MemberHistoryRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 17<br>
 * 설명              : MemberHistoryRepository 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 9. 17</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class MemberHistoryRepositoryIT extends IntegrationTest {

    @Autowired
    private MemberHistoryRepository target;

    @Nested
    @DisplayName("변경 내역을 저장할 때")
    class InsertUpdate {

        Member member;

        @BeforeEach
        void setUp() {
            member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .build();

            insertMember(member);
        }

        @Test
        @DisplayName("회원의 변경내역을 저장한다.")
        void savesMemberHistory_whenRequestIsValid() {
        	//given
            MemberHistory history = MemberHistory.update(member.getId(), "이름", member.getName(), "김철수", LocalDateTime.of(2026, 3, 15, 13, 20));

        	//when
            target.insertHistory(history);

        	//then
            Map<String, Object> saved = jdbcTemplate.queryForMap(
                    "SELECT * FROM member_history WHERE member_id = ?",
                    member.getId()
            );

            assertThat(saved.get("id")).isNotNull();
            assertThat(saved.get("member_id")).isEqualTo(member.getId());
            assertThat(saved.get("type")).isEqualTo(HistoryType.UPDATE.name());
            assertThat(saved.get("item")).isEqualTo(history.getItem());
            assertThat(saved.get("before_info")).isEqualTo(history.getBeforeInfo());
            assertThat(saved.get("after_info")).isEqualTo(history.getAfterInfo());
        }
        
        @Test
        @DisplayName("회원이 존재하지 않으면 저장에 실패한다.")
        void throwsException_whenMemberDoesNotExist() {
        	//given
            MemberHistory history = MemberHistory.update("no-member", "이름", member.getName(), "김철수", LocalDateTime.of(2026, 3, 15, 13, 20));
        	
        	//when
            assertThatThrownBy(() ->target.insertHistory(history))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }

    }

}