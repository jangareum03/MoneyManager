package com.moneymanager.support;

import com.moneymanager.global.exception.code.ErrorCode;
import com.moneymanager.global.exception.exception.ApplicationException;
import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager<br>
 * 파일이름       : ApplicationExceptionAssert<br>
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
public class ApplicationExceptionAssert extends AbstractAssert<ApplicationExceptionAssert, ApplicationException> {

	public ApplicationExceptionAssert(ApplicationException actual) {
		super(actual, ApplicationExceptionAssert.class);
	}

	public static ApplicationExceptionAssert assertThatApplicationException(Throwable actual) {
		assertThat(actual).isInstanceOf(ApplicationException.class);

		return new ApplicationExceptionAssert((ApplicationException) actual);
	}

	public ApplicationExceptionAssert hasErrorCode(ErrorCode errorCode) {
		isNotNull();

		assertThat(actual.getErrorCode()).isEqualTo(errorCode);

		return this;
	}

	public ApplicationExceptionAssert hasUserMessage(String... values) {
		isNotNull();

		assertThat(actual.getUserMessage()).contains(values);

		return this;
	}

	public ApplicationExceptionAssert hasWork(String work) {
		isNotNull();

		assertThat(actual.getDeveloperLog().getWork()).isEqualTo(work);

		return this;
	}

	public ApplicationExceptionAssert hasCauseMessage(String cause) {
		isNotNull();

		assertThat(actual.getDeveloperLog().getCause()).isEqualTo(cause);

		return this;
	}

	public ApplicationExceptionAssert hasTarget(Class<?> target) {
		isNotNull();

		assertThat(actual.getDeveloperLog().getTarget()).isEqualTo(target);

		return this;
	}

	public ApplicationExceptionAssert hasField(String field) {
		isNotNull();

		assertThat(actual.getDeveloperLog().getField()).contains(field);

		return this;
	}

	public ApplicationExceptionAssert hasValue(Object... value) {
		isNotNull();

		if(value == null) {
			assertThat(actual.getDeveloperLog().getValue()).isNull();
		}else {
			for(Object v : value) {
				assertThat(actual.getDeveloperLog().getValue()).contains(String.valueOf(v));
			}
		}

		return this;
	}

	public ApplicationExceptionAssert hasOption(Object key, Object value) {
		isNotNull();

		assertThat(actual.getDeveloperLog().getOptions()).containsKeys(String.valueOf(key));
		assertThat(actual.getDeveloperLog().getOptions().get(key))
				.asString()
				.contains(String.valueOf(value));

		return this;
	}

}
