package com.moneymanager.domain.ledger.vo;

import com.moneymanager.exception.BusinessException;
import lombok.Getter;
import lombok.Value;

import static com.moneymanager.domain.global.enums.RegexPattern.*;
import static com.moneymanager.exception.error.ErrorCode.*;
import static com.moneymanager.utils.validation.ValidationUtils.isNullOrBlank;
import static com.moneymanager.utils.validation.ValidationUtils.matchesPattern;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
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
 * 		 	  <td>25. 12. 27</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 삭제] validateAddress, validateJiBunAddress</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
@Getter
public class Place {
	String placeName;			//장소명
	String roadAddress;		//도로명 주소
	String detailAddress;		//상세주소

	public Place(String placeName, String road, String detail) {
		validate(placeName, road, detail);

		this.placeName = placeName;
		this.roadAddress = road;
		this.detailAddress = detail;
	}

	private void validate(String placeName, String roadAddress, String detailAddress) {
		//1. 필수값이 없으면 종료
		if(isNullOrBlank(placeName) && isNullOrBlank(roadAddress)) return;

		//2. 장소명 검증
		validatePlaceName(placeName);

		//3. 도로명 검증
		validateRoadAddress(roadAddress);

		//4.상세주소 검증
		if(!isNullOrBlank(detailAddress)) {
			validateDetailAddress(detailAddress);
		}

	}

	private void validatePlaceName(String placeName) {
		String message = "장소명이 올바르지 않습니다. 다시 선택해 주세요.";

		//1. null 검증
		if(isNullOrBlank(placeName)) {
			throw BusinessException.of(
					LEDGER_INPUT_NULL,
					"객체생성 실패   |   reason=필수값 누락   |   object=Place   |   field=placeName"
			).withUserMessage(message);
		}

		//2. 길이 검증
		if(placeName.length() > 100) {
			throw BusinessException.of(
					LEDGER_INPUT_LENGTH,
					"객체생성 실패   |   reason=길이오류   |   object=Place   |   field=placeName   |   maxLength=100   |   value=" + placeName.length()
			).withUserMessage(message);
		}

		//3. 형식 검증
		if(!matchesPattern(placeName, ADDRESS_PLACE_NAME.getPattern())) {
			throw BusinessException.of(
					LEDGER_INPUT_FORMAT,
					"객체생성 실패   |   reason=형식오류   |   object=Place   |   field=placeName   |   expectedFormat=한글, 영문, 숫자, 공백, 괄호, 하이픈, 점 (예: CGV 강남점)   |   value=" + placeName
			).withUserMessage(message);
		}
	}

	private void validateRoadAddress(String roadAddress) {
		String message = "주소가 올바르지 않습니다. 다시 선택해 주세요.";

		//1. null 검증
		if(isNullOrBlank(roadAddress)) {
			throw BusinessException.of(
					LEDGER_INPUT_NULL,
					"객체생성 실패   |   reason=필수값 누락   |   object=Place   |   field=roadAddress"
			).withUserMessage(message);
		}

		//2. 길이 검증
		if(roadAddress.length() > 300) {
			throw BusinessException.of(
					LEDGER_INPUT_LENGTH,
					"객체생성 실패   |   reason=길이오류   |   object=Place   |   field=roadAddress   |   maxLength=300   |   value=" + roadAddress.length()
			).withUserMessage(message);
		}

		//3.. 형식 검증
		if(!matchesPattern(roadAddress, ADDRESS_ROAD_NAME.getPattern())) {
			throw BusinessException.of(
					LEDGER_INPUT_FORMAT,
					"객체생성 실패   |   reason=형식오류   |   object=Place   |   field=roadAddress   |   expectedFormat=한글, 영문, 숫자, 공백, 하이픈   |   value=" + roadAddress
			).withUserMessage(message);
		}
	}

	private void validateDetailAddress(String detailAddress) {
		//1. 길이 검증
		if(detailAddress.length() > 500) {
			throw BusinessException.of(
					LEDGER_INPUT_LENGTH,
					"객체생성 실패   |   reason=길이오류   |   object=Place   |   field=detailAddress   |   maxLength=500   |   value=" + detailAddress.length()
			).withUserMessage("상세 주소는 최대 500자까지만 입력 가능합니다.");
		}

		//2. 형식 검증
		if(!matchesPattern(detailAddress, ADDRESS_DETAIL_NAME.getPattern())) {
			throw BusinessException.of(
					LEDGER_INPUT_FORMAT,
					"가계부 검증 실패   |   reason=형식오류   |   Object=Place   |   field=detailAddress   |   expectedFormat=한글, 영문, 숫자, 공백, 하이픈, 괄호, 쉼표, 슬래시, 점, #   |   value=" + detailAddress
			).withUserMessage("상세 주소는 한글, 영문, 숫자, 공백, 하이픈, 괄호, 쉼표, 슬래시, 점, #만 입력 가능합니다.");
		}
	}

}