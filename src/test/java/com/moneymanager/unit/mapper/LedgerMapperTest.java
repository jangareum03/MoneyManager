package com.moneymanager.unit.mapper;

import com.moneymanager.domain.ledger.dto.response.CategoryResponse;
import com.moneymanager.domain.ledger.dto.response.LedgerDetailResponse;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.domain.ledger.enums.AmountType;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.fixture.category.CategoryTreeFixture;
import com.moneymanager.fixture.ledger.LedgerFixture;
import com.moneymanager.fixture.ledger.LedgerImageFixture;
import com.moneymanager.mapper.LedgerMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * <p>
 * 패키지이름    : com.moneymanager.mapper<br>
 * 파일이름       : LedgerMapperTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 30<br>
 * 설명              : LedgerMapper 클래스 로직을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 4. 30</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class LedgerMapperTest {

	private final LedgerMapper target = new LedgerMapper();

	@Nested
	@DisplayName("상세 가계부 응답 변환")
	class DetailDtoMapperTest {

		@Test
		@DisplayName("필수값만 있는 요청이면 정상적으로 LedgerDetailResponse을 반환한다.")
		void buildsResponse_whenEssentialFieldGiven() {
			//given
			Ledger incomeLedger = LedgerFixture.defaultLedger().build();
			Category category = CategoryTreeFixture.incomeSalary();
			List<LedgerImage> imageList = Arrays.asList(null, null, null);

			//when
			LedgerDetailResponse result = target.toDetailDto(incomeLedger, category, imageList);

			//then
			assertThat(result).isNotNull();

			//가계부 검증
			assertThat(result.getDate()).isEqualTo("2026. 01. 01 (목)");
			assertThat(result.getType()).isSameAs(CategoryType.INCOME);
			assertThat(result.getAmount()).isEqualTo(10000L);
			assertThat(result.getPaymentType()).isSameAs(AmountType.NONE);

			assertThat(result)
					.extracting(
							LedgerDetailResponse::getPlaceName,
							LedgerDetailResponse::getRoadAddress,
							LedgerDetailResponse::getDetailAddress
					).containsOnlyNulls();

			//카테고리 검증
			assertThat(result.getCategory())
					.extracting(CategoryResponse::getCode, CategoryResponse::getName)
					.containsExactly("010101", "월급");

			//이미지 검증
			assertThat(result.getImages()).hasSize(3).containsOnlyNulls();
		}

		@Test
		@DisplayName("선택값도 있는 요청이면 정상적으로 LedgerDetailResponse을 반환한다.")
		void buildsResponse_whenFullFieldsGiven() {
			//given
			Ledger incomeLedger = LedgerFixture.withPlace().memo("메모").build();
			Category category = CategoryTreeFixture.incomeSalary();
			List<LedgerImage> imageList = Collections.emptyList();

			//when
			LedgerDetailResponse result = target.toDetailDto(incomeLedger, category, imageList);

			//then
			assertThat(result).isNotNull();
			assertThat(result.getMemo()).isEqualTo("메모");

			//장소 검증
			assertThat(result)
					.extracting(LedgerDetailResponse::getPlaceName, LedgerDetailResponse::getRoadAddress, LedgerDetailResponse::getDetailAddress)
					.containsExactly("CGV 강남점", "서울특별시 강남구 강남대로 438 스타플렉스", "4층");
		}

		@Test
		@DisplayName("사진이 포함된 요청이면 정상적으로 LedgerDetailResponse을 반환한다.")
		void buildsResponse_whenImagesExist() {
			//given
			Ledger incomeLedger = LedgerFixture.withPlace().memo("메모").build();
			Category category = CategoryTreeFixture.incomeSalary();
			List<LedgerImage> imageList = Arrays.asList(
					LedgerImageFixture.defaultImage(1L, 1L).sortOrder(1).build(),
					LedgerImageFixture.defaultImage(2L, 1L).sortOrder(2).build(),
					null
			);

			//when
			LedgerDetailResponse result = target.toDetailDto(incomeLedger, category, imageList);

			//then
			assertThat(result).isNotNull();

			//이미지 경로 검증
			List<String> imagePaths = result.getImages();

			assertThat(imagePaths).hasSize(3);
			assertThat(imagePaths)
					.containsExactly("test-mid/images/default.png", "test-mid/images/default.png", null);
		}

	}

}
