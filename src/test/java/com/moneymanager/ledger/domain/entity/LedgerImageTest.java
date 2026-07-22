package com.moneymanager.ledger.domain.entity;

import com.moneymanager.domain.ledger.entity.LedgerImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import com.moneymanager.support.ApplicationExceptionAssert;

import java.util.stream.Stream;

import static com.moneymanager.exception.code.CommonErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.domain.ledger.entity<br>
 * 파일이름       : LedgerImageTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 5. 29<br>
 * 설명              : LedgerImage 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 5. 29</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class LedgerImageTest {

	@Nested
	@DisplayName("가계부이미지 생성")
	class CreateTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("가계부 이미지를 생성한다.")
			void createsLedgerImage_whenRequestIsValid() {
				//given: 정상적인 가계부ID, 경로, 순서가 준비되어 있다.
				Long ledgerId = 1L;
				String path = "path";
				int order = 1;
				
				//when: 가계부 이미지를 생성한다.
				LedgerImage result = LedgerImage.create(ledgerId, path, order);
				
				//then: 가계부 ID, 경로, 순서가 저장된다.
				assertThat(result.getId()).isNull();

				assertThat(result.getLedgerId()).isEqualTo(ledgerId);
				assertThat(result.getImagePath()).isEqualTo(path);
				assertThat(result.getSortOrder()).isEqualTo(1);
			}

			@Test
			@DisplayName("이미지 경로에 '\\\\'가 포함되면 /로 변환되어 생성한다.")
			void createsLedgerImage_whenPathContainsBackslash() {
				//given: \\가 포함된 이미지 경로가 준비되어 있다.
				String path = "\\경로1\\경로2/이미지.확장자";
				Long ledgerId = 1L;
				int order = 1;
				
				//when: \\가 포함된 경로로 가계부 이미지를 생성한다.
				LedgerImage result = LedgerImage.create(ledgerId, path, order);
				
				//then: 이미지 경로가 /로 변경된다.
				assertThat(result.getImagePath()).isEqualTo("/경로1/경로2/이미지.확장자");
			}
			
			@Test
			@DisplayName("이미지 경로가 /로 시작하지 않으면 /를 추가하여 생성한다.")
			void createsLedgerImage_whenPathDoesNotStartWithSlash() {
				//given: /로 시작하지 않는 이미지 경로가 준비되어 있다.
				String path = "경로1/경로2/이미지.확장자";
				Long ledgerId = 1L;
				int order = 1;

				//when: /로 시작하지 않는 경로로 가계부 이미지를 생성한다.
				LedgerImage result = LedgerImage.create(ledgerId, path, order);
				
				//then: 이미지 경로가 /로 시작된다.
				assertThat(result.getImagePath()).startsWith("/");
			}
			
			@Test
			@DisplayName("이미지 경로가 이미 /로 시작하면 그대로 생성한다.")
			void createsLedgerImage_whenPathStartsWithSlash() {
				//given: /로 시작하는 이미지 경로가 준비되어 있다.
				String path = "/경로/이미지";
				Long ledgerId = 1L;
				int order = 1;
				
				//when: /로 시작하는 이미지 경로로 가계부 이미지를 생성한다.
				LedgerImage result = LedgerImage.create(ledgerId, path, order);
				
				//then: 이미지 경로는 변경되지 않는다.
				assertThat(result.getImagePath()).isEqualTo(path);
			}
			
		}
		
		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@ParameterizedTest
			@NullSource
			@DisplayName("가계부 ID가 null이면 생성에 실패한다.")
			void throwException_whenLedgerIdIsNull(Long id) {
				//when: 가계부 ID가 null인 가계부 이미지를 생성한다.
				Throwable throwable = catchThrowable(() -> LedgerImage.create(id, "최상위/폴더/이미지", 1));

				//then: 가계부 ID 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("가계부 이미지 검증")
						.hasCauseMessage("가계부 ID 없음")
						.hasTarget(LedgerImage.class)
						.hasField("ledgerId")
						.hasValue(null)
						.hasUserMessage("번호는 필수");
			}

			@ParameterizedTest
			@MethodSource("invalidLedgerIds")
			@DisplayName("가계부 ID가 유효하지 않으면 생성에 실패한다.")
			void throwsException_whenLedgerIdIsInvalid(Long id) {
				//when: 유효하지 않은 가계부 ID로 가계부 이미지를 생성한다.
				Throwable throwable = catchThrowable(() -> LedgerImage.create(id, "최상위/폴더/이미지", 1));

				//then: 가계부 ID 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(INVALID_VALUE)
						.hasWork("가계부 이미지 검증")
						.hasCauseMessage("허용 범위 미만")
						.hasTarget(LedgerImage.class)
						.hasField("ledgerId")
						.hasValue(String.valueOf(id))
						.hasUserMessage("않은 가계부 번호");
			}

			static Stream<Arguments> invalidLedgerIds() {
				return Stream.of(
						Arguments.of(Named.of("0인 경우", 0L)),
						Arguments.of(Named.of("음수인 경우", -1L))
				);
			}
			
			@ParameterizedTest
			@NullAndEmptySource
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("이미지 경로가 비어있으면 생성에 실패한다.")
			void throwsException_whenPathIsEmpty(String path) {
				//when: 빈 이미지 경로로 가계부 이미지를 생성한다.
				Throwable throwable = catchThrowable(() -> LedgerImage.create(1L, path, 1));

				//then: 이미지 경로 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(REQUIRED_VALUE)
						.hasWork("가계부 이미지 검증")
						.hasCauseMessage("경로 없음")
						.hasField("imagePath")
						.hasValue(path)
						.hasUserMessage("없는 이미지 경로");
			}
			
			@ParameterizedTest
			@MethodSource("invalidSorts")
			@DisplayName("이미지 순서가 허용 범위를 벗어나면 생성에 실패한다.")
			void throwsException_whenImageOrderIsOutOfRange(int order) {
				//when: 유효하지 않은 이미지 순서로 가계부 이미지를 생성한다.
				Throwable throwable = catchThrowable(() -> LedgerImage.create(1L, "최상위/폴더/이미지", order));

				//then: 이미지 순서 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(OUT_OF_RANGE)
						.hasWork("가계부 이미지 검증")
						.hasCauseMessage("순서 허용 범위 초과")
						.hasField("sortOrder")
						.hasValue(String.valueOf(order))
						.hasUserMessage("않은 정렬 순서");
			}

			static Stream<Arguments> invalidSorts() {
				return Stream.of(
						Arguments.of(Named.of("음수인 경우", -1)),
						Arguments.of(Named.of("0인 경우", 0)),
						Arguments.of(Named.of("4 이상인 경우", 4))
				);
			}

		}

	}

}
