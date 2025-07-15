package com.areum.moneymanager.dto.response.main;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 서버에서 가계부 카테고리 관련 작업을 사용자에게 전송할 데이터 객체
 */
public class CategoryResponseDTO {

	/**
	 * 가계부 카테고리 정보를 저장한객체입니다.<p>
	 * <b color='white'>name</b>는 카테고리 이름, <b color='white'>code</b>는 카테고리 코드입니다.
	 */
	@Builder
	@Getter
	// 카테고리 정보를 가져올 때
	public static class Read {
		private String name;
		private String code;

	}

	@Builder
	@Getter
	// 카테고리 이름 가져올 때
	public static class Name {
		private String name;
	}
}

