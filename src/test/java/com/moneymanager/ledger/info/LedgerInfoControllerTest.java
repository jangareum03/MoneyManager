package com.moneymanager.ledger.info;

import com.moneymanager.config.SecurityConfig;
import com.moneymanager.controller.web.GlobalWebControllerAdvice;
import com.moneymanager.controller.web.main.LedgerController;
import com.moneymanager.domain.ledger.dto.CategoryResponse;
import com.moneymanager.domain.ledger.dto.LedgerDetailResponse;
import com.moneymanager.domain.ledger.dto.LedgerEditResponse;
import com.moneymanager.domain.ledger.dto.LedgerFixedResponse;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.*;
import com.moneymanager.domain.ledger.vo.AmountInfo;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.domain.member.dto.MemberLoginResponse;
import com.moneymanager.security.jwt.JwtAuthenticationFilter;
import com.moneymanager.service.main.CategoryService;
import com.moneymanager.service.main.ImageServiceImpl;
import com.moneymanager.service.main.LedgerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.info<br>
 * 파일이름       : LedgerInfoControllerTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 10<br>
 * 설명              : 가계부 정보 요청에 대한 컨트롤러 테스트 클래스
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
 * 		 	  <td>25. 12. 10</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@WebMvcTest(
		controllers = LedgerController.class,
		excludeFilters = {
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						classes = {
								SecurityConfig.class,
								JwtAuthenticationFilter.class,
								GlobalWebControllerAdvice.class
						}
				)
		}
)
@AutoConfigureMockMvc(addFilters = false)
public class LedgerInfoControllerTest {

	@Autowired	private MockMvc	mockMvc;

	@MockBean	private LedgerService ledgerService;
	@MockBean	private CategoryService categoryService;
	@MockBean	@Qualifier("ledgerImage") private ImageServiceImpl imageService;

	@DisplayName("일회성 가게부가 수정모드면 고정주기가 화면에 안보여야 한다.")
	@Test
	void 일회성_가계부_수정모드면_정상동작() throws Exception {
		//given
		LedgerEditResponse mockResponse = LedgerEditResponse.builder()
				.date("2025. 11. 12 (수)")
				.type(LedgerType.fromCode("010101"))
				.fixed(
						LedgerFixedResponse.from(
								Ledger.builder().fixed(FixedYN.VARIABLE).cycleType(null).build()
						)
				)
				.category(List.of(
						CategoryResponse.builder().code("010000").name("수입").build(),
						CategoryResponse.builder().code("010100").name("소득").build(),
						CategoryResponse.builder().code("010101").name("월급").build()
				))
				.memo("강아지 똥")
				.amountInfo(AmountInfo.builder().amount(15000L).type(PaymentType.NONE).build())
				.place(Place.builder().placeName("강남 CGV").roadAddress("서울특별시 강남구 강남대로 123").detailAddress("☆☆빌딩 2층").build())
				.build();
		when(ledgerService.getLedgerEdit("member123", "123"))
						.thenReturn(mockResponse);

		when(categoryService.getAllCategoriesByCode(mockResponse.getCategory().get(mockResponse.getCategory().size() -1).getCode())).thenReturn(
				java.util.Map.of(
						CategoryLevel.MIDDLE.name(), List.of(
								CategoryResponse.builder().code("010100").name("소득1").build(),
								CategoryResponse.builder().code("010200").name("소득2").build(),
								CategoryResponse.builder().code("010300").name("소득3").build()
						),
						CategoryLevel.LOW.name(), List.of(
								CategoryResponse.builder().code("010101").name("소1").build(),
								CategoryResponse.builder().code("010102").name("소2").build(),
								CategoryResponse.builder().code("010103").name("소3").build(),
								CategoryResponse.builder().code("010104").name("소4").build(),
								CategoryResponse.builder().code("010105").name("소5").build()
						)
				)
		);

		//when
		mockMvc.perform(
				MockMvcRequestBuilders.get("/ledgers/123")
						.param("mode", "edit")
						.sessionAttr("mid", "member123")
						.requestAttr("member", MemberLoginResponse.Success.builder()
								.nickName("홍길동").profile(null).build())
		)
				.andExpect(status().isOk())
				.andExpect(view().name("/main/ledger_update"))
				.andExpect(model().attributeExists("ledger"))
				.andExpect(model().attributeExists("category"))
				.andDo(print());
	}

	@DisplayName("고정 가계부가 수정모드면 고정주기가 화면에 보여야 한다.")
	@Test
	void 고정_가계부_수정모드면_정상동작() throws Exception {
		//given
		LedgerEditResponse mockResponse = LedgerEditResponse.builder()
				.date("2025. 11. 12 (수)")
				.type(LedgerType.fromCode("010101"))
				.fixed(
						LedgerFixedResponse.from(
								Ledger.builder().fixed(FixedYN.REPEAT).cycleType(FixedPeriod.YEARLY).build()
						)
				)
				.category(List.of(
						CategoryResponse.builder().code("010000").name("수입").build(),
						CategoryResponse.builder().code("010100").name("소득").build(),
						CategoryResponse.builder().code("010101").name("월급").build()
				))
				.memo("강아지 똥")
				.amountInfo(AmountInfo.builder().amount(15000L).type(PaymentType.NONE).build())
				.images(
						List.of("강아지.png", "고양이.jpg", "다람쥐.jpg")
				)
				.build();
		when(ledgerService.getLedgerEdit("member123", "123"))
				.thenReturn(mockResponse);

		when(categoryService.getAllCategoriesByCode(mockResponse.getCategory().get(mockResponse.getCategory().size() -1).getCode())).thenReturn(
				java.util.Map.of(
						CategoryLevel.MIDDLE.name(), List.of(
								CategoryResponse.builder().code("010100").name("소득1").build(),
								CategoryResponse.builder().code("010200").name("소득2").build(),
								CategoryResponse.builder().code("010300").name("소득3").build()
						),
						CategoryLevel.LOW.name(), List.of(
								CategoryResponse.builder().code("010101").name("소1").build(),
								CategoryResponse.builder().code("010102").name("소2").build(),
								CategoryResponse.builder().code("010103").name("소3").build(),
								CategoryResponse.builder().code("010104").name("소4").build(),
								CategoryResponse.builder().code("010105").name("소5").build()
						)
				)
		);

		when(imageService.getImageSlots("member123")).thenReturn(List.of(true, true, true));

		//when
		mockMvc.perform(
						MockMvcRequestBuilders.get("/ledgers/123")
								.param("mode", "edit")
								.sessionAttr("mid", "member123")
								.requestAttr("member", MemberLoginResponse.Success.builder()
										.nickName("홍길동").profile(null).build())
				)
				.andExpect(status().isOk())
				.andExpect(view().name("/main/ledger_update"))
				.andExpect(model().attributeExists("ledger"))
				.andExpect(model().attributeExists("category"))
				.andExpect(model().attributeExists("slots"))
				.andDo(print());
	}

	@DisplayName("모드가 view면 가계부 상세내역 화면을 보여줘야 한다.")
	@Test
	void 상세모드_상세화면_이등() throws Exception {
		//given
		List<String> images = new ArrayList<>();
		images.add("/2025/11/01/3c2a8f0e-7d6b-4e1c-9f5a-0d4b3c2e1f0d.png");
		images.add(null);
		images.add(null);

		LedgerDetailResponse mockResponse = LedgerDetailResponse.builder()
				.date("2025. 11. 01 (토)")
				.type(LedgerType.INCOME)
				.category(CategoryResponse.builder().code("010101").name("월급").build())
				.memo("얏호~~")
				.amountInfo(AmountInfo.builder().amount(25000L).type(PaymentType.CASH).build())
				.place(Place.builder().placeName("성수 빵집").roadAddress("서울특별시 성수동").detailAddress("상세주소").build())
				.images(images)
				.build();

		when(ledgerService.getLedgerDetail("member123", "123"))
				.thenReturn(mockResponse);

		//when
		mockMvc.perform(
				MockMvcRequestBuilders.get("/ledgers/123")
						.param("mode", "view")
						.sessionAttr("mid", "member123")
						.requestAttr("member", MemberLoginResponse.Success.builder()
								.nickName("홍길동").profile(null).build())
		)
				.andExpect(status().isOk())
				.andExpect(view().name("/main/ledger_detail"))
				.andExpect(model().attributeExists("ledger"))
				.andDo(print());
	}
}
