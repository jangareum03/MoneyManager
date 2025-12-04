package com.moneymanager.domain.ledger.dto;

import com.moneymanager.domain.ledger.enums.DateType;
import lombok.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.domain.ledger.dto<br>
 * * 파일이름       : LedgerSearchRequest<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 29.<br>
 * * 설명              : 가계부 검색 요청을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 29.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LedgerSearchRequest {
	@Builder.Default
	private DateType type = DateType.MONTH;										//날짜유형
	@Builder.Default
	private Integer year = LocalDate.now().getYear();							//년
	@Builder.Default
	private Integer month = LocalDate.now().getMonthValue();			//월
	@Builder.Default
	private Integer week = null;																//주
	@Builder.Default
	private String mode = "all";																//검색 유형
	@Builder.Default
	private List<String> keywords = Collections.emptyList();				//검색 키워드

	public void changeType(DateType type) {
		this.type = type;
	}
}
