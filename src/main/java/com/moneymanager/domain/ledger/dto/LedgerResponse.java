package com.moneymanager.domain.ledger.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.budgetBook.response<br>
 * * 파일이름       : BudgetBookDetailResponse<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 26.<br>
 * * 설명              : 가계부 상세정보 응답을 위한 데이터 클래스
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
public class BudgetBookDetailResponse {
	//가계부 식별 번호
	private  Long id;
	//가계부 작성 날짜
	private ReadDate date;
	//가계부 등록 주기
	private LedgerFixResponse fix;
	//카테고리
	private LedgerCategoryResponse category;
	//메모
	private String memo;
	//금액
	private Long price;
	//금액 유형
	private String paymentType;
	//가계부 사진
	private List<String> image;
	//위치
	private PlaceDTO place;

	/**
	 * 가계부 내역 날짜를 읽기 위한 DTO
	 */
	@Builder
	@Getter
	public static class ReadDate {
		//time 태그용
		private String read;
		//html 문자용
		private String text;
	}

}
