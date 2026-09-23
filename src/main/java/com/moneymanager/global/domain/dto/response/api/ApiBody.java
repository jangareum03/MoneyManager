package com.moneymanager.global.domain.dto.response.api;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.domain.dto.response<br>
 * 파일이름       : ApiBody<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 24<br>
 * 설명              : API 성공 응답 Body 정보를 담은 클래스
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
 * 		 	  <td>26. 8. 24</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
@Builder
public class ApiBody<T> {

    @Builder.Default
    private final HttpStatus status =  HttpStatus.OK;
    private final String messageKey;
    private final String next;
    private final T data;

}