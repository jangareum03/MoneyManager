package com.moneymanager.global.utils.string;

import com.moneymanager.global.util.string.StringUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
	@DisplayName("문자 마스킹할 때")
	class Masking {
		
		@Nested
		@DisplayName("성공")
		class Success {
		
			@ParameterizedTest
			@MethodSource("validMasking")
			@DisplayName("정상적인 인자값이면 마스킹된 문자를 반환한다.")
			void returnsMaskedString_whenValueIsValid(String text, int startIndex, int maskLength, String expectedMask) {
				//when
				String result = StringUtil.masking(text, startIndex, maskLength);
				
				//then
				assertThat(result).isEqualTo(expectedMask);
			}

			static Stream<Arguments> validMasking() {
				String text = "도시락";

				return Stream.of(
						Arguments.of(text, 0, 1, "*시락"),		//첫문자
						Arguments.of(text, 1, 1, "도*락"),			//중간문자
						Arguments.of(text, 2, 1, "도시*"),			//마지막 문자
						Arguments.of(text, 0, 2, "**락"),		//앞 2개
						Arguments.of(text, 1, 2, "도**"),			//뒤 2개
						Arguments.of(text, 0, 3, "***")			//전체
				);
			}
			
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
			@DisplayName("문자가 null이거나 비어있으면 예외를 발생시킨다.")
			void throwsIllegalArgumentException_whenStringIsNullOrEmpty(String text) {
				//when
				assertThatThrownBy(() -> StringUtil.masking(text, 0, 1))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("마스킹할 문자 누락");
			}
			
			@ParameterizedTest
			@ValueSource(ints = {-1, -2, -3})
			@DisplayName("시작 인덱스가 음수면 예외를 발생시킨다.")
			void throwsIllegalArgumentException_whenStartIndexIsNegative(int startIndex) {
				//given
				String text = "빠른병원";
				
				//when
				assertThatThrownBy(() -> StringUtil.masking(text, startIndex, 1))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("시작 위치 음수");
			}

			@ParameterizedTest
			@ValueSource(ints = {0, -1, -2, -3})
			@DisplayName("마스킹할 길이가 0이하면 예외를 발생시킨다.")
			void throwsIllegalArgumentException_whenMaskingLengthIsZeroOrLess(int maskLength) {
				//given
				String text = "빠른병원";

				//when
				assertThatThrownBy(() -> StringUtil.masking(text, 0, maskLength))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("마스킹할 길이 0 이하");
			}

			@ParameterizedTest
			@ValueSource(ints = {5, 6})
			@DisplayName("시작 인덱스가 문자 길이보다 길면 예외를 발생시킨다.")
			void throwsIllegalArgumentException_whenStartIndexIsGreaterThanStringLength(int startIndex) {
				//given
				String text = "빠른병원";

				//when
				assertThatThrownBy(() -> StringUtil.masking(text, startIndex, 1))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("시작 위치가 문자길이 초과");
			}
			
			@ParameterizedTest
			@MethodSource("invalidMasking")
			@DisplayName("시작 인덱스와 마스킹할 길이의 합이 문자 길이보다 길면 예외를 발생시킨다.")
			void throwsIllegalArgumentException_whenSumOfIndexAndLengthIsGreaterThanStringLength(int startIndex, int maskLength) {
				//given
				String text = "빠른병원";

				//when
				assertThatThrownBy(() -> StringUtil.masking(text, startIndex, maskLength))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("마스킹할 길이가 문자길이 초과");
			}

			static Stream<Arguments> invalidMasking() {
				return Stream.of(
						Arguments.of(0, 5),
						Arguments.of(1, 5),
						Arguments.of(4, 1),
						Arguments.of(4, 2)
				);
			}

		}
	}

}