package com.moneymanager.domain.ledger.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.areum.moneymanager.domain.ledger.dto<br>
 * 파일이름       : LedgerWriteRequest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 7. 25.<br>
 * 설명              : 가계부 작성 요청을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 25</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 8. 27</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 추가] toRequiredFields() - 필수항목만 있는 Required객체로 변환</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LedgerWriteRequest {
	private String date;									//날짜
	private String category;							//카테고리 코드
	private String memo;									//메모

	private boolean fixed;								//고정여부(true: 반복, false: 일회성)
	private String period;								//반복주기

	private Long amount;								//금액
	private String paymentType;						//금액 유형

	private List<MultipartFile> image;			//이미지 리스트
}