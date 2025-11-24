package com.moneymanager.domain.budgetBook.vo;

import com.moneymanager.domain.global.enums.RegexPattern;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import static com.moneymanager.domain.global.enums.RegexPattern.*;
import static com.moneymanager.exception.ErrorUtil.createClientException;
import static com.moneymanager.utils.ValidationUtils.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : Place<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 31.<br>
 * 설명              : 주소의 장소명, 도로명주소, 지번주소, 상세주소의 값을 나타내는 클래스
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
 * 		 	  <td>25. 8. 31</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 11. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>
 * 		 	      [클래스 이름] AddressVO → Place<br>
 * 		 	      [메서드 삭제] hasJiBunAddress, hasRoadAddress → 사용빈도 없음<br>
 * 		 	      [메서드 추가] isNullOrBlank → 값이 null 이거나 빈 문자열인지 확인
 * 		 	  </td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
@Getter
public class Place {
	String placeName;			//장소명
	String roadAddress;		//도로명 주소
	String jiBunAddress;		//지번주소
	String detailAddress;		//상세주소


	@Builder
	public Place(String placeName, String roadAddress, String jiBunAddress, String detailAddress) {
		validatePlaceName(placeName);
		validateAddress(roadAddress, jiBunAddress);

		this.placeName = placeName;
		this.roadAddress = roadAddress;
		this.jiBunAddress = jiBunAddress;
		this.detailAddress = detailAddress;
	}


	/**
	 * 주어진 장소명 문자열이 유효한지 검증합니다.
	 * <ul>
	 *     <li>값이 비어있으면 검증 불가 → {@link ErrorCode#BUDGET_PLACE_MISSING}</li>
	 *     <li>한글·숫자·영문자가 아니면 형식 오류 → {@link ErrorCode#BUDGET_PLACE_FORMAT}</li>
	 * </ul>
	 *
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	validatePlaceName("CGV 강남점");		//형식이 일치하여 통과
	 *     	validatePlaceName("올리브영-A");		//특수문자(-)가 입력되어 예외 발생
	 *     }</pre>
	 * </p>
	 *
	 * @param placeName			검증할 장소 문자열
	 * @throws ClientException	장소명 값이 없거나, 형식이 잘못된 경우 발생
	 */
	private void validatePlaceName(String placeName) {
		if( isNullOrBlank(placeName) )	throw createClientException(ErrorCode.BUDGET_PLACE_MISSING, "장소명은 필수입니다.");

		if(isMatchedPattern(placeName, ADDRESS_PLACE_NAME.getPattern())) {
			throw createClientException(ErrorCode.BUDGET_PLACE_FORMAT, "장소명은 한글, 숫자, 영문자만 입력 가능합니다.", placeName);
		}
	}



	/**
	 * 도로명주소와 지번주소 문자열이 유효한지 검증합니다.
	 * <ul>
	 *     <li>도로명·지번 주소가 모두 비어있으면 검증 불가 → {@link ErrorCode#BUDGET_PLACE_MISSING}</li>
	 *     <li>각 주소는 각각의 형식 규칙에 따라 상세 검증 진행</li>
	 * </ul>
	 *
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	validateAddress("서울 강남구 테헤란로 123", null);		//도로명주소만 입력되어 통과
	 *     	validateAddress(null, "서울 강남구 123-45");				//지번주소만 입력되어 통과
	 *
	 *     	validateAddress(null, null);										//둘 다 없어 예외 발생
	 *     }</pre>
	 * </p>
	 *
	 * @param roadAddress			검증할 도로명 주소 문자열
	 * @param jiBunAddress		검증할 지번 주소 문자열
	 * @throws ClientException	값이 모두 없거나, 각 주소 형식이 잘못된 경우 발생
	 */
	private void validateAddress(String roadAddress, String jiBunAddress) {
		if( isNullOrBlank(roadAddress) && isNullOrBlank(jiBunAddress) ) {
			throw createClientException(ErrorCode.BUDGET_PLACE_MISSING, "주소는 필수입니다.");
		}

		if( !isNullOrBlank(roadAddress) )	validateRoadAddress(roadAddress);
		if( !isNullOrBlank(jiBunAddress) ) validateJiBunAddress(jiBunAddress);
	}


	/**
	 * 주어진 도로명 주소 문자열이 유효한지 검증합니다.
	 * <ul>
	 *     <li>값이 비어있으면 검증 불가 → {@link ErrorCode#BUDGET_PLACE_MISSING}</li>
	 *     <li>한글·숫자·영문자·특수문자(·) 외의 문자가 포함되면 형식 오류 → {@link ErrorCode#BUDGET_PLACE_FORMAT}</li>
	 * </ul>
	 *
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	validateRoadAddress("서울 강남구 테헤란로 123");		//도로명주소 형식이 일치하여 통과
	 *     	validateRoadAddress("강남구... 테헤란로");					//도로명주소 형식이 불일치하여 예외 발생
	 *     }</pre>
	 * </p>
	 *
	 * @param roadAddress			검증할 도로명 주소 문자열
	 * @throws ClientException	값이 없거나 형식이 잘못된 경우 발생
	 */
	private void validateRoadAddress(String roadAddress) {
		if( isNullOrBlank(roadAddress) ) throw createClientException(ErrorCode.BUDGET_PLACE_MISSING, "도로명주소를 입력해주세요.");

		if(isMatchedPattern(roadAddress, RegexPattern.ADDRESS_ROAD_NAME.getPattern())) {
			throw createClientException(ErrorCode.BUDGET_PLACE_FORMAT, "도로명주소는 한글, 숫자, 영문자, 특수문자(·)만 입력 가능합니다.", roadAddress);
		}
	}


	/**
	 * 주어진 지번주소 문자열이 유효한지 검증합니다.
	 * <ul>
	 *     <li>값이 비어있으면 검증 불가 → {@link ErrorCode#BUDGET_PLACE_MISSING}</li>
	 *     <li>한글·숫자·영문자·특수문자(-) 외의 문자가 포함되면 형식 오류 → {@link ErrorCode#BUDGET_PLACE_FORMAT}</li>
	 * </ul>
	 *
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	validateJiBunAddress("서울 강남구 123-45");		//지번주소 형식이 일치하여 통과
	 *     	validateJiBunAddress("강남구@111");					//지번주소 형식이 불일치하여 예외 발생
	 *     }</pre>
	 * </p>
	 *
	 * @param 	jiBunAddress		검증할 지번 주소 문자열
	 * @throws ClientException	값이 없거나 형식이 잘못된 경우 발생
	 */
	private void validateJiBunAddress(String jiBunAddress) {
		if( isNullOrBlank(jiBunAddress) ) throw createClientException(ErrorCode.BUDGET_PLACE_MISSING, "지번주소를 입력해주세요.");

		if(isMatchedPattern(jiBunAddress, ADDRESS_JIBUN_NAME.getPattern())) {
			throw createClientException(ErrorCode.BUDGET_PLACE_FORMAT, "지번주소는 한글, 숫자, 영문자, 특수문자(-)만 입력 가능합니다.", roadAddress);
		}
	}

}
