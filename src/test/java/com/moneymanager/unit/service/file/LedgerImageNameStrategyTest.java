package com.moneymanager.unit.service.file;

import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ErrorCode;
import com.moneymanager.exception.error.ErrorInfo;
import com.moneymanager.service.file.LedgerImageNameStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.file<br>
 * 파일이름       : LedgerImageNameStrategyTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 19<br>
 * 설명              :  LedgerImageNameStrategy 클래스 로직을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 3. 19</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class LedgerImageNameStrategyTest {

	private final LedgerImageNameStrategy strategy = new LedgerImageNameStrategy();

	//==================[ TEST ]==================
	@Test
	@DisplayName("파일명이 정상적이면 ULID로 이름이 변경 가능하다.")
	void generateStoredName_Success() {
		//given
		String originalFileName = "image.png";

		//when
		String result = strategy.generateStoredName(originalFileName);

		//then
		assertThat(result)
				.isNotNull()
				.endsWith(".png");

		String name = result.split("\\.")[0];
		assertThat(name.length()).isEqualTo(26);
	}

	@ParameterizedTest(name = "[{index}] fileName={0}")
	@NullAndEmptySource
	@DisplayName("파일명이 null, 공백이면 예외가 발생한다.")
	void generateStoredName_Failure_NameIsNullAndEmpty(String name) {
		//when & then
		assertThatExceptionOfType(BusinessException.class)
				.isThrownBy(() -> strategy.generateStoredName(name))
				.satisfies(e -> {
					ErrorInfo errorDTO = e.getErrorInfo();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.FILE_TARGET_INVALID);
					assertThat(errorDTO.getLogMessage()).contains("파일 이름 없음");
				});
	}

	@ParameterizedTest(name = "[{index}] name={0}")
	@ValueSource(strings = {"test", "test."})
	@DisplayName("파일명에 확장자가 없으면 예외가 발생한다.")
	void generateStoredName_Failure_ExtensionIsNone(String name) {
		//when & then
		assertThatExceptionOfType(BusinessException.class)
				.isThrownBy(() -> strategy.generateStoredName(name))
				.satisfies(e -> {
					ErrorInfo errorDTO = e.getErrorInfo();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.FILE_TARGET_INVALID);
					assertThat(errorDTO.getLogMessage()).contains("형식오류", name);
				});
	}

	@ParameterizedTest(name = "[{index}] date=오늘 날짜")
	@MethodSource("validateRelativePath")
	@DisplayName("오늘 날짜 기준으로 상대경로를 생성한다.")
	void generateRelativePath_Success(String expected) {
		//when
		String result = strategy.generateRelativePath();

		//then
		assertThat(result).isEqualTo(expected);
	}


	//==================[ Method Source ]==================
	static Stream<String> validateRelativePath() {
		LocalDate today = LocalDate.now();

		String expected = String.join(
				File.separator,
				String.valueOf(today.getYear()), String.format("%02d", today.getMonthValue()));

		return Stream.of(expected);
	}
}
