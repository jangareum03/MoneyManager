package com.moneymanager.global.operation.enums;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.log<br>
 * 파일이름       : OperationResult<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 1<br>
 * 설명              : 운영 로그 결과를 정의한 클래스
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
 * 		 	  <td>26. 7. 1</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public enum OperationResult {
	SUCCESS("성공"),
	FAIL("실패");

	@Getter
	private final String korean;

	OperationResult(String korean) {
		this.korean = korean;
	}
}
