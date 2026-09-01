package com.moneymanager.ledger.service.policy;

import com.moneymanager.ledger.domain.dto.vo.LedgerPeriod;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.policy<br>
 * 파일이름       : LedgerHistoryPeriodPolicy<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 28<br>
 * 설명              : 가계부 내역 기간 정책을 정의하는 클래스
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
 * 		 	  <td>26. 8. 28.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class LedgerHistoryPeriodPolicy {

    private static final DayOfWeek START_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final DayOfWeek END_DAY_OF_WEEK = DayOfWeek.SUNDAY;

    private final Clock clock;

    public LedgerHistoryPeriodPolicy(Clock clock) {
        this.clock = clock;
    }

    LedgerPeriod resolveYear() {
        LocalDate today = LocalDate.now(clock);

        return LedgerPeriod.of(
                today.with(TemporalAdjusters.firstDayOfYear()),
                today.with(TemporalAdjusters.lastDayOfYear())
        );
    }

    LedgerPeriod resolveYear(LocalDate date) {
        return LedgerPeriod.of(
                date.with(TemporalAdjusters.firstDayOfYear()),
                date.with(TemporalAdjusters.lastDayOfYear())
        );
    }

    LedgerPeriod resolveMonth() {
        LocalDate today = LocalDate.now(clock);

        return LedgerPeriod.of(
                today.with(TemporalAdjusters.firstDayOfMonth()),
                today.with(TemporalAdjusters.lastDayOfMonth())
        );
    }

    LedgerPeriod resolveWeek() {
        LocalDate today = LocalDate.now(clock);

        return LedgerPeriod.of(
                getFromDate(today),
                getToDate(today)
        );
    }

    int getWeekOfMonth(LocalDate date) {
        LocalDate firstDay = date.withDayOfMonth(1);

        int value = firstDay.getDayOfWeek().getValue() -  START_DAY_OF_WEEK.getValue() - 1;

        return (date.getDayOfMonth() + value) / 7 + 1;
    }


    //===== resolveWeek 보조 메서드 =====
    private LocalDate getFromDate(LocalDate date) {
        LocalDate fromDate = date.with(TemporalAdjusters.previousOrSame(START_DAY_OF_WEEK));

        LocalDate preMonth = date.with(TemporalAdjusters.firstDayOfMonth()).minusDays(1);
        if(fromDate.isBefore(preMonth)) {
            fromDate = date.withDayOfMonth(1);
        }

        return fromDate;
    }

    private LocalDate getToDate(LocalDate date) {
        LocalDate toDate = date.with(TemporalAdjusters.nextOrSame(END_DAY_OF_WEEK));

        if(toDate.isAfter(date.with(TemporalAdjusters.firstDayOfNextMonth()))) {
            toDate = date.with(TemporalAdjusters.lastDayOfMonth());
        }

        return toDate;
    }

}