package com.moneymanager.ledger.service.policy;

import com.moneymanager.global.config.MutableClock;
import com.moneymanager.ledger.domain.dto.vo.LedgerPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.policy<br>
 * 파일이름       : LedgerHistoryPeriodPolicyTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 28<br>
 * 설명              : LedgerHistoryPeriodPolicy 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 8. 28</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class LedgerHistoryPeriodPolicyTest {

    LedgerHistoryPeriodPolicy target;

    MutableClock clock;

    @BeforeEach
    void setUp() {
        clock = new MutableClock();

        target = new LedgerHistoryPeriodPolicy(clock);
    }

    @Nested
    @DisplayName("연도 기간을 결정할 때")
    class ResolveYear {

        @Test
        @DisplayName("현재 날짜의 연초와 연말로 생성한다.")
        void createsStartAndEndOfYearPeriod_whenDateIsGiven() {
            //given
            clock.set(LocalDate.of(2026, 6, 10));

            //when
            LedgerPeriod result = target.resolveYear();

            //then
            assertThat(result.getFromDate()).isEqualTo(LocalDate.of(2026, 1, 1));
            assertThat(result.getToDate()).isEqualTo(LocalDate.of(2026, 12, 31));
        }

    }

    @Nested
    @DisplayName("월 기간을 결정할 때")
    class ResolveMonth {

        @ParameterizedTest
        @MethodSource("validMonthDates")
        @DisplayName("현재 날짜의 첫날과 마지막날로 생성한다.")
        void createsFirstAndLastDayOfMonthPeriod_whenDateIsGiven(LocalDate date, LocalDate fromDate, LocalDate toDate) {
            //given
            clock.set(date);

            //when
            LedgerPeriod result = target.resolveMonth();

            //then
            assertThat(result.getFromDate()).isEqualTo(fromDate);
            assertThat(result.getToDate()).isEqualTo(toDate);
        }

        static Stream<Arguments> validMonthDates() {
            return Stream.of(
                    Arguments.of(
                            named("마지막이 28일인 경우 (2026년 2월 15일)", LocalDate.of(2026, 2, 15)),
                            LocalDate.of(2026, 2, 1),
                            LocalDate.of(2026, 2, 28)
                    ),
                    Arguments.of(
                            named("마지막이 29일인 경우 (2024년 2월 15일)", LocalDate.of(2024, 2, 15)),
                            LocalDate.of(2024, 2, 1),
                            LocalDate.of(2024, 2, 29)
                    ),
                    Arguments.of(
                            named("마지막이 30일인 경우 (2026년 11월 20일)", LocalDate.of(2026, 11, 20)),
                            LocalDate.of(2026, 11, 1),
                            LocalDate.of(2026, 11, 30)
                    ),
                    Arguments.of(
                            named("마지막이 31일인 경우 (2026년 5월 10일)", LocalDate.of(2026, 5, 10)),
                            LocalDate.of(2026, 5, 1),
                            LocalDate.of(2026, 5, 31)
                    )
            );
        }

    }

    @Nested
    @DisplayName("주 기간을 결정할 때")
    class ResolveWeek {

        @ParameterizedTest
        @MethodSource("validWeekDates")
        @DisplayName("현재 날짜가 포함된 주의 월요일과 일요일로 생성한다.")
        void createsWeeklyRange_whenCurrentDateIsGiven(LocalDate date, LocalDate fromDate, LocalDate toDate) {
            //given
            clock.set(date);

            //when
            LedgerPeriod result = target.resolveWeek();

            //then
            assertThat(result.getFromDate()).isEqualTo(fromDate);
            assertThat(result.getToDate()).isEqualTo(toDate);
        }

        static Stream<Arguments> validWeekDates() {
            return Stream.of(
                    Arguments.of(
                            named("3월 첫째주 날짜인 경우 (2026년 3월 4일)", LocalDate.of(2026, 3, 4)),
                            LocalDate.of(2026, 3, 2),
                            LocalDate.of(2026, 3, 8)
                    ),
                    Arguments.of(
                            named("3월 둘째주 날짜인 경우 (2026년 3월 11일)", LocalDate.of(2026, 3, 11)),
                            LocalDate.of(2026, 3, 9),
                            LocalDate.of(2026, 3, 15)
                    ),
                    Arguments.of(
                            named("3월 셋째주 날짜인 경우 (2026년 3월 18일)", LocalDate.of(2026, 3, 18)),
                            LocalDate.of(2026, 3, 16),
                            LocalDate.of(2026, 3, 22)
                    ),
                    Arguments.of(
                            named("3월 넷째주 날짜인 경우 (2026년 3월 25일)", LocalDate.of(2026, 3, 25)),
                            LocalDate.of(2026, 3, 23),
                            LocalDate.of(2026, 3, 29)
                    ),
                    Arguments.of(
                            named("3월 다섯째주 날짜인 경우 (2026년 3월 30일)", LocalDate.of(2026, 3, 30)),
                            LocalDate.of(2026, 3, 30),
                            LocalDate.of(2026, 3, 31)
                    )
            );
        }

        @Test
        @DisplayName("현재 날짜가 1일이면 시작일을 1일로 지정하여 생성한다.")
        void createsWeeklyRangeWithFirstDay_whenCurrentDateIsFirstDayOfMonth() {
            //given
            clock.set(LocalDate.of(2026, 3, 1));

            //when
            LedgerPeriod result = target.resolveWeek();

            //then
            assertThat(result.getFromDate()).isEqualTo(LocalDate.of(2026, 3, 1));
            assertThat(result.getToDate()).isEqualTo(LocalDate.of(2026, 3, 1));
        }

        @Test
        @DisplayName("현재 날짜가 월의 마지막 날이면 종료일을 말일로 지정하여 생성한다.")
        void createsWeeklyRangeWithLastDay_whenCurrentDateIsLastDayOfMonth() {
            //given
            clock.set(LocalDate.of(2026, 8, 31));

            //when
            LedgerPeriod result = target.resolveWeek();

            //then
            assertThat(result.getFromDate()).isEqualTo(LocalDate.of(2026, 8, 31));
            assertThat(result.getToDate()).isEqualTo(LocalDate.of(2026, 8, 31));
        }

    }


    @Nested
    @DisplayName("월의 주차 조회할 때")
    class GetWeek {

        @ParameterizedTest
        @MethodSource("validWeekDates")
        @DisplayName("월요일 기준으로 주차를 계산한다.")
        void calculatesWeek_whenWeekStartsOnMonday(LocalDate date, int week) {
            //when
            int result = target.getWeekOfMonth(date);
        	
        	//then
        	assertThat(result).isEqualTo(week);
        }

        static Stream<Arguments> validWeekDates() {
            return Stream.of(
                    Arguments.of(
                            named("1주차인 경우 (일요일)", LocalDate.of(2026, 3,1)),
                            1
                    ),
                    Arguments.of(
                            named("1주차인 경우 (월요일)", LocalDate.of(2026, 6,1)),
                            1
                    ),
                    Arguments.of(
                            named("2주차인 경우 (수요일)", LocalDate.of(2026, 8,5)),
                            2
                    ),
                    Arguments.of(
                            named("3주차인 경우 (월요일)", LocalDate.of(2026, 8,10)),
                            3
                    ),
                    Arguments.of(
                            named("4주차인 경우 (목요일)", LocalDate.of(2026, 8,20)),
                            4
                    ),
                    Arguments.of(
                            named("5주차인 경우 (화요일)", LocalDate.of(2026, 8,25)),
                            5
                    ),
                    Arguments.of(
                            named("5주차인 경우 (일요일)", LocalDate.of(2026, 5,31)),
                            5
                    ),
                    Arguments.of(
                            named("6주차인 경우 (월요일)", LocalDate.of(2026, 8,31)),
                            6
                    ),
                    Arguments.of(
                            named("6주차인 경우 (화요일)", LocalDate.of(2026, 3,31)),
                            6
                    )
            );
        }
        
    }

}