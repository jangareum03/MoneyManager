package com.moneymanager.domain.global;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.global<br>
 * 파일이름       : Policy<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 30<br>
 * 설명              : 서비스 정책을 나타내는 클래스
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
 * 		 	  <td>26. 3. 30</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public final class Policy {

	public static final int LEDGER_MAX_YEAR = 5;
	public static final int LEDGER_MAX_IMAGE = 3;

	private Policy() {}

}
