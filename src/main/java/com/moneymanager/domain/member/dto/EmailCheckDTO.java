package com.moneymanager.domain.member.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.member.validation<br>
 * * 파일이름       : EmailCheckDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 29.<br>
 * * 설명              : 회원 이메일 검증을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 29.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailCheckDTO {
	//이메일
	private String email;
	//인증코드
	private String code;
	//이메일 인증시간
	private EmailTimer timer;


	/**
	 * 이메일 인증시간 정보를 담은 DTO
	 */
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class EmailTimer {
		//분
		private int minute;
		//초
		private int second;
	}

}
