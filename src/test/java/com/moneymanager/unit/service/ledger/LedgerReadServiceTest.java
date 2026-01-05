package com.moneymanager.unit.service.ledger;

import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep1Response;
import com.moneymanager.exception.custom.ServerException;
import com.moneymanager.service.ledger.LedgerReadService;
import com.moneymanager.service.validation.LedgerValidator;
import com.moneymanager.utils.DateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.ledger<br>
 * 파일이름       : LedgerReadServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 5<br>
 * 설명              : LedgerReadService 클래스 로직을 검증하는 테스트 클래스
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
@ExtendWith(MockitoExtension.class)
public class LedgerReadServiceTest {

	@InjectMocks	private LedgerReadService service;


	//==================[ 📌getInitialData  ]==================
	@ParameterizedTest(name = "[{index}] year={0}, month={1}, day={2}")
	@MethodSource("validNowDate")
	@DisplayName("가계부 초기 작성 데이터 조회 시 현재 기준 연/월/일 정보를 반환한다.")
	void 가계부_초기작성에_필요한_데이터_반환(int year, int month, int day){
		//given
		Clock todayClock = Clock.fixed(
				LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant(),
				ZoneId.systemDefault()
		);

		service = new LedgerReadService(todayClock);
		int minYear = year - LedgerValidator.getPAST_YEAR();

		//when
		LedgerWriteStep1Response result = service.getInitialData();

		//then
		assertThat(result.getYears())
				.startsWith(minYear)
				.endsWith(year)
				.hasSize(year - minYear + 1);

		assertThat(result.getMonths())
				.startsWith(1)
				.endsWith(month)
				.hasSize(month);

		assertThat(result.getDays())
				.startsWith(1)
				.endsWith(day)
				.hasSize(day);

		assertThat(result.getDisplayDate()).isEqualTo(String.format("%d년 %02d월 %02d일", year,  month, day));
	}

	static Stream<Arguments> validNowDate(){
		return Stream.of(
				Arguments.of(2026, 1, 1),
				Arguments.of(2026, 1, 31),
				Arguments.of(2025, 6, 30),
				Arguments.of(2025, 10, 15),
				Arguments.of(2025, 12, 31),
				Arguments.of(2023, 2, 28),
				Arguments.of(2024, 2, 29)

		);
	}

	@Test
	@DisplayName("DateUtils 클래스의 getListByXXXRange메서드에서 예외가 발생하면 ServerException으로 변환된다.")
	void DateUtils에서_예외가_발생하면_서버_예외발생(){
		//given
		LocalDate date = LocalDate.of(2025, 1, 1);
		Clock clock = Clock.fixed(
				date.atStartOfDay(ZoneId.systemDefault()).toInstant(),
				ZoneId.systemDefault()
		);

		service = new LedgerReadService(clock);

		try(MockedStatic<DateUtils> mocked = mockStatic(DateUtils.class)) {
			mocked.when(() -> DateUtils.getListByYearRange(anyInt(), anyInt()))
					.thenThrow(new IllegalArgumentException("날짜 문제"));

			//when & then
			assertThatExceptionOfType(ServerException.class)
					.isThrownBy(() -> service.getInitialData());
		}
	}
}
