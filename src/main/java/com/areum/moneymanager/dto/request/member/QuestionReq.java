package com.areum.moneymanager.dto.request.member;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class QuestionReq {

	@Builder
	@Getter
	// 질문을 등록할 때
	public static class Create {
		private String title;
		private Character open;
		private String content;
	}


	@Builder
	@Getter
	// 질문 검색할 때
	public static class Search {
		private String type;
		private String keyword;
		private List<String> date;
	}

}
