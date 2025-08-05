package com.moneymanager.enums.type;

import lombok.Getter;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.enums.type<br>
 *  * 파일이름       : HistoryType<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 7. 15<br>
 *  * 설명              : 내역 종류를 정의한 클래스
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
 *		 	  <td>22. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>22. 7. 30</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>
 *		 	      [이름 변경] 상수명 앞 모두 'MEMBER_'로 변경<br>
 *		 	      [상수 추가] MEMBER_DELETE
 *		 	  </td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Getter
public enum HistoryType {
	MEMBER_JOIN("회원가입"),
	MEMBER_UPDATE_NONE(""), MEMBER_UPDATE_NAME("이름"), MEMBER_UPDATE_GENDER("성별"), MEMBER_UPDATE_EMAIL("이메일"), MEMBER_UPDATE_PROFILE("프로필"), MEMBER_UPDATE_PASSWORD("비밀번호"),
	MEMBER_DELETE("회원탈퇴");

	private final String text;

	HistoryType( String  update ) {
		this.text = update;
	}

}
