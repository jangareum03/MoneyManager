package com.moneymanager.global.exception.code;

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
public enum LedgerErrorCode implements ErrorCode {

	INVALID_VALUE("LGR-003", "입력한 값이 올바르지 않습니다."),
	OUT_OF_RANGE("LGR-004", "입력한 값의 범위 또는 길이가 허용 범위를 벗어났습니다."),
	NOT_FOUND_DATA("LGR-100", "존재하지 않은 가계부입니다."),
	DATA_INTEGRITY_ERROR("LGR-105", "가계부 데이터의 무결성이 손상되어 있습니다."),

	POLICY_VIOLATION("LGR-400", "서비스 정책에 따라 요청을 처리할 수 없습니다.");

	private final String code;
	private final String defaultMessage;

	LedgerErrorCode(String errorCode, String defaultMessage) {
		this.code = errorCode;
		this.defaultMessage = defaultMessage;
	}

	@Override
	public String getCode() {
		return code;
	}

}
