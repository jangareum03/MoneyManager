package com.moneymanager.budgetBook.domain;

import com.moneymanager.domain.budgetBook.vo.Place;
import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <p>
 * 패키지이름    : com.moneymanager.budgetBook.domain<br>
 * 파일이름       : PlaceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 24.<br>
 * 설명              : Place 검증하는 테스트 클래스
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
 * 		 	  <td>25. 11. 24.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class PlaceTest {

	private String placeName;			//장소명
	private String roadAddress;		//도로명 주소
	private String jiBunAddress;		//지번주소
	private String detailAddress;		//상세주소

	@BeforeEach
	void setUp(){
		this.placeName = "CGV 강남점";
		this.roadAddress = "서울 강남구 테헤란로 123";
		this.jiBunAddress = "서울 강남구 123-45";
		this.detailAddress = "강남빌딩 A동 3층";
	}

	@DisplayName("장소명이 없거나 null이면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@NullAndEmptySource
	void 장소명_없으면_예외발생(String place){
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> new Place(place, roadAddress, jiBunAddress, detailAddress))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_PLACE_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("장소명은 필수입니다.");
				});
	}

	@DisplayName("장소명에 특수문자가 포함되면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(strings = {"올리브영-A", "A&W"})
	void 장소명_특수문자_예외발생(String placeName) {
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> new Place(placeName, roadAddress, jiBunAddress, detailAddress))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_PLACE_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("장소명은 한글, 숫자, 영문자만 입력 가능합니다.");
				});
	}

	@DisplayName("도로명주소와 지번주소 둘 다 없으면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@NullAndEmptySource
	void 도로명과지번_모두없으면_예외발생(String address){
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> new Place(placeName, address, address, detailAddress))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_PLACE_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo("주소는 필수입니다.");
				});
	}

	@DisplayName("도로명 주소에 특수문자(· 제외) 포함되면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(strings = {"강남구... 테헤란로", "강남 123&34", "로로 姜"})
	void 도로명_특수문자_예외발생(String roadAddress) {
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> new Place(placeName, roadAddress, jiBunAddress, detailAddress))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_PLACE_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("도로명주소는 한글, 숫자, 영문자, 특수문자(·)만 입력 가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(roadAddress);
				});
	}

	@DisplayName("지번 주소에 특수문자(- 제외) 포함되면 ClientException 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(strings = {"강남구... 1234", "강남 123&34", "지번주소 姜빌딩 3층", "안녕 4F"})
	void 지번_특수문자_예외발생(String jiBunAddress) {
		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> new Place(placeName, roadAddress, jiBunAddress, detailAddress))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.BUDGET_PLACE_FORMAT);
					assertThat(errorDTO.getMessage()).isEqualTo("지번주소는 한글, 숫자, 영문자, 특수문자(-)만 입력 가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(jiBunAddress);
				});
	}

	@DisplayName("장소명과 도로명 주소가 있으면 객체를 생성한다.")
	@ParameterizedTest
	@NullAndEmptySource
	void 지번주소만_없으면_객체생성(String jiBunAddress) {
		//when
		Place result = new Place(placeName, roadAddress, jiBunAddress, detailAddress);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getPlaceName()).isEqualTo(placeName);
		assertThat(result.getRoadAddress()).isEqualTo(roadAddress);
		assertThat(result.getJiBunAddress()).isEqualTo(jiBunAddress);
		assertThat(result.getDetailAddress()).isEqualTo(detailAddress);
	}

	@DisplayName("장소명과 지번 주소가 있으면 객체를 생성한다.")
	@ParameterizedTest
	@NullAndEmptySource
	void 도로명주소만_없으면_객체생성(String roadAddress){
		//when
		Place result = new Place(placeName, roadAddress, jiBunAddress, detailAddress);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getPlaceName()).isEqualTo(placeName);
		assertThat(result.getRoadAddress()).isEqualTo(roadAddress);
		assertThat(result.getJiBunAddress()).isEqualTo(jiBunAddress);
		assertThat(result.getDetailAddress()).isEqualTo(detailAddress);
	}

	@DisplayName("상세주소가 없어도 객체를 생성한다.")
	@ParameterizedTest
	@NullAndEmptySource
	void 상세주소_없으면_객체생성(String detailAddress){
		//when
		Place result = new Place(placeName, roadAddress, jiBunAddress, detailAddress);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getPlaceName()).isEqualTo(placeName);
		assertThat(result.getRoadAddress()).isEqualTo(roadAddress);
		assertThat(result.getJiBunAddress()).isEqualTo(jiBunAddress);
		assertThat(result.getDetailAddress()).isEqualTo(detailAddress);
	}
}
