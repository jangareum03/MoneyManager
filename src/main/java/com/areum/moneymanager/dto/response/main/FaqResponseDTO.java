package com.areum.moneymanager.dto.response.main;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 서버에서 자주묻는질문 관련 작업을 사용자에게 전송할 데이터 객체
 */
public class FaqResponseDTO {


	/**
	 * 자주묻는질문 정보를 저장한 객체입니다. <br>
	 * <b color='white'>question</b>는 질문, <b color='white'>answer</b>는 답변입니다.
	 */
	@Getter
	@NoArgsConstructor
	public static  class Read {
		private String question;
		private String answer;
	}

}
