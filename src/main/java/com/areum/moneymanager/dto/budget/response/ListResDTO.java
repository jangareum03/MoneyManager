package com.areum.moneymanager.dto.budget.response;

import com.areum.moneymanager.dto.budget.CategoryDTO;
import lombok.*;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.budget.response<br>
 * * 파일이름       : ListResDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 25.<br>
 * * 설명              : 가계부 목록 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 25.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class ListResDTO {

	/**
	 * 가계부 금액별 통계를 보기 위한 DTO
	 */
	@Builder
	@Getter
	public static class Stats {
		//총합
		private Long total;
		//수입
		private Long income;
		//지출
		private Long outlay;
	}

	/**
	 * 가계부 카드(= 가계부 내역 한 개) 정보를 보기 위한 DTO
	 */
	@Builder
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Card {
		//가계부 식별번호
		private Long id;
		//가계부 카테고리
		private CategoryDTO category;
		//가계부 메모
		private String memo;
		//가계부 금액
		private Long price;
	}

}
