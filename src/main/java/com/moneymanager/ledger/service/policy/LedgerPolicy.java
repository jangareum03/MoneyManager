package com.moneymanager.ledger.service.policy;

import com.moneymanager.global.exception.code.LedgerErrorCode;
import com.moneymanager.global.exception.exception.BusinessException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.ledger.domain.dto.response.ImageSlot;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.DateUnit;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

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

    private final LedgerDatePolicy datePolicy;
    private final LedgerDateOptionPolicy dateOptionPolicy;
    private final ImageSlotPolicy imageSlotPolicy;

    public LocalDate minimumDate() {
        return datePolicy.minimum();
    }

    public LocalDate maximumDate() {
        return datePolicy.maximum();
    }

    public List<Integer> dateOptions(DateUnit unit, String date) {
        return dateOptionPolicy.getOptions(unit, date);
    }

    public List<ImageSlot> imageSlots(int count) {
        return imageSlotPolicy.createSlots(count);
    }

    public void validateCreatable(Ledger ledger) {
        if (!datePolicy.isValidDate(ledger.getDate())) {
            throw BusinessException.of(
                    LedgerErrorCode.POLICY_VIOLATION,
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
            throw BusinessException.of(
                    LedgerErrorCode.POLICY_VIOLATION,
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

}