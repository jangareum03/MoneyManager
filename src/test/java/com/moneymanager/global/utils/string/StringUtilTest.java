package com.moneymanager.global.utils.string;

import com.moneymanager.global.util.string.StringUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.common.utils.string<br>
 * 파일이름       : StringUtilTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 9<br>
 * 설명              : StringUtil 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 7. 9</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class StringUtilTest {

	@Nested
	@DisplayName("문자열 유효섬 검사")
	class StringValidation {

		@ParameterizedTest
		@NullAndEmptySource
		@MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
		@DisplayName("문자열이 비어있으면 true가 반환힌다.")
		void returnsTrue_whenDateIsInvalid(String value) {
			//when: 문자열이 공백인지 확인한다.
			boolean result = StringUtil.isNullOrBlank(value);
			
			//then: true를 반환된다.
			assertThat(result).isTrue();
		}
		
		@Test
		@DisplayName("문자열이 비어있지 않으면 false이 반환한다.")
		void returnsFalse_whenStringIsNotEmpty() {
			//given: 비어있지 않은 문자열이 준비되어 있다.
			String value = "text";
			
			//when: 문자열이 공백인지 확인한다.
			boolean result = StringUtil.isNullOrBlank(value);
			
			//then: false을 반환된다.
			assertThat(result).isFalse();
		}

	}


	@Nested
	@DisplayName("정규식 검증")
	class PatternValidation {

		@ParameterizedTest
		@MethodSource("validPatterns")
		@DisplayName("문자열이 정규식 패턴과 일치하면 true가 반환한다.")
		void returnsTrue_whenRequestIsValid(String value, String pattern) {
			//when: 문자열이 정규식 패턴과 일치하는지 확인한다.
			boolean result = StringUtil.matchesPattern(value, pattern);
			
			//then: true가 반환된다.
			assertThat(result).isTrue();
		}

		static Stream<Arguments> validPatterns() {
			return Stream.of(
					Arguments.of("12345", "\\d+"),
					Arguments.of("abcde", "[a-z]+"),
					Arguments.of("한글123", "[가-힣0-9]+")
			);
		}

		@ParameterizedTest
		@MethodSource("invalidPatterns")
		@DisplayName("문자열이 정규식 패턴과 불일치하면 false이 반환한다.")
		void returnsFalse_whenRequestIsInvalid(String value, String pattern) {
			//when: 문자열이 정규식 패턴과 일치하는지 확인한다.
			boolean result = StringUtil.matchesPattern(value, pattern);

			//then: false이 반환된다.
			assertThat(result).isFalse();
		}

		static Stream<Arguments> invalidPatterns() {
			return Stream.of(
					Arguments.of("한글", "\\d+"),
					Arguments.of("AB123", "[a-z]+"),
					Arguments.of("abc", "[가-힣0-9]+")
			);
		}

		@ParameterizedTest
		@NullAndEmptySource
		@MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
		@DisplayName("문자열이 비어있으면 false을 반환한다.")
		void returnsFalse_whenTextIsInvalid(String value) {
			//given: 정상적인 정규식 패턴이 준비되어 있다.
			String pattern = "\\d+";

			//when: 문자열이 정규식 패턴과 일치하는지 확인한다.
			boolean result = StringUtil.matchesPattern(value, pattern);
			
			//then: false을 반환한다.
			assertThat(result).isFalse();
		}

		@ParameterizedTest
		@NullAndEmptySource
		@MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
		@DisplayName("패턴이 비어있으면 false을 반환한다.")
		void returnsFalse_whenPatternIsInvalid(String pattern) {
			//given: 정상적인 문자열이 준비되어 있다.
			String value = "text";

			//when: 문자열이 정규식 패턴과 일치하는지 확인한다.
			boolean result = StringUtil.matchesPattern(value, pattern);

			//then: false을 반환한다.
			assertThat(result).isFalse();
		}

	}

}