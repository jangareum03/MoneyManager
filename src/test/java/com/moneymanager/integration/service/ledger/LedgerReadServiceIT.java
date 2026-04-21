package com.moneymanager.integration.service.ledger;

import com.moneymanager.domain.global.Policy;
import com.moneymanager.domain.ledger.dto.response.CategoryResponse;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep1Response;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep2Response;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.domain.member.enums.MemberType;
import com.moneymanager.repository.member.MemberRepository;
import com.moneymanager.security.support.WithMockCustomUser;
import com.moneymanager.service.ledger.LedgerReadService;
import com.moneymanager.unit.fixture.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.integration.service.ledger<br>
 * 파일이름       : LedgerReadServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 15<br>
 * 설명              : LedgerReadService 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 4. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class LedgerReadServiceIT {


	@Autowired
	private LedgerReadService service;

	@Autowired
	private MemberRepository memberRepository;

	private static final LocalDate TEST_DATE = LocalDate.of(2026, 3, 10);


	@BeforeEach
	void setUp() {
		memberRepository.deleteAll();;

		memberRepository.save(
				MemberFixture.defaultMember()
				.id("UCt01001")
				.type(MemberType.NORMAL)
				.name("김철수")
				.birthDate("20001010")
				.nickName("철수")
				.email("cheolsu@test.com")
				.memberInfo(MemberFixture.defaultMemberInfo())
				.build());
	}


	@Test
	@DisplayName("작성 1단계 요청 데이터가 정상 생성된다.")
	void createsWriteStep1Data_successfully() {
		//when
		LedgerWriteStep1Response result = service.getWriteStep1Data();

		//then
		assertThat(result).isNotNull();
		assertThat(result.getTypes()).isNotNull().isNotEmpty();
		assertThat(result.getYears()).isNotNull().isNotEmpty();
		assertThat(result.getMonths()).isNotNull().isNotEmpty();
		assertThat(result.getDays()).isNotNull().isNotEmpty();
		assertThat(result.getCurrentYear()).isNotZero();
		assertThat(result.getCurrentMonth()).isNotZero();
		assertThat(result.getCurrentDay()).isNotZero();
		assertThat(result.getDisplayDate()).isNotBlank();
	}


	@WithMockCustomUser
	@Nested
	@DisplayName("작성 2단계 응답 데이터")
	class Step2ResponseTest {

		@Test
		@DisplayName("수입유형 요청 시 수입 작성 2단계 응답을 반환된다.")
		void returnsIncomeResponse_whenCategoryTypeIsIncome() {
			//given
			CategoryType type = CategoryType.INCOME;

			//when
			LedgerWriteStep2Response result = service.getWriteStep2Data(type, TEST_DATE);

			//then
			assertThat(result).isNotNull();

			assertThat(result.getCategories()).hasSize(3);
			assertThat(result.getCategories())
					.allMatch(category -> category.getCode().startsWith("01"))
					.allMatch(category -> category.getCode().endsWith("00"));

			assertThat(result.getImageSlot()).hasSize(Policy.LEDGER_MAX_IMAGE);

			assertThat(result.getTitle()).isEqualTo("2026년 03월 10일 화요일");
		}

		@Test
		@DisplayName("지출유형 요청 시 지출 작성 2단계 응답을 반환된다.")
		void returnsOutlayResponse_whenCategoryTypeIsOutlay() {
			//given
			CategoryType type = CategoryType.OUTLAY;

			//when
			LedgerWriteStep2Response result = service.getWriteStep2Data(type, TEST_DATE);

			//then
			assertThat(result).isNotNull();

			assertThat(result.getCategories()).hasSize(9);
			assertThat(result.getCategories())
					.allMatch(category -> category.getCode().startsWith("02"))
					.allMatch(category -> category.getCode().endsWith("00"));

			assertThat(result.getImageSlot()).hasSize(Policy.LEDGER_MAX_IMAGE);

			assertThat(result.getTitle()).isEqualTo("2026년 03월 10일 화요일");
		}

		@ParameterizedTest
		@EnumSource(CategoryType.class)
		@DisplayName("중분류 카테고리만 조회되어 응답에 포함된다.")
		void returnsStep2Response_onlyMiddleLevelCategories(CategoryType type) {
			//when
			LedgerWriteStep2Response result = service.getWriteStep2Data(type, TEST_DATE);

			//then
			List<CategoryResponse> categories = result.getCategories();

			assertThat(categories)
					.allMatch(c -> !c.getCode().startsWith("00", 2));
		}

	}

}
