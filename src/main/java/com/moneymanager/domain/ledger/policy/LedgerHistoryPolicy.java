package com.moneymanager.domain.ledger.policy;

import com.moneymanager.domain.global.Policy;
import com.moneymanager.domain.global.vo.DateRange;
import com.moneymanager.domain.ledger.enums.HistoryType;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ErrorCode;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.moneymanager.utils.date.DateTimeUtils.isDateInRange;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.policy<br>
 * 파일이름       : LedgerHistoryPolicy<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 2<br>
 * 설명              : 가계부 거래내역 정책을 나타내는 클래스
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
 * 		 	  <td>26. 4. 2</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class LedgerHistoryPolicy {

	/**
	 *	<p>
	 *	   	조회기간(from, to)은 현재 날짜 기준 최대 {@code Policy.LEDGER_MAX_YEAR}년 이내여야 합니다.
	 *	   	두 날짜 모두 허용 범위를 벗어날 경우 예외가 발생합니다.
	 *	</p>
	 *
	 * @param dateRange	검증할 조회 기간을 담은 객체
	 * @throws BusinessException	조회 기간이 허용 범위를 초과한 경우 발생
	 */
	public void validate(DateRange dateRange) {
		LocalDate now = LocalDate.now();
		LocalDate fiveYearsAgo = now.minusYears(Policy.LEDGER_MAX_YEAR);

		if( !(isDateInRange(dateRange.getFrom(), fiveYearsAgo, now) && isDateInRange(dateRange.getTo(), fiveYearsAgo, now)) ) {
			throw BusinessException.of(
					ErrorCode.LEDGER_HISTORY_POLICY_VIOLATION,
					"가계부 내역은 최근 5년 이내만 가능합니다.",
					"가계부 거래내역 검증 실패   |   reason=조건불만족   |   object=DateRange   |   condition=기간 초과   |   value={from: " + dateRange.getFrom().toString() + ", to: " + dateRange.getTo().toString() + "}"
			);
		}
	}


	/**
	 *	기간 일수({@link DateRange#daysBetween()})를 기준으로 아래와 같이 분류합니다.
	 *<ul>
	 *     <li>7일 이하: {@link HistoryType#WEEK}</li>
	 *     <li>31일 이하 또는 365일 초과: {@link HistoryType#MONTH}</li>
	 *     <li>그 외: {@link HistoryType#YEAR}</li>
	 *</ul>
	 *
	 * @param dateRange	조회 기간을 담은 객체
	 * @return	기간 일수에 따른 {@link HistoryType}
	 */
	public HistoryType resolveRangeType(DateRange dateRange) {
		long days = dateRange.daysBetween();

		if(days <= 7) return HistoryType.WEEK;
		if(days <= 31 || days > 365) return HistoryType.MONTH;

		return HistoryType.YEAR;
	}
}
