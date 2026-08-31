package com.moneymanager.ledger.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.request<br>
 * 파일이름       : LedgerSearchRequest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 29<br>
 * 설명              : 가계부 내역 검색을 위한 데이터 클래스
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
 * 		 	  <td>26. 8. 29</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
@Builder
public class LedgerSearchRequest {

    private String type;
    private String menu;
    private List<String> categories;
    private String memo;
    private String fromDate;
    private String toDate;

}