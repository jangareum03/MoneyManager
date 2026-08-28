package com.moneymanager.ledger.service.policy;

import com.moneymanager.global.config.MutableClock;
import com.moneymanager.ledger.domain.dto.vo.LedgerPeriod;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.HistoryType;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.fixture.entity.LedgerTestFixture;
import com.moneymanager.support.stream.StringTestStream;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

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
    private LedgerHistoryPeriodPolicy historyPeriodPolicy;

    private MutableClock clock;

    @BeforeEach
    void setUp() {
        clock = new MutableClock();

        datePolicy = new LedgerDatePolicy(clock);
        historyPeriodPolicy = spy(new LedgerHistoryPeriodPolicy(clock));

        LedgerDateOptionPolicy dateOptionPolicy = new LedgerDateOptionPolicy(datePolicy, clock);
        ImageSlotPolicy imageSlotPolicy = new ImageSlotPolicy();

        target = new LedgerPolicy(
                clock,
                datePolicy,
                dateOptionPolicy,
                historyPeriodPolicy,
                imageSlotPolicy
        );
    }


    @Nested
    @DisplayName("유형별로 내역기간을 생성할 때")
    class HistoryPeriod {

        @Test
        @DisplayName("YEAR이면 연도 기간 메서드를 호출한다.")
        void callsYearlyPeriodMethod_whenPeriodTypeIsYear() {
            //when
            LedgerPeriod result = target.resolveHistoryPeriod(HistoryType.YEAR);

            //then
            verify(historyPeriodPolicy).resolveYear();
        }

        @Test
        @DisplayName("MONTH이면 월 기간 메서드를 호출한다")
        void callsMonthlyPeriodMethod_whenPeriodTypeIsMonth() {
            //when
            LedgerPeriod result = target.resolveHistoryPeriod(HistoryType.MONTH);

            //then
            verify(historyPeriodPolicy).resolveMonth();
        }

        @Test
        @DisplayName("WEEK면 주 기간 메서드를 호출한다.")
        void callsWeeklyPeriodMethod_whenPeriodTypeIsWeek() {
            //when
            LedgerPeriod result = target.resolveHistoryPeriod(HistoryType.WEEK);

            //then
            verify(historyPeriodPolicy).resolveWeek();
        }

    }


    @Nested
    @DisplayName("내역 유형별로 제목을 생성할 때")
    class TitleByHistoryType {

        @BeforeEach
        void setUp() {
            clock.set(LocalDate.of(2026, 3, 2));
        }
        
        @Test
        @DisplayName("YEAR이면 연도만 반환한다.")
        void returnsYearOnly_whenHistoryTypeIsYear() {
        	//when
            String result = target.getTitleByHistoryType(HistoryType.YEAR);
        	
        	//then
        	assertThat(result).isEqualTo("2026년");
        }
        
        @Test
        @DisplayName("MONTH면 연도+월을 반환한다.")
        void returnsYearAndMonth_whenHistoryTypeIsMonth() {
            //when
            String result = target.getTitleByHistoryType(HistoryType.MONTH);

            //then
            assertThat(result).isEqualTo("2026년 03월");
        }
        
        @Test
        @DisplayName("WEEK면 연도+월+주를 반환한다.")
        void returnsYearMonthAndWeek_whenHistoryTypeIsWeek() {
            //when
            String result = target.getTitleByHistoryType(HistoryType.WEEK);

            //then
            assertThat(result).isEqualTo("2026년 03월 2주");
        }

    }

    @Nested
    @DisplayName("가계부 생성 비즈니스 규칙 검증할 때")
    class ValidateCreate {

        @BeforeEach
        void setUp() {
            clock.set(LocalDate.of(2026, 1, 1));
        }

        @Test
        @DisplayName("가계부 거래날짜가 작성이 가능하면 검증에 통과한다.")
        void validatesTransactionDate_whenDateIsValid() {
            //given
            Ledger ledger = LedgerTestFixture.builder()
                    .date(LocalDate.now(clock).minusMonths(3))
                    .build();

            //when
            assertDoesNotThrow(() -> target.validateCreatable(ledger));
        }

        @Test
        @DisplayName("가계부 거래날짜가 작성이 불가능하면 예외를 발생시킨다.")
        void throwsException_whenTransactionDateIsOutOfRange() {
            //given
            Ledger ledger = LedgerTestFixture.builder()
                    .date(LocalDate.now(clock).plusDays(1))
                    .build();

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
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("메모가 null이거나 비어있으면 검증에 통과한다.")
        void validatesMemo_whenMemoIsBlank(String memo) {
            //when
            Ledger ledger = LedgerTestFixture.builder()
                    .memo(memo)
                    .build();

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
            Ledger ledger = LedgerTestFixture.builder()
                    .memo(memo)
                    .build();

            //when
            assertDoesNotThrow(
                    () -> target.validateCreatable(ledger)
            );
        }

        static Stream<Arguments> validMemo() {
            return StringTestStream.validLengths("가", 0, 300);
        }

        @ParameterizedTest
        @MethodSource("invalidMemo")
        @DisplayName("메모 길이가 300초과하면 에외를 발생시킨다.")
        void throwsBusinessException_whenMemoLengthExceeds300(String memo) {
            //given
            Ledger ledger = LedgerTestFixture.builder()
                    .date(LocalDate.now(clock))
                    .memo(memo)
                    .build();

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
            return StringTestStream.invalidLengths("가", 0, 300);
        }

    }

}