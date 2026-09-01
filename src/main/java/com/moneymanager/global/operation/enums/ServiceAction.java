package com.moneymanager.global.operation.enums;

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
	//화면 요청
	LOGIN("로그인", "/"),
	LEDGER_REGISTER_STEP1_VIEW("가계부 작성 1단계 화면 조회", "/ledgers"),
	LEDGER_REGISTER_STEP2_VIEW("가계부 작성 2단계 화면 조회", "/ledgers/new/step1"),
	LEDGER_REGISTER("가계부 등록", "/ledgers/new/step2"),
	LEDGER_MAP_VIEW("가계부 장소 작성 화면 조회", "/ledgers/new/step2"),
	LEDGER_HISTORY_VIEW("가계부 내역 화면 조회", "/error/404"),

	//API 요청
	LEDGER_CATEGORY("하위 카테고리 목록 조회 API"),
	LEDGER_REGISTER_DATE("작성할 가계부 날짜 목록 조회 API"),
	LEDGER_SEARCH("가계부 내역 검색 API"),
	LEDGER_CHART("가계부 차트 조회 API"),
	LEDGER_DELETE("가계부 삭제 API");

	private final String description;
	private final String path;

	ServiceAction(String description) {
		this(description, null);
	}

	ServiceAction(String description, String path) {
		this.description = description;
		this.path = path;
	}
}
