package com.areum.moneymanager.dto.request.main.validation;


import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Q&A의 정보를 검증하기 위해 서버로 전송하는 데이터 객체
 */
public class QnaRequestDTO {


	/**
	 * 질문 제목값을 검증할 때 필요한 객체입니다. <br>
	 * <b color='white'>title</b>은 제목입니다.
	 */
	@Getter
	@NoArgsConstructor
	public static class Title {
		private String title;
	}


	/**
	 * 질문 내용값을 검증할 때 필요한 객체입니다. <br>
	 * <b color='white'>content</b>은 내용입니다.
	 */
	@Getter
	@NoArgsConstructor
	public static class Content {
		private String content;
	}

}
