package com.areum.moneymanager.dto.request.main;

import lombok.*;

import java.time.LocalDate;

public class QnARequestDTO {


	@Getter
	@AllArgsConstructor
	public static class Create {
		private String title;
		private Boolean isOpen;
		private String content;
	}



	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CheckWriter {
		private Long id;
		private String memberId;
	}


	@Getter
	@Builder
	@ToString
	public static class Update {
		private Long id;
		private String title;
		private Boolean isOpen;
		private String content;
	}

}
