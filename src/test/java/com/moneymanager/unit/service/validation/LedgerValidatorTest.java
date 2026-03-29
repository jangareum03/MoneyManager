package com.moneymanager.unit.service.validation;

import com.moneymanager.BusinessExceptionAssert;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
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

	//==================[ TEST ]==================
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("com.moneymanager.unit.fixture.LedgerRequestFixture#successWriteRequest")
	@DisplayName("이미지 없는 가계부 등록 정보가 검증 통과한다.")
	void validateLedger_Success_WithoutImage(String title, LedgerWriteRequest request){
		//when & then
		assertThatCode(() -> ledgerValidator.register(request))
				.doesNotThrowAnyException();
	}

	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("com.moneymanager.unit.fixture.LedgerRequestFixture#successWriteRequestWithImage")
	@DisplayName("이미지 있는 가계부 등록 정보가 검증 통과한다.")
	void validateLedger_Success_WithImage(String description, LedgerWriteRequest request) {
		//when & then
		assertThatCode(() -> ledgerValidator.register(request))
				.doesNotThrowAnyException();
	}


	@ParameterizedTest
	@NullSource
	@DisplayName("가계부 등록 요청이 없으면 예외가 발생한다.")
	void validateLedger_Failure_Null(LedgerWriteRequest request){
		//when
		Throwable throwable = catchThrowable(()-> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_MISSING)
						.hasUserMessage("등록")
						.hasLogMessage("객체없음", "value=null");
	}

	@ParameterizedTest(name = "[{index}] date={0}")
	@NullAndEmptySource
	@DisplayName("거래날짜가 없으면 예외가 발생한다.")
	void validateDate_Failure_NullAndEmpty(String date) {
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.date(date)
							.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_NULL)
						.hasUserMessage("날짜")
						.hasLogMessage("필수값누락");
	}

	@ParameterizedTest(name="[{index}] date={0}")
	@ValueSource(strings = {"2025년 1월 1일", "2026년 12월", "2026년 05월 1일"})
	@DisplayName("거래날짜가 숫자가 아닌 문자가 포함되면 예외가 발생한다.")
	void validateDate_Failure_NotNumber(String date) {
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.date(date)
							.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_FORMAT)
						.hasUserMessage("날짜", "yyyyMMdd")
						.hasLogMessage("형식오류", date);
	}

	@ParameterizedTest(name="[{index}] date={0}")
	@ValueSource(strings = {"2025", "202612", "20264", "2026051"})
	@DisplayName("거래날짜가 8자리가 아니면 예외가 발생한다.")
	void validateDate_Failure_Length(String date) {
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.date(date)
						.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_FORMAT)
				.hasUserMessage("날짜", "yyyyMMdd")
				.hasLogMessage("형식오류", date);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("카테고리가 없으면 예외가 발생한다.")
	void validateCategory_Failure_NullAndEmpty(String category){
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.categoryCode(category)
								.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(LEDGER_INPUT_NULL)
						.hasUserMessage("카테고리")
						.hasLogMessage("필수값누락");
	}

	@ParameterizedTest(name = "[{index}] category={0}")
	@ValueSource(strings = {"apple!!@", "you123", "12345#"})
	@DisplayName("카테고리가 숫자가 아닌 문자가 포함되면 예외가 발생한다.")
	void validateCategory_Failure_Format(String category){
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.categoryCode(category)
								.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_INVALID)
				.hasUserMessage("카테고리")
				.hasLogMessage("형식오류", "6자리 숫자", category);
	}

	@ParameterizedTest(name = "[{index}] category={0}")
	@ValueSource(strings = {"01", "0201", "02011", "0101011"})
	@DisplayName("카테고리 길이가 6자리가 아니면 예외가 발생한다.")
	void validateCategory_Failure_Length(String category) {
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.categoryCode(category)
						.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_INVALID)
				.hasUserMessage("카테고리")
				.hasLogMessage("형식오류", "6자리 숫자", category);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("금액유형이 없으면 예외가 발생한다.")
	void validateAmountType_Failure_NullAndEmpty(String type){
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.amountType(type)
								.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_NULL)
				.hasUserMessage("금액 유형")
				.hasLogMessage("필수값누락");
	}

	@ParameterizedTest(name = "[{index}] type={0}")
	@ValueSource(strings = {"1", "mM", "month", "w1"})
	@DisplayName("고정주기가 영문자 1글자가 아니면 예외가 발생한다.")
	void validateAmountType_Failure_Format(String cycle){
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
	@MethodSource("validateMemo")
	@DisplayName("메모가 150글자 이하면 통과한다.")
	void validateMemo_Success_Length(String memo){
		//given
		LedgerWriteRequest request =
				defaultLedgerWriteRequest()
						.memo(memo)
								.build();

		//when & then
		assertThatCode(() -> ledgerValidator.register(request))
				.doesNotThrowAnyException();
	}

	@ParameterizedTest(name = "[{index}] memo={0}")
	@MethodSource("invalidateMemo")
	@DisplayName("메모가 150글자 초과하면 예외가 발생한다.")
	void validateMemo_Failure_Length(String memo){
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
				.hasLogMessage("길이오류", "length", String.valueOf(memo.length()));
	}

	@ParameterizedTest(name = "[{index}] place={0}")
	@NullAndEmptySource
	@DisplayName("장소만 없어도 통과한다.")
	void validatePlace_Success_NullAndEmpty(String place) {
		//given
		LedgerWriteRequest request =
				withPlace()
						.placeName(place)
						.build();

		//when & then
		assertThatCode(() -> ledgerValidator.register(request))
				.doesNotThrowAnyException();
	}

	@ParameterizedTest(name = "[{index} place={0}]")
	@MethodSource("validatePlace")
	@DisplayName("장소명 길이가 100글자 이하면 통과한다.")
	void validatePlace_Success_Length(String placeName){
		//given
		LedgerWriteRequest request =
				withPlace()
						.placeName(placeName)
								.build();

		//when & then
		assertThatCode(() -> ledgerValidator.register(request))
				.doesNotThrowAnyException();
	}

	@ParameterizedTest(name = "[{index} place={0}]")
	@MethodSource("invalidatePlace")
	@DisplayName("장소명 길이가 100글자를 초과하면 예외가 발생한다.")
	void validatePlace_Failure_Length(String place){
		//given
		LedgerWriteRequest request =
				withPlace()
						.placeName(place)
								.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_LENGTH)
				.hasUserMessage("장소 정보")
				.hasLogMessage("길이초과", "length", String.valueOf(place.length()));
	}

	@ParameterizedTest(name = "[{index}] place={0}")
	@ValueSource(strings = {"특수문자@#", "김家", "이마트 a동 11-2", "사랑♥"})
	@DisplayName("장소명에 한글, 숫자, 영문자, 공백 제외한 문자가 포함되면 예외가 발생한다.")
	void validatePlace_Failure_Format(String place) {
		//given
		LedgerWriteRequest request =
				withPlace()
						.placeName(place)
								.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_INVALID)
				.hasUserMessage("장소 정보")
				.hasLogMessage("형식오류", "format", place);
	}

	@ParameterizedTest(name = "[index] road={0}")
	@NullAndEmptySource
	@DisplayName("도로명 주소가 없어도 검증에 통과한다.")
	void validateRoadAddress_Success_NullAndEmpty(String address){
		//given
		LedgerWriteRequest request =
				withPlace()
						.roadAddress(address)
						.build();

		//when & then
		assertThatCode(() -> ledgerValidator.register(request))
				.doesNotThrowAnyException();
	}

	@ParameterizedTest(name = "[{index}] address={0}")
	@MethodSource("validateRoad")
	@DisplayName("도로명 주소가 300글자 이하면 통과한다.")
	void validateRoad_Success_Length(String address) {
		//given
		LedgerWriteRequest request =
				withPlace()
						.roadAddress(address)
								.build();

		//when & then
		assertThatCode(() -> ledgerValidator.register(request))
				.doesNotThrowAnyException();
	}

	@ParameterizedTest(name = "[{index}] address={0}")
	@MethodSource("invalidateRoad")
	@DisplayName("기본 주소가 300글자 초과하면 예외가 발생한다.")
	void validateRoad_Failure_Length(String address) {
		//given
		LedgerWriteRequest request =
				withPlace()
						.roadAddress(address)
						.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_LENGTH)
				.hasUserMessage("장소 정보")
				.hasLogMessage("길이오류", "length", String.valueOf(address.length()));
	}

	@ParameterizedTest(name = "[{index}] address={0}")
	@ValueSource(strings = {"도로명 a", "도로명!!@", "도로☆", "도로家"})
	@DisplayName("도로명 주소에 한글, 숫자, 공백, 일부 특수문자 제외한 문자가 포함되면 예외가 발생한다.")
	void validateRoad_Failure_Format(String address){
		//given
		LedgerWriteRequest request =
				withPlace()
						.roadAddress(address)
						.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_INVALID)
				.hasUserMessage("장소 정보")
				.hasLogMessage("형식오류", "format")
				.hasLogMessageNotContaining(address);
	}

	@ParameterizedTest(name = "[index] road={0}")
	@NullAndEmptySource
	@DisplayName("상세 주소가 없어도 검증에 통과한다.")
	void validateDetailAddress_Success_NullAndEmpty(String address){
		//given
		LedgerWriteRequest request =
				withPlace()
						.detailAddress(address)
						.build();

		//when & then
		assertThatCode(() -> ledgerValidator.register(request))
				.doesNotThrowAnyException();
	}

	@ParameterizedTest(name = "[{index}] address={0}")
	@MethodSource("validateDetail")
	@DisplayName("상세 주소가 500글자 이하면 통과한다.")
	void validateDetail_Success_Length(String address) {
		//given
		LedgerWriteRequest request =
				withPlace()
						.detailAddress(address)
						.build();

		//when & then
		assertThatCode(() -> ledgerValidator.register(request))
				.doesNotThrowAnyException();
	}

	@ParameterizedTest(name = "[{index}] address={0}")
	@MethodSource("invalidateDetail")
	@DisplayName("상세 주소가 500글자 초과하면 예외가 발생한다.")
	void validateDetail_Failure_Length(String address) {
		//given
		LedgerWriteRequest request =
				withPlace()
						.detailAddress(address)
						.build();

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.register(request));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(LEDGER_INPUT_LENGTH)
				.hasUserMessage("상세 주소")
				.hasLogMessage("길이오류", "length", String.valueOf(address.length()));
	}

	@Test
	@DisplayName("이미지 크기가 0이면 예외가 발생한다.")
	void validateImage_Failure_SizeIsZero() {
		//given
		byte[] content = new byte[0];

		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.validateImage(mockMultipartFile));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(FILE_INPUT_EMPTY)
				.hasUserMessage("빈 파일")
				.hasLogMessage("범위오류", "range");
	}

	@Test
	@DisplayName("이미지 크기가 최대 크기보다 크면 예외가 발생한다.")
	void validateImage_Failure_Size() {
		//given
		byte[] content = new byte[6 * 1024 * 1024];
		content[0] = (byte)0xFF;
		content[1] = (byte)0xD8;
		content[2] = (byte)0xFF;
		content[3] = (byte)0x02;

		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpg", content);

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.validateImage(mockMultipartFile));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(FILE_POLICY_LIMIT_EXCEEDED)
				.hasUserMessage("파일", "최대", "업로드 가능")
				.hasLogMessage("범위오류", "range", String.valueOf(mockMultipartFile.getSize()));
	}

	@ParameterizedTest(name = "[{index}] fileName={0}")
	@ValueSource(strings = {"test", "testjpg"})
	@DisplayName("파일 이름에 확장자가 없으면 예외가 발생한다.")
	void validateImage_Failure_ExtIsNone(String fileName) {
		//given
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", fileName, "image/png", "test".getBytes());

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.validateImage(mockMultipartFile));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(FILE_INPUT_FORMAT)
				.hasUserMessage("잘못된 파일 이름")
				.hasLogMessage("형식오류", String.valueOf(fileName));
	}

	@ParameterizedTest(name = "[{index}] name={0}, type={1}")
	@CsvSource({
			"text..txt, text/plan",
			"1월.pdf, application/pdf",
			"압축.zip, application/zip"
	})
	@DisplayName("이미지 타입이 범위 내면 예외가 발생한다.")
	void validateImage_Failure_Type(String name, String type){
		//given
		byte[] content = new byte[2 * 1024 * 1024];
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", name, type, content);

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.validateImage(mockMultipartFile));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(FILE_INPUT_FORMAT)
				.hasUserMessage("지원하지", "확장자")
				.hasLogMessage("허용값아님", "allowed");
	}

	@Test
	@DisplayName("파일 헤더 길이가 부족하면 예외가 발생한다.")
	void validateImage_Failure_HeaderSizeIsSmall() {
		//given
		byte[] content = new byte[] {1, 2, 3};
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.png", "image/png", content);

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.validateImage(mockMultipartFile));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(FILE_INPUT_ETC)
				.hasUserMessage("손상된 파일")
				.hasLogMessage("길이오류", String.valueOf(mockMultipartFile.getSize()));
	}

	@Test
	@DisplayName("파일 헤더가 jpg, png가 아니면 예외가 발생한다.")
	void validateImage_Failure_OutOfRange_Header(){
		//given
		byte[] header = new byte[] {0x11, 0x22, 0x33, 0x44};
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.png", "image/png", header);

		//when
		Throwable throwable = catchThrowable(() -> ledgerValidator.validateImage(mockMultipartFile));

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
						.hasErrorCode(FILE_POLICY_NOT_ALLOWED)
						.hasUserMessage("지원하지 않은 파일")
						.hasLogMessage("허용값아님");
	}

	@Test
	@DisplayName("손상된 파일이면 예외가 발생한다.")
	void validateImage_Failure_CorruptedFile() throws IOException {
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
				.hasLogMessage("읽기실패");
	}


	//==================[ Method Sources ]==================
	static Stream<String> validateMemo() {
		return Stream.of(
				"안녕하세요. 메모 적습니다.",
				"ㅎㅎ",
				"!",
				"가나다123 @!★".repeat(15)	//경계값
		);
	}

	static Stream<String> invalidateMemo() {
		return Stream.of(
				"가나다123 @!★".repeat(15) + "a",	//경계값
				"가나다라마12345 @#%^ ☆♥ 家羅".repeat(10)
		);
	}

	static Stream<String> validatePlace() {
		String boundaryValue = "가".repeat(100);	//경계값

		return Stream.of(
			"강남 CGV",
			"서울둘레길 8코스 자연생태가 복원된 도시하천길",
			new StringBuilder(boundaryValue).deleteCharAt(50).toString(),
			boundaryValue
		);
	}

	static Stream<String> invalidatePlace() {
		return Stream.of(
				"가".repeat(100) + "a",
				"a".repeat(100) +"강아지"
		);
	}

	static Stream<String> validateRoad() {
		String boundaryValue = "서".repeat(300);	//경계값

		return Stream.of(
				"도로명 12",
				"경기 성남시 분당구 대왕판교로 606번길 58",
				new StringBuilder(boundaryValue).deleteCharAt(1).toString(),
				boundaryValue
		);
	}

	static Stream<String> invalidateRoad() {
		String boundaryValue = "서".repeat(300);	//경계값

		return Stream.of(
				boundaryValue + "1",
				boundaryValue + "--"
		);
	}

	static Stream<String> validateDetail() {
		String boundaryValue = "상".repeat(500);	//경계값

		return Stream.of(
				"2층",
				"101동 2단지 11F 1012호",
				new StringBuilder(boundaryValue).deleteCharAt(1).toString(),
				boundaryValue
		);
	}

	static Stream<String> invalidateDetail() {
		String boundaryValue = "자".repeat(500);	//경계값

		return Stream.of(
				boundaryValue + "1",
				boundaryValue + "()"
		);
	}

}
