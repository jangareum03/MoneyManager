package com.moneymanager.unit.domain.ledger.entity;

import com.moneymanager.BusinessExceptionAssert;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.AmountType;
import com.moneymanager.domain.ledger.enums.FixCycle;
import com.moneymanager.domain.ledger.enums.FixedYN;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.moneymanager.exception.error.ErrorCode.*;
import static com.moneymanager.unit.fixture.LedgerRequestFixture.defaultLedgerWriteRequest;
import static com.moneymanager.unit.fixture.LedgerRequestFixture.withPlace;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.domain.ledger.entity<br>
 * 파일이름       : LedgerTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 14<br>
 * 설명              : Ledger 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 3. 14</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class LedgerTest {

	//==================[ TEST ]==================
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("provideValidLedgers")
	@DisplayName("정상적으로 Ledger 객체가 생성된다.")
	void createLedger_success(String description, LedgerWriteRequest request, Consumer<Ledger> validator) {
		//when
		Ledger ledger = Ledger.create("member", request);

		//then
		validator.accept(ledger);
	}

	static Stream<Arguments> provideValidLedgers() {
		return Stream.of(
				Arguments.of(
						"기본 Ledger",
						defaultLedgerWriteRequest().build(),
						(Consumer<Ledger>) ledger -> {
							assertThat(ledger)
									.extracting(
											Ledger::getCode,
											Ledger::getMemberId,
											Ledger::getDate,
											Ledger::getCategory,
											Ledger::getAmount
									).isNotNull();

							assertThat(ledger.getFix()).isSameAs(FixedYN.VARIABLE);
							assertThat(ledger.getFixCycle()).isNull();

							assertThat(ledger.getAmountType()).isSameAs(AmountType.NONE);
						}
				),
				Arguments.of(
						"고정주기가 있는 Ledger",
						defaultLedgerWriteRequest()
								.fixed(true)
								.fixCycle("y")
								.build(),
						(Consumer<Ledger>) ledger  -> {
							assertThat(ledger.getFix()).isSameAs(FixedYN.REPEAT);
							assertThat(ledger.getFixCycle()).isSameAs(FixCycle.YEARLY);
						}
				),
				Arguments.of(
						"메모가 있는 Ledger",
						defaultLedgerWriteRequest()
								.memo("넷플릭스 구독료")
								.build(),
						(Consumer<Ledger>) ledger  -> {
							assertThat(ledger.getMemo())
									.isNotNull()
									.contains("구독료");
						}
				),
				Arguments.of(
						"장소가 있는 Ledger",
						withPlace()
								.build(),
						(Consumer<Ledger>) ledger  -> {
							assertThat(ledger.getPlace())
									.isNotNull()
									.satisfies(
											p -> {
												assertThat(p.getName()).isEqualTo("CGV 강남점");
												assertThat(p.getRoadAddress()).isEqualTo("서울특별시 강남구 강남대로 438 스타플렉스");
												assertThat(p.getDetailAddress()).isEqualTo("4층");
											}
									);
						}
				)
		);
	}


	@ParameterizedTest(name = "[{index}] date={0}")
	@MethodSource("provideValidTransactionDates")
	@DisplayName("거래날짜가 5년 이내라면 객체가 생성된다.")
	void validateDate_success_boundaryValue(String date){
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.date(date)
						.build();

		//when
		Ledger ledger = Ledger.create(anyString(), request);

		//then
		assertThat(ledger.getDate()).isEqualTo(date);
	}

	static Stream<String> provideValidTransactionDates() {
		LocalDate today = LocalDate.now();

		return Stream.of(
				today,
				today.minusYears(3).minusMonths(2).minusDays(10),
				today.minusYears(5)
		).map(d -> d.format(DateTimeFormatter.BASIC_ISO_DATE));
	}


	@ParameterizedTest(name = "[{index}] date={0}")
	@MethodSource("provideInvalidTransactionDates")
	@DisplayName("거래날짜가 5년전 보다 과거거나 미래면 예외가 발생한다.")
	void validateDate_failure_pastAndFuture(String date){
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
					.date(date)
						.build();

		//when
		Throwable throwable = catchThrowable(() -> Ledger.create(anyString(), request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_RANGE)
						.hasUserMessage("날짜만 가능")
						.hasLogMessage("범위오류", "range=", date);
	}

	static Stream<String> provideInvalidTransactionDates() {
		LocalDate today = LocalDate.now();

		return Stream.of(
				today.minusYears(6),		//6년전
				today.minusYears(5).minusDays(1),	//5년전 + 하루 더 과거
				today.plusDays(1)		//다음날
		).map(d -> d.format(DateTimeFormatter.BASIC_ISO_DATE));
	}


	@ParameterizedTest(name = "[{index}] fix=false, cycle={0}")
	@EnumSource(FixCycle.class)
	@DisplayName("고정이 아닌데 주기가 있으면 예외가 발생한다.")
	void validateFixCycle_failure_mismatch(FixCycle fixCycle) {
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.fixed(false)
						.fixCycle(fixCycle.getValue())
							.build();

		//when
		Throwable throwable = catchThrowable(() -> Ledger.create(anyString(), request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_POLICY_NOT_ALLOWED)
				.hasUserMessage("주기를 설정", "고정 여부")
				.hasLogMessage("정책위반", "detail=", fixCycle.getValue());
	}


	@ParameterizedTest(name = "[{index}] cycle={0}")
	@ValueSource(strings = {"Q", "r", "f"})
	@DisplayName("고정주기가 유효하지 않으면 예외가 발생한다.")
	void validateFixCycle_failure_outOfRange(String cycle){
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.fixed(true)
						.fixCycle(cycle)
							.build();

		//when
		Throwable throwable = catchThrowable(() -> Ledger.create(anyString(), request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_INVALID)
				.hasUserMessage("고정주기")
				.hasLogMessage("허용값아님", "allowed=", cycle);
	}


	@ParameterizedTest(name = "[{index}] category={0}")
	@ValueSource(strings = {"000000", "110101", "030101"})
	@DisplayName("카테고리가 01, 02로 시작하지 않으면 예외가 발생한다.")
	void validateDate_failure_boundaryValue(String category){
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.categoryCode(category)
								.build();

		//when
		Throwable throwable = catchThrowable(() -> Ledger.create(anyString(), request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_FORMAT)
				.hasUserMessage("없는 카테고리")
				.hasLogMessage("형식오류", "format=", category);
	}


	@ParameterizedTest(name = "[{index}] amount={0}")
	@ValueSource(longs = {-1000, -1, 0})
	@DisplayName("금액이 0이하면 예외가 발생한다.")
	void validateAmount_failure_boundaryValue(long amount){
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.amount(amount)
								.build();

		//when
		Throwable throwable = catchThrowable(() -> Ledger.create(anyString(), request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_RANGE)
				.hasUserMessage("1 이상")
				.hasLogMessage("범위오류", "range=", String.valueOf(amount));
	}


	@ParameterizedTest(name = "[{index}] type={0}")
	@ValueSource(strings = {"1","ca", "free", "bank#", "n0ne"})
	@DisplayName("금액유형이 유효하지 않으면 예외가 발생한다.")
	void validateAmountType_failure_outOfRange(String type){
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.amountType(type)
							.build();

		//when
		Throwable throwable = catchThrowable(() -> Ledger.create(anyString(), request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_INVALID)
				.hasUserMessage("금액유형")
				.hasLogMessage("허용값아님", "allowed=", type);
	}


	@ParameterizedTest(name = "[{index}] place={0}, address={1}")
	@MethodSource("provideValidPlaces")
	@DisplayName("장소명과 기본주소 둘 다 없으면 통과한다.")
	void validatePlace_success_nullAndEmpty(String place, String address) {
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.placeName(place)
						.roadAddress(address)
							.build();

		//when & then
		assertThatCode(() -> Ledger.create(anyString(), request))
				.doesNotThrowAnyException();
	}

	static Stream<Arguments> provideValidPlaces() {
		return Stream.of(
				Arguments.of(
						null, null
				),
				Arguments.of(
						null, ""
				),
				Arguments.of(
						"", null
				),
				Arguments.of(
						"", ""
				)
		);
	}


	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("provideInvalidPlaces")
	@DisplayName("장소명과 기본주소 둘 중 하나라도 없으면 예외가 발생한다.")
	void validatePlace_failure_mismatch(String description, String place, String address) {
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.placeName(place)
						.roadAddress(address)
							.build();

		//when
		Throwable throwable = catchThrowable(() -> Ledger.create(anyString(), request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_POLICY_NOT_ALLOWED)
				.hasUserMessage("장소명", "기본주소")
				.hasLogMessage("정책위반", "detail=");
	}

	static Stream<Arguments> provideInvalidPlaces() {
		return Stream.of(
				Arguments.of(
						"장소명 null",
						null, "기본주소"
				),
				Arguments.of(
						"장소명 공백",
						"", "기본주소"
				),
				Arguments.of(
						"기본주소 null",
						"장소", null
				),
				Arguments.of(
						"기본주소 공백",
						"장소", ""
				)
		);
	}


	@ParameterizedTest(name = "[{index}] address={0}")
	@ValueSource(strings = {"상세 주소!!@", "상세☆", "상세家"})
	@DisplayName("상세 주소에 한글, 숫자, 공백, 영문, 일부 특수문자 제외한 문자가 포함되면 예외가 발생한다.")
	void validateDetail_failure_format(String address){
		//given
		LedgerWriteRequest request =
				withPlace()
						.detailAddress(address)
							.build();

		//when
		Throwable throwable = catchThrowable(() -> Ledger.create(anyString(), request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_FORMAT)
				.hasUserMessage("상세 주소", "입력 가능")
				.hasLogMessage("형식오류", "format=");
	}
}
