package com.moneymanager.domain.member.dto;

import com.moneymanager.domain.global.dto.LogDTO;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;

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
 * 		 	  <td>25. 7. 25</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 7. 25</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[필드 추가] ip</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LoginLogDTO extends LogDTO {
	//아이디
	private final String id;
	//접속 IP 주소
	protected String ip;
	//실패 사유
	private final String cause;

	public LoginLogDTO(boolean success, HttpServletRequest request, String id, String cause) {
		super( success, request );

		this.id = id;
		this.ip = request.getRemoteAddr();
		this.cause = cause;
	}
}
