package com.moneymanager.domain.enums.type;

import lombok.Getter;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.enums.type<br>
 *  * 파일이름       : MemberType<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 7. 15<br>
 *  * 설명              : 회원 유형을 정의한 클래스
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
 *		</tbody>
 * </table>
 */
@Getter
public enum MemberType {
	NORMAL('C', "일반회원"), KAKAO('K', "카카오"), GOOGLE('G', "구글"), NAVER('N', "네이버");

	private final char value;
	private final String text;

	MemberType( char value, String text ) {
		this.value = value;
		this.text = text;
	}

	public static MemberType match( char value ) {
		for( MemberType m : values() ) {
			if( m.getValue() == value ) {
				return m;
			}
		}

		return MemberType.NORMAL;
	}
}
