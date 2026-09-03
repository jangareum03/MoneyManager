package com.moneymanager.ledger.service.policy;

import com.moneymanager.global.config.MutableClock;
import com.moneymanager.ledger.domain.dto.request.LedgerSearchRequest;
import com.moneymanager.ledger.domain.dto.response.history.HistoryDateFilter;
import com.moneymanager.ledger.domain.dto.vo.LedgerPeriod;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.HistoryMenu;
import com.moneymanager.ledger.domain.enums.HistoryType;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.fixture.entity.LedgerTestFixture;
import com.moneymanager.support.stream.StringTestStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.ErrorCode.POLICY_VIOLATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Named.named;
import static org.mockito.Mockito.*;

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

    private MutableClock clock;
    private LedgerDatePolicy datePolicy;
    private LedgerHistoryPeriodPolicy historyPeriodPolicy;


    @BeforeEach
    void setUp() {
        clock = new MutableClock();

        datePolicy = new LedgerDatePolicy(clock);
        historyPeriodPolicy = spy(new LedgerHistoryPeriodPolicy());

        LedgerDateOptionPolicy dateOptionPolicy = new LedgerDateOptionPolicy(datePolicy, clock);
        ImageSlotPolicy imageSlotPolicy = new ImageSlotPolicy();

        target = new LedgerPolicy(
                datePolicy,
                dateOptionPolicy,
                historyPeriodPolicy,
                imageSlotPolicy
        );
    }


    @Nested
    @DisplayName("내역기간을 생성할 때")
    class HistoryPeriod {

        @ParameterizedTest
        @ValueSource(ints = {2026, 2025, 2020})
        @DisplayName("YEAR이면 연초과 연말 기간을 생성한다.")
        void createsYearlyPeriod_whenTypeIsYear(int year) {
            //given
            HistoryType type = HistoryType.YEAR;
            LocalDate date = LocalDate.of(year, 6, 16);

            //when
            LedgerPeriod result = target.resolveHistoryPeriod(type, date);

            //then
            assertThat(result)
                    .extracting(
                            LedgerPeriod::getFromDate, LedgerPeriod::getToDate
                    )
                    .containsExactly(
                            LocalDate.of(year, 1, 1),
                            LocalDate.of(year, 12, 31)
                    );
        }

        @ParameterizedTest
        @MethodSource("validMonthDates")
        @DisplayName("MONTH면 월초와 연말 기간을 생성한다.")
        void createsMonthlyPeriod_whenTypeIsMonth(int day, int year, int month) {
            //given
            HistoryType type = HistoryType.MONTH;
            LocalDate date = LocalDate.of(year, month, 16);

            //when
            LedgerPeriod result = target.resolveHistoryPeriod(type, date);

            //then
            assertThat(result)
                    .extracting(
                            LedgerPeriod::getFromDate, LedgerPeriod::getToDate
                    )
                    .containsExactly(
                            LocalDate.of(year, month, 1),
                            LocalDate.of(year, month, day)
                    );
        }

        static Stream<Arguments> validMonthDates() {
            return Stream.of(
                    Arguments.of(
                            named("월말이 28일인 날짜 (year: 2026, month: 2)", 28),
                            2026, 2
                    ),
                    Arguments.of(
                            named("월말이 29일인 날짜 (year: 2024, month: 2)", 29),
                            2024, 2
                    ),
                    Arguments.of(
                            named("월말이 30일인 날짜 (year: 2026, month: 6)", 30),
                            2026, 6
                    ),
                    Arguments.of(
                            named("월말이 30일인 날짜 (year: 2026, month: 6)", 30),
                            2026, 6
                    ),
                    Arguments.of(
                            named("월말이 31일인 날짜 (year: 2026, month: 8)", 31),
                            2026, 8
                    )
            );
        }

        @ParameterizedTest
        @MethodSource("validHistoryDateFilter")
        @DisplayName("WEEK면 주의 시작일과 마지막일로 기간을 생성한다.")
        void createsWeeklyPeriod_whenTypeIsWeek(LocalDate date, LocalDate fromDate, LocalDate toDate) {
            //given
            HistoryType type = HistoryType.WEEK;

            //when
            LedgerPeriod result = target.resolveHistoryPeriod(type, date);

            //then
            assertThat(result)
                    .extracting(
                            LedgerPeriod::getFromDate, LedgerPeriod::getToDate
                    )
                    .containsExactly(
                            fromDate,
                            toDate
                    );
        }

        static Stream<Arguments> validHistoryDateFilter() {
            return Stream.of(
                    Arguments.of(
                            named("연도, 월, 주 모두 있는 경우 (year: 2026, month: 8, week: 1)", LocalDate.of(2026, 8, 1)),
                            LocalDate.of(2026, 8, 1),
                            LocalDate.of(2026, 8, 2)
                    ),
                    Arguments.of(
                            named("연도, 월, 주 모두 있는 경우 (year: 2026, month: 8, week: 2)", LocalDate.of(2026, 8, 8)),
                            LocalDate.of(2026, 8, 3),
                            LocalDate.of(2026, 8, 9)
                    ),
                    Arguments.of(
                            named("연도, 월, 주 모두 있는 경우 (year: 2026, month: 8, week: 3)", LocalDate.of(2026, 8, 15)),
                            LocalDate.of(2026, 8, 10),
                            LocalDate.of(2026, 8, 16)
                    ),
                    Arguments.of(
                            named("연도, 월, 주 모두 있는 경우 (year: 2026, month: 8, week: 4)", LocalDate.of(2026, 8, 22)),
                            LocalDate.of(2026, 8, 17),
                            LocalDate.of(2026, 8, 23)
                    ),
                    Arguments.of(
                            named("연도, 월, 주 모두 있는 경우 (year: 2026, month: 8, week: 5)", LocalDate.of(2026, 8, 29)),
                            LocalDate.of(2026, 8, 24),
                            LocalDate.of(2026, 8, 30)
                    ),
                    Arguments.of(
                            named("연도, 월, 주 모두 있는 경우 (year: 2026, month: 8, week: 6)", LocalDate.of(2026, 8, 31)),
                            LocalDate.of(2026, 8, 31),
                            LocalDate.of(2026, 8, 31)
                    )
            );
        }

    }


    @Nested
    @DisplayName("차트 기간을 생성할 때")
    class ChartPeriod {

        @ParameterizedTest
        @ValueSource(ints = {2026, 2025, 2020})
        @DisplayName("YEAR이면 연초과 연말 기간을 생성한다.")
        void createsYearlyPeriod_whenTypeIsYear(int year) {
            //given
            HistoryType type = HistoryType.YEAR;
            LocalDate date = LocalDate.of(year, 6, 16);

            //when
            LedgerPeriod result = target.resolveChartPeriod(type, date);

            //then
            assertThat(result)
                    .extracting(
                            LedgerPeriod::getFromDate, LedgerPeriod::getToDate
                    )
                    .containsExactly(
                            LocalDate.of(year, 1, 1),
                            LocalDate.of(year, 12, 31)
                    );
        }

        @ParameterizedTest
        @MethodSource("validMonthDates")
        @DisplayName("MONTH면 월초와 연말 기간을 생성한다.")
        void createsMonthlyPeriod_whenTypeIsMonth(int day, int year, int month) {
            //given
            HistoryType type = HistoryType.MONTH;
            LocalDate date = LocalDate.of(year, month, 16);

            //when
            LedgerPeriod result = target.resolveChartPeriod(type, date);

            //then
            assertThat(result)
                    .extracting(
                            LedgerPeriod::getFromDate, LedgerPeriod::getToDate
                    )
                    .containsExactly(
                            LocalDate.of(year, month, 1),
                            LocalDate.of(year, month, day)
                    );
        }

        @ParameterizedTest
        @MethodSource("validMonthDates")
        @DisplayName("WEEK면 월초와 연말 기간을 생성한다.")
        void createsMonthlyPeriod_whenTypeIsWeek(int day, int year, int month) {
            //given
            HistoryType type = HistoryType.WEEK;
            LocalDate date = LocalDate.of(year, month, 16);

            //when
            LedgerPeriod result = target.resolveChartPeriod(type, date);

            //then
            assertThat(result)
                    .extracting(
                            LedgerPeriod::getFromDate, LedgerPeriod::getToDate
                    )
                    .containsExactly(
                            LocalDate.of(year, month, 1),
                            LocalDate.of(year, month, day)
                    );
        }

        static Stream<Arguments> validMonthDates() {
            return Stream.of(
                    Arguments.of(
                            named("월말이 28일인 날짜 (year: 2026, month: 2)", 28),
                            2026, 2
                    ),
                    Arguments.of(
                            named("월말이 29일인 날짜 (year: 2024, month: 2)", 29),
                            2024, 2
                    ),
                    Arguments.of(
                            named("월말이 30일인 날짜 (year: 2026, month: 6)", 30),
                            2026, 6
                    ),
                    Arguments.of(
                            named("월말이 30일인 날짜 (year: 2026, month: 6)", 30),
                            2026, 6
                    ),
                    Arguments.of(
                            named("월말이 31일인 날짜 (year: 2026, month: 8)", 31),
                            2026, 8
                    )
            );
        }

    }


    @Nested
    @DisplayName("내역 유형별로 제목을 생성할 때")
    class TitleByHistoryType {

        @Test
        @DisplayName("YEAR이면 연도만 반환한다.")
        void returnsYearOnly_whenHistoryTypeIsYear() {
            //given
            LocalDate date = LocalDate.of(2025, 8, 1);

            //when
            String result = target.getTitleByHistoryType(HistoryType.YEAR, date);

            //then
            assertThat(result).isEqualTo("2025년");
        }

        @Test
        @DisplayName("MONTH면 연도+월을 반환한다.")
        void returnsYearAndMonth_whenHistoryTypeIsMonth() {
            //given
            LocalDate date = LocalDate.of(2025, 8, 1);

            //when
            String result = target.getTitleByHistoryType(HistoryType.MONTH, date);

            //then
            assertThat(result).isEqualTo("2025년 08월");
        }

        @ParameterizedTest
        @MethodSource("validWeekDates")
        @DisplayName("WEEK면 연도+월+주를 반환한다.")
        void returnsYearMonthAndWeek_whenHistoryTypeIsWeek(LocalDate date, String expected) {
            //when
            String result = target.getTitleByHistoryType(HistoryType.WEEK, date);

            //then
            assertThat(result).isEqualTo(expected);
        }

        static Stream<Arguments> validWeekDates() {
            return Stream.of(
                    Arguments.of(
                            named("1주차 날짜인 경우", LocalDate.of(2026, 3, 1)),
                            "2026년 03월 1주"
                    ),
                    Arguments.of(
                            named("2주차 날짜인 경우", LocalDate.of(2026, 3, 2)),
                            "2026년 03월 2주"
                    ),
                    Arguments.of(
                            named("3주차 날짜인 경우", LocalDate.of(2026, 3, 15)),
                            "2026년 03월 3주"
                    ),
                    Arguments.of(
                            named("4주차 날짜인 경우", LocalDate.of(2026, 3, 19)),
                            "2026년 03월 4주"
                    ),
                    Arguments.of(
                            named("5주차 날짜인 경우", LocalDate.of(2026, 3, 24)),
                            "2026년 03월 5주"
                    ),
                    Arguments.of(
                            named("6주차 날짜인 경우", LocalDate.of(2026, 3, 31)),
                            "2026년 03월 6주"
                    )
            );
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


    @Nested
    @DisplayName("내역 조회 날짜 규칙 검증할 때")
    class ValidateHistoryDate {

        @Nested
        @DisplayName("성공")
        class Success {

            HistoryDateFilter dateFilter = new HistoryDateFilter(2026, 1, 1);

            @ParameterizedTest
            @EnumSource(HistoryType.class)
            @DisplayName("type 상관없이 연도검증 메서드를 호출한다.")
            void validatesYear_whenCalledRegardlessOfType(HistoryType type) {
                //when
                target.validateHistoryDate(type, dateFilter);

                //then
                verify(historyPeriodPolicy).validateYear(dateFilter);
            }

            @ParameterizedTest
            @EnumSource(
                    value = HistoryType.class,
                    names = {"MONTH", "WEEK"}
            )
            @DisplayName("MONTH와 WEEK면 월 검증 메서드를 호출한다.")
            void validatesMonth_whenTypeIsMonthOrWeek(HistoryType type) {
                //when
                target.validateHistoryDate(type, dateFilter);

                //then
                verify(historyPeriodPolicy).validateYear(dateFilter);
                verify(historyPeriodPolicy).validateMonth(dateFilter);
            }

            @Test
            @DisplayName("WEEK면 주검증 메서드를 호출한다.")
            void validatesWeek_whenTypeIsWeek() {
                //given
                HistoryType type = HistoryType.WEEK;

                //when
                target.validateHistoryDate(type, dateFilter);

                //then
                verify(historyPeriodPolicy).validateYear(dateFilter);
                verify(historyPeriodPolicy).validateMonth(dateFilter);
                verify(historyPeriodPolicy).validateWeek(dateFilter);
            }

            @Test
            @DisplayName("YEAR 타입인데 연도가 있으면 검증에 통과한다.")
            void validatesYear_whenYearIsGiven() {
                //given
                HistoryDateFilter dateFilter = new HistoryDateFilter(2026, null, null);

                //when
                assertDoesNotThrow(() -> target.validateHistoryDate(HistoryType.YEAR, dateFilter));
            }

            @Test
            @DisplayName("MONTH 타입인데 연도와 월이 있으면 검증에 통과한다.")
            void validatesMonth_whenYearAndMonthIsGiven() {
                //given
                HistoryDateFilter dateFilter = new HistoryDateFilter(2026, 8, null);

                //when
                assertDoesNotThrow(() -> target.validateHistoryDate(HistoryType.MONTH, dateFilter));
            }

            @Test
            @DisplayName("WEEK 타입인데 연도, 월, 주가 있으면 검증에 통과한다.")
            void validatesWeek_whenYearAndMonthAndWeekIsGiven() {
                //given
                HistoryDateFilter dateFilter = new HistoryDateFilter(2026, 8, 2);

                //when
                assertDoesNotThrow(() -> target.validateHistoryDate(HistoryType.WEEK, dateFilter));
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("YEAR 타입인데 연도가 null이면 예외를 발생시킨다.")
            void throwsException_whenYearIsNull() {
            	//given
                HistoryDateFilter dateFilter = new HistoryDateFilter(null, 1, null);
            	
            	//when
                Throwable throwable = catchThrowable(() -> target.validateHistoryDate(HistoryType.YEAR, dateFilter));

            	//then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(POLICY_VIOLATION)
                        .hasWork("연도 검증")
                        .hasTarget(HistoryDateFilter.class)
                        .hasValue("year", null)
                        .hasCauseMessage("내역 조회에 필요한 연도 누락");
            }

            @Test
            @DisplayName("MONTH 타입인데 월이 null이면 예외를 발생시킨다.")
            void throwsException_whenMonthIsNull() {
                //given
                HistoryDateFilter dateFilter = new HistoryDateFilter(2026, null, 1);

                //when
                Throwable throwable = catchThrowable(() -> target.validateHistoryDate(HistoryType.MONTH, dateFilter));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(POLICY_VIOLATION)
                        .hasWork("월 검증")
                        .hasTarget(HistoryDateFilter.class)
                        .hasValue("month", null)
                        .hasCauseMessage("내역 조회에 필요한 월 누락");
            }

            @Test
            @DisplayName("WEEK 타입인데 주가 null이면 예외를 발생시킨다.")
            void throwsException_whenWeekIsNull() {
                //given
                HistoryDateFilter dateFilter = new HistoryDateFilter(2026, 1, null);

                //when
                Throwable throwable = catchThrowable(() -> target.validateHistoryDate(HistoryType.WEEK, dateFilter));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(POLICY_VIOLATION)
                        .hasWork("주 검증")
                        .hasTarget(HistoryDateFilter.class)
                        .hasValue("week", null)
                        .hasCauseMessage("내역 조회에 필요한 주 누락");
            }

        }

    }


    @Nested
    @DisplayName("가계부 내역 검색 규칙 검증할 때")
    class ValidateSearch {

        @Test
        @DisplayName("ALL이면 검증을 진행하지 않는다.")
        void doesNothing_whenTypeIsAll() {
            //given
            LedgerSearchRequest request = LedgerSearchRequest.builder()
                    .type("month")
                    .menu("all")
                    .build();

            //when
            assertDoesNotThrow(() -> target.validateSearchCondition(HistoryMenu.ALL, request));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("CATEGORY이고 카테고리 목록이 null이거나 비어있으면 예외를 발생시킨다.")
        void throwsException_whenCategoryTypeAndCategoryListIsNull(List<String> categories) {
            //given
            LedgerSearchRequest request = LedgerSearchRequest.builder()
                    .type("month")
                    .menu("type")
                    .categories(categories)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validateSearchCondition(HistoryMenu.CATEGORY, request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(POLICY_VIOLATION)
                    .hasWork("가계부 내역 검색 검증")
                    .hasCauseMessage("선택한 카테고리 누락")
                    .hasTarget(LedgerSearchRequest.class)
                    .hasValue("categories", categories);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("SUB_CATEGORY이고 카테고리 목록이 null이거나 비어있으면 예외를 발생시킨다.")
        void throwsException_whenSubCategoryTypeAndCategoryListIsNull(List<String> categories) {
            //given
            LedgerSearchRequest request = LedgerSearchRequest.builder()
                    .type("month")
                    .menu("category")
                    .categories(categories)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validateSearchCondition(HistoryMenu.SUB_CATEGORY, request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(POLICY_VIOLATION)
                    .hasWork("가계부 내역 검색 검증")
                    .hasCauseMessage("선택한 카테고리 누락")
                    .hasTarget(LedgerSearchRequest.class)
                    .hasValue("categories", categories);
        }

        @ParameterizedTest
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("MEMO이고 메모 내용이 null이거나 비어있으면 예외를 발생시킨다.")
        void throwsException_whenMemoTypeAndMemoIsNullOrBlank(String memo) {
            //given
            LedgerSearchRequest request = LedgerSearchRequest.builder()
                    .type("month")
                    .menu("memo")
                    .memo(memo)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validateSearchCondition(HistoryMenu.MEMO, request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(POLICY_VIOLATION)
                    .hasWork("가계부 내역 검색 검증")
                    .hasCauseMessage("메모 누락")
                    .hasTarget(LedgerSearchRequest.class)
                    .hasValue("memo", memo);
        }

        @ParameterizedTest
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("DATE이고 시작일이 null이거나 비어있으면 예외를 발생시킨다.")
        void throwsException_whenDateTypeAndStartDateIsNullOrBlank(String date) {
            //given
            LedgerSearchRequest request = LedgerSearchRequest.builder()
                    .type("month")
                    .menu("period")
                    .fromDate(date)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validateSearchCondition(HistoryMenu.PERIOD, request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(POLICY_VIOLATION)
                    .hasWork("가계부 내역 검색 검증")
                    .hasCauseMessage("시작일 누락")
                    .hasTarget(LedgerSearchRequest.class)
                    .hasValue("fromDate", date);
        }

        @ParameterizedTest
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("DATE이고 종료일이 null이거나 비어있으면 예외를 발생시킨다.")
        void throwsException_whenDateTypeAndEndDateIsNullOrBlank(String date) {
            //given
            LedgerSearchRequest request = LedgerSearchRequest.builder()
                    .type("month")
                    .menu("period")
                    .fromDate("20260101")
                    .toDate(date)
                    .build();

            //when
            Throwable throwable = catchThrowable(() -> target.validateSearchCondition(HistoryMenu.PERIOD, request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(POLICY_VIOLATION)
                    .hasWork("가계부 내역 검색 검증")
                    .hasCauseMessage("종료일 누락")
                    .hasTarget(LedgerSearchRequest.class)
                    .hasValue("toDate", date);
        }

    }

}