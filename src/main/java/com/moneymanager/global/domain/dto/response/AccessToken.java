package com.moneymanager.global.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.domain.response<br>
 * 파일이름       : AccessToken<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 17<br>
 * 설명              : 토큰 정보를 담은 데이터 클래스
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
 * 		 	  <td>26. 8. 17</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
@AllArgsConstructor
public class AccessToken {

	String token;
	Date expiration;

}