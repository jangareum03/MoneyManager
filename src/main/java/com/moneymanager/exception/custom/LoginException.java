package com.moneymanager.exception.custom;

import com.moneymanager.domain.global.dto.ErrorDTO;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.custom<br>
 * 파일이름       : LoginException<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 6.<br>
 * 설명              : 로그인 요청에서 발생하는 커스텀 예외 클래스
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
 * 		 	  <td>25. 8. 6</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 8. 21</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[생성자 수정] 매개변수 ErrorDTO → ErrorCode, message</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LoginException extends AuthenticationException {

	private final ErrorDTO errorDTO;

	public LoginException( ErrorDTO errorDTO ) {
		super(errorDTO.getMessage());

		this.errorDTO = errorDTO;
	}
}
