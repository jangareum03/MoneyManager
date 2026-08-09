package com.moneymanager.global.validation;


import com.moneymanager.global.util.string.StringUtil;
import org.springframework.stereotype.Component;

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

		}

		if(!StringUtil.matchesPattern(date, DATE_FORMAT)) {

		}
	}

	public static void validatePeriod(String startDate, String endDate) {
		if(StringUtil.isNullOrBlank(startDate)) {

		}

		if(!StringUtil.matchesPattern(startDate, DATE_FORMAT)) {

		}

		if(StringUtil.isNullOrBlank(endDate)) {

		}

		if(!StringUtil.matchesPattern(endDate, DATE_FORMAT)) {

		}

		//기간 확인
		if(startDate.compareTo(endDate) > 0) {

		}
	}

}
