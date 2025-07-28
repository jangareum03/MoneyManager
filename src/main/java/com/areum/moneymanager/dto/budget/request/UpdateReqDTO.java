package com.areum.moneymanager.dto.budget.request;

import com.areum.moneymanager.dto.budget.FixDTO;
import com.areum.moneymanager.dto.budget.PlaceDTO;
import com.areum.moneymanager.dto.common.ImageDTO;
import lombok.*;

import java.util.List;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.budget.request<br>
 * * 파일이름       : UpdateReqDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 25.<br>
 * * 설명              : 가계부 수정 요청을 위한 데이터 클래스
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
public class UpdateReqDTO {

	/**
	 * 가계부 상세 정보를 가져오기 위한 DTO<br>
	 * <span color='#BE2E22'>날짜, 고정, 카테고리, 금액</span>은 필수값입니다.
	 */
	@Builder
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class DetailedBudget {
		private String date;
		private FixDTO fix = FixDTO.defaultValue();
		private String category;
		private String memo;
		private Long price;
		private String paymentType;
		private List<ImageDTO> image;
		private PlaceDTO place = PlaceDTO.defaultValue();
	}

}
