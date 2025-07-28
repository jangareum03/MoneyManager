package com.areum.moneymanager.dto.budget.request;

import com.areum.moneymanager.dto.budget.FixDTO;
import com.areum.moneymanager.dto.budget.PlaceDTO;
import com.areum.moneymanager.dto.common.ImageDTO;
import com.areum.moneymanager.dto.common.request.DateReqDTO;
import lombok.*;

import java.util.List;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.budget.request<br>
 * * 파일이름       : WriteReqDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 25.<br>
 * * 설명              : 가계부 작성 요청을 위한 데이터 클래스
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
public class WriteReqDTO {

	/**
	 * 날짜의 마지막 일자를 알기 위한 DTO
	 */
	@Builder
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class LastDay {
		private DateReqDTO.MonthRange date;
	}

	/**
	 *	가계부 초기 설정(가계부 날짜, 가계부 유형)을 가져오기 위한 DTO<br>
	 * 가계부 날짜는 'yyyymmdd' 형식이며, 가계부 유형은 'income(수입)/outlay(지출)'입니다.
	 */
	@Builder
	@Getter
	public static class InitialBudget {
		//가계부 날짜 - 범위: 현재년도부터 5년전까지 사이의값
		private String date;
		//가계부 유형
		private String type;
	}

	/**
	 * 가계부 상세 정보를 가져오기 위한 DTO<br>
	 * <span color='#BE2E22'>날짜, 고정, 카테고리, 금액</span>은 필수값입니다.
	 */
	@Builder
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class DetailedBudget {
		//가계부 날짜
		private String date;
		//가계부 등록 주기
		private FixDTO fix = FixDTO.defaultValue();
		//카테고리 코드
		private String category;
		//메모
		private String memo;
		//금액
		private Long price;
		//금액유형
		private String paymentType;
		//가계부 사진
		private List<ImageDTO> image;
		//위치
		private PlaceDTO place = PlaceDTO.defaultValue();
	}

}