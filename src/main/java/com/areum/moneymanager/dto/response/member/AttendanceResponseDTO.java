package com.areum.moneymanager.dto.response.member;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class AttendanceResponseDTO {

	@Builder
	@Getter
	//회원 출석정보 가져올 때
	public static class Complete {
		private List<String> dates;
	}

}
