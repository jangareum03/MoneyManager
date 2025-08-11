package com.moneymanager.enums.type;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.enums.type<br>
 * 파일이름       : MemberStatus<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 4.<br>
 * 설명              : 회원 상태를 정의한 클래스
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
 * 		 	  <td>25. 8. 4</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 8. 10</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[필드 추가] REPAIR</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 8. 11</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 추가] valueOf - type 매개변수와 일치하는 상수 반환</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public enum MemberStatus {
	ACTIVE('A'), LOCKED('L'), REPAIR('R'),DELETE('D'), UNKNOWN('U');

	private final char type;

	MemberStatus( char type ) {
		this.type = type;
	}

	public static MemberStatus valueOf( char type ) {
		for( MemberStatus status : values() ) {
			if( status.getType() == type ) {
				return status;
			}
		}

		return MemberStatus.UNKNOWN;
	}
}
