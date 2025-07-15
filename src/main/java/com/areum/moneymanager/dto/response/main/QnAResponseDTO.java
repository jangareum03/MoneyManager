package com.areum.moneymanager.dto.response.main;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

public class QnAResponseDTO {


	@Builder
	@Getter
	public  static class Update {
		private String title;
		private Boolean isOpen;
		private String content;
	}

	/**
	 * 설명<br>
	 * <b color='white'>title</b>는 제목,
	 * <b color='white'>writer</b>는 작성자,
	 * <b color='white'>date</b>는 작성날짜,
	 * <b color='white'>content</b>는 내용,
	 * <b color='white'>answer</b>는 답변 입니다.
	 */
	@Builder
	@Getter
	public static class Detail {
		private Question question;
		private Answer answer;
	}


	/**
	 * 설명<br>
	 * <b color='white'>date</b>는 작성날짜,
	 * <b color='white'>writer</b>는 작성자,
	 * <b color='white'>title</b>는 제목,
	 * <b color='white'>content</b>는 내용 입니다.
	 */
	@Builder
	@Getter
	public static class Question {
		private String date;
		private String writer;
		private String title;
		private String content;
	}


	/**
	 * 설명<br>
	 * <b color='white'>date</b>는 작성날짜,
	 * <b color='white'>writer</b>는 작성자,
	 * <b color='white'>content</b>는 내용 입니다.
	 */
	@Builder
	@Getter
	public static class Answer {
		private String date;
		private String writer;
		private String content;
	}


}