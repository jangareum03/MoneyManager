package com.moneymanager.domain.ledger.vo;

import com.moneymanager.domain.global.enums.RegexPattern;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.utils.ValidationUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : Address<br>
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
 * 		 	  <td>25. 8. 31.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
@Getter
public class Address {
	String placeName;			//장소명
	String roadAddress;		//도로명 주소
	String jiBunAddress;		//지번주소
	String detailAddress;		//상세주소


	@Builder
	public Address(String placeName, String roadAddress, String jiBunAddress, String detailAddress) {
		validatePlaceName();
		validateAddress();

		this.placeName = placeName;
		this.roadAddress = roadAddress;
		this.jiBunAddress = jiBunAddress;
		this.detailAddress = detailAddress;
	}


	private String validateOrNull(String value) {
		return (value == null || value.isBlank()) ? null : value;
	}


	/**
	 *	입력된 도로명 주소({@code roadAddress})가 유효한지 확인합니다.
	 *<p>
	 *     다음과 같은 경우에만 유효합니다.
	 *     <ul>
	 *         <li>도로명 주소가 {@code null}이 아닌 경우</li>
	 *         <li>도로명 주소가 공백으로만 이뤄지지 않은 경우</li>
	 *     </ul>
	 *</p>
	 *
	 * @param roadAddress		도로명 주소
	 * @return	도로명주소가 null이 아니고 공백이 아닌 경우 true, 아니면 false
	 */
	public boolean hasRoadAddress(String roadAddress) {
		return !(roadAddress == null || roadAddress.isBlank());
	}


	/**
	 * 입력된 지번주소({@code jiBunAddress})가 유효한지 확인합니다.
	 * <p>
	 *     다음과 같은 경우에만 유효합니다.
	 *     <ul>
	 *         <li>지번주소가 {@code null}이 아닌 경우</li>
	 *         <li>지번주소가 공백으로만 이뤄지지 않은 경우</li>
	 *     </ul>
	 * </p>
	 * @param jiBunAddress	지번주소
	 * @return	지번주소가 null이 아니고 공백이 아닌 경우 true, 아니면 false
	 */
	public boolean hasJiBunAddress(String jiBunAddress) {
		return !(jiBunAddress == null || jiBunAddress.isBlank());
	}


	/**
	 * 장소명({@code placeName})을 검증합니다.
	 * <p>
	 *     다음과 같은 경우에 {@link ClientException}예외가 발생합니다.
	 * <ul>
	 *     <li>장소명이 null 또는 "" 경우 → {@link ValidationUtil#createClientException(ErrorCode, String)} 메서드 호출</li>
	 *     <li>장소명이 정규식 패턴과 일치하지 않은 경우 → {@link ValidationUtil#createClientException(ErrorCode, String, Object)} 메서드 호출</li>
	 * </ul>
	 * </p>
	 * @throws ClientException	장소명이 null 이거나 정규식 패턴과 일치하지 않은 경우 발생
	 * @see RegexPattern
	 */
	private void validatePlaceName() {
		if( placeName == null || placeName.isBlank() ) throw ValidationUtil.createClientException(ErrorCode.COMMON_PLACENAME_MISSING, "장소명은 필수입니다.");

		if( !placeName.matches(RegexPattern.ADDRESS_PLACE_NAME.getPattern()) ) {
			throw ValidationUtil.createClientException(ErrorCode.COMMON_PLACENAME_FORMAT, "장소명은 한글, 숫자, 영문자만 입력 가능합니다.", placeName);
		}
	}


	/**
	 * 도로명주소({@code roadAddress})와 지번주소({@code jiBunAddress})를 검증합니다.<br>
	 * 도로명 주소와 지번 주소 중 최소 하나는 반드시 입력되어야 하며, 각각의 주소 형식이 올바른지도 확인합니다.
	 * <p>
	 *     다음과 같은 경우에 {@link ClientException}예외가 발생합니다.
	 *     <ul>
	 *         <li>도로명 주소가 null 또는 "" 경우 → {@link ValidationUtil#createClientException(ErrorCode, String)} 메서드 호출</li>
	 *         <li>지번 주소가 null 또는 "" 경우 → {@link ValidationUtil#createClientException(ErrorCode, String)} 메서드 호출</li>
	 *     </ul>
	 * </p>
	 * @throws ClientException	도로명 주소와 지번주소 둘 다 입력되지 않은 경우 발생
	 */
	private void validateAddress() {
		//지번주소와 도로명 주소 둘 다 입력되지 않은 경우
		if( !hasRoadAddress(roadAddress) && !hasJiBunAddress(jiBunAddress)) {
			throw ValidationUtil.createClientException(ErrorCode.COMMON_ADDRESS_MISSING, "도로명주소 또는 지번주소 중 하나는 필수입니다.");
		}

		if( hasRoadAddress(roadAddress) ) {
			validateRoadAddress();
		}

		if( hasJiBunAddress(jiBunAddress) ) {
			validateJiBunAddress();
		}
	}


	/**
	 * 도로명 주소({@code roadAddress})를 검증합니다.
	 * <p>
	 *     다음과 같은 경우에 {@link ClientException}예외가 발생합니다.
	 *     <ul>
	 *         <li>도로명 주소가 null 또는 "" 경우 → {@link ValidationUtil#createClientException(ErrorCode, String)} 메서드 호출</li>
	 *         <li>도로명 주소가 정규식 패턴과 일치하지 않은 경우 → {@link ValidationUtil#createClientException(ErrorCode, String, Object)} 메서드 호출</li>
	 *     </ul>
	 * </p>
	 * @throws ClientException	주소가 비어있거나 패턴과 일치하지 않은 경우 발생
	 * @see RegexPattern
	 */
	private void validateRoadAddress() {
		if( roadAddress == null || roadAddress.isBlank() ) throw ValidationUtil.createClientException(ErrorCode.COMMON_ROAD_MISSING, "도로명주소를 입력해주세요.");

		if( !roadAddress.matches(RegexPattern.ADDRESS_ROAD_NAME.getPattern()) ) {
			throw ValidationUtil.createClientException(ErrorCode.COMMON_ROAD_FORMAT, "도로명주소는 한글, 숫자, 영문자, 특수문자(·)만 입력 가능합니다.", roadAddress);
		}
	}


	/**
	 * 지번 주소({@code jiBunAddress})를 검증합니다.
	 * <p>
	 *     다음과 같은 경우에 {@link ClientException}예외가 발생합니다.
	 *     <ul>
	 *         <li>지번 주소가 null 또는 "" 경우 → {@link ValidationUtil#createClientException(ErrorCode, String)} 메서드 호출</li>
	 *         <li>지번 주소가 정규식 패턴과 일치하지 않은 경우 → {@link ValidationUtil#createClientException(ErrorCode, String, Object)} 메서드 호출</li>
	 *     </ul>
	 * </p>
	 * @throws ClientException	주소가 비어있거나 패턴과 일치하지 않은 경우 발생
	 * @see RegexPattern
	 */
	private void validateJiBunAddress() {
		if( jiBunAddress == null || jiBunAddress.isBlank() ) throw ValidationUtil.createClientException(ErrorCode.COMMON_JIBUN_MISSING, "지번주소를 입력해주세요.");

		if( !jiBunAddress.matches(RegexPattern.ADDRESS_JIBUN_NAME.getPattern()) ) {
			throw ValidationUtil.createClientException(ErrorCode.COMMON_JIBUN_FORMAT, "지번주소는 한글, 숫자, 영문자, 특수문자(-)만 입력 가능합니다.", roadAddress);
		}
	}

}
