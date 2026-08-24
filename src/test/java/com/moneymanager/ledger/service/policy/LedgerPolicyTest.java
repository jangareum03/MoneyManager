package com.moneymanager.ledger.service.policy;

import com.moneymanager.global.config.MutableClock;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.StringTestData;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.ErrorCode.POLICY_VIOLATION;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.policy<br>
 * 파일이름       : LedgerPolicyTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 19<br>
 * 설명              : LedgerPolicy 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 8. 19.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class LedgerPolicyTest {

    private LedgerPolicy target;

    private LedgerDatePolicy datePolicy;
    private MutableClock clock;

    @BeforeEach
    void setUp() {
        clock = new MutableClock();
        clock.set(LocalDate.of(2026, 1, 1));

        datePolicy = new LedgerDatePolicy(clock);
        LedgerDateOptionPolicy dateOptionPolicy = new LedgerDateOptionPolicy(datePolicy, clock);
        ImageSlotPolicy imageSlotPolicy = new ImageSlotPolicy();

        target = new LedgerPolicy(
                datePolicy,
                dateOptionPolicy,
                imageSlotPolicy
        );
    }

    @Nested
    @DisplayName("가계부 생성 비즈니스 규칙 검증할 때")
    class ValidateCreate {

        @Test
        @DisplayName("가계부 거래날짜가 작성이 가능하면 검증에 통과한다.")
        void validatesTransactionDate_whenDateIsValid() {
        	//given
            Ledger ledger = LedgerFixture.builder()
                    .date(LocalDate.now(clock).minusMonths(3))
                    .saved();
        	
        	//when
            assertDoesNotThrow(() -> target.validateCreatable(ledger));
        }
        
        @Test
        @DisplayName("가계부 거래날짜가 작성이 불가능하면 예외를 발생시킨다.")
        void throwsException_whenTransactionDateIsOutOfRange() {
            //given
            Ledger ledger = LedgerFixture.builder()
                    .date(LocalDate.now(clock).plusDays(1))
                    .saved();

        	//when
            Throwable throwable = catchThrowable(() -> target.validateCreatable(ledger));
        	
        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    
                    .hasErrorCode(POLICY_VIOLATION)
                    .hasWork("가계부 비즈니스 규칙 검증")
                    .hasCauseMessage("거래날짜 범위 초과")
                    .hasTarget(Ledger.class)
                    .hasValue("date", ledger.getDate())
                    .hasOption("min", datePolicy.minimum())
                    .hasOption("max", datePolicy.maximum());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
        @DisplayName("메모가 null이거나 비어있으면 검증에 통과한다.")
        void validatesMemo_whenMemoIsBlank(String memo) {
            //when
            Ledger ledger = LedgerFixture.builder()
                    .memo(memo)
                    .saved();

        	//when
        	assertDoesNotThrow(
                    () -> target.validateCreatable(ledger)
            );
        }
        
        @ParameterizedTest
        @MethodSource("validMemo")
        @DisplayName("메모 길이가 300이하면 검증에 통과한다.")
        void validatesMemo_whenMemoLengthIsWithin300(String memo) {
            //when
            Ledger ledger = LedgerFixture.builder()
                    .memo(memo)
                    .saved();

            //when
            assertDoesNotThrow(
                    () -> target.validateCreatable(ledger)
            );
        }

        static Stream<Arguments> validMemo() {
            return StringTestData.validLengths("가", 0, 300);
        }

        @ParameterizedTest
        @MethodSource("invalidMemo")
        @DisplayName("메모 길이가 300초과하면 에외를 발생시킨다.")
        void throwsBusinessException_whenMemoLengthExceeds300(String memo) {
        	//given
            Ledger ledger = LedgerFixture.builder()
                    .memo(memo)
                    .saved();

        	//when
            Throwable throwable = catchThrowable(() -> target.validateCreatable(ledger));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    
                    .hasErrorCode(POLICY_VIOLATION)
                    .hasWork("가계부 비즈니스 규칙 검증")
                    .hasCauseMessage("메모 길이 초과")
                    .hasTarget(Ledger.class)
                    .hasValue("memo", memo)
                    .hasOption("min", 0)
                    .hasOption("max", 300);
        }

        static Stream<Arguments> invalidMemo() {
            return StringTestData.invalidLengths("가", 0, 300);
        }

    }


}