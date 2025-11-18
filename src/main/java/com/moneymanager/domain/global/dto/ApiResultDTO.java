package com.moneymanager.domain.global.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.common<br>
 * * 파일이름       : ApiResultDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 29.<br>
 * * 설명              : 내부 API 응답을 위한 데이터 클래스
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
@Builder
@Getter
public class ApiResultDTO<T> {
	//결과 : true(성공), false(실패)
	private boolean success;
	//실패 시 원인
	private String message;
	//성공 시 반환할 값
	private T data;


	/**
	 * API 응답이 성공일 때 반환합니다.
	 *
	 * @param data		반환할 값
	 * @return	성공한 응답을 저장한 객체
	 * @param <T>	반환할 값의 유형
	 */
	public static <T> ApiResultDTO<T> success( T data ) {
		return ApiResultDTO.<T>builder()
				.success(true)
				.data(data)
				.build();
	}


	/**
	 * API 응답이 실패일 때 반환합니다.
	 *
	 * @param message	실패 메시지
	 * @return	실패한 응답을 저장한 객체
	 * @param <T>	반환할 값의 유형
	 */
	public static <T> ApiResultDTO<T> failure(String message) {
		return ApiResultDTO.<T>builder()
				.success(false)
				.message(message)
				.build();
	}
}
