package com.moneymanager.ledger.domain.entity;

import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.domain.global.enums.DatePatterns;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.FixCycle;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.ledger.vo.Money;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.exception.exception.ValidationException;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import com.moneymanager.support.fixture.request.LedgerWriteRequestFixture;
import com.moneymanager.utils.date.DateTimeUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import com.moneymanager.support.ApplicationExceptionAssert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static com.moneymanager.exception.code.LedgerErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Named.named;

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

	private static final String DATE_FORMAT = "yyyyMMdd";

	@Nested
	@DisplayName("가계부 생성")
	class CreateTest {

		private final String memberId = MemberTestData.MEMBER_ID;

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("필수 정보만 가지고 있는 가계부를 생성한다.")
			void createsLedger_whenOnlyRequiredInfoIsGiven() {
				//given: 필수 정보만 포함된 가계부 생성 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.create();

				//when: 필수 정보만 포함된 가계부를 생성한다.
				Ledger result = Ledger.create(memberId, request);
				
				//then: 생성된 가계부 값이 요청값과 동일하게 가진다.
				assertThat(result.getMemberId()).isEqualTo(memberId);
				assertThat(result.getDate()).isEqualTo(LedgerTestData.LOCAL_DATE);
				assertThat(result.getCategory()).isEqualTo(request.getCategoryCode());

				assertThat(result.getMoney())
						.extracting(Money::getAmount, Money::getPaymentType)
						.containsExactly(request.getAmount(), PaymentType.NONE);

				assertThat(result)
						.extracting(Ledger::getId, Ledger::getCode, Ledger::getCreatedAt)
						.isNotNull();

				assertThat(result)
						.extracting(
								Ledger::getMemo,
								Ledger::getUpdatedAt,
								Ledger::getPlace
						)
						.containsOnlyNulls();
			}

			@ParameterizedTest
			@MethodSource("validDates")
			@DisplayName("거래날짜가 허용 범위내면 가계부를 생성한다.")
			void createsLedger_whenTransactionDateIsInRange(String date) {
				//given: 허용 범위 내의 날짜를 포함한 가계부 생성 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.date(date)
						.build();
				
				//when: 가계부를 생성한다.
				Ledger result = Ledger.create(memberId, request);
				
				//then: 생성된 가계부 날짜가 요청된 날짜와 일치한다.
				assertThat(result.getDate())
						.isEqualTo(LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT)));
			}

			static Stream<Arguments> validDates() {
				LocalDate today = LocalDate.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

				return Stream.of(
						Arguments.of(
								Named.of(
										"5년전 날짜인 경우",
										today.minusYears(5).format(formatter)
								)
						),
						Arguments.of(
								Named.of(
										"허용 범위 내 과거 날짜인 경우",
										today.minusYears(3).minusMonths(2).minusDays(5).format(formatter)
								)
						),
						Arguments.of(
								Named.of(
										"어제 날짜인 경우",
										today.minusDays(1).format(formatter)
								)
						),
						Arguments.of(
								Named.of(
										"오늘 날짜인 경우",
										today.format(formatter)
								)
						)
				);
			}
			
			@ParameterizedTest
			@MethodSource("validRequest")
			@DisplayName("선택항목을 가지고 있는 가계부를 생성한다.")
			void createsLedger_whenOptionalInfoIsGiven(LedgerWriteRequest request) {
				//when: 선택 정보를 포함한 가계부를 생성한다.
				Ledger result = Ledger.create(memberId, request);
				
				//then:
				FixedYN expectedFix = request.getFixed() == null
						? null
						: FixedYN.from(request.getFixed());

				FixCycle expectedCycle = request.getFixCycle() == null
						? null
						: FixCycle.from(request.getFixCycle());

				assertAll(
						() -> assertThat(result.getMemberId()).isEqualTo(memberId),
						() -> assertThat(result.getMemo()).isEqualTo(request.getMemo()),
						() -> {
							if(result.getPlace() == null) {
								assertThat(result.getPlace()).isNull();
								return;
							}

							assertThat(result.getPlace())
									.extracting(Place::getPlaceName, Place::getRoadAddress, Place::getDetailAddress)
									.containsExactly(request.getPlaceName(), request.getRoadAddress(), request.getDetailAddress());
						},
						() -> assertThat(result.getFix()).isEqualTo(expectedFix),
						() -> assertThat(result.getFixCycle()).isEqualTo(expectedCycle)
				);
			}

			static Stream<Arguments> validRequest() {
				return Stream.of(
						Arguments.of(
								Named.of(
										"메모를 가진 요청인 경우",
										LedgerWriteRequestFixture.builder().memo("물").build()
								)
						),
						Arguments.of(
								Named.of(
										"장소를 가진 요청인 경우",
										LedgerWriteRequestFixture.withPlace()
								)
						),
						Arguments.of(
								Named.of(
										"고정주기를 가진 요청인 경우",
										LedgerWriteRequestFixture.builder().fixed(LedgerTestData.FIX_Y).fixCycle("w").build()
								)
						)
				);
			}

			@Test
			@DisplayName("가계부를 생성될 때마다 가계부 코드가 다르다.")
			void validatesLedgerCodeUniqueness_whenLedgersAreCreated() {
				//given: 필수 정보만 포함된 가계부 생성 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.create();
				
				//when: 동일한 요청으로 가계부를 두 번 생성한다.
				Ledger ledger1 = Ledger.create(memberId, request);
				Ledger ledger2 = Ledger.create(memberId, request);

				//then: 생성된 가계부는 서로 다른 가계부 코드를 가진다.
				assertThat(ledger1.getCode()).isNotEqualTo(ledger2.getCode());
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@ParameterizedTest
			@MethodSource("com.moneymanager.data.DateTestData#unsupportedFormats")
			@DisplayName("거래날짜 형식이 yyyyMMdd가 아니면 생성에 실패한다.")
			void throwsException_whenDateFormatIsInvalid(String date) {
				//given: 유효하지 않은 거래날짜 형식을 가진 생성 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.date(date)
						.build();
				
				//when: 잘못된 날짜 형식을 포함한 가계부를 생성한다.
				Throwable throwable = catchThrowable(() -> Ledger.create(memberId, request));
				
				//then: 날짜 형식 검증에 대한 예외가 발생한다.
				assertThat(throwable).isInstanceOf(ValidationException.class);
			}
			
			@ParameterizedTest
			@MethodSource("invalidDateRanges")
			@DisplayName("거래 날짜가 등록 가능한 범위를 벗어나면 생성에 실패한다.")
			void throwsException_whenTransactionDateIsOutOfRange(String date) {
				//given: 유효하지 않은 거래날짜를 포함한 생성 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.date(date)
						.build();
				
				//when: 유효하지 않은 거래날짜를 포함한 가계부를 생성한다.
				Throwable throwable = catchThrowable(() -> Ledger.create(memberId, request));
				
				//then: 거래날짜 범위 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(OUT_OF_RANGE)
						.hasWork("가계부 검증")
						.hasCauseMessage("거래날짜 허용 범위 초과")
						.hasField("date")
						.hasValue(date)
						.hasOption("max", DateTimeUtil.formatDate(LocalDate.now(), DatePatterns.DATE.getPattern()))
						.hasUserMessage("최근", "이내 날짜");
			}

			static Stream<Arguments> invalidDateRanges() {
				LocalDate today = LocalDate.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

				return Stream.of(
						Arguments.of(
								Named.of(
										"허용 범위를 초과한 과거 날짜인 경우",
										today.minusYears(5).minusMonths(1).format(formatter)
								)
						),
						Arguments.of(
								Named.of(
										"미래 날짜인 경우",
										today.plusYears(1).format(formatter)
								)
						)
				);
			}

			@ParameterizedTest
			@ValueSource(strings = {"030101", "001010", "101111", "201122"})
			@DisplayName("카테고리 시작이 01 또는 02가 아니면 생성에 실패한다.")
			void throwsException_whenCategoryPrefixIsInvalid(String code) {
				//given: 유효하지 않은 카테고리 코드를 포함한 생성 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.categoryCode(code)
						.build();

				//when: 유효하지 않은 카테고리 코드를 포함한 가계부를 생성한다.
				Throwable throwable = catchThrowable(() -> Ledger.create(memberId, request));

				//then: 카테고리 코드 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(INVALID_VALUE)
						.hasWork("가계부 검증")
						.hasCauseMessage("허용되지 않은 카테고리 코드")
						.hasField("category")
						.hasValue(code)
						.hasOption("allowedPrefix", "01, 02")
						.hasUserMessage("없는 카테고리");
			}
			
			@ParameterizedTest
			@ValueSource(strings = {"", " ", "m", "w"})
			@DisplayName("고정이 아닌데 고정주기가 있으면 생성에 실패한다.")
			void throwsException_whenPeriodIsGivenAndNotFixed(String cycle) {
				//given: 일회성인데 고정주기를 포함한 생성 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.fixed(LedgerTestData.FIX_N)
						.fixCycle(cycle)
						.build();
				
				//when: 비즈니스 규칙에 벗어난 가계부를 생성한다.
				Throwable throwable = catchThrowable(() -> Ledger.create(memberId, request));
				
				//then: 고정 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(POLICY_VIOLATION)
						.hasWork("가계부 검증")
						.hasCauseMessage("고정 여부와 주기 불일치")
						.hasField("fixCycle")
						.hasValue(cycle)
						.hasOption("policy", "고정이 아닌 경우 주기 설정 불가")
						.hasUserMessage("고정이 아닌", "주기를 설정");
			}
			
			@Test
			@DisplayName("고정인데 고정주기가 없으면 생성에 실패한다.")
			void throwsException_whenPeriodIsNullAndFixed() {
				//given: 고정주기가 없는 반복적인 가계부 생성 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.fixed(LedgerTestData.FIX_Y)
						.fixCycle(null)
						.build();

				//when: 비즈니스 규칙에 벗어난 가계부를 생성한다.
				Throwable throwable = catchThrowable(() -> Ledger.create(memberId, request));

				//then: 고정 검증에 대한 예외가 발생한다.
				assertThat(throwable).isInstanceOf(ValidationException.class);
			}
			
			@Test
			@DisplayName("유효하지 않은 고정 여부면 생성에 실패한다.")
			void throwsException_whenFixedStatusIsInvalid() {
				//given: 유효하지 않은 고정 여부를 포함한 가계부 생성 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.fixed("error")
						.build();

				//when: 유효하지 않은 고정 여부를 가진 가계부를 생성한다.
				Throwable throwable = catchThrowable(() -> Ledger.create(memberId, request));

				//then: 고정 검증에 대한 예외가 발생한다.
				assertThat(throwable).isInstanceOf(ValidationException.class);
			}
			
			@Test
			@DisplayName("유효하지 않은 고정주기면 생성에 실패한다.")
			void throwsException_whenFixedPeriodIsInvalid() {
				//given: 유효하지 않은 고정주기를 포함한 가계부 생성 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.fixed(LedgerTestData.FIX_Y)
						.fixCycle("error")
						.build();

				//when: 유효하지 않은 고정주기를 가진 가계부를 생성한다.
				Throwable throwable = catchThrowable(() -> Ledger.create(memberId, request));

				//then: 고정 검증에 대한 예외가 발생한다.
				assertThat(throwable).isInstanceOf(ValidationException.class);
			}

		}

	}


	@Nested
	@DisplayName("고정 수정")
	class ChangeFixInfoTest {

		Ledger ledger;

		@BeforeEach
		void setUp() {
			ledger = LedgerFixture.builder()
					.fix(FixedYN.REPEAT)
					.fixCycle(FixCycle.YEARLY)
					.build();
		}


		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("기존값과 동일하지 않으면 수정된다.")
			void updatesData_whenValueIsChanged() {
				//given: 기존 가계부와 다른 fix, fixCycle이 준비되어 있다.
				String fix = "n";

				//when: 기존과 다른 고정주기로 수정한다.
				ledger.changeFixInfo(fix, null);

				//then: 고정주기가 변경된다.
				assertThat(ledger.getFix()).isEqualTo(FixedYN.VARIABLE);
				assertThat(ledger.getFixCycle()).isNull();

				assertThat(ledger.getUpdatedAt()).isNotNull();
			}
			
			@Test
			@DisplayName("기존값과 동일하면 아무것도 수정하지 않는다.")
			void doesNothing_whenValueIsEqual() {
				//given: 기존 가계부에 포함된 fix, fixCycle 이 준비되어 있다.
				FixedYN beforeFix = ledger.getFix();
				FixCycle beforeCycle = ledger.getFixCycle();
				LocalDateTime beforeUpdateAt = ledger.getUpdatedAt();

				//when: 기존과 동일한 고정주기로 수정한다.
				ledger.changeFixInfo(beforeFix.getValue(), beforeCycle.getValue());

				//then: 값이 변경되지 않고 기존값과 동일하게 된다.
				assertThat(ledger.getFix()).isEqualTo(beforeFix);
				assertThat(ledger.getFixCycle()).isEqualTo(beforeCycle);

				assertThat(ledger.getUpdatedAt()).isEqualTo(beforeUpdateAt);
			}
			
			@Test
			@DisplayName("반복적인 가계부의 고정주기는 수정된다.")
			void updatesPeriod_whenFixed() {
				//when: 고정주기를 월로 수정한다.
				ledger.changeFixInfo("y", "m");
				
				//then: 고정주기만 변경된다.
				assertThat(ledger.getFix()).isEqualTo(FixedYN.REPEAT);
				assertThat(ledger.getFixCycle()).isEqualTo(FixCycle.MONTHLY);
			}

		}
		
		@Nested
		@DisplayName("실패 케이스")
		class Failure {
		
			@Test
			@DisplayName("유효하지 않은 고정 정보면 수정에 실패한다.")
			void throwsException_whenFixedInfoIsInvalid() {
				//given: 유효하지 않은 고정 정보를 준비되어 있다.
				String fix = "y";
				String cycle = "Q";
				
				//when: 고정 주기를 변경한다.
				Throwable throwable = catchThrowable(() -> ledger.changeFixInfo(fix, cycle));
				
				//then: 고정 정보 검증에 대한 예외가 발생한다.
				assertThat(throwable).isInstanceOf(ValidationException.class);
			}
			
		}

	}


	@Nested
	@DisplayName("카테고리 수정")
	class ChangeCategoryTest {

		Ledger incomeLedger;

		@BeforeEach
		void setUp() {
			incomeLedger = LedgerFixture.builder().build();
		}
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("카테고리가 변경되면 수정된다.")
			void updatesCategory_whenCategoryIsChanged() {
				//given: 기존 가계부와 다른 카테고리 코드가 준비되어 있다.
				String category = CategoryTestData.FOOD_CODE;
				LocalDateTime beforeUpdatedAt = incomeLedger.getUpdatedAt();

				//when: 지출 카테고리로 가계부를 수정한다.
				incomeLedger.changeCategory(category);

				//then: 지출 카테고리로 수정된다.
				assertThat(incomeLedger.getCategory()).isEqualTo(category);
				assertThat(incomeLedger.getUpdatedAt()).isNotEqualTo(beforeUpdatedAt);
			}
		
			@Test
			@DisplayName("기존 카테고리와 동일하면 수정하지 않는다.")
			void doesNothing_whenCategoryIsEqual() {
				//given: 동일한 카테고리 코드가준비되어 있다.
				String category = CategoryTestData.SALARY_CODE;
				LocalDateTime beforeUpdatedAt = incomeLedger.getUpdatedAt();
				
				//when: 동일한 카테고리로 가계부를 수정한다.
				incomeLedger.changeCategory(category);
				
				//then: 카테고리가 수정되지 않고 그래도 동일한 값이다.
				assertThat(incomeLedger.getCategory()).isEqualTo(category);
				assertThat(beforeUpdatedAt).isNull();
			}
			
		}
		
		@Nested
		@DisplayName("실패 케이스")
		class Failure {
			@Test
			@DisplayName("유효하지 않은 카테고리면 수정에 실패한다.")
			void throwsException_whenCategoryIsInvalid() {
				//given: 유효하지 않은 카테고리 코드가 준비되어 있다.
				String category = "error";

				//when: 유효하지 않은 카테고리로 가계부를 수정한다.
				Throwable throwable = catchThrowable(() -> incomeLedger.changeCategory(category));

				//then: 카테고리 검증에 대한 예외가 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(throwable)
						.hasErrorCode(INVALID_VALUE)
						.hasWork("가계부 검증")
						.hasCauseMessage("허용되지 않은 카테고리 코드")
						.hasField("category")
						.hasValue(category)
						.hasOption("allowedPrefix", "01, 02")
						.hasUserMessage("없는 카테고리");
			}
		}
		
	}


	@Nested
	@DisplayName("메모 수정")
	class ChangeMemoTest {

		Ledger ledger;

		@BeforeEach
		void setUp() {
			ledger = LedgerFixture.savedLedger();
		}
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {
		
			@Test
			@DisplayName("메모가 변경되면 수정된다.")
			void updatesMemo_whenMemoIsChanged() {
				//given: 메모 내용이 준비되어 있다.
				String memo = "수정";
				LocalDateTime beforeUpdatedAt = ledger.getUpdatedAt();
				
				//when: 메모를 포함하게 가계부를 수정한다.
				ledger.changeMemo(memo);
				
				//then: 요청한 메모 내용으로 수정된다.
				assertThat(ledger.getMemo()).isEqualTo(memo);
				assertThat(ledger.getUpdatedAt()).isNotEqualTo(beforeUpdatedAt);
			}
			
			@Test
			@DisplayName("기존 메모와 동일하면 수정하지 않는다.")
			void doesNothing_whenMemoIsEqual() {
				//given: 메모가 포함된 가계부인 상태이다.
				ledger.changeMemo("수정");
				
				//when: 메모가 포함되지 않게 가계부를 수정한다.
				ledger.changeMemo(null);
				
				//then: 메모가 삭제된 채로 저장된다.
				assertThat(ledger.getMemo()).isNull();
			}
			
		}
		
	}


	@Nested
	@DisplayName("금액 수정")
	class ChangeMoneyTest {

		Ledger ledger;

		@BeforeEach
		void setUp() {
			ledger = LedgerFixture.savedLedger();
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@ParameterizedTest
			@MethodSource("validMonies")
			@DisplayName("금액 정보가 변경되면 수정된다.")
			void updatesAmount_whenMoneyChanged(Money money) {
				//when: 변경된 금액정보로 가계부를 수정한다.
				ledger.changeMoney(money);

				//then: 금액과 결제 유형이 변경된다.
				assertThat(ledger.getMoney()).isEqualTo(money);
				assertThat(ledger.getUpdatedAt()).isNotNull();
			}

			static Stream<Arguments> validMonies() {
				return Stream.of(
						Arguments.of(Named.of("금액만 수정한 경우", Money.of(5000L, PaymentType.NONE))),
						Arguments.of(Named.of("결제 유형만 수정한 경우", Money.of(10000L, PaymentType.CARD))),
						Arguments.of(Named.of("금액과 결제 유형을 모두 수정한 경우", Money.of(20000L, PaymentType.BANK)))
				);
			}
			
			@Test
			@DisplayName("기존 금액 정보와 동일하면 수정하지 않는다.")
			void doesNothing_whenMoneyIsEqual() {
				//given: 금액과 카드유형이 기존과 동일하게 준비되어 있다.
				Money money = ledger.getMoney();

				//when: 기존과 동일한 금액 정보로 가계부를 수정한다.
				ledger.changeMoney(money);

				//then: 금액정보가 변경되지 않는다
				assertThat(ledger.getMoney()).isEqualTo(money);
				assertThat(ledger.getUpdatedAt()).isNull();
			}
			
		}

	}


	@Nested
	@DisplayName("장소 수정")
	class ChangePlaceTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			Ledger ledger;

			@BeforeEach
			void setUp() {
				ledger = LedgerFixture.builderWithPlace().build();
			}

			@ParameterizedTest
			@MethodSource("validPlaces")
			@DisplayName("장소 정보가 변경되면 수정된다.")
			void updatesPlace_whenPlaceIsChanged(Place place) {
				//when: 변경된 장소정보로 가계부를 수정한다.
				ledger.changePlace(place);

				//then: 금액과 결제 유형이 변경된다.
				assertThat(ledger.getPlace()).isEqualTo(place);
				assertThat(ledger.getUpdatedAt()).isNotNull();
			}

			static Stream<Arguments> validPlaces() {
				return Stream.of(
						Arguments.of(
								named(
										"장소명만 수정한 경우",
										Place.of("장소명", LedgerTestData.ROAD_ADDRESS, LedgerTestData.DETAIL_ADDRESS)
								)
						),
						Arguments.of(
								named(
										"기본 주소만 수정한 경우",
										Place.of(LedgerTestData.PLACE_NAME, "기본주소", LedgerTestData.DETAIL_ADDRESS)
								)
						),
						Arguments.of(
								named(
										"상세 주소만 수정한 경우",
										Place.of(LedgerTestData.PLACE_NAME, LedgerTestData.ROAD_ADDRESS, "상세주소")
								)
						),
						Arguments.of(
								named(
										"장소명, 기본주소, 상세 주소을 모두 수정한 경우",
										Place.of("장소명", "기본 주소", "상세 주소")
								)
						)
				);
			}
			
			@Test
			@DisplayName("기존 장소와 동일하면 수정하지 않는다.")
			void doesNothing_whenPlaceIsEqual() {
				//given: 장소명, 기본주소, 상세주소가 기존과 동일하게 준비되어 있다.
				Place place = ledger.getPlace();
				
				//when: 기존과 동일한 장소 정보로 가계부를 수정한다.
				ledger.changePlace(place);

				//then: 장소정보가 변경되지 않는다
				assertThat(ledger.getPlace()).isEqualTo(place);
				assertThat(ledger.getUpdatedAt()).isNull();
			}
			
		}

	}

}
