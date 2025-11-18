package com.moneymanager.dto.common;

import com.moneymanager.exception.code.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.common<br>
 * * 파일이름       : ErrorDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 28.<br>
 * * 설명              : 에러 정보의 요청/ 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 28.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 7. 31</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>
 * 		 	      [필드 추가] code
 * 		 	  </td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class ErrorDTO<T> {
	//식별번호
	@Builder.Default
	private String errorId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + '-' + UUID.randomUUID().toString().substring(0, 8);
	//요청 데이터
	private T requestData;
	//에러코드
	private ErrorCode errorCode;
	//안내 메시지
	private String message;
}
