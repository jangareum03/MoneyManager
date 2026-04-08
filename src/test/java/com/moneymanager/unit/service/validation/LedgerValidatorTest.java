package com.moneymanager.unit.service.validation;

import com.moneymanager.BusinessExceptionAssert;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.exception.error.ErrorCode;
import com.moneymanager.service.validation.LedgerValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static com.moneymanager.exception.error.ErrorCode.*;
import static com.moneymanager.unit.fixture.LedgerRequestFixture.defaultLedgerWriteRequest;
import static com.moneymanager.unit.fixture.LedgerRequestFixture.withPlace;
import static org.assertj.core.api.Assertions.*;
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

	private final LedgerValidator ledgerValidator = new LedgerValidator();

	static final int PLACE_MAX_LENGTH = 100;
	static final int ROAD_MAX_LENGTH = 300;
	static final int DETAIL_MAX_LENGTH = 500;

	static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024;

	//==================[ register ]==================
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("com.moneymanager.unit.fixture.LedgerRequestFixture#successWriteRequest")
	@DisplayName("이미지 없는 가계부 등록 정보가 검증 통과한다.")
	void register_success_withoutImage(String title, LedgerWriteRequest request){
		//when & then
		assertThatCode(() -> ledgerValidator.register(request))
				.doesNotThrowAnyException();
	}


	@ParameterizedTest
	@NullSource
	@DisplayName("가계부 등록 요청 객체 자체가 없으면 예외가 발생한다.")
	void register_failure_requestIsNull(LedgerWriteRequest request){
		//when
		Throwable throwable = catchThrowable(()-> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_MISSING)
						.hasUserMessage("등록")
						.hasLogMessage("객체없음", "value=null");
	}


	@ParameterizedTest(name = "[{index}] {0}   |   value={1}")
	@MethodSource("provideInvalidDates")
	@DisplayName("날짜 입력값이 잘못된 경우에 예외가 발생한다.")
	void register_failure_dateIsInvalid(String description, String value, ErrorCode errorCode, String[] userMsg, String[] logMsg) {
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.date(value)
							.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(errorCode)
						.hasUserMessage(userMsg)
						.hasLogMessage(logMsg);
	}

	private static Stream<Arguments> provideInvalidDates() {
		return Stream.of(
			Arguments.of("date가 없는 경우", null,
					LEDGER_INPUT_NULL, new String[] {"날짜", "선택"}, new String[] {"필수값누락", "date"}
			),
			Arguments.of("date가 빈 문자열인 경우", "   ",
					LEDGER_INPUT_NULL, new String[] {"날짜", "선택"}, new String[] {"필수값누락", "date"}
			),
			Arguments.of("date에 한글 형식이 포함된 경우", "2025년 1월 1일",
					LEDGER_INPUT_FORMAT, new String[] {"날짜", "yyyyMMdd"}, new String[] {"형식오류", "date"}
			),
			Arguments.of("date에 한자가 포함된 경우", "2025年 1月 1日",
					LEDGER_INPUT_FORMAT, new String[] {"날짜", "yyyyMMdd"}, new String[] {"형식오류", "date"}
			),
			Arguments.of("date에 구분자가 포함된 경우 ", "2026. 01. 01",
					LEDGER_INPUT_FORMAT, new String[] {"날짜", "yyyyMMdd"}, new String[] {"형식오류", "date"}
			),
			Arguments.of("date 길이가 8이 아닌 경우 ", "2025",
					LEDGER_INPUT_FORMAT, new String[] {"날짜", "yyyyMMdd"}, new String[] {"형식오류", "date"}
			),
			Arguments.of("date 길이가 8이 아닌 경우 ", "2026051",
					LEDGER_INPUT_FORMAT, new String[] {"날짜", "yyyyMMdd"}, new String[] {"형식오류", "date"}
			)
		);
	}


	@ParameterizedTest(name = "[{index}] {0}   |   value={1}")
	@MethodSource("provideInvalidCategories")
	@DisplayName("카테고리 입력값이 잘못된 경우에 예외가 발생한다.")
	void register_failure_categoryIsInvalid(String description, String value, ErrorCode errorCode, String[] userMsg, String[] logMsg) {
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.categoryCode(value)
								.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(errorCode)
						.hasUserMessage(userMsg)
						.hasLogMessage(logMsg);
	}

	private static Stream<Arguments> provideInvalidCategories() {
		return Stream.of(
				Arguments.of("category가 없는 경우", null,
						LEDGER_INPUT_NULL, new String[] {"카테고리", "선택"}, new String[] {"필수값누락", "category"}
				),
				Arguments.of("category가 빈 문자열인 경우", "   ",
						LEDGER_INPUT_NULL, new String[] {"카테고리", "선택"}, new String[] {"필수값누락", "category"}
				),
				Arguments.of("category에 특수문자가 포함된 경우", "010101@",
						LEDGER_INPUT_INVALID, new String[] {"카테고리"}, new String[] {"형식오류", "6자리 숫자"}
				),
				Arguments.of("category 길이가 6자리가 아닌 경우", "02010",
						LEDGER_INPUT_INVALID, new String[] {"카테고리"}, new String[] {"형식오류", "6자리 숫자"}
				),
				Arguments.of("category 길이가 6자리가 아닌 경우", "0101011",
						LEDGER_INPUT_INVALID, new String[] {"카테고리"}, new String[] {"형식오류", "6자리 숫자"}
				)
		);
	}


	@ParameterizedTest(name = "[{index}] value={0}")
	@NullSource
	@DisplayName("금액 입력값이 null이면 예외가 발생한다.")
	void register_failure_amountIsNull(Long value) {
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.amount(value)
						.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_NULL)
				.hasUserMessage("금액", "입력")
				.hasLogMessage("필수값누락", "amount");
	}


	@ParameterizedTest(name = "[{index}] {0}   |   value={1}")
	@MethodSource("provideInvalidAmountTypes")
	@DisplayName("금액유형이 잘못된 경우에 예외가 발생한다.")
	void register_failure_amountTypeIsInvalid(String description, String value, ErrorCode errorCode, String[] userMsg, String[] logMsg) {
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.amountType(value)
								.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(errorCode)
				.hasUserMessage(userMsg)
				.hasLogMessage(logMsg);
	}

	private static Stream<Arguments> provideInvalidAmountTypes() {
		return Stream.of(
			Arguments.of("amountType이 없는 경우", null,
					LEDGER_INPUT_NULL, new String[] {"금액 유형", "선택"}, new String[] {"필수값누락", "amountType"}
			),
			Arguments.of("amountType이 빈 문자열인 경우", "   ",
					LEDGER_INPUT_NULL, new String[] {"금액 유형", "선택"}, new String[] {"필수값누락", "amountType"}
			)
		);
	}


	@ParameterizedTest(name = "[{index}] type={0}")
	@ValueSource(strings = {"mM", "month", "w1", "주"})
	@DisplayName("고정주기가 영문자 1글자가 아니면 예외가 발생한다.")
	void register_failure_amountFixCycleIsInvalid(String cycle){
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.fixed(true)
						.fixCycle(cycle)
								.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_INVALID)
				.hasUserMessage("고정주기")
				.hasLogMessage("형식오류", "1자리 영어", cycle);
	}


	@ParameterizedTest(name = "[{index}] memo={0}")
	@MethodSource("provideValidMemos")
	@DisplayName("메모가 150글자 이하면 통과한다.")
	void register_success_memo(String memo){
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.memo(memo)
								.build();

		//when & then
		assertThatCode(() -> ledgerValidator.register(request))
				.doesNotThrowAnyException();
	}

	static Stream<String> provideValidMemos() {
		return Stream.of(
				"안녕하세요. 메모 적습니다.",
				"ㅎㅎ",
				"!",
				"가나다123 @!★".repeat(15)	//경계값
		);
	}


	@ParameterizedTest(name = "[{index}] memo={0}")
	@MethodSource("provideInvalidMemos")
	@DisplayName("메모가 150글자 초과하면 예외가 발생한다.")
	void register_failure_memoIsValid(String memo){
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.memo(memo)
								.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_LENGTH)
				.hasUserMessage("메모", "최대")
				.hasLogMessage("길이오류", "maxLength", String.valueOf(memo.length()));
	}

	static Stream<String> provideInvalidMemos() {
		return Stream.of(
				"가나다123 @!★".repeat(15) + "a",	//경계값
				"가나다라마12345 @#%^ ☆♥ 家羅".repeat(10)
		);
	}


	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("provideValidPlace")
	@DisplayName("주소가 입력값이 정상인 경우 검증에 통과한다.")
	void register_success_place(String description, LedgerWriteRequest request) {
		//when & then
		assertThatCode(() -> ledgerValidator.register(request))
				.doesNotThrowAnyException();
	}

	static Stream<Arguments> provideValidPlace() {
		return Stream.of(
			Arguments.of("장소명만 null인 경우", withPlace().placeName(null).build()),
			Arguments.of("장소명만 빈 문자열인 경우", withPlace().placeName("   ").build()),
			Arguments.of("장소명 길이가 100글자 이하인 경우", withPlace().placeName("서울둘레길 8코스 자연생태가 복원된 도시하천길").build()),
			Arguments.of("장소명 길이가 최대 길이인 경우", withPlace().placeName("가".repeat(PLACE_MAX_LENGTH)).build()),
			Arguments.of("도로명 주소만 null인 경우", withPlace().roadAddress(null).build()),
			Arguments.of("도로명 주소만 빈 문자열인 경우", withPlace().roadAddress("   ").build()),
			Arguments.of("도로명 주소 길이가 300글자 이하인 경우", withPlace().roadAddress("경기 성남시 분당구 대왕판교로 606번길 58").build()),
			Arguments.of("도로명 주소 길이가 최대 길이인 경우", withPlace().roadAddress("도".repeat(ROAD_MAX_LENGTH)).build()),
			Arguments.of("상세 주소만 null인 경우", withPlace().detailAddress(null).build()),
			Arguments.of("상세 주소만만 빈 문자열인 경우", withPlace().detailAddress("   ").build()),
			Arguments.of("상세 주소 길이가 500글자 이하인 경우", withPlace().detailAddress("101동 2단지 11F 1012호").build()),
			Arguments.of("상세 주소 길이가 최대 길이인 경우", withPlace().detailAddress("상".repeat(DETAIL_MAX_LENGTH)).build())
		);
	}


	@ParameterizedTest(name = "[{index} {0}]")
	@MethodSource("provideInvalidPlace")
	@DisplayName("주소 입력값이 잘못된 경우 예외가 발생한다.")
	void register_failure_place(String description, LedgerWriteRequest request, ErrorCode errorCode, String[] userMsg, String[] logMsg){
		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(errorCode)
				.hasUserMessage(userMsg)
				.hasLogMessage(logMsg);
	}

	static Stream<Arguments> provideInvalidPlace() {
		return Stream.of(
				Arguments.of(
						"장소명 길이가 최대 길이를 초과한 경우", withPlace().placeName("가".repeat(PLACE_MAX_LENGTH + 1)).build(),
						LEDGER_INPUT_LENGTH, new String[] {"장소 정보", "선택"}, new String[] {"길이오류", "placeName", "maxLength", String.valueOf(PLACE_MAX_LENGTH)}
				),
				Arguments.of(
						"장소명에 특수문자가 포함된 경우", withPlace().placeName("이디야#").build(),
						LEDGER_INPUT_INVALID, new String[] {"장소 정보", "선택"}, new String[] {"형식오류", "placeName", "expectedFormat"}
				),
				Arguments.of(
						"장소명에 한자가 포함된 경우", withPlace().placeName("김家네").build(),
						LEDGER_INPUT_INVALID, new String[] {"장소 정보", "선택"}, new String[] {"형식오류", "placeName", "expectedFormat"}
				),
				Arguments.of(
						"도로명 주소 길이가 최대 길이를 초과한 경우", withPlace().roadAddress("도".repeat(ROAD_MAX_LENGTH + 1)).build(),
						LEDGER_INPUT_LENGTH, new String[] {"장소 정보", "선택"}, new String[] {"길이오류", "roadAddress", "maxLength"}
				),
				Arguments.of(
						"도로명 주소에 영어가 포함된 경우", withPlace().roadAddress("도로명 aA").build(),
						LEDGER_INPUT_INVALID, new String[] {"장소 정보", "선택"}, new String[] {"형식오류", "roadAddress", "expectedFormat"}
				),
				Arguments.of(
						"도로명 주소에 한자가 포함된 경우", withPlace().roadAddress("도로家").build(),
						LEDGER_INPUT_INVALID, new String[] {"장소 정보", "선택"}, new String[] {"형식오류", "roadAddress", "expectedFormat"}
				),
				Arguments.of(
						"도로명 주소에 허용 불가한 특수문자가 포함된 경우", withPlace().roadAddress("도로명!!@").build(),
						LEDGER_INPUT_INVALID, new String[] {"장소 정보", "선택"}, new String[] {"형식오류", "roadAddress", "expectedFormat"}
				),
				Arguments.of(
						"상세 주소 길이가 최대 길이를 초과한 경우", withPlace().detailAddress("상".repeat(DETAIL_MAX_LENGTH + 1)).build(),
						LEDGER_INPUT_LENGTH, new String[] {"상세 주소", "입력"}, new String[] {"길이오류", "detailAddress", "maxLength"}
				)
		);
	}


	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("provideInvalidImage")
	@DisplayName("이미지가 잘못된 경우 예외가 발생한다.")
	void register_failure_image(String description, MockMultipartFile mockMultipartFile, ErrorCode errorCode, String[] userMsg, String[] logMsg) {
		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.validateImage(mockMultipartFile));

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
	void register_failure_corruptedFile() throws IOException {
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
		Throwable throwable = catchThrowable(() -> ledgerValidator.validateImage(multipartFile));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(FILE_INPUT_ETC)
				.hasUserMessage("손상된 파일")
				.hasLogMessage("파일읽기실패", "imageFile");
	}

}
