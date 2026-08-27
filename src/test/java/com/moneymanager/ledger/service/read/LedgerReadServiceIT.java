package com.moneymanager.ledger.service.read;

import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.read<br>
 * 파일이름       : LedgerReadServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 26<br>
 * 설명              : LedgerReadService 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 8. 26</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Transactional
class LedgerReadServiceIT extends IntegrationTest {

    @Autowired
    LedgerReadService target;

    @Nested
    @Sql("/sql/ledger-get-test.sql")
    @DisplayName("자신의 가계부 여러개를 조회할 때")
    class GetOwnerLedgers {

        @Test
        @DisplayName("유효한 가계부이고 소유자라면 리스트에 추가한다.")
        void addsToList_whenLedgerIsValidAndOwner() {
            //given
            String memberId = "member1";
            List<String> codes = List.of("code-1", "code-2", "code-4");

            //when
            List<Ledger> result = target.getOwnerLedgers(memberId, codes);

            //then
            assertThat(result)
                    .hasSize(codes.size())
                    .extracting(Ledger::getMemberId)
                    .containsOnly(memberId);
        }

        @Test
        @DisplayName("타인의 가계부는 제외한다.")
        void excludesList_whenLedgerBelongsToOtherUser() {
            //given
            String memberId = "member1";
            List<String> codes = List.of("code-1", "code-2", "code-3");

            //when
            List<Ledger> result = target.getOwnerLedgers(memberId, codes);

            //then
            assertThat(result)
                    .hasSize(2)
                    .extracting(Ledger::getCode)
                    .doesNotContain("code-3");
        }

        @Test
        @DisplayName("존재하지 않은 코드면 제외한다.")
        void excludesList_whenCodeDoesNotExist() {
            //given
            String memberId = "member1";
            List<String> codes = List.of("no-exist", "code-2");

            //when
            List<Ledger> result = target.getOwnerLedgers(memberId, codes);

            //then
            assertThat(result)
                    .hasSize(1)
                    .extracting(Ledger::getCode)
                    .doesNotContain("no-exist");
        }
        
        @Test
        @DisplayName("자신의 가계부가 없으면 빈 리스트를 반환한다.")
        void returnsEmptyList_whenHouseholdLedgerDoesNotExist() {
            //given
            String memberId = "member1";
            List<String> codes = List.of("no-exist1", "no-exist2");

            //when
            List<Ledger> result = target.getOwnerLedgers(memberId, codes);

        	//then
        	assertThat(result).isEmpty();
        }

    }

}