package com.areum.moneymanager.enums.type;

import lombok.Getter;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.enums.type<br>
 *  * 파일이름       : AnswerStatus<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 7. 15<br>
 *  * 설명              : 문의사항 답변상태를 정의한 클래스
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
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Getter
public enum AnswerStatus {

	YES('Y', "답변 완료"), NO('N', "답변 준비");

	private final char type;
	private final String text;

	AnswerStatus( char type, String text ) {
		this.type = type;
		this.text = text;
	}

	public static String match( char type ) {
		for( AnswerStatus status : values() ) {
			if( status.type== type ) {
				return status.getText();
			}
		}

		return AnswerStatus.NO.getText();
	}
}
