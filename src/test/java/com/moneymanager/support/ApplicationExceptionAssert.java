package com.moneymanager.support;

import com.moneymanager.global.enums.ErrorCode;
import com.moneymanager.global.exception.ApplicationException;
import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager<br>
 * 파일이름       : ApplicationExceptionAssert<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-03-22<br>
 * 역할             : BusinessException 예외 전용 테스트 클래스
 * </p>
 */
public class ApplicationExceptionAssert extends AbstractAssert<ApplicationExceptionAssert, ApplicationException> {

	public ApplicationExceptionAssert(ApplicationException actual) {
		super(actual, ApplicationExceptionAssert.class);
	}

	public static ApplicationExceptionAssert assertThatApplicationException(Throwable actual) {
		return new ApplicationExceptionAssert((ApplicationException) actual);
	}

	public ApplicationExceptionAssert hasErrorCode(ErrorCode errorCode) {
		isNotNull();

		assertThat(actual.getErrorCode()).isEqualTo(errorCode);

		return this;
	}

	public ApplicationExceptionAssert hasMessageKey(String key) {
		isNotNull();

		assertThat(actual.getMessageKey()).isEqualTo(key);

		return this;
	}

}
