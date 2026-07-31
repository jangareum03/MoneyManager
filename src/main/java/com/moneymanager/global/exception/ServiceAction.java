package com.moneymanager.global.exception;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.error<br>
 * 파일이름       : ServiceAction<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 6<br>
 * 설명              : 서비스 요청 상황을 코드로 정의한 클래스
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
 * 		 	  <td>26. 3. 6</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public enum ServiceAction {
	LOGIN("로그인", "로그인 중 문제가 발생했습니다."),
	LEDGER_REGISTER("가계부 등록", "가계부 등록 중 문제가 발생했습니다. 다시 시도해주세요."),
	LEDGER_DETAIL("가계부 상세 조회", "가계부 정보를 불러오는 중 문제가 발생했습니다."),
	LEDGER_EDIT_VIEW("가계부 수정 화면 조회", "가계부 정보를 불러오는 중 문제가 발생했습니다."),
	LEDGER_EDIT("가계부 수정", "가계부 수정 중 문제가 발생했습니다.");

	private final String title;
	private final String message;

	ServiceAction(String title, String message) {
		this.title = title;
		this.message = message;
	}
}
