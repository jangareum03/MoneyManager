package com.areum.moneymanager.dto.member.log;

import com.areum.moneymanager.dto.common.LogDTO;
import lombok.Builder;
import lombok.Getter;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.member.log<br>
 * * 파일이름       : LoginLogDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 25.<br>
 * * 설명              : 로그인 로그 정보를 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 25.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LoginLogDTO extends LogDTO {
	//아이디
	private final String id;
	//접속 브라우저
	private final String browser;

	public LoginLogDTO( String id, String browser ) {
		super();

		this.id = id;
		this.browser = browser;
	}
}
