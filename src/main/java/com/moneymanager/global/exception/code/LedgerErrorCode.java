package com.moneymanager.global.exception.code;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.code<br>
 * 파일이름       : LedgerErrorCode<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 30<br>
 * 설명              : 가계부 오류에 사용하는 에러코드
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
 * 		 	  <td>26. 6. 30</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public enum LedgerErrorCode implements ErrorCode {

	DATA_NOT_FOUND("LGR-100", "가계부 없음"),
	POLICY_VIOLATION("LGR-400", "가계부 정책 위반"),
	DATA_PERSISTENCE_FAILED("LGR-703", "데이터 조작 문제");

	private final String code;
	private final String reason;

	LedgerErrorCode(String errorCode, String reason) {
		this.code = errorCode;
		this.reason = reason;
	}

}