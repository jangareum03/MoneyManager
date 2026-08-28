package com.moneymanager.ledger.domain.dto.vo;

import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import lombok.Value;

import java.time.LocalDate;

import static com.moneymanager.global.exception.code.ErrorCode.OUT_OF_RANGE;
import static com.moneymanager.global.exception.code.ErrorCode.REQUIRED_VALUE;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.vo<br>
 * 파일이름       : LedgerPeriod<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 28<br>
 * 설명              : 가계부 기간을 나타내는 클래스
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
@Value
public class LedgerPeriod {

    LocalDate fromDate;
    LocalDate toDate;

    private LedgerPeriod(LocalDate fromDate, LocalDate toDate) {
        if(fromDate == null || toDate == null) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            "LedgerPeriod 생성",
                            LedgerPeriod.class,
                            "fromDate", fromDate,
                            "toDate", toDate
                    ).withCause("fromDate와 toDate 모두 null 불가")
            );
        }

        if(fromDate.isAfter(toDate)) {
            throw new ApplicationException(
                    OUT_OF_RANGE,
                    LogContent.of(
                            "LedgerPeriod 생성",
                            LedgerPeriod.class,
                            "fromDate", fromDate,
                            "toDate", toDate
                    ).withCause("fromDate > toDate")
            );
        }

        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public static LedgerPeriod of(LocalDate fromDate, LocalDate toDate) {
        return new LedgerPeriod(fromDate, toDate);
    }

}