package com.moneymanager.global.validation;


import com.moneymanager.global.domain.DateRange;
import com.moneymanager.global.exception.exception.BusinessException;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.exception.log.DeveloperLogInfo;
import com.moneymanager.global.util.string.StringUtil;
import org.springframework.stereotype.Component;

import static com.moneymanager.global.exception.code.CommonErrorCode.INVALID_FORMAT;
import static com.moneymanager.global.exception.code.CommonErrorCode.REQUIRED_VALUE;
import static com.moneymanager.global.exception.code.LedgerErrorCode.POLICY_VIOLATION;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.validation<br>
 * 파일이름       : DateValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 30<br>
 * 설명              : 날짜 관련 입력값 검증 로직을 처리하는 클래스
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
 * 		 	  <td>26. 3. 30</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class DateValidator {

	//TODO(refactor):
	//DateValidator가 Ledger 도메인에 종속되어 있어 날짜 공통 검증(DateValidator)과 Ledger 도메인 검증(LedgerValidator)으로 분리 예정

	private final static String DATE_FORMAT = "^[12]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])$";

	public static void validateLedgerDate(String date) {
		if(StringUtil.isNullOrBlank(date)) {
			throw ValidationException.of(
					REQUIRED_VALUE,
					DeveloperLogInfo.of("가계부 검증", "날짜 없음", "date", date),
					"날짜를 선택해주세요."
			);
		}

		if(!StringUtil.matchesPattern(date, DATE_FORMAT)) {
			throw ValidationException.of(
					INVALID_FORMAT,
					DeveloperLogInfo.of("가계부 검증", "날짜 형식 불일치", "date", date)
							.addOption("format", "yyyyMMdd (예: 20260101)"),
					"날짜는 yyyyMMdd 형식으로 입력해주세요."
			);
		}
	}

	public static void validatePeriod(String startDate, String endDate) {
		if(StringUtil.isNullOrBlank(startDate)) {
			throw ValidationException.of(
					REQUIRED_VALUE,
					DeveloperLogInfo.of("거래내역 검증", "시작일 없음", "startDate", startDate),
					"시작일을 입력해주세요."
			);
		}

		if(!StringUtil.matchesPattern(startDate, DATE_FORMAT)) {
			throw ValidationException.of(
					INVALID_FORMAT,
					DeveloperLogInfo.of("거래내역 검증", "시작일 형식 불일치", "startDate", startDate)
							.addOption("format", "yyyyMMdd (예: 20260101)"),
					"시작일은 yyyyMMdd 형식으로 입력해주세요."
			);
		}

		if(StringUtil.isNullOrBlank(endDate)) {
			throw ValidationException.of(
					REQUIRED_VALUE,
					DeveloperLogInfo.of("거래내역 검증", "종료일 없음", "endDate", endDate),
					"날짜를 선택해주세요."
			);
		}

		if(!StringUtil.matchesPattern(endDate, DATE_FORMAT)) {
			throw ValidationException.of(
					INVALID_FORMAT,
					DeveloperLogInfo.of("거래내역 검증", "종료일 형식 불일치", "endDate", endDate)
							.addOption("format", "yyyyMMdd (예: 20260101)"),
					"종료일은 yyyyMMdd 형식으로 입력해주세요."
			);
		}

		//기간 확인
		if(startDate.compareTo(endDate) > 0) {
			throw BusinessException.of(
					POLICY_VIOLATION,
					DeveloperLogInfo.of("거래내역 검증", "시작일 > 종료일", DateRange.class, DeveloperLogInfo.valueOf("from", startDate, "to", endDate)),
					"시작일은 종료일보다 이전 날짜로 입력해주세요."
			);
		}
	}

}
