package com.moneymanager.domain.enums;

import lombok.Getter;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.enums<br>
 *  * 파일이름       : SystemMessage<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 24<br>
 *  * 설명              : 시스템 오류 메시지를 정의한 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>22. 7. 24</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>최초 생성(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Getter
public enum SystemMessage {

	SYSTEM("일시적인 오류가 발생했어요. 잠시 후 다시 시도해주세요."),
	DATABASE("데이터를 불러오는 데 문제가 발생했어요. 계속되면 문의해주세요."),
	EXTERNAL("다른 서비스와 연결에 문제가 있어요. 잠시 후 다시 시도해주세요."),
	FILE("파일 처리 중 오류가 발생했어요. 다시 시도해주시거나, 다른 파일을 선택해주세요."),
	SECURITY("접근 권한이 없거나, 인증 정보가 유효하지 않아요."),
	SETTING("서비스 준비 중 문제가 발생했어요. 되도록  빠르게 문제를 해결하겠습니다.");

	private final String message;

	SystemMessage( String message ) {
		this.message = message;
	}
}
