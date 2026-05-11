package com.moneymanager.service.validation;


import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ErrorCode;
import org.springframework.stereotype.Component;

import static com.moneymanager.exception.error.ErrorCode.*;
import static com.moneymanager.utils.validation.ValidationUtils.isNullOrBlank;
import static com.moneymanager.utils.validation.ValidationUtils.matchesPattern;

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

	private final static String DATE_FORMAT = "^[12]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])$";

	public static void validateLedgerDate(String date) {
		checkEmpty(date, LEDGER_INPUT_NULL, "날짜를 선택해주세요.","가계부 검증 실패   |   reason=필수값누락   |   field=date   |   value=" + date);
		checkFormat(date, LEDGER_INPUT_FORMAT, "날짜는 yyyyMMdd 형식으로 입력해주세요.","가계부 검증 실패   |   reason=형식오류   |   field=date   |   expectedFormat=yyyyMMdd (예: 20260101)   |   value=" + date);
	}

	public static void validatePeriod(String startDate, String endDate) {
		validateHistoryDate(startDate, "시작일");
		validateHistoryDate(endDate, "종료일");

		//기간 확인
		if(startDate.compareTo(endDate) > 0) {
			throw BusinessException.of(
					LEDGER_HISTORY_INPUT_CONFLICT,
					"가계부 거래내역 검증 실패   |   reason=논리충돌   |   rule=시작일<=종료일   |   value={startDate: " + startDate +", endDate: " + endDate +"}"
			).withUserMessage("시작일은 종료일보다 이전 날짜로 입력해주세요.");
		}
	}

	public static void validateHistoryDate(String date, String fieldName) {
		checkEmpty(date, LEDGER_HISTORY_INPUT_NULL,fieldName + "을 입력해주세요.","가계부 거래내역 검증 실패   |   reason=필수값누락   |   field=date   |   value=" + date);
		checkFormat(date,	LEDGER_HISTORY_INPUT_FORMAT,fieldName + "는 yyyyMMdd 형식으로 입력해주세요.","가계부 거래내역 검증 실패   |   reason=형식오류   |   field=date   |   value=" + date);
	}

	private static void checkEmpty(String date, ErrorCode errorCode, String userMsg, String logMsg) {
		if(isNullOrBlank(date)) {
			throw BusinessException.of(errorCode, logMsg).withUserMessage(userMsg);
		}
	}

	private static void checkFormat(String date, ErrorCode errorCode, String userMsg, String logMsg) {
		if(!matchesPattern(date, DATE_FORMAT)) {
			throw BusinessException.of(errorCode, logMsg).withUserMessage(userMsg);
		}
	}
}
