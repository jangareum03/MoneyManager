package com.areum.moneymanager.dto.inquiry.request;

import com.areum.moneymanager.dto.common.request.PageRequest;
import lombok.*;

import java.util.List;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.inquiry.request<br>
 * * 파일이름       : InquirySearchRequest<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 29.<br>
 * * 설명              : 문의사항 검색 요청을 위한 데이터 클래스
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class InquirySearchRequest {
	//검색 유형
	private String mode;
	//검색 키워드
	private List<String> keyword;
	//페이지 정보
	private PageRequest page;
}
