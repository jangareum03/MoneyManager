package com.moneymanager.ledger.service.policy;

import com.moneymanager.global.exception.code.LedgerErrorCode;
import com.moneymanager.global.exception.exception.BusinessException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.date.DateRangeUtils;
import com.moneymanager.ledger.domain.enums.DateUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.policy<br>
 * 파일이름       : LedgerDateOptionPolicy<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 14<br>
 * 설명              : 가계부 날짜 옵션 정책을 정의하는 클래스
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
 * 		 	  <td>26. 8. 14</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
@RequiredArgsConstructor
public class LedgerDateOptionPolicy {

    private final LedgerDatePolicy ledgerDatePolicy;
    private final Clock clock;

    List<Integer> getOptions(DateUnit unit, String date) {
        return switch (unit) {
            case YEAR -> getMonths(date);
            case MONTH -> getDays(date);
        };
    }


    //===== getOptions 보조 메서드 =====
    private List<Integer> getMonths(String date) {
        Year year = Year.parse(date);

        if (!ledgerDatePolicy.isValidYear(year)) {
            throw BusinessException.of(
                    LedgerErrorCode.POLICY_VIOLATION,
                    LogContent.of(
                            "월 목록 조회",
                            "date",
                            date
                    ).withCause("허용되지 않은 연도")
            );
        }

        int endMonth = year.equals(Year.now(clock))
                ? LocalDate.now(clock).getMonthValue()
                : 12;

        return DateRangeUtils.getMonthsInRange(1, endMonth);
    }

    private List<Integer> getDays(String date) {
        YearMonth yearMonth = YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyyMM"));

        if (!ledgerDatePolicy.isValidYearMonth(yearMonth)) {
            throw BusinessException.of(
                    LedgerErrorCode.POLICY_VIOLATION,
                    LogContent.ofValues(
                            "일 목록 조회",
                            "year",
                            String.valueOf(yearMonth.getYear()),
                            "month",
                            String.valueOf(yearMonth.getMonthValue())
                    ).withCause("허용되지 않은 월")
            );
        }

        int endDay = yearMonth.equals(YearMonth.now(clock))
                ? LocalDate.now(clock).getDayOfMonth()
                : yearMonth.lengthOfMonth();

        return DateRangeUtils.getDaysInRange(1, endDay);
    }

}