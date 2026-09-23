package com.moneymanager.member.repository;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberHistory;
import com.moneymanager.member.domain.enums.HistoryType;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : MemberHistoryRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 23<br>
 * 설명              : MemberHistoryRepository 통합 테스트
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
class MemberHistoryRepositoryIT extends IntegrationTest {

    @Autowired
    MemberHistoryRepository target;

    @Nested
    @DisplayName("회원 내역을 저장할 때")
    class Insert {

        Member member;

        @BeforeEach
        void setUp() {
            member = MemberTestFixture.builder().build();

            insertMember(member);
        }
        
        @Test
        @DisplayName("탈퇴 내역이면 삭제 유형이랑 현재날짜로 저장한다.")
        void savesMemberWithdrawal_whenRequestIsValid() {
        	//given
            MemberHistory history = MemberHistory.delete(member.getId());
        	
        	//when
            assertThatCode(() -> target.insertHistory(history))
                    .doesNotThrowAnyException();
        	
        	//then
            Map<String, Object> historySaved = jdbcTemplate.queryForObject(
                    """
                            SELECT member_id, type, item, before_info, after_info
                                FROM member_history
                                WHERE member_id = ?
                                    ORDER BY id DESC
                                FETCH FIRST 1 ROWS ONLY
                        """,
                    (rs, num) -> {
                        Map<String, Object> map = new HashMap<>();

                        map.put("memberId", rs.getString("member_id"));
                        map.put("type", HistoryType.valueOf(rs.getString("type")));
                        map.put("item", rs.getString("item"));
                        map.put("beforeInfo", rs.getString("before_info"));
                        map.put("afterInfo", rs.getString("after_info"));

                        return map;
                    },
                    member.getId()
            );

            assertThat(historySaved).isNotNull();

            assertThat(historySaved.get("type")).isSameAs(HistoryType.DELETE);
            assertThat(historySaved.get("item")).isEqualTo("회원탈퇴");
            assertThat(historySaved.get("beforeInfo")).isNull();
            assertThat(historySaved.get("afterInfo")).isNull();
        }
        
    }

}