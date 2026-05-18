package com.moneymanager.domain.ledger.vo;

import com.moneymanager.exception.BusinessException;
import lombok.Getter;
import lombok.Value;

import static com.moneymanager.domain.global.enums.RegexPattern.ADDRESS_DETAIL_NAME;
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
		//둘 다 없는 경우
		if(isEmpty(placeName, roadAddress)) return;

		//둘 중 하나만 존재하는 경우
		if(isNullOrBlank(placeName) ^ isNullOrBlank(roadAddress)) {
			throw BusinessException.of(
					LEDGER_POLICY_NOT_ALLOWED,
					"가계부 검증 실패   |   reason=정책위반   |   policy=장소명과 기본주소 둘 다 필요   |   value={placeName:" + placeName + ", roadAddress:"+roadAddress
			).withUserMessage("장소명과 기본주소 둘 다 있어야 합니다.");
		}

		//상세주소 검증
		if(detailAddress != null && !matchesPattern(detailAddress, ADDRESS_DETAIL_NAME.getPattern())) {
			throw BusinessException.of(
					LEDGER_INPUT_FORMAT,
					"가계부 검증 실패   |   reason=형식오류   |   field=detailAddress   |   expectedFormat=한글, 숫자, 영문, 공백, -, (, ), /, .   |   value=" + detailAddress
			).withUserMessage("상세 주소는 한글,숫자,영문, -, (, ), /, .만 입력 가능합니다.");
		}
	}

	private boolean isEmpty(String name, String roadAddress) {
		return isNullOrBlank(name) && isNullOrBlank(roadAddress);
	}

}