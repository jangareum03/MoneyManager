package com.moneymanager.vo;


import com.moneymanager.dto.common.ErrorDTO;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : AddressVOTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 31.<br>
 * 설명              : AddressVO 검증하는 테스트 클래스
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
 * 		 	  <td>25. 8. 31.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class AddressVOTest {

	private final String placeName = "스타벅스 강남에비뉴점";
	private final String roadAddress = "서울 서초구 서초대로 77길 62";
	private final String jiBunAddress = "강남구 서초동 1303-16";
	private final String detail = "강남역 아이파크 1층 B102~B105호";

	//실패케이스
	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("장소명이 미입력되면 예외가 발생한다.")
	void shouldThrowExceptionWhenPlaceNameIsNullOrEmpty(String placeName) {
		//when & then
		assertThatThrownBy(() -> new AddressVO(placeName, roadAddress, jiBunAddress, detail))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_PLACENAME_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("장소명은 필수입니다.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"A/N빌라", "서울시청(본근)", "롯데월드·몰", "법화로-26점", "강남@타워", "스페이스&무비"})
	@DisplayName("장소명은 특수문자를 입력하면 예외가 발생한다.")
	void shouldThrowExceptionWhenPlaceNameHasSpecial(String place) {
		//when & then
		assertThatThrownBy(() -> new AddressVO(place, roadAddress, jiBunAddress, detail))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_PLACENAME_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("장소명은 한글, 숫자, 영문자만 입력 가능합니다.");
				});
	}


	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("도로명주소와 지번주소 둘 다 입력하지 않으면 예외가 발생한다.")
	void shouldThrowExceptionWhenRoadOrJiBunIsEmpty(String value) {
		//when&then
		assertThatThrownBy(() -> new AddressVO(placeName, value, value, detail))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_ADDRESS_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("도로명주소 또는 지번주소 중 하나는 필수입니다.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"세종-대로", "광화문로(북측)", "법화로 26 (하원동)", "A/N로", "세종대로·-", "세종·대로?"})
	@DisplayName("도로명 주소는 · 제외한 특수문자가 입력되면 예외가 발생한다.")
	void shouldThrowExceptionWhenRoadHasSpecial(String road) {
		assertThatThrownBy(() -> new AddressVO(placeName, road, jiBunAddress, detail))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_ROAD_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("도로명주소는 한글, 숫자, 영문자, 특수문자(·)만 입력 가능합니다.");
				});
	}


	@ParameterizedTest
	@ValueSource(strings = {"세종·대로", "광화문로(북측)", "법화로 26 (하원동)", "A/N로", "세종대로·-", "세종-대로?"})
	@DisplayName("지번 주소는 - 제외한 특수문자가 입력되면 예외가 발생한다.")
	void shouldThrowExceptionWhenJiBunHasSpecial(String jiBun) {
		assertThatThrownBy(() -> new AddressVO(placeName, roadAddress, jiBun, detail))
				.isInstanceOf(ClientException.class)
				.satisfies(e -> {
					ClientException ce = (ClientException) e;
					ErrorDTO<?> errorDTO = ce.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.COMMON_JIBUN_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("지번주소는 한글, 숫자, 영문자, 특수문자(-)만 입력 가능합니다.");
				});
	}


	//성공 케이스
	@ParameterizedTest
	@ValueSource(strings = {"GT타워", "서울숲트리마제", "코엑스 Hall A", "인천국제공항 제1여객터미널"})
	@DisplayName("장소명에 한글, 숫자, 영문자, 공백을 입력하면 AddressVO 생성이 가능하다.")
	void shouldCreateAddressVOWhenValidPlaceName(String name) {
		//when
		AddressVO vo = new AddressVO(name, roadAddress, jiBunAddress, detail);

		//then
		assertThat(vo)
				.isNotNull()
				.extracting(AddressVO::getPlaceName, AddressVO::getRoadAddress, AddressVO::getJiBunAddress, AddressVO::getDetailAddress)
				.containsExactly(name, roadAddress, jiBunAddress, detail);
	}


	@ParameterizedTest
	@ValueSource(strings = {"세종대로", "3·1대로", "강북구 4·11로", "광화문로 1길"})
	@DisplayName("도로명 주소는 한글,숫자,특수문자(·)를 입력하면  AddressVO 생성이 가능하다.")
	void shouldCreateAddressVOWhenValidRoadName(String roadAddress) {
		//when
		AddressVO vo = new AddressVO(placeName, roadAddress, jiBunAddress, detail);

		//then
		assertThat(vo).isNotNull()
				.extracting(AddressVO::getPlaceName, AddressVO::getRoadAddress, AddressVO::getJiBunAddress, AddressVO::getDetailAddress)
				.containsExactly(placeName, roadAddress, jiBunAddress, detail);

		assertThatCode(() -> new AddressVO(placeName, roadAddress, jiBunAddress, detail))
				.doesNotThrowAnyException();
	}



	@ParameterizedTest
	@ValueSource(strings = {"세종대로", "3-1대로", "강북구 4-11로", "광화문로 1길"})
	@DisplayName("지번 주소는 한글,숫자,특수문자(-)만 입력 가능합니다.")
	void shouldCreateAddressVOWhenValidJiBunName(String jiBunAddress) {
		//when
		AddressVO vo = new AddressVO(placeName, roadAddress, jiBunAddress, detail);

		//then
		assertThat(vo).isNotNull()
				.extracting(AddressVO::getPlaceName, AddressVO::getRoadAddress, AddressVO::getJiBunAddress, AddressVO::getDetailAddress)
				.containsExactly(placeName, roadAddress, jiBunAddress, detail);

		assertThatCode(() -> new AddressVO(placeName, roadAddress, jiBunAddress, detail))
				.doesNotThrowAnyException();
	}


	@ParameterizedTest
	@CsvSource({
			//도로명주소, 지번주소 순
			"'서울시 강남구 테헤란로 123', ",
			", '서울시 강남구 123-45'",
			"'서울시 강남구 테헤란로 123', '서울시 강남구 123-45'"
	})
	@DisplayName("도로명주소나 지번주소 중 하나라도 입력되면 AddressVO 생성이 가능하다.")
	void shouldCreateAddressVOWhenAtLeastOneAddressIsProvided(String roadAddress, String jiBunAddress){
		//when
		AddressVO vo = new AddressVO(placeName, roadAddress, jiBunAddress, detail);

		//then
		assertThat(vo).isNotNull()
				.extracting(AddressVO::getPlaceName, AddressVO::getRoadAddress, AddressVO::getJiBunAddress, AddressVO::getDetailAddress)
				.containsExactly(placeName, roadAddress, jiBunAddress, detail);

		assertThatCode(() -> new AddressVO(placeName, roadAddress, jiBunAddress, detail))
				.doesNotThrowAnyException();
	}



	@Test
	@DisplayName("장소명, 도로명 주소, 지번주소 모두 입력하면 AddressVO 생성이 성공한다.")
	void shouldCreateAddressWhenAllFieldsAreValid() {
		//when
		AddressVO vo = new AddressVO(placeName, roadAddress, jiBunAddress, detail);

		//then
		assertThat(vo.placeName).isEqualTo(placeName);
		assertThat(vo.roadAddress).isEqualTo(roadAddress);
		assertThat(vo.jiBunAddress).isEqualTo(jiBunAddress);
	}

}