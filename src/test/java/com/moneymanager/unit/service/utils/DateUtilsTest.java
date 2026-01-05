package com.moneymanager.unit.service.utils;

import com.moneymanager.utils.DateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.utils<br>
 * 파일이름       : DateUtilsTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 5<br>
 * 설명              : DateUtils 클래스 로직을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 1. 5.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class DateUtilsTest {

	//==================[ 📌getListByYearRange  ]==================
	@ParameterizedTest(name = "[{index}] start={0}, end={1} -> size={2}")
	@CsvSource({
			"2020, 2025, 6",
			"1990, 2000, 11"
	})
	@DisplayName("연도 start와 end가 모두 1 이상이면 정상 동작한다.")
	void 연도가_정상_범위이면_리스트를_반환(int start, int end, int expected){
		//when
		List<Integer> result = DateUtils.getListByYearRange(start, end);

		//then
		assertThat(result)
				.startsWith(start)
				.endsWith(end)
				.hasSize(expected);
	}

	@Test
	@DisplayName("연도 start와 end가 같아도 정상 동작한다.")
	void 연도의_시작과_종료가_같아도_리스트를_반환(){
		//given
		int start = 2020;
		int end = 2020;

		//when
		List<Integer> result = DateUtils.getListByYearRange(start, end);

		//then
		assertThat(result)
				.startsWith(start)
				.endsWith(end)
				.hasSize(1);
	}

	@ParameterizedTest(name = "[{index}] start={0}, end={1}")
	@CsvSource({
			"0, 2020",
			"-1, 1900",
			"1850, -1",
			"2024, 0",
			"0, 0",
			"-1, 0"
	})
	@DisplayName("연도 중 0 이하가 있으면 IllegalArgumentException이 발생한다.")
	void 연도가_0이하면_예외발생(int start, int end){
		//when & then
		assertThatIllegalArgumentException()
				.isThrownBy(() -> DateUtils.getListByYearRange(start, end))
				.withMessageContainingAll("연도", "0보다", String.valueOf(start));
	}

	@Test
	@DisplayName("연도 start가 end보다 크면 IllegalArgumentException이 발생한다.")
	void 연도의_시작이_종료보다_크면_예외발생(){
		//given
		int end = 2000;
		int start = end + 1;

		//when & then
		assertThatIllegalArgumentException()
				.isThrownBy(() -> DateUtils.getListByYearRange(start, end))
				.withMessageContainingAll("시작연도", "큽니다", String.valueOf(start));
	}


	//==================[ 📌getListByMonthRange  ]==================
	@ParameterizedTest(name = "start={0}, end={1} -> size={2}")
	@CsvSource({
			"1, 12, 12",
			"4, 7, 4",
			"11, 12, 2"
	})
	@DisplayName("월 start와 end가 모두 1~12 범위 내이면 정상 동작한다.")
	void 월이_정상_범위이면_리스트를_반환(int start, int end, int expected){
		//when
		List<Integer> result = DateUtils.getListByMonthRange(start, end);

		//then
		assertThat(result)
				.startsWith(start)
				.endsWith(end)
				.hasSize(expected);

	}

	@ParameterizedTest(name = "[{index}] start={0}, end={1}")
	@CsvSource({
			"1, 1",
			"6, 6",
			"12, 12"
	})
	@DisplayName("월 start와 end가 같아도 정상 동작한다.")
	void 월의_시작과_종료가_같아도_리스트_반환(int start, int end){
		//when
		List<Integer> result = DateUtils.getListByMonthRange(start, end);

		//then
		assertThat(result)
				.startsWith(start)
				.endsWith(end)
				.hasSize(1);
	}

	@ParameterizedTest(name = "[{index}] start={0}, end={1}")
	@CsvSource({
			"0, 10",
			"-1, 6",
			"1, 0",
			"11, -1",
			"0, 0",
			"-1, 0",
			"0, -1",
			"-1, -1"
	})
	@DisplayName("월 start와 end 중 1~12 범위 밖이면 IllegalArgumentException이 발생한다.")
	void 월이_1과_12사이_아니면_예외발생(int start, int end){
		//when & then
		assertThatIllegalArgumentException()
				.isThrownBy(() -> DateUtils.getListByMonthRange(start, end))
				.withMessageContainingAll("월은", "사이여야");
	}

	@ParameterizedTest(name = "[{index}] end={0}")
	@ValueSource(ints= {1, 6, 11})
	@DisplayName("월 start가 end보다 크면 IllegalArgumentException이 발생한다.")
	void 월_시작이_종료보다_크면_예외발생(int end){
		//given
		int start = end + 1;

		//when & then
		assertThatIllegalArgumentException()
				.isThrownBy(() -> DateUtils.getListByMonthRange(start, end))
				.withMessageContainingAll("보다", "큽니다");
	}


	//==================[ 📌getListByDayRange  ]==================
	@ParameterizedTest(name = "[{index}] start={0}, end={1}")
	@MethodSource("validDayRange")
	@DisplayName("일 start와 end가 모두 1~31 범위 내이면 정상 동작한다.")
	void 일이_정상_범위면_리스트를_반환(int start, int end){
		//when
		List<Integer> result = DateUtils.getListByDayRange(start, end);

		//then
		assertThat(result)
				.startsWith(start)
				.endsWith(end)
				.hasSize(end - start + 1);
	}

	static Stream<Arguments> validDayRange(){
		return Stream.of(
			Arguments.of(1, 1),
			Arguments.of(1, 30),
			Arguments.of(1, 31),
			Arguments.of(5, 5),
			Arguments.of(10, 20)
		);
	}

	@ParameterizedTest(name = "[{index}] start={0}, end={1}")
	@MethodSource("validDayOutRange")
	@DisplayName("일 start와 end 중 1~31 범위 밖이면 IllegalArgumentException이 발생한다.")
	void 일이_정상_범위를_벗어나면_예외발생(int start, int end){
		//when & then
		assertThatIllegalArgumentException()
				.isThrownBy(() -> DateUtils.getListByDayRange(start, end))
				.withMessageContainingAll("사이");
	}

	static Stream<Arguments> validDayOutRange(){
		return Stream.of(
				Arguments.of(0, 10),
				Arguments.of(1, 32),
				Arguments.of(-1, 5),
				Arguments.of(10, 40)
		);
	}

	@ParameterizedTest(name = "[{index}] start={0}, end={1}")
	@MethodSource("validDaySize")
	@DisplayName("일 start가 end보다 크면 IllegalArgumentException이 발생한다.")
	void 시작일이_종료일보다_크면_예외발생(int start, int end){
		//when & then
		assertThatIllegalArgumentException()
				.isThrownBy(() -> DateUtils.getListByDayRange(start, end))
				.withMessageContainingAll("보다");
	}

	static Stream<Arguments> validDaySize(){
		return IntStream.rangeClosed(1, 31)
				.filter( day -> day != 31)
				.mapToObj(day -> Arguments.of( day+1, day ));
	}
}
