package com.moneymanager.domain.ledger.dto.response;

import com.moneymanager.domain.ledger.enums.LedgerType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto.response<br>
 * 파일이름       : LedgerTypeResponse<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 5<br>
 * 설명              : 가계부 유형 정보를 클라이언트에게 전달하기 위한 클래스
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
 * 		 	  <td>26. 1. 5.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
@AllArgsConstructor
public class LedgerTypeResponse {

	private final String label;		//사용자 화면 문구
	private final String value;		//가계부 유형 코드

	public static List<LedgerTypeResponse> fromEnum() {
		return Arrays.stream(LedgerType.values())
				.map(t -> new LedgerTypeResponse(t.getLabel(), t.getUrlCode()))
				.collect(Collectors.toList());
	}

}
