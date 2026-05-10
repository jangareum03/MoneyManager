package com.moneymanager.domain.ledger.enums;


import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.moneymanager.utils.validation.ValidationUtils.isNullOrBlank;


/**
 * <p>
 *  * 패키지이름    : com.moneymanager.domain.ledger.enums<br>
 *  * 파일이름       : CategoryType<br>
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
public enum CategoryType {

	INCOME("수입", "01"),
	OUTLAY("지출", "02");

	private final String label;						//화면 표시용
	private final String prefixCode;			//DB 기준코드 (앞 2자리)

	CategoryType(String label, String prefixCode) {
		this.label = label;
		this.prefixCode = prefixCode;
	}


	/**
	 * API 요청/응답 값과 일치하는 {@link CategoryType} 객체를 반환합니다.
	 *
	 * @param value		가계부 유형 값
	 * @return	가계부 유형 정보를 담은 {@link CategoryType} 객체
	 * @throws IllegalArgumentException 필수값이 누락되었거나 허용되지 않은 값인 경우
	 */
	public static CategoryType from(String value) {
		if(isNullOrBlank(value)) {
			throw new IllegalArgumentException(
				"reason=필수값누락   |   enum=CategoryType   |   field=apiCode   |   value=" + value
			);
		}

		for(CategoryType type : values()) {
			if(type.name().equalsIgnoreCase(value)) return type;
		}

		throw new IllegalArgumentException(
				"reason=허용값 아님   |   enum=CategoryType   |   field=apiCode   |   allowedValues=" + getAllowedApiCodes() + "   |   value=" + value
		);
	}


	/**
	 *데이터베이스에 저장된 카테고리 코드({@code code})의 앞 2자리를 비교하여 일치하는 {@link CategoryType} 객체를 반환합니다.
	 *
	 * @param code	카테고리 코드
	 * @return 가계부 유형 정보를 담은 {@link CategoryType} 객체
	 * @throws IllegalArgumentException 필수값이 누락되었거나 허용되지 않은 코드인 경우
	 */
	public static CategoryType fromCode(String code) {
		if(isNullOrBlank(code)) {
			throw new IllegalArgumentException("reason=필수값누락   |   enum=CategoryType   |   field=categoryCode   |   value=" + code);
		}

		for( CategoryType type : values() ) {
			if( code.startsWith(type.prefixCode) ) {
				return type;
			}
		}

		throw new IllegalArgumentException("reason=허용값 아님   |   enum=CategoryType   |   field=prefixCode   |   allowedValues=" + getAllowedPrefixCodes() + "   |   value=" + code);
	}


	private static String getAllowedApiCodes() {
		return Arrays.stream(values())
				.map(CategoryType::name)
				.collect(Collectors.joining(", "));
	}

	private static String getAllowedPrefixCodes() {
		return Arrays.stream(values())
				.map(CategoryType::getPrefixCode)
				.collect(Collectors.joining(", "));
	}

}
