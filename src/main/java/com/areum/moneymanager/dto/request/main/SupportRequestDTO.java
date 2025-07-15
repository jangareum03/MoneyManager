package com.areum.moneymanager.dto.request.main;

import lombok.*;

import java.util.List;

public class SupportRequestDTO {


	/**
	 * 설명<br>
	 * <b color='white'>page</b>는 현재 페이지 번호,
	 * <b color='white'>size</b>는 한 페이지 당 공지사항 수입니다.
	 */
	@Builder
	@Data
	@AllArgsConstructor
	@ToString
	public static class Page {
		private Integer num;
		private Integer size;

		public Page() {
			this.num = null;
			this.size = null;
		}
	}


	@Builder
	@Getter
	@AllArgsConstructor
	public static class Search {
		private String mode;
		private List<String> keyword;

		public Search() {
			this.mode = "all";
			this.keyword = null;
		}
	}


	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SearchAPI {
		private Page pagination = new Page();
		private Search search = new Search();
	}

}
