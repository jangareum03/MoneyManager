package com.moneymanager.unit.service.ledger;

import com.moneymanager.domain.ledger.dto.response.CategoryResponse;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep1Response;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep2Response;
import com.moneymanager.domain.ledger.enums.CategoryLevel;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.ledger.vo.LedgerRuleVO;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.ledger.CategoryReadService;
import com.moneymanager.service.ledger.LedgerReadService;
import com.moneymanager.service.member.MemberReadService;
import com.moneymanager.utils.DateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
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

	@InjectMocks
	@Spy		private LedgerReadService service;

	@Mock	private CategoryReadService categoryReadService;
	@Mock	private SecurityUtil securityUtil;
	@Mock	private MemberReadService memberReadService;


	//==================[ 📌getWriteStep1Data  ]==================
	@ParameterizedTest(name = "[{index}] year={0}, month={1}, day={2}")
	@MethodSource("validNowDate")
	@DisplayName("가계부 초기 작성 데이터 조회 시 현재 기준 연/월/일 정보를 반환한다.")
	void 가계부_초기작성에_필요한_데이터_반환(int year, int month, int day){
		//given
		Clock clock = Clock.fixed(
				LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant(),
				ZoneId.systemDefault()
		);

		LedgerReadService ledgerReadService = new LedgerReadService(clock, securityUtil, categoryReadService, memberReadService);

		int minYear = year - LedgerRuleVO.instance.getMAX_YEAR_RANGE();

		//when
		LedgerWriteStep1Response result = ledgerReadService.getWriteStep1Data();

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

		LedgerReadService ledgerReadService = new LedgerReadService(clock, securityUtil, categoryReadService, memberReadService);

		try(MockedStatic<DateUtils> mocked = mockStatic(DateUtils.class)) {
			mocked.when(() -> DateUtils.getYearsInRange(anyInt(), anyInt()))
					.thenThrow(new IllegalArgumentException("날짜 문제"));

			//when & then
			assertThatExceptionOfType(RuntimeException.class)
					.isThrownBy(ledgerReadService::getWriteStep1Data);
		}
	}


	//==================[ 📌getWriteStep2Data  ]==================
	@ParameterizedTest(name = "[{index}] type={0}")
	@EnumSource(LedgerType.class)
	@DisplayName("가계부 유형 type에 따라 응답데이터가 반환된다.")
	void 정상_흐름이면_작성_2단계_데이터_반환(LedgerType type){
		//given
		LocalDate date = LocalDate.of(2025, 8, 1);
		List<CategoryResponse> mockCategories = List.of(
				CategoryResponse.builder().code("010100").name("수입1").build(),
				CategoryResponse.builder().code("010200").name("수입2").build(),
				CategoryResponse.builder().code("010300").name("수입3").build()
		);
		List<Boolean> mockSlot = List.of(false, false, false);

		when(categoryReadService.getCategoriesByTypeAndLevel(type, CategoryLevel.MIDDLE))
				.thenReturn(mockCategories);

		doReturn(mockSlot).when(service).fetchBooleanList();

		//when
		LedgerWriteStep2Response result = service.getWriteStep2Data(type, date);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo("2025년 08월 01일 금요일");
		assertThat(result.getType()).isSameAs(type);
		assertThat(result.getCategories()).isEqualTo(mockCategories);
		assertThat(result.getImageSlot()).isEqualTo(mockSlot);
		assertThat(result.getFixed()).isEqualTo(List.of(FixedYN.values()));
		assertThat(result.getPaymentTypes()).isEqualTo(List.of(PaymentType.values()));
	}


	//==================[ 📌fetchBooleanList  ]==================
	@ParameterizedTest(name = "[{index}] limit={0}, expected={1}")
	@MethodSource("validImageLimit")
	@DisplayName("회원의 등록 가능한 개수별로 Boolean형 리스트를 반환한다.")
	void 정상_흐름이면_리스트_반환(int limit, List<Boolean> expected){
		//given
		String memberId = "ok";

		when(securityUtil.getMemberId()).thenReturn(memberId);
		when(memberReadService.getImageLimit(memberId))
				.thenReturn(limit);

		//when
		List<Boolean> result = service.fetchBooleanList();

		//then
		assertThat(result).isNotEmpty().hasSize(LedgerRuleVO.instance.getMAX_IMAGE_COUNT());
		assertThat(result).isEqualTo(expected);
	}

	static Stream<Arguments> validImageLimit() {
		return Stream.of(
				Arguments.of(0, List.of(false, false, false)),
				Arguments.of(1, List.of(true, false, false)),
				Arguments.of(2, List.of(true, true, false)),
				Arguments.of(3, List.of(true, true, true))
		);
	}

}
