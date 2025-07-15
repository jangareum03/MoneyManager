package com.areum.moneymanager.dto.request.main;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 가계부의 검증할 정보를 서버로 전송하는 데이터 객체
 */
public class ValidationRequestDTO {


	/**
	 * 날짜의 마지막 일자를 얻을 때 필요한 객체입니다.<br>
	 * <b color='white'>year</b>는 년, <b color='white'>month</b>은 월입니다.
	 */
	@Getter
	@ToString
	@NoArgsConstructor
	public static class JsonLastDay {
		private String year;
		private String month;
	}

}
