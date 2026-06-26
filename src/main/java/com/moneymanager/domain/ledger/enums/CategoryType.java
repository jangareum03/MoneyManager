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

	INCOME("01","수입"),
	OUTLAY("02", "지출");

	private final String prefix;			//카테고리 코드 (앞 2자리)
	private final String label;				//화면 표시용

	CategoryType(String prefix, String label) {
		this.prefix= prefix;
		this.label = label;
	}

	public static CategoryType from(String type) {
		validateType(type);

		return Arrays.stream(values())
				.filter(t -> t.name().equalsIgnoreCase(type))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"reason=허용값 아님   |   object=CategoryType   |   allowedValues=" + getAllowedValues() + "   |   value=" + type
				));
	}

	private static void validateType(String type) {
		if(isNullOrBlank(type)) {
			throw new IllegalArgumentException(
					"reason=필수값누락   |   object=CategoryType   |   value=" + type
			);
		}
	}

	private static String getAllowedValues() {
		return Arrays.stream(values())
				.map(CategoryType::name)
				.collect(Collectors.joining(", "));
	}

	public static CategoryType fromCode(String code) {
		validateCategoryCode(code);

		return Arrays.stream(values())
				.filter(t -> code.startsWith(t.prefix))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"reason=허용값 아님   |   object=CategoryType   |   allowedValues=" + getAllowedPrefixes() + "   |   value=" + code
				));
	}

	private static void validateCategoryCode(String code) {
		if(isNullOrBlank(code)) {
			throw new IllegalArgumentException(
					"reason=필수값누락   |   object=CategoryType   |   value=" + code
			);
		}
	}

	private static String getAllowedPrefixes() {
		return Arrays.stream(values())
				.map(CategoryType::getPrefix)
				.collect(Collectors.joining(", "));
	}

}
