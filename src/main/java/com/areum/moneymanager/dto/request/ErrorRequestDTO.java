package com.areum.moneymanager.dto.request;


import lombok.Builder;
import lombok.Getter;


/**
 * 로그를 작성할 때 필요한 객체입니다. <br>
 * <b color='white'>key</b>는 식별번호(ex. 회원번호, 가계부 번호 등),
 * <b color='white'>requestBody</b>는 요청값입니다.
 */
@Builder
@Getter
public class ErrorRequestDTO {

	Object key;
	Object requestBody;


}
