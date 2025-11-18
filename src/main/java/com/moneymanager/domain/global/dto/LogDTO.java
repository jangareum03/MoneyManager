package com.moneymanager.domain.global.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.common<br>
 * * 파일이름       : LogDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 25.<br>
 * * 설명              : 로그 작성 정보를 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 30</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[이름 변경] browser → userAgent</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@SuperBuilder
@Getter
public class LogDTO {
	//성공여부
	protected boolean success;
	//접속 브라우저
	protected String userAgent;
	//접속 일시
	protected LocalDateTime dateTime;

	public LogDTO(boolean success, HttpServletRequest request ) {
		this.success = success;
		this.userAgent = request.getHeader("User-Agent");
		this.dateTime = LocalDateTime.now();
	}
}
