package com.moneymanager.ledger.domain.vo;

import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.StringTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import com.moneymanager.support.ApplicationExceptionAssert;

import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.CommonErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.domain.ledger.vo<br>
 * 파일이름       : PlaceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 5<br>
 * 설명              : Place 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 6. 5</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class PlaceTest {

	@Nested
	@DisplayName("성공 케이스")
	class Success {

		@Test
		@DisplayName("필수항목이 모두 null이면 Place 객체가 생성한다.")
		void createsPlace_whenPlaceNameAndRoadAddressAreNull() {
			//when: Place 객체를 생성한다.
			Place result = Place.of(null, null, null);

			//then: 모든 내부 필드는 null로 설정된다.
			assertThat(result).isNull();
		}

		@Test
		@DisplayName("필수 항목만 있으면 Place 객체가 생성한다.")
		void createsPlace_whenOnlyPlaceNameAndRoadAddressAreGiven() {
			//given: 유효힌 장소명과 도로명주소를 준비한다.
			String placeName = LedgerTestData.PLACE_NAME;
			String roadAddress = LedgerTestData.ROAD_ADDRESS;

			//when: Place 객체를 생성한다.
			Place result = Place.of(placeName, roadAddress, null);
			
			//then: 주어진 값으로 설정된다.
			assertThat(result)
					.extracting(Place::getPlaceName, Place::getRoadAddress, Place::getDetailAddress)
					.containsExactly(placeName, roadAddress, null);
		}

		@Test
		@DisplayName("상세주소가 있으면 Place 객체가 생성한다.")
		void createsPlace_whenAllAddressFieldAreGiven() {
			//given: 유효한 장소명, 도로명주소, 상세주소를 준비한다.
			String placeName = LedgerTestData.PLACE_NAME;
			String roadAddress = LedgerTestData.ROAD_ADDRESS;
			String detailAddress = LedgerTestData.DETAIL_ADDRESS;

			//when: Place 객체를 생성한다.
			Place result = Place.of(placeName, roadAddress, detailAddress);
			
			//then: 값을 확인한다.
			assertThat(result)
					.extracting(Place::getPlaceName, Place::getRoadAddress, Place::getDetailAddress)
					.containsExactly(placeName, roadAddress, detailAddress);
		}

		@ParameterizedTest
		@MethodSource("validPlaceNameLengths")
		@DisplayName("장소명이 100글자 이하면 Place 객체를 생성한다.")
		void createsPlace_whenPlaceNameIsWithinRange(String placeName) {
			//given: 유효한 도로명주소를 준비한다.
			String roadAddress = LedgerTestData.ROAD_ADDRESS;

			//when: Place 객체를 생성한다.
			Place result = Place.of(placeName, roadAddress, null);
			
			//then: 정상적으로 객체가 생성된다.
			assertThat(result).isNotNull();
		}

		private static Stream<Arguments> validPlaceNameLengths() {
			return StringTestData.validLengths("가", 1, 100);
		}

		@ParameterizedTest
		@MethodSource("validPlaceNameFormats")
		@DisplayName("장소명이 허용된 글자만 있으면 Place 객체를 생성한다.")
		void createsPlace_whenPlaceNameHasValidCharacters(String placeName) {
			//given: 유효한 도로명주소를 준비한다.
			String roadAddress = LedgerTestData.ROAD_ADDRESS;

			//when: Place 객체를 생성한다.
			Place result = Place.of(placeName, roadAddress, null);

			//then: 정상적으로 객체가 생성된다.
			assertThat(result).isNotNull();
		}

		private static Stream<Arguments> validPlaceNameFormats() {
			return Stream.of(
					Arguments.of(named("영문이 포함된 장소", "CGV 강남점")),
					Arguments.of(named("숫자가 포함된 장소", "지하철역 1호선")),
					Arguments.of(named("괄호가 포함된 장소", "한국빌딩(신)")),
					Arguments.of(named("하이픈과 점이 포함된 장소", "88-올림픽.공원"))
			);
		}

		@ParameterizedTest
		@MethodSource("validRoadAddressLengths")
		@DisplayName("도로명주소가 300글자 이하면 Place 객체를 생성한다.")
		void createsPlace_whenRoadAddressIsWithinRange(String roadAddress) {
			//given: 유효한 장소명을 준비한다.
			String placeName = LedgerTestData.PLACE_NAME;

			//when: Place 객체를 생성한다.
			Place result = Place.of(placeName, roadAddress, null);

			//then: 정상적으로 객체가 생성된다.
			assertThat(result).isNotNull();
		}

		private static Stream<Arguments> validRoadAddressLengths() {
			return StringTestData.validLengths("가", 1, 300);
		}

		@ParameterizedTest
		@MethodSource("validRoadAddressFormats")
		@DisplayName("도로명주소에 허용된 글자만 있으면 Place 객체를 생성한다.")
		void createsPlace_whenRoadAddressHasValidCharacters(String roadAddress) {
			//given: 유효한 장소명을 준비한다.
			String placeName = LedgerTestData.PLACE_NAME;

			//when: Place 객체를 생성한다.
			Place result = Place.of(placeName, roadAddress, null);

			//then: 정상적으로 객체가 생성된다.
			assertThat(result).isNotNull();
		}

		private static Stream<Arguments> validRoadAddressFormats() {
			return Stream.of(
					Arguments.of(named("영문이 포함된 주소", "CGV 강남점")),
					Arguments.of(named("숫자가 포함된 장소", "지하철역 1호선")),
					Arguments.of(named("하이픈이 포함된 장소", "88-올림픽공원"))
			);
		}

		@ParameterizedTest(name = "[{index}] {0} - detailAddress: {1}")
		@MethodSource("validDetailAddressLengths")
		@DisplayName("상세주소가 500글자 이하면 Place 객체를 생성한다.")
		void createsPlace_whenDetailAddressIsWithinRange(String detailAddress) {
			//given: 유효한 장소명, 도로명주소를 준비한다.
			String placeName = LedgerTestData.PLACE_NAME;
			String roadAddress = LedgerTestData.ROAD_ADDRESS;

			//when: Place 객체를 생성한다.
			Place result = Place.of(placeName, roadAddress, detailAddress);

			//then: 정상적으로 객체가 생성된다.
			assertThat(result).isNotNull();
		}

		private static Stream<Arguments> validDetailAddressLengths() {
			return StringTestData.validLengths("가", 1, 500);
		}

		@ParameterizedTest
		@MethodSource("validDetailAddressFormats")
		@DisplayName("상세주소에 허용된 글자만 있으면 Place 객체를 생성한다.")
		void createsPlace_whenDetailAddressHasValidCharacters(String detailAddress) {
			//given: 유효한 장소명, 도로명주소를 준비한다.
			String placeName = LedgerTestData.PLACE_NAME;
			String roadAddress = LedgerTestData.ROAD_ADDRESS;

			//when: Place 객체를 생성한다.
			Place result = Place.of(placeName, roadAddress, detailAddress);

			//then: 정상적으로 객체가 생성된다.
			assertThat(result).isNotNull();
		}

		private static Stream<Arguments> validDetailAddressFormats() {
			return Stream.of(
					Arguments.of(named("영문이 포함된 주소", "2F 201호")),
					Arguments.of(named("숫자가 포함된 장소", "지하철역 1호선")),
					Arguments.of(named("괄호와 쉼표가 포함된 장소", "한국빌딩(#신)")),
					Arguments.of(named("하이픈과 쉼표 포함된 장소", "88-올림픽공원, 2층")),
					Arguments.of(named("슬래시와 점포함된 장소", "별빛마을/하늘.구름.달"))
			);
		}

	}

	@Nested
	@DisplayName("실패 케이스")
	class Failure {
		
		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("장소명이 없으면 예외가 발생한다.")
		void throwsException_whenOnlyRoadAddressIsGiven(String placeName) {
			//given: 유효한 도로명 주소만 준비한다.
			String roadAddress = LedgerTestData.ROAD_ADDRESS;

			//when: Place 객체를 생성한다.
			Throwable throwable = catchThrowable(() -> Place.of(placeName, roadAddress, null));
			
			//then: 장소명 누락 예외가 발생한다.
			ApplicationExceptionAssert.assertThatApplicationException(throwable)
					.hasErrorCode(REQUIRED_VALUE)
					.hasWork("장소 검증")
					.hasCauseMessage("장소명 없음")
					.hasField("placeName")
					.hasValue(placeName);
		}
		
		@ParameterizedTest
		@MethodSource("invalidPlaceNameLengths")
		@DisplayName("장소명이 100자를 초과하면 예외가 발생한다.")
		void throwsException_whenPlaceNameIsOutOfRange(String placeName) {
			//given: 최대 허용 길이를 초과한 도로명 주소를 준비한다.
			String roadAddress = LedgerTestData.ROAD_ADDRESS;
			String detailAddress = LedgerTestData.DETAIL_ADDRESS;

			//when: Place 객체를 생성한다.
			Throwable throwable = catchThrowable(() -> Place.of(placeName, roadAddress, detailAddress));
			
			//then: 장소명 길이 초과 예외가 발생한다.
			ApplicationExceptionAssert.assertThatApplicationException(throwable)
					.hasErrorCode(OUT_OF_RANGE)
					.hasWork("장소 검증")
					.hasCauseMessage("장소명 길이 초과")
					.hasField("placeName")
					.hasValue(String.valueOf(placeName.length()))
					.hasOption("maxLength", String.valueOf(100))
					.hasUserMessage("장소명", "다시 선택");
		}

		private static Stream<Arguments> invalidPlaceNameLengths() {
			return Stream.of(
					Arguments.of(named("101자", "가".repeat(101))),
					Arguments.of(named("150자", "가".repeat(150)))
			);
		}
		
		@ParameterizedTest
		@MethodSource("invalidPlaceNames")
		@DisplayName("장소명이 허용되지 않은 문자가 포함되면 예외가 발생한다.")
		void throwsException_whenPlaceNameContainsInvalidCharacters(String placeName) {
			//given: 유효하지 않은 장소명을 준비한다.
			String roadAddress = LedgerTestData.ROAD_ADDRESS;
			String detailAddress = LedgerTestData.DETAIL_ADDRESS;

			//when: Place 객체를 생성한다.
			Throwable throwable = catchThrowable(() -> Place.of(placeName, roadAddress, detailAddress));

			//then: 장소명 형식 검증 예외가 발생한다.
			ApplicationExceptionAssert.assertThatApplicationException(throwable)
					.hasErrorCode(INVALID_FORMAT)
					.hasWork("장소 검증")
					.hasCauseMessage("장소명 형식 불일치")
					.hasField("placeName")
					.hasValue(placeName)
					.hasOption("format", "한글, 영문, 숫자, 공백, 괄호, 하이픈, 점")
					.hasUserMessage("장소명", "다시 선택");
		}

		private static Stream<Arguments> invalidPlaceNames() {
			return Stream.of(
					Arguments.of(named("한자가 포함된 장소", "김家네")),
					Arguments.of(named("특수문자가 포함된 장소", "🍒야호★")),
					Arguments.of(named("허용되지 않은 문자가 포함된 장소", "아파트, ***호"))
			);
		}

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("도로명 주소가 없으면 예외가 발생한다.")
		void throwsException_whenOnlyPlaceNameIsGiven(String roadAddress) {
			//given: 유효한 장소 정보를 준비힌다.
			String placeName = LedgerTestData.PLACE_NAME;

			//when: Place 객체를 생성한다.
			Throwable throwable = catchThrowable(() -> Place.of(placeName, roadAddress, null));

			//then: 도로명 누락 예외가 발생한다.
			ApplicationExceptionAssert.assertThatApplicationException(throwable)
					.hasErrorCode(REQUIRED_VALUE)
					.hasWork("장소 검증")
					.hasCauseMessage("도로명 주소 없음")
					.hasField("roadAddress")
					.hasValue(roadAddress)
					.hasUserMessage("주소", "다시 선택");
		}

		@ParameterizedTest
		@MethodSource("invalidRoadAddressLengths")
		@DisplayName("도로명 주소가 300자를 초과하면 예외가 발생한다.")
		void throwsException_whenRoadAddressIsOutOfRange(String roadAddress) {
			//given: 유효한 장소명과 상세주소를 준비한다.
			String placeName = LedgerTestData.PLACE_NAME;
			String detailAddress = LedgerTestData.DETAIL_ADDRESS;
			
			//when: Place 객체를 생성한다.
			Throwable throwable = catchThrowable(() -> Place.of(placeName, roadAddress, detailAddress));
			
			//then: 도로명 주소 길이 초과 예외가 발생한다.
			ApplicationExceptionAssert.assertThatApplicationException(throwable)
					.hasErrorCode(OUT_OF_RANGE)
					.hasWork("장소 검증")
					.hasCauseMessage("도로명 주소 길이 초과")
					.hasField("roadAddress")
					.hasValue(String.valueOf(roadAddress.length()))
					.hasOption("min", String.valueOf(1))
					.hasOption("max", String.valueOf(300))
					.hasUserMessage("주소", "다시 선택");
		}

		private static Stream<Arguments> invalidRoadAddressLengths() {
			return Stream.of(
					Arguments.of(named("301자", "가".repeat(301))),
					Arguments.of(named("350자", "가".repeat(350)))
			);
		}

		@ParameterizedTest
		@MethodSource("invalidRoadAddress")
		@DisplayName("도로명 주소에 허용되지 않은 문자가 포함되면 예외가 발생한다.")
		void throwsException_whenRoadAddressContainsInvalidCharacters(String roadAddress) {
			//given: 유효한 장소명과 상세주소를 준비한다.
			String placeName = LedgerTestData.PLACE_NAME;
			String detailAddress = LedgerTestData.DETAIL_ADDRESS;

			//when: Place 객체를 생성한다.
			Throwable throwable = catchThrowable(() -> Place.of(placeName, roadAddress, detailAddress));

			//then: 도로명 주소 형식 검증 예외가 발생한다.
			ApplicationExceptionAssert.assertThatApplicationException(throwable)
					.hasErrorCode(INVALID_FORMAT)
					.hasWork("장소 검증")
					.hasCauseMessage("도로명 주소 형식 불일치")
					.hasField("roadAddress")
					.hasValue(roadAddress)
					.hasOption("format", "한글, 영문, 숫자, 공백, 하이픈")
					.hasUserMessage("주소", "다시 선택");
		}

		private static Stream<Arguments> invalidRoadAddress() {
			return Stream.of(
					Arguments.of(named("한자가 포함된 주소", "김家네")),
					Arguments.of(named("특수문자가 포함된 주소", "🍒야호★")),
					Arguments.of(named("허용되지 않은 문자가 포함된 주소", "아파트, ***호"))
			);
		}

		@ParameterizedTest
		@MethodSource("invalidDetailAddressLengths")
		@DisplayName("상세주소가 500자를 초과하면 예외가 발생한다.")
		void throwsException_whenDetailAddressIsOutOfRange(String detailAddress) {
			//given: 유효한 장소명과 도로명주소를 준비한다.
			String placeName = LedgerTestData.PLACE_NAME;
			String roadAddress = LedgerTestData.ROAD_ADDRESS;

			//when: Place 객체를 생성한다.
			Throwable throwable = catchThrowable(() -> Place.of(placeName, roadAddress, detailAddress));

			//then: 상세 주소 길이 초과 예외가 발생한다.
			ApplicationExceptionAssert.assertThatApplicationException(throwable)
					.hasErrorCode(OUT_OF_RANGE)
					.hasWork("장소 검증")
					.hasCauseMessage("상세 주소 길이 초과")
					.hasField("detailAddress")
					.hasValue(String.valueOf(detailAddress.length()))
					.hasOption("min", String.valueOf(0))
					.hasOption("max", String.valueOf(500))
					.hasUserMessage("상세 주소", "최대 500자");
		}

		private static Stream<Arguments> invalidDetailAddressLengths() {
			return Stream.of(
					Arguments.of(named("501자", "가".repeat(501))),
					Arguments.of(named("550자", "가".repeat(550)))
			);
		}

		@ParameterizedTest
		@MethodSource("invalidDetailAddressFormats")
		@DisplayName("상세주소에 허용되지 않은 문자가 포함되면 예외가 발생한다.")
		void throwsException_whenDetailAddressContainsInvalidCharacters(String detailAddress) {
			//given: 유효한 장소명과 도로명주소를 준비한다.
			String placeName = LedgerTestData.PLACE_NAME;
			String roadAddress = LedgerTestData.ROAD_ADDRESS;

			//when: Place 객체를 생성한다.
			Throwable throwable = catchThrowable(() -> Place.of(placeName, roadAddress, detailAddress));

			//then: 상세 주소 형식 검증 예외가 발생한다.
			ApplicationExceptionAssert.assertThatApplicationException(throwable)
					.hasErrorCode(INVALID_FORMAT)
					.hasWork("장소 검증")
					.hasCauseMessage("상세 주소 형식 불일치")
					.hasField("detailAddress")
					.hasValue(detailAddress)
					.hasOption("format", "한글, 영문, 숫자, 공백, 하이픈, 괄호, 쉼표, 슬래시, 점, #")
					.hasUserMessage("상세 주소", "입력 가능");
		}

		private static Stream<Arguments> invalidDetailAddressFormats() {
			return Stream.of(
					Arguments.of(named("한자가 포함된 주소", "김家네")),
					Arguments.of(named("특수문자가 포함된 주소", "🍒야호★")),
					Arguments.of(named("허용되지 않은 문자가 포함된 주소", "아파트, ***호"))
			);
		}

	}

}
