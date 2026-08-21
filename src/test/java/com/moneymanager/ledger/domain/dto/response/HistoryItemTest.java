package com.moneymanager.ledger.domain.dto.response;

import com.moneymanager.ledger.domain.dto.response.item.HistoryItem;
import com.moneymanager.ledger.domain.enums.CategoryType;
import com.moneymanager.ledger.domain.query.LedgerHistoryQuery;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.fixture.response.LedgerHistoryQueryFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

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
	@DisplayName("HistoryItem 생성할 때")
	class FromTest {

		@Nested
		@DisplayName("성공")
		class Success {
		
			@Test
			@DisplayName("유효한 값이면 생성한다.")
			void createsHistoryItem_whenQueryIsValid() {
				//given
				LedgerHistoryQuery historyQuery = LedgerHistoryQueryFixture.create();
				
				//when
				HistoryItem result = HistoryItem.from(historyQuery);
				
				//then
				assertThat(result.getCategoryType()).isEqualTo(CategoryType.INCOME);
				assertThat(result.getAmount()).isEqualTo(historyQuery.getAmount());
				assertThat(result.getCode()).isEqualTo(historyQuery.getCode());
			}
			
		}
		
		@Nested
		@DisplayName("실패")
		class Failure {
		
			@Test
			@DisplayName("카테고리 코드가 존재하지 않으면 예외를 전파한다.")
			void throwsException_whenCategoryCodeIsInvalid() {
				//given
				String category = "030101";

				LedgerHistoryQuery historyQuery = new LedgerHistoryQuery(
						LedgerTestData.CODE,
						LedgerTestData.LOCAL_DATE,
						LedgerTestData.AMOUNT,
						LedgerTestData.MEMO,
						CategoryTestData.SALARY_NAME,
						category
				);

				//when & then
				assertThatThrownBy(() -> HistoryItem.from(historyQuery))
						.isInstanceOf(NoSuchElementException.class);
			}
			
		}

	}

}