package com.moneymanager.ledger.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneymanager.config.SecurityConfig;
import com.moneymanager.controller.web.GlobalWebControllerAdvice;
import com.moneymanager.controller.web.main.LedgerController;
import com.moneymanager.domain.ledger.dto.request.LedgerUpdateRequest;
import com.moneymanager.domain.ledger.dto.response.CategoryResponse;
import com.moneymanager.domain.ledger.dto.response.LedgerEditResponse;
import com.moneymanager.domain.ledger.dto.response.LedgerFixedResponse;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.*;
import com.moneymanager.domain.member.dto.MemberLoginResponse;
import com.moneymanager.security.jwt.JwtAuthenticationFilter;
import com.moneymanager.service.main.CategoryService;
import com.moneymanager.service.main.ImageServiceImpl;
import com.moneymanager.service.main.LedgerService;
import com.moneymanager.service.main.api.GoogleChartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.controller<br>
 * 파일이름       : LedgerModificationControllerTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 27<br>
 * 설명              : 가계부 변경 요청에 대한 컨트롤러 테스트 클래스
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
 * 		 	  <td>25. 12. 27.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@WebMvcTest(controllers = LedgerController.class,
		excludeFilters = {
		@ComponentScan.Filter(
				type = FilterType.ASSIGNABLE_TYPE,
				classes = {
						SecurityConfig.class,
						JwtAuthenticationFilter.class,
						GlobalWebControllerAdvice.class
				}
		)
})
@AutoConfigureMockMvc(addFilters = false)		//스프링 시큐리티 필터 제거
public class LedgerModificationControllerTest {

	@Autowired	private MockMvc mockMvc;
	@Autowired	private ObjectMapper objectMapper;

	@MockBean	private LedgerService ledgerService;
	@MockBean	private CategoryService	categoryService;
	@MockBean	private GoogleChartService chartService;
	@MockBean @Qualifier("ledgerImage")	private ImageServiceImpl imageService;


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
				.amount(15000L)
				.paymentType(PaymentType.NONE)
				.placeName("강남 CGV").roadAddress("서울특별시 강남구 강남대로 123").detailAddress("☆☆빌딩 2층")
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
				.amount(15000L)
				.paymentType(PaymentType.NONE)
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

	@DisplayName("가계부 정보를 수정이 가능하다.")
	@Test
	void 임시() throws Exception {
		//given
		LedgerUpdateRequest request = LedgerUpdateRequest.builder()
				.category("010101").memo("수정욥 :)")
				.fixed(false)
				.amount(15000L).paymentType(PaymentType.CARD)
				.build();
		String updateJson = objectMapper.writeValueAsString(request);

		MockMultipartFile updateInfo = new MockMultipartFile(
				"update",
				"update.json",
				MediaType.APPLICATION_JSON_VALUE,
				updateJson.getBytes()
		);

		MockMultipartFile updateFiles = new MockMultipartFile(
				"image",
				"apple.jpg",
				MediaType.IMAGE_JPEG_VALUE,
				"apple is fruit".getBytes()
		);

		doNothing().when(ledgerService).updateLedger(anyString(), anyString(), any());

		//when & then
		mockMvc.perform(
						multipart("/api/ledgers/{id}", "1")
								.file(updateInfo)
								.file(updateFiles)
								.with(servletRequest -> {
									servletRequest.setMethod("PUT");
									return servletRequest;
								})
								.sessionAttr("mid" ,"member123")
								.contentType(MediaType.MULTIPART_FORM_DATA)
								.requestAttr("member", MemberLoginResponse.Success.builder()
										.nickName("홍길동").profile(null).build())
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("수정 완료했습니다."));
	}
}
