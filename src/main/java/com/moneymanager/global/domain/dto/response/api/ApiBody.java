package com.moneymanager.global.domain.dto.response.api;

import lombok.Getter;

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
public class ApiBody<T> {

    private final String message;
    private final String next;
    private final T data;

    private ApiBody(String message, String next, T data) {
        this.message = message;
        this.next = next;
        this.data = data;
    }

    public static ApiBody<Void> next(String message, String next) {
      return new ApiBody<>(message, next, null);
    }

    public static <T> ApiBody<T> data(String message, T data) {
        return new ApiBody<>(message, null, data);
    }

}