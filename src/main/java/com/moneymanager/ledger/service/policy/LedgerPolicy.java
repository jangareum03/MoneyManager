package com.moneymanager.ledger.service.policy;

import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.ledger.domain.dto.request.LedgerSearchRequest;
import com.moneymanager.ledger.domain.dto.response.ImageSlot;
import com.moneymanager.ledger.domain.dto.vo.LedgerPeriod;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.DateUnit;
import com.moneymanager.ledger.domain.enums.HistoryMenu;
import com.moneymanager.ledger.domain.enums.HistoryType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.moneymanager.global.domain.enums.DatePatterns.KOREAN_YEAR;
import static com.moneymanager.global.domain.enums.DatePatterns.KOREAN_YEAR_MONTH;
import static com.moneymanager.global.exception.code.ErrorCode.POLICY_VIOLATION;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.policy<br>
 * 파일이름       : LedgerPolicy<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 19<br>
 * 설명              : 가계부 정책을 정의하는 클래스
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
@Component
@AllArgsConstructor
public class LedgerPolicy {

    private final Clock clock;

    private final LedgerDatePolicy datePolicy;
    private final LedgerDateOptionPolicy dateOptionPolicy;
    private final LedgerHistoryPeriodPolicy historyPeriodPolicy;
    private final ImageSlotPolicy imageSlotPolicy;

    public LocalDate minimumDate() {
        return datePolicy.minimum();
    }

    public LocalDate maximumDate() {
        return datePolicy.maximum();
    }

    public LedgerPeriod resolveHistoryPeriod(HistoryType type) {
        return switch (type) {
            case YEAR -> historyPeriodPolicy.resolveYear();
            case MONTH -> historyPeriodPolicy.resolveMonth();
            case WEEK -> historyPeriodPolicy.resolveWeek();
        };
    }

    public LedgerPeriod resolveChartPeriod(HistoryType type) {
        return switch (type) {
            case YEAR -> historyPeriodPolicy.resolveYear(maximumDate());
            case MONTH, WEEK -> historyPeriodPolicy.resolveMonth();
        };
    }

    public String getTitleByHistoryType(HistoryType type) {
        LocalDate date = LocalDate.now(clock);

        return switch (type) {
            case YEAR -> date.format(DateTimeFormatter.ofPattern(KOREAN_YEAR.getPattern()));
            case MONTH -> date.format(DateTimeFormatter.ofPattern(KOREAN_YEAR_MONTH.getPattern()));
            case WEEK -> {
                int weekOfMonth = historyPeriodPolicy.getWeekOfMonth(date);

                yield String.format("%d년 %02d월 %d주", date.getYear(), date.getMonthValue(), weekOfMonth);
            }
        };
    }

    public List<Integer> dateOptions(DateUnit unit, String date) {
        return dateOptionPolicy.getOptions(unit, date);
    }

    public List<ImageSlot> imageSlots(int count) {
        return imageSlotPolicy.buildCreatableImageSlots(count);
    }

    public List<ImageSlot> imageSlots(List<String> images) {
        return imageSlotPolicy.buildDisplayImageSlots(images);
    }

    public List<ImageSlot> imageSlots(int count, List<String> images) {
        return imageSlotPolicy.buildEditableImageSlots(count, images);
    }

    public void validateCreatable(Ledger ledger) {
        if (!datePolicy.isValidDate(ledger.getDate())) {
            throw new ApplicationException(
                    POLICY_VIOLATION,
                    LogContent.of(
                                    "가계부 비즈니스 규칙 검증",
                                    Ledger.class,
                                    "date",
                                    ledger.getDate()
                            ).withCause("거래날짜 범위 초과")
                            .withOption("min", minimumDate())
                            .withOption("max", maximumDate())
            );
        }

        String memo = ledger.getMemo();
        if (!StringUtil.isNullOrBlank(memo) && memo.length() > 300) {
            throw new ApplicationException(
                    POLICY_VIOLATION,
                    LogContent.of(
                                    "가계부 비즈니스 규칙 검증",
                                    Ledger.class,
                                    "memo",
                                    ledger.getMemo()
                            ).withCause("메모 길이 초과")
                            .withOption("min", 0)
                            .withOption("max", 300)
            );
        }
    }

    public void validateSearchCondition(HistoryMenu menu, LedgerSearchRequest request) {
        switch (menu) {
            case CATEGORY, SUB_CATEGORY -> validateCategory(request.getCategories());
            case MEMO -> validateMemo(request.getMemo());
            case PERIOD -> validatePeriod(request.getFromDate(), request.getToDate());
        }
    }

    private void validateCategory(List<String> categories) {
        if(categories == null || categories.isEmpty()) {
            throw new ApplicationException(
                    POLICY_VIOLATION,
                    LogContent.of(
                            "가계부 내역 검색 검증",
                            LedgerSearchRequest.class,
                            "categories",
                            categories
                    ).withCause("선택한 카테고리 누락")
            );
        }
    }

    private void validateMemo(String memo) {
        if(StringUtil.isNullOrBlank(memo)) {
            throw new ApplicationException(
                    POLICY_VIOLATION,
                    LogContent.of(
                            "가계부 내역 검색 검증",
                            LedgerSearchRequest.class,
                            "memo",
                            memo
                    ).withCause("메모 누락")
            );
        }
    }

    private void validatePeriod(String fromDate, String toDate) {
        if(StringUtil.isNullOrBlank(fromDate)) {
            throw new ApplicationException(
                    POLICY_VIOLATION,
                    LogContent.of(
                            "가계부 내역 검색 검증",
                            LedgerSearchRequest.class,
                            "fromDate",
                            fromDate
                    ).withCause("시작일 누락")
            );
        }

        if(StringUtil.isNullOrBlank(toDate)) {
            throw new ApplicationException(
                    POLICY_VIOLATION,
                    LogContent.of(
                            "가계부 내역 검색 검증",
                            LedgerSearchRequest.class,
                            "toDate",
                            toDate
                    ).withCause("종료일 누락")
            );
        }
    }

}