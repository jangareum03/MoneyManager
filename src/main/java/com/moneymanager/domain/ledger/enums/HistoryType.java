package com.moneymanager.domain.ledger.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.moneymanager.utils.validation.ValidationUtils.isNullOrBlank;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.enums<br>
 * 파일이름       : HistoryType<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 2<br>
 * 설명              : 가계부 내역 유형을 정의한 클래스
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
@Getter
public enum HistoryType {
	WEEK("yyyy년 MM월 W주"),
	MONTH("yyyy년 MM월"),
	YEAR("yyyy년");

	private final String format;

	HistoryType(String format) {
		this.format = format;
	}


	/**
	 *	문자열을 {@link HistoryType}으로 변환합니다.
	 *<p>
	 *     입력값은 대소문자 구분없이 내부적으로 비교하여 처리합니다.
	 *</p>
	 * <p>
	 *     아래와 같은 경우에는 예외가 발생합니다.
	 *     <ul>
	 *         <li>문자열({@code type})이 null 또는 공백인 경우</li>
	 *         <li>정의되지 않은 enum 값인 경우</li>
	 *     </ul>
	 * </p>
	 *
	 * @param type	변환할 문자열
	 * @return	변환된 {@link HistoryType}
	 * @throws IllegalArgumentException	필수값이 없거나 허용되지 않은 값인 경우 발생
	 */
	public static HistoryType from(String type) {
		if(isNullOrBlank(type)) {
			throw new IllegalArgumentException("reason=필수값누락   |   enum=HistoryType   |   value=" + type);
		}

		try{
			return HistoryType.valueOf(type.toUpperCase());
		}catch (IllegalArgumentException e) {
			String allowedValues = Arrays.stream(values())
					.map(HistoryType::name)
					.collect(Collectors.joining(", "));

			throw new IllegalArgumentException("reason=허용값아님   |   enum=HistoryType   |   allowedValues=" + allowedValues + "   |   value=" + type);
		}
	}
}
