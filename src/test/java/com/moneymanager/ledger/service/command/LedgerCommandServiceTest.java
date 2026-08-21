package com.moneymanager.ledger.service.command;

import com.moneymanager.global.exception.code.CategoryErrorCode;
import com.moneymanager.global.exception.exception.BusinessException;
import com.moneymanager.global.security.CurrentUser;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.FixedType;
import com.moneymanager.ledger.repository.LedgerRepository;
import com.moneymanager.ledger.service.read.CategoryReadService;
import com.moneymanager.ledger.service.read.LedgerReadService;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.request.LedgerWriteRequestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.command<br>
 * 파일이름       : LedgerCommandServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 19<br>
 * 설명              : LedgerCommandService 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 8. 19</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class LedgerCommandServiceTest {

    @InjectMocks
    private LedgerCommandService target;

    @Mock
    private CurrentUser currentUser;

    @Mock
    private LedgerReadService ledgerReadService;

    @Mock
    private CategoryReadService categoryReadService;

    @Mock
    private LedgerRepository ledgerRepository;

    @Nested
    @DisplayName("Ledger 객체 생성할 때")
    class Create {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("존재하는 회원과 유효한 요청이면 생성한다.")
            void createsLedger_whenUserExistsAndRequestIsValid() {
                //given
                String memberId = MemberTestData.MEMBER_ID;
                LedgerWriteRequest request = LedgerWriteRequestFixture
                        .withPlace()
                        .fixCycle("m")
                        .build();

                when(categoryReadService.exists(request.getCategoryCode()))
                        .thenReturn(Boolean.TRUE);

                //when
                Ledger result = target.create(memberId, request);

                //then
                assertThat(result).isNotNull();

                assertThat(result.getId()).isNull();
                assertThat(result.getMemo()).isNull();

                assertThat(result.getCode()).isNotBlank();
                assertThat(result.getMemberId()).isEqualTo(memberId);
                assertThat(result.getDate()).isEqualTo(LedgerTestData.LOCAL_DATE);
                assertThat(result.getCategory()).isEqualTo(request.getCategoryCode());
                assertThat(result.getFix()).isSameAs(FixedType.VARIABLE);
                assertThat(result.getFixCycle()).isSameAs(FixCycle.MONTHLY);
                assertThat(result.getMoney()).isEqualTo(Money.of(request.getAmount(), request.getPaymentType()));
                assertThat(result.getPlace()).isEqualTo(Place.ofOrNull(request.getPlaceName(), request.getRoadAddress(), request.getDetailAddress()));
            }

            @Test
            @DisplayName("장소 정보가 없으면 null로 포함되어 생성한다.")
            void createsLedgerWithNullPlace_whenLocationIsNull() {
                //given
                String memberId = MemberTestData.MEMBER_ID;
                LedgerWriteRequest request = LedgerWriteRequestFixture.builder().build();

                when(categoryReadService.exists(request.getCategoryCode()))
                        .thenReturn(Boolean.TRUE);

                //when
                Ledger result = target.create(memberId, request);

                //then
                assertThat(result.getPlace()).isNull();
            }

            @Test
            @DisplayName("매번 다른 가계부 코드로 생성한다.")
            void createsLedgerWithUniqueCode_whenCalledMultipleTimes() {
                //given
                String memberId = MemberTestData.MEMBER_ID;
                LedgerWriteRequest request = LedgerWriteRequestFixture.builder().build();

                when(categoryReadService.exists(request.getCategoryCode()))
                        .thenReturn(Boolean.TRUE);

                //when
                Ledger ledgerA = target.create(memberId, request);
                Ledger ledgerB = target.create(memberId, request);

                //then
                assertThat(ledgerA.getCode()).isNotEqualTo(ledgerB.getCode());
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("존재하지 않은 카테고리면 예외를 발생시킨다.")
            void throwsBusinessException_whenCategoryDoesNotExist() {
                //given
                String memberId = MemberTestData.MEMBER_ID;
                LedgerWriteRequest request = LedgerWriteRequestFixture.builder().build();

                when(categoryReadService.exists(request.getCategoryCode()))
                        .thenReturn(Boolean.FALSE);

                //when
                Throwable throwable = catchThrowable(() -> target.create(memberId, request));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .isInstanceOf(BusinessException.class)
                        .hasErrorCode(CategoryErrorCode.DATA_NOT_FOUND)
                        .hasWork("Ledger 생성")
                        .hasTarget(LedgerWriteRequest.class)
                        .hasValue("categoryCode", request.getCategoryCode());
            }

        }

    }

}