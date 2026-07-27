package com.moneymanager.ledger.domain.dto.response;

import com.moneymanager.domain.ledger.dto.query.LedgerHistoryQuery;
import com.moneymanager.domain.ledger.dto.response.HistoryItem;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.exception.exception.ValidationException;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.fixture.response.LedgerHistoryQueryFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.response<br>
 * 파일이름       : HistoryItemTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 23<br>
 * 설명              : HistoryItem 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 7. 23.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class HistoryItemTest {

	@Nested
	@DisplayName("HistoryItem 변환")
	class FromTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {
		
			@Test
			@DisplayName("LedgerHistoryQuery가 HistoryItem으로 변환한다.")
			void createsHistoryItem_whenQueryIsValid() {
				//given
				LedgerHistoryQuery historyQuery = LedgerHistoryQueryFixture.create();
				
				//when: HistoryItem으로 변환한다.
				HistoryItem result = HistoryItem.from(historyQuery);
				
				//then: 변환된 값들이 필드에 정확히 저장되어 있다.
				assertThat(result.getCategoryType()).isEqualTo(CategoryType.INCOME);
				assertThat(result.getAmount()).isEqualTo(historyQuery.getAmount());
				assertThat(result.getCode()).isEqualTo(historyQuery.getCode());
			}
			
		}
		
		@Nested
		@DisplayName("실패 케이스")
		class Failure {
		
			@Test
			@DisplayName("잘못된 categoryCode가 전달되면 예외를 전달한다.")
			void throwsException_whenCategoryCodeIsInvalid() {
				//given: 유효하지 않은 카테고리 코드가 주어진다.
				String category = "030101";

				LedgerHistoryQuery historyQuery = new LedgerHistoryQuery(
						LedgerTestData.CODE,
						LedgerTestData.LOCAL_DATE,
						LedgerTestData.AMOUNT,
						LedgerTestData.MEMO,
						CategoryTestData.SALARY_NAME,
						category
				);

				//when & then: HistoryItem으로 변환 중 ValidationException이 발생한다.
				assertThatThrownBy(() -> HistoryItem.from(historyQuery))
						.isInstanceOf(ValidationException.class);
			}
			
		}

	}

}