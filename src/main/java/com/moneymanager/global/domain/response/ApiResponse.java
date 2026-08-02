package com.moneymanager.global.domain.response;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.domain.response<br>
 * 파일이름       : ApiResponse<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 31<br>
 * 설명              : API 응답을 위한 데이터 클래스
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
 * 		 	  <td>26. 7. 31</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class ApiResponse<T> {

	private final boolean success;
	private final String message;
	private final T data;

	private ApiResponse(boolean success, String message, T data) {
		this.success = success;
		this.message = message;
		this.data = data;
	}

	public static ApiResponse<Void> success(String message) {
		return new ApiResponse<>(true, message,null);
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(true, message,data);
	}

	public static <T> ApiResponse<T> fail(String  message) {
		return new ApiResponse<>(false, message,null);
	}

	public static <T> ApiResponse<T> fail(String message, T data) {
		return new ApiResponse<>(false, message,data);
	}

}