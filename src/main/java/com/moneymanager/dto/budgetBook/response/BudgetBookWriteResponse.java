package com.moneymanager.dto.budgetBook.response;

import com.moneymanager.dto.budgetBook.CategoryDTO;
import com.moneymanager.dto.budgetBook.FixDTO;
import com.moneymanager.dto.budgetBook.PlaceDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.budgetBook.response<br>
 * * 파일이름       : BudgetBookWriteResponse<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 25.<br>
 * * 설명              : 가계부 작성 응답을 위한 데이터 클래스
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
public class BudgetBookWriteResponse {

	/**
	 * 가계부 상세 정보 작성을 위한 DTO<br>
	 * 가계부 날짜는 'yyyymmdd' 형식이며, 가계부 유형은 'income(수입)/outlay(지출)'입니다.
	 */
	@Builder
	@Getter
	public static class InitialBudget {
		//가계부 날짜 - 범위: 현재년도 ~ 5년전 사이의 값
		private String date;
		//가계부 유형
		private String type;
		//가계부 유형에 따른 카테고리 리스트
		private List<CategoryDTO> categories;
		//등록 가능한 이미지 수
		private int maxImage;
		//가계부 등록 주기
		@Builder.Default
		private FixDTO fix = FixDTO.defaultValue();
		//위치
		@Builder.Default
		private PlaceDTO place = PlaceDTO.defaultValue();
	}

}
