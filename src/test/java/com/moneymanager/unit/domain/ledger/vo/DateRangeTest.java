package com.moneymanager.unit.domain.ledger.vo;

import com.moneymanager.domain.global.vo.DateRange;
import com.moneymanager.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.domain.ledger.vo<br>
 * 파일이름       : DateRangeTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 30<br>
 * 설명              : DateRange 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 3. 30</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class DateRangeTest {

	//==================[ TEST ]==================
	@Test
	@DisplayName("정상적인 시작일과 종료일이면 DateRange 생성된다.")
	void create_Success() {
		//given
		String from = "20260101";
		String to = "20260131";

		//when
		DateRange result = new DateRange(from, to);

		//then
		assertThat(result.getFrom()).isEqualTo(LocalDate.of(2026,1,1));
		assertThat(result.getTo()).isEqualTo(LocalDate.of(2026,1,31));
	}

	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("invalidCreateCases")
	@DisplayName("비정상적인 날짜면 예외가 발생한다.")
	void create_Failure_Exception(String description, String from, String to){
		//when & then
		assertThatExceptionOfType(BusinessException.class)
				.isThrownBy(() -> new DateRange(from, to));
	}


	@ParameterizedTest(name = "[{index}] from={0}, end={1}")
	@MethodSource("validBetweenCases")
	@DisplayName("시작일과 종료일 사이의 일자를 계산할 수 있다.")
	void daysBetween_Success(String from, String to, long expectedValue) {
		//given
		DateRange dateRange = new DateRange(from, to);

		//when
		long result = dateRange.daysBetween();

		//then
		assertThat(result).isEqualTo(expectedValue);
	}


	//==================[ Method Source ]==================
	static Stream<Arguments> invalidCreateCases() {
		return Stream.of(
			Arguments.of(
					"시작일이 없는 경우",
					null,
					"20260101"
			),
				Arguments.of(
						"종료일이 없는 경우",
						"20260101",
						null
				),
				Arguments.of(
						"시작일 > 종료일",
						"20260101",
						"20250101"
				)
		);
	}

	static Stream<Arguments> validBetweenCases() {
		return Stream.of(
				Arguments.of(
						"20260101",
						"20260131",
						30
				),
				Arguments.of(
						"20260301",
						"20260308",
						7
				),
				Arguments.of(
						"20260101",
						"20260101",
						0
				)
		);
	}
}
