package com.moneymanager.domain.ledger.vo;

import com.moneymanager.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

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
	String name;					//장소명
	String roadAddress;		//도로명 주소
	String detailAddress;		//상세주소


	@Builder
	public Place(String placeName, String roadAddress, String detailAddress) {
		this.name = placeName;
		this.roadAddress = roadAddress;
		this.detailAddress = detailAddress;
	}

}