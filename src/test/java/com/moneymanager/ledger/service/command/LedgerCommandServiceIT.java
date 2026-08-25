package com.moneymanager.ledger.service.command;

import com.moneymanager.global.exception.code.ErrorCode;
import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.fixture.entity.LedgerTestFixture;
import com.moneymanager.support.fixture.request.LedgerUpdateRequestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.command<br>
 * 파일이름       : LedgerCommandServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 24<br>
 * 설명              : LedgerCommandService 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 8. 24</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Transactional
class LedgerCommandServiceIT extends IntegrationTest {

    @Autowired
    LedgerCommandService target;

    @Nested
    @DisplayName("가계부 수정할 때")
    class Update {

        Ledger saved;

        @BeforeEach
        void setUp() {
            Long id = ledgerRepository.save(
                    LedgerTestFixture.builder().build()
            );

            saved = ledgerRepository.findById(id);
        }

        @Test
        @DisplayName("수정된 정보로 요청하면 가계부 정보를 수정한다.")
        void updatesAccountBook_whenRequestIsValid() {
        	//given
            LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
                    .fixed(LedgerTestData.FIXED_REPEAT.getValue())
                    .fixCycle(LedgerTestData.MONTHLY_CYCLE.getValue())
                    .amount(50000L)
                    .paymentType(PaymentType.CARD.name())
                    .build();

        	//when
            target.updateLedger(request, saved);
        	
        	//then
        	assertThat(saved.getFix()).isEqualTo(LedgerTestData.FIXED_REPEAT);
        	assertThat(saved.getFixCycle()).isEqualTo(LedgerTestData.MONTHLY_CYCLE);
            assertThat(saved.getMoney().getAmount()).isEqualTo(50000L);
            assertThat(saved.getMoney().getPaymentType()).isEqualTo(PaymentType.CARD);
        }
        
        @Test
        @DisplayName("존재하지 않은 카테고리면 예외를 발생시킨다.")
        void throwsException_whenCategoryDoesNotExist() {
        	//given
            LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
                    .categoryCode("no-exist")
                    .build();
        	
        	//when
            Throwable throwable = catchThrowable(() -> target.updateLedger(request, saved));

        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(ErrorCode.INVALID_VALUE)
                    .hasWork("Ledger 수정")
                    .hasTarget(LedgerUpdateRequest.class)
                    .hasValue("categoryCode", request.getCategoryCode());
        }

    }

}