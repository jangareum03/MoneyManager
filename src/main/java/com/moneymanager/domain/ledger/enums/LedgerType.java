package com.moneymanager.domain.ledger.enums;


import lombok.Getter;


/**
 * <p>
 *  * 패키지이름    : com.moneymanager.domain.ledger.enums<br>
 *  * 파일이름       : LedgerType<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 7. 15<br>
 *  * 설명              : 가계부 유형을 정의한 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>22. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Getter
public enum LedgerType {

	INCOME("수입", "income", "010000"),
	OUTLAY("지출", "outlay", "020000");

	private final String label;						//화면 표시용
	private final String urlCode;					//API 용
	private final String dbCode;					//DB에서 구분할 값

	LedgerType(String label, String urlCode, String dbCode) {
		this.label = label;
		this.urlCode = urlCode;
		this.dbCode = dbCode;
	}


	/**
	 * API용 URL에서 전송된 값이 {@link LedgerType} 객체의 {@code urlCode}이 동일한지 확인합니다.
	 * <p>
	 *     동일한 값이 있으면 해당 {@link LedgerType} 객체를 반환합니다.
	 *     만약 동일한 값이 없으면 {@link IllegalArgumentException} 예외가 발생합니다.
	 * </p>
	 *
	 * @param urlCode		가계부 유형을 확인할 URL 값
	 * @return	가계부 유형 정보를 담은 {@link LedgerType} 객체
	 * @throws IllegalArgumentException 없는 가계부 유형인 경우
	 */
	public static LedgerType fromUrl(String urlCode) {
		for( LedgerType type : values() ) {
			if( urlCode.equalsIgnoreCase(type.getUrlCode()) ) {
				return type;
			}
		}

		throw new IllegalArgumentException(
				String.format("지원하지 않은 가계부 유형 (urlCode=%s)",urlCode)
		);
	}


	/**
	 *데이터베이스에 저장된 카테고리 코드({@code code})와 비교하여 동일한 값이 있으면 해당 {@link LedgerType} 객체를 반환합니다.
	 * <p>
	 *     카테고리 코드({@code code})의 앞 2자리와 DB의 카테고리 코드의 앞 2자리를 비교합니다. 만약 동일한 값이 없으면 예외가 발생합니다.
	 * </p>
	 *
	 * @param code	카테고리 코드
	 * @return 가계부 유형 정보를 담은 {@link LedgerType} 객체
	 */
	public static LedgerType fromCode(String code) {
		for( LedgerType type : values() ) {
			String codePrefix = code.substring(0, 2);
			String dbPrefix = type.dbCode.substring(0, 2);

			if( codePrefix.equals(dbPrefix) ) {
				return type;
			}
		}

		return null;
	}

}
