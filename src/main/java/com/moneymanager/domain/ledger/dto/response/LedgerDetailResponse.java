package com.moneymanager.domain.ledger.dto.response;

import com.moneymanager.domain.ledger.enums.AmountType;
import com.moneymanager.domain.ledger.enums.CategoryType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.domain.ledger.dto<br>
 * * 파일이름       : LedgerDetailResponse<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 26.<br>
 * * 설명              : 가계부 단건 조회 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 26.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class LedgerDetailResponse {
	private String date;										//가계부 날짜
	private CategoryType type;							//가계부 유형
	private CategoryItem category;					//카테고리
	private String memo;										//메모
	private List<String> images;							//가계부 사진

	private Long amount;									//금액
	private AmountType paymentType;				//금액 유형

	private String placeName;								//장소명
	private String roadAddress;							//기본주소
	private String detailAddress;						//상세주소
}