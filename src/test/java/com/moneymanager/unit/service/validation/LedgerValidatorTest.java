package com.moneymanager.unit.service.validation;

import com.moneymanager.BusinessExceptionAssert;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.exception.error.ErrorCode;
import com.moneymanager.fixture.ledger.LedgerWriteRequestFixture;
import com.moneymanager.service.validation.LedgerValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static com.moneymanager.exception.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.validation<br>
 * 파일이름       : LedgerValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 25<br>
 * 설명              : LedgerValidator 클래스 로직을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 1. 25.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class LedgerValidatorTest {

	private final LedgerValidator target = new LedgerValidator();

	static final int PLACE_MAX_LENGTH = 100;
	static final int ROAD_MAX_LENGTH = 300;
	static final int DETAIL_MAX_LENGTH = 500;
	static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024;


	@Nested
	@DisplayName("가계부 등록 검증")
	class RegisterTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("필수 정보만 있는 요청은 검증에 통과한다.")
			void passesValidation_whenEssentialFieldsGiven(){
				//given: 필수 정보만 있는 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest().build();
				
				//when & then: 필수 정보 검증이 성공한다.
				assertThatCode(() -> target.register(request))
						.doesNotThrowAnyException();
			}

			@Test
			@DisplayName("고정주기가 없어도 검증에 통과한다.")
			void passesValidation_whenFixedCycleIsEmpty() {
				//given: 고정주기가 없이 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest().fixCycle(null).build();
				
				//when & then: 고정주기 검증이 성공한다.
				assertThatCode(() -> target.register(request))
						.doesNotThrowAnyException();
			}

			@ParameterizedTest(name = "[{index}] fixCycle={0}")
			@ValueSource(strings = {"a", "W"})
			@DisplayName("고정주기가 1자리 영문자면 검증에 통과한다.")
			void passesValidation_whenFixedCycleIsSingleLetter(String cycle) {
				//given: 허용 범위(1자, 영문자)의 고정주기로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest()
						.fixCycle(cycle)
						.build();

				//when & then: 고정주기 검증이 성공한다.
				assertThatCode(() -> target.register(request))
						.doesNotThrowAnyException();;
			}

			@Test
			@DisplayName("메모가 없어도 검증에 통과한다.")
			void passesValidation_whenMemoIsEmpty() {
				//given: 메모가 없는 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest().memo(null).build();
				
				//when & then: 메모 검증이 성공한다.
				assertThatCode(() -> target.register(request))
						.doesNotThrowAnyException();
			}

			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("com.moneymanager.fixture.ledger.LedgerRequestTestData#provideValidMemos")
			@DisplayName("메모가 150자 이하면 검증에 통고한다.")
			void passesValidation_whenMemoIsWithinLimit(String caseName, String memo) {
				//given: 허용 길이(150자 이하)의 메모로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest()
						.memo(memo)
						.build();

				//when & then: 메모 길이 검증이 성공한다.
				assertThatCode(() -> target.register(request))
						.doesNotThrowAnyException();;
			}

			@Test
			@DisplayName("주소가 없어도 검증에 통과한다.")
			void passesValidation_whenAddressIsEmpty() {
				//given: 주소가 없는 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest()
						.placeName(null).roadAddress(null).detailAddress(null).build();
				
				//when & then: 주소 검증이 성공한다.
				assertThatCode(() -> target.register(request))
						.doesNotThrowAnyException();
			}
			
			@Test
			@DisplayName("주소명와 기본주소만 있어도 검증에 통과한다.")
			void validatesPlace_whenOnlyAddressNameAndBasicAddressGiven() {
				//given: 주소명과 기본주소만 있는 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest()
						.placeName("CGV 강남정")
						.roadAddress("서울특별시 강남구 강남대로 기본주소 123")
						.detailAddress(null)
						.build();
				
				//when & then: 가계부 주소 검증이 성공한다.
				assertThatCode(() -> target.register(request))
						.doesNotThrowAnyException();;
			}

			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("com.moneymanager.fixture.ledger.LedgerRequestTestData#provideValidPlaceNames")
			@DisplayName("장소명이 100자 이하면 검증에 통과한다.")
			void validatesPlaceName_whenInRange(String caseName, String placeName) {
				//given: 허용 길이(100자 이하)의 장소명으로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.withPlace()
						.placeName(placeName)
						.build();

				//when & then: 장소명 길이 검증이 성공한다.
				assertThatCode(() -> target.register(request))
						.doesNotThrowAnyException();
			}
			
			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("com.moneymanager.fixture.ledger.LedgerRequestTestData#provideValidRoadAddresses")
			@DisplayName("기본주소가 300자 이하면 검증에 통과한다.")
			void validatesBasicAddress_whenInRange(String caseName, String roadAddress) {
				//given: 허용 길이(300자 이하)의 기본주소로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.withPlace()
						.roadAddress(roadAddress)
						.build();

				//when & then: 기본주소 길이 검증이 성공한다.
				assertThatCode(() -> target.register(request))
						.doesNotThrowAnyException();
			}
			
			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("com.moneymanager.fixture.ledger.LedgerRequestTestData#provideValidDetailAddresses")
			@DisplayName("상세주소가 500자 이하면 검증에 통과한다.")
			void validatesDetailAddress_whenInRange(String caseName, String detailAddress) {
				//given: 허용 길이(500자 이하)의 상세주소로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.withPlace()
						.detailAddress(detailAddress)
						.build();
				
				//when & then: 상세주소 길이 검증이 성공한다.
				assertThatCode(() -> target.register(request))
						.doesNotThrowAnyException();
			}

		}


		@Nested
		@DisplayName("실패 케이스")
		class Failure {


			@Test
			@DisplayName("가계부 등록 요청 객체 자체가 없으면 예외가 발생한다.")
			void throwsException_whenRequestIsNull(){
				//given: 객체가 null인 요청 생성
				LedgerWriteRequest request = null;

				//when: 객체 검증하면 예외가 발생한다.
				Throwable throwable = catchThrowable(()-> target.register(request));

				//then: 에러코드와 메시지 확인
				BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_MISSING)
						.hasUserMessage("등록")
						.hasLogMessage("객체없음", "LedgerWriteRequest", "value=null");
			}

			@ParameterizedTest
			@NullAndEmptySource
			@DisplayName("날짜가 null 또는 빈 문자열이면 예외가 발생한다.")
			void throwsException_whenDateIsBlank(String date) {
				//given: null 또는 빈 문자열 날짜로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest()
						.date(date)
						.build();
				
				//when: 날짜 검증하면 예외가 발생한다.
				Throwable throwable = catchThrowable(() -> target.register(request));

				//then: 에러코드와 메시지 확인
				BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_NULL)
						.hasUserMessage("날짜를 선택")
						.hasLogMessage("필수값누락", "date");
			}
			
			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("com.moneymanager.fixture.ledger.LedgerRequestTestData#provideInvalidDates")
			@DisplayName("날짜가 8글자 숫자가 아니라면 예외가 발생한다.")
			void throwsException_whenDateIsNotEightDigits(String caseName, String date) {
				//given: 허용하지 않은 날짜 형식으로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest()
						.date(date)
						.build();
				
				//when: 날짜 검증하면 예외가 발생한다.
				Throwable throwable = catchThrowable(() -> target.register(request));

				//then: 에러코드와 메시지 확인
				BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_FORMAT)
						.hasUserMessage("yyyyMMdd")
						.hasLogMessage("형식오류", "date", "yyyyMMdd");
			}

			@ParameterizedTest
			@NullAndEmptySource
			@DisplayName("카테고리 코드가 null 또는 빈 문자열이면 예외가 발생한다.")
			void throwsException_whenCategoryCodeIsBlank(String code) {
				//given: null 또는 빈 문자열 카테고리 코드로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest()
						.categoryCode(code)
						.build();

				//when: 카테고리 검증하면 예외가 발생한다.
				Throwable throwable = catchThrowable(() -> target.register(request));

				//then: 에러코드와 메시지 확인
				BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_NULL)
						.hasUserMessage("카테고리", "선택")
						.hasLogMessage("필수값누락", "categoryCode");
			}

			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("com.moneymanager.fixture.ledger.LedgerRequestTestData#provideInvalidCategories")
			@DisplayName("카테고리 코드가 6자리 숫자가 아니면 예외가 발생한다.")
			void throwsException_whenCategoryCodeIsInvalid(String caseName, String code) {
				//given: 허용하지 않은 카테고리 코드로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest()
						.categoryCode(code)
						.build();

				//when: 카테고리 검증하면 예외가 발생한다.
				Throwable throwable = catchThrowable(() -> target.register(request));

				//then: 에러코드와 메시지 확인
				BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_INVALID)
						.hasUserMessage("허용하지 않은", "카테고리")
						.hasLogMessage("형식오류", "6자리 숫자");
			}

			@Test
			@DisplayName("금액이 null이면 예외가 발생한다.")
			void throwsException_whenAmountIsNull() {
				//given: null인 금액으로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest()
						.amount(null)
						.build();
				
				//when: 금액 검증하면 예외가 발생한다.
				Throwable throwable = catchThrowable(() -> target.register(request));

				//then: 에러코드와 메시지 확인
				BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_NULL)
						.hasUserMessage("금액을 입력")
						.hasLogMessage("필수값누락", "amount");
			}

			@ParameterizedTest
			@NullAndEmptySource
			@DisplayName("금액유형이 null 또는 빈 문자열이면 예외가 발생한다.")
			void throwsException_whenAmountTypeIsBlank(String paymentType) {
				//given: null 또는 빈 문자열 금액 유형으로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest()
						.paymentType(paymentType)
						.build();
				
				//when: 금액 유형을 검증하면 예외가 발생한다.
				Throwable throwable = catchThrowable(() -> target.register(request));

				//then: 에러코드와 메시지 확인
				BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_NULL)
						.hasUserMessage("금액 유형")
						.hasLogMessage("필수값누락", "paymentType");
			}
			
			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("com.moneymanager.fixture.ledger.LedgerRequestTestData#provideInvalidFixCycles")
			@DisplayName("고정주기가 1자리 영문자가 아니면 예외가 발생한다.")
			void throwsException_whenFixedPeriodIsNotSingleAlphabet(String caseName, String fixCycle) {
				//given: 허용하지 않은 고정주기로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest()
						.fixCycle(fixCycle)
						.build();

				//when: 금액 유형을 검증하면 예외가 발생한다.
				Throwable throwable = catchThrowable(() -> target.register(request));

				//then: 에러코드와 메시지 확인
				BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_INVALID)
						.hasUserMessage("고정주기")
						.hasLogMessage("형식오류", "fixCycle", "1자리 영어");
			}

			@ParameterizedTest(name = "[{index}] memo={0}")
			@MethodSource("com.moneymanager.fixture.ledger.LedgerRequestTestData#provideInvalidLimitMemos")
			@DisplayName("메모가 150글자 초과하면 예외가 발생한다.")
			void throwsException_whenMemoExceedsLimit(String caseName, String memo){
				//given: 150자 초과한 메모로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.createRequest()
								.memo(memo)
								.build();

				//when: 메모를 검증하면 예외가 발생한다.
				Throwable throwable = catchThrowable(() -> target.register(request));

				//then: 에러코드와 메시지 확인
				BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_LENGTH)
						.hasUserMessage("메모", "최대 150자")
						.hasLogMessage("길이오류", "maxLength", String.valueOf(memo.length()));
			}

			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("com.moneymanager.fixture.ledger.LedgerRequestTestData#provideInvalidLimitPlaceName")
			@DisplayName("장소명이 100글자 초과하면 예외가 발생한다.")
			void throwsException_whenPlaceNameExceedsLimit(String caseName, String placeName) {
				//given: 100자 초과한 장소명으로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.withPlace()
						.placeName(placeName)
						.build();

				//when: 장소명을 검증하면 예외가 발생한다.
				Throwable throwable = catchThrowable(() -> target.register(request));

				//then: 에러코드와 메시지 확인
				BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_LENGTH)
						.hasUserMessage("장소 정보", "다시 선택")
						.hasLogMessage("길이오류", "placeName", "100");
			}
			
			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("com.moneymanager.fixture.ledger.LedgerRequestTestData#provideInvalidCharacterPlaceNames")
			@DisplayName("장소명에 허용되지 않은 문자가 포함되면 예외가 발생한다.")
			void throwsException_whenPlaceNameContainsInvalidCharacters(String caseName, String placeName) {
				//given: 허용하지 않은 장소명으로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.withPlace()
						.placeName(placeName)
						.build();
				
				//when: 장소명을 검증하면 예외가 발생한다.
				Throwable throwable = catchThrowable(() -> target.register(request));

				//then: 에러코드와 메시지 확인
				BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_INVALID)
						.hasUserMessage("장소 정보", "다시 선택")
						.hasLogMessage("형식오류", "placeName");
			}

			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("com.moneymanager.fixture.ledger.LedgerRequestTestData#provideInvalidLimitRoadAddress")
			@DisplayName("기본주소가 300글자 초과하면 예외가 발생한다.")
			void throwsException_whenRoadAddressExceedsLimit(String caseName, String roadAddress) {
				//given: 300자 초과한 기본주소로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.withPlace()
						.roadAddress(roadAddress)
						.build();

				//when: 기본주소를 검증하면 예외가 발생한다.
				Throwable throwable = catchThrowable(() -> target.register(request));

				//then: 에러코드와 메시지 확인
				BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_LENGTH)
						.hasUserMessage("장소 정보", "다시 선택")
						.hasLogMessage("길이오류", "roadAddress", "300");
			}
			
			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("com.moneymanager.fixture.ledger.LedgerRequestTestData#provideInvalidCharacterRoadAddresses")
			@DisplayName("기본주소에 허용되지 않은 문자가 포함되면 예외가 발생한다.")
			void throwsException_whenAddressContainsInvalidCharacters(String caseName, String roadAddress) {
				//given: 허용하지 않은 기본주소로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.withPlace()
						.roadAddress(roadAddress)
						.build();

				//when: 기본주소를 검증하면 예외가 발생한다.
				Throwable throwable = catchThrowable(() -> target.register(request));

				//then: 에러코드와 메시지 확인
				BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_INVALID)
						.hasUserMessage("장소 정보", "다시 선택")
						.hasLogMessage("형식오류", "roadAddress");
			}

			@ParameterizedTest(name = "[{index}] {0}")
			@MethodSource("com.moneymanager.fixture.ledger.LedgerRequestTestData#provideInvalidLimitDetailAddress")
			@DisplayName("상세주소가 500글자 초과하면 예외가 발생한다.")
			void throwsException_whenDetailAddressExceedsLimit(String caseName, String detailAddress) {
				//given: 300자 초과한 상세주소로 요청 생성
				LedgerWriteRequest request = LedgerWriteRequestFixture.withPlace()
						.detailAddress(detailAddress)
						.build();

				//when: 상세주소를 검증하면 예외가 발생한다.
				Throwable throwable = catchThrowable(() -> target.register(request));

				//then: 에러코드와 메시지 확인
				BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_LENGTH)
						.hasUserMessage("상세 주소", "500자")
						.hasLogMessage("길이오류", "detailAddress", "500");
			}

		}

	}


	@Nested
	@DisplayName("이미지 검증")
	class ImageTest {

		@ParameterizedTest(name = "[{index}] {0}")
		@MethodSource("provideInvalidImage")
		@DisplayName("이미지가 잘못된 경우 예외가 발생한다.")
		void throwsException_whenImageIsInvalid(String description, MockMultipartFile mockMultipartFile, ErrorCode errorCode, String[] userMsg, String[] logMsg) {
			//when
			Throwable throwable = catchThrowable(() -> target.validateImage(mockMultipartFile));

			//then
			BusinessExceptionAssert.assertThatBusinessException(throwable)
					.hasErrorCode(errorCode)
					.hasUserMessage(userMsg)
					.hasLogMessage(logMsg);
		}

		static Stream<Arguments> provideInvalidImage() {
			return Stream.of(
					Arguments.of(
							"크기가 0인 경우", new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]),
							FILE_INPUT_EMPTY, new String[] {"빈 파일", "업로드"}, new String[] {"크기오류", "imageFile", "size", "maxSize"}
					),
					Arguments.of(
							"크기가 최대 허용치보다 큰 경우", new MockMultipartFile("file", "test.jpg", "image/jpeg", createImage(MAX_IMAGE_SIZE + 1)),
							FILE_POLICY_LIMIT_EXCEEDED, new String[] {"최대", "업로드", "확인"}, new String[] {"파일크기오류", "imageFile", "size", "maxSize"}
					),
					Arguments.of(
							"이름에 확장자가 없는 경우", new MockMultipartFile("file", "test", "image/jpeg", "test".getBytes()),
							FILE_INPUT_FORMAT, new String[] {"파일 이름", "다른 파일"}, new String[] {"형식오류", "imageFile", "fileName"}
					),
					Arguments.of(
							"이미지 파일이 아닌 경우", new MockMultipartFile("file", "test.png", "application/zip", createImage(2* 1024 * 1024)),
							FILE_POLICY_NOT_ALLOWED, new String[] {"이미지 파일"}, new String[] {"정책위반", "file", "contentType", "policy"}
					),
					Arguments.of(
							"파일헤더 길이가 부족한 경우", new MockMultipartFile("file", "test.png", "image/png", new byte[] {1, 2, 3}),
							FILE_INPUT_ETC, new String[] {"손상된 파일", "다른 파일"}, new String[] {"길이오류", "imageFile", "header", "expectedLength"}
					),
					Arguments.of(
							"파일헤더가 지원하지 않은 형식인 경우", new MockMultipartFile("file", "test.png", "image/png", new byte[] {0x11, 0x22, 0x33, 0x44}),
							FILE_POLICY_NOT_ALLOWED, new String[] {"지원하지", "다른 파일"}, new String[] {"허용값아님", "imageFile", "headerHex", "allowedValues"}
					)
			);
		}

		private static byte[] createImage(int size) {
			byte[] content = new byte[size];

			content[0] = (byte)0xFF;
			content[1] = (byte)0xD8;
			content[2] = (byte)0xFF;
			content[3] = (byte)0x02;

			return content;
		}

		@Test
		@DisplayName("손상된 파일이면 예외가 발생한다.")
		void throwsException_whenImageCorrupted() throws IOException {
			//given
			byte[] header = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x02};

			MultipartFile multipartFile = mock(MultipartFile.class);
			InputStream is = mock(InputStream.class);

			when(multipartFile.getSize()).thenReturn(10L);
			when(multipartFile.getOriginalFilename()).thenReturn("test.png");
			when(multipartFile.getContentType()).thenReturn("image/png");

			when(multipartFile.getInputStream()).thenReturn(is);
			when(is.read(any(byte[].class))).thenThrow(new IOException("파일 손상"));

			//when
			Throwable throwable = catchThrowable(() -> target.validateImage(multipartFile));

			//then
			BusinessExceptionAssert.assertThatBusinessException(throwable)
					.hasErrorCode(FILE_INPUT_ETC)
					.hasUserMessage("손상된 파일")
					.hasLogMessage("파일읽기실패", "imageFile");
		}

	}

}
