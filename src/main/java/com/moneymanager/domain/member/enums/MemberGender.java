package com.moneymanager.domain.member.enums;

import lombok.Getter;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.enums.type<br>
 *  * 파일이름       : GenderType<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 7. 15<br>
 *  * 설명              : 회원의 성별를 정의한 클래스
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
public enum GenderType {

	DEFAULT('N', "선택없음"), MAN('M', "남자"), WOMAN('F', "여자");

	private char type;
	private String text;

	GenderType( char type, String text ) {
		this.type = type;
		this.text= text;
	}


	public static GenderType match( char type ) {
		for( GenderType g : values() ) {
			if( g.getType() == type ) {
				return g;
			}
		}

		return GenderType.DEFAULT;
	}
}
