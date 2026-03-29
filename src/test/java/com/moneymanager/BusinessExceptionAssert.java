package com.moneymanager;

import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ErrorCode;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager<br>
 * 파일이름       : BusinessExceptionAssert<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 22<br>
 * 설명              : BusinessException 예외 전용 테스트 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 * 		<thead>
 * 		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 * 		 	  	<td>날짜</td>
 * 		 	  	<td>작성자</td>
 * 		 	  	<td>변경내용</td>
 * 		 	</tr>
 * 		</thead>
 * 		<tbody>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 3. 22</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class BusinessExceptionAssert extends AbstractAssert<BusinessExceptionAssert, BusinessException> {
	public BusinessExceptionAssert(BusinessException actual) {
		super(actual, BusinessExceptionAssert.class);
	}

	public static BusinessExceptionAssert assertThatBusinessException(Throwable actual) {
		assertThat(actual).isInstanceOf(BusinessException.class);

		return new BusinessExceptionAssert((BusinessException) actual);
	}

	public BusinessExceptionAssert hasErrorCode(ErrorCode errorCode) {
		isNotNull();
		assertThat(actual.getErrorInfo().getErrorCode()).isEqualTo(errorCode);

		return this;
	}

	public BusinessExceptionAssert hasUserMessage(String... values) {
		isNotNull();
		assertThat(actual.getErrorInfo().getUserMessage()).contains(values);

		return this;
	}

	public BusinessExceptionAssert hasLogMessage(String... values) {
		isNotNull();
		assertThat(actual.getErrorInfo().getLogMessage()).contains(values);

		return this;
	}

	public BusinessExceptionAssert hasLogMessageNotContaining(String... values) {
		for(String value : values) {
			assertThat(actual.getErrorInfo().getLogMessage())
					.doesNotContain(values);
		}

		return this;
	}
}
