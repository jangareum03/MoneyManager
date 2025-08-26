package com.moneymanager.dto.budgetBook.request;

import com.moneymanager.dto.budgetBook.FixDTO;
import com.moneymanager.dto.budgetBook.PlaceDTO;
import com.moneymanager.vo.YearMonthDayVO;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.budgetBook.request<br>
 * * 파일이름       : BudgetBookWriteRequest<br>
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
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class BudgetBookWriteRequest {
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
	private List<MultipartFile> image;
	//위치
	private PlaceDTO place = PlaceDTO.defaultValue();
}