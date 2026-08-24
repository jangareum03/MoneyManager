package com.moneymanager.ledger.domain.dto.vo;

import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.StringTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

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
@DisplayName("Place 객체를 생성할 때")
public class PlaceTest {

	@Nested
	@DisplayName("성공")
	class Success {

		@ParameterizedTest
		@NullAndEmptySource
		@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
		@DisplayName("필수 필드가 모두 null이거나 비어있으면 null을 반환한다.")
		void returnsNull_whenRequiredFieldsAreNull(String value) {
			//when
			Place result = Place.ofOrNull(value, value, value);

			//then
			assertThat(result).isNull();
		}

		@Test
		@DisplayName("유효한 장소명과 도로명주소만 있으면 생성한다.")
		void createsPlace_whenNameAndRoadAddressAreValid() {
			//given
			String placeName = LedgerTestData.PLACE_NAME;
			String roadAddress = LedgerTestData.ROAD_ADDRESS;

			//when
			Place result = Place.ofOrNull(placeName, roadAddress, null);
			
			//then
			assertThat(result).isNotNull();
		}
		
		@Test
		@DisplayName("유효한 상세주소가 포함되면 생성한다.")
		void createsPlace_whenDetailAddressIsIncluded() {
			//given
			String placeName = LedgerTestData.PLACE_NAME;
			String roadAddress = LedgerTestData.ROAD_ADDRESS;
			String detailAddress = LedgerTestData.DETAIL_ADDRESS;

			//when
			Place result = Place.ofOrNull(placeName, roadAddress, detailAddress);

			//then
			assertThat(result).isNotNull();

			assertThat(result.getPlaceName()).isEqualTo(placeName);
			assertThat(result.getRoadAddress()).isEqualTo(roadAddress);
			assertThat(result.getDetailAddress()).isEqualTo(detailAddress);
		}

		@ParameterizedTest
		@MethodSource("validPlaceNameLengths")
		@DisplayName("장소명이 100글자 이하면 생성한다.")
		void createsPlace_whenPlaceNameIsWithinRange(String placeName) {
			//given
			String roadAddress = LedgerTestData.ROAD_ADDRESS;

			//when
			Place result = Place.ofOrNull(placeName, roadAddress, null);
			
			//then
			assertThat(result).isNotNull();
		}

		private static Stream<Arguments> validPlaceNameLengths() {
			return StringTestData.validLengths("가", 1, 100);
		}

		@ParameterizedTest
		@MethodSource("validRoadAddressLengths")
		@DisplayName("도로명주소가 300글자 이하면 생성한다.")
		void createsPlace_whenRoadAddressIsWithinRange(String roadAddress) {
			//given
			String placeName = LedgerTestData.PLACE_NAME;

			//when
			Place result = Place.ofOrNull(placeName, roadAddress, null);

			//then
			assertThat(result).isNotNull();
		}

		private static Stream<Arguments> validRoadAddressLengths() {
			return StringTestData.validLengths("가", 1, 300);
		}

		@ParameterizedTest(name = "[{index}] {0} - detailAddress: {1}")
		@MethodSource("validDetailAddressLengths")
		@DisplayName("상세주소가 300글자 이하면 생성한다.")
		void createsPlace_whenDeailAddressIsWithinRange(String detailAddress) {
			//given
			String placeName = LedgerTestData.PLACE_NAME;
			String roadAddress = LedgerTestData.ROAD_ADDRESS;

			//when
			Place result = Place.ofOrNull(placeName, roadAddress, detailAddress);

			//then
			assertThat(result).isNotNull();
		}

		private static Stream<Arguments> validDetailAddressLengths() {
			return StringTestData.validLengths("가", 1, 300);
		}

	}

	@Nested
	@DisplayName("실패")
	class Failure {

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("장소명만 없으면 예외를 발생시킨다.")
		void throwsException_whenOnlyPlaceNameIsGiven(String placeName) {
			//given
			String roadAddress = LedgerTestData.ROAD_ADDRESS;
			String detailAddress = LedgerTestData.DETAIL_ADDRESS;

			//when
			Throwable throwable = catchThrowable(() -> Place.ofOrNull(placeName, roadAddress, detailAddress));

			//then
			ApplicationExceptionAssert.assertThatApplicationException(throwable)
					
					.hasErrorCode(REQUIRED_VALUE)
					.hasWork("Place 생성")
					.hasCauseMessage("필수값 누락")
					.hasTarget(Place.class)
					.hasValue("placeName", placeName);
		}

		@ParameterizedTest
		@MethodSource("invalidPlaceNameLengths")
		@DisplayName("장소명이 100자를 초과하면 예외를 발생시킨다.")
		void throwsValidationException_whenPlaceNameIsOutOfRange(String placeName) {
			//given
			String roadAddress = LedgerTestData.ROAD_ADDRESS;
			String detailAddress = LedgerTestData.DETAIL_ADDRESS;

			//when
			Throwable throwable = catchThrowable(() -> Place.ofOrNull(placeName, roadAddress, detailAddress));
			
			//then
			ApplicationExceptionAssert.assertThatApplicationException(throwable)
					.hasErrorCode(OUT_OF_LENGTH)
					.hasWork("Place 생성")
					.hasCauseMessage("길이 초과")
					.hasValue("placeName", placeName)
					.hasOption("min", 1)
					.hasOption("max", 100);
		}

		private static Stream<Arguments> invalidPlaceNameLengths() {
			return StringTestData.invalidLengths("가", 1, 100);
		}

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("도로명 주소만 있으면 예외를 발생시킨다.")
		void throwsValidationException_whenOnlyPlaceNameIsGiven(String roadAddress) {
			//given
			String placeName = LedgerTestData.PLACE_NAME;
			String detailAddress = LedgerTestData.DETAIL_ADDRESS;

			//when
			Throwable throwable = catchThrowable(() -> Place.ofOrNull(placeName, roadAddress, detailAddress));

			//then
			ApplicationExceptionAssert.assertThatApplicationException(throwable)
					.hasErrorCode(REQUIRED_VALUE)
					.hasWork("Place 생성")
					.hasTarget(Place.class)
					.hasValue("roadAddress", roadAddress);
		}

		@ParameterizedTest
		@MethodSource("invalidRoadAddressLengths")
		@DisplayName("도로명 주소가 300자를 초과하면 예외를 발생시킨다.")
		void throwsValidationException_whenRoadAddressIsOutOfRange(String roadAddress) {
			//given
			String placeName = LedgerTestData.PLACE_NAME;
			String detailAddress = LedgerTestData.DETAIL_ADDRESS;
			
			//when
			Throwable throwable = catchThrowable(() -> Place.ofOrNull(placeName, roadAddress, detailAddress));
			
			//then
			ApplicationExceptionAssert.assertThatApplicationException(throwable)
					.hasErrorCode(OUT_OF_LENGTH)
					.hasWork("Place 생성")
					.hasTarget(Place.class)
					.hasValue("roadAddress", roadAddress)
					.hasOption("min", 1)
					.hasOption("max", 300);
		}

		private static Stream<Arguments> invalidRoadAddressLengths() {
			return StringTestData.invalidLengths("가", 1, 300);
		}

		@ParameterizedTest
		@MethodSource("invalidDetailAddressLengths")
		@DisplayName("상세주소가 300자를 초과하면 예외를 발생시킨다.")
		void throwsValidationException_whenDetailAddressIsOutOfRange(String detailAddress) {
			//given
			String placeName = LedgerTestData.PLACE_NAME;
			String roadAddress = LedgerTestData.ROAD_ADDRESS;

			//when
			Throwable throwable = catchThrowable(() -> Place.ofOrNull(placeName, roadAddress, detailAddress));

			//then
			ApplicationExceptionAssert.assertThatApplicationException(throwable)
					.hasErrorCode(OUT_OF_LENGTH)
					.hasWork("Place 생성")
					.hasTarget(Place.class)
					.hasOption("min", 0)
					.hasOption("max", 300);
		}

		private static Stream<Arguments> invalidDetailAddressLengths() {
			return StringTestData.invalidLengths("가", 1, 300);
		}

	}

}
