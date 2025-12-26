package com.moneymanager.ledger.update;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneymanager.config.SecurityConfig;
import com.moneymanager.controller.api.main.LedgerApiController;
import com.moneymanager.controller.web.GlobalWebControllerAdvice;
import com.moneymanager.domain.ledger.dto.request.LedgerUpdateRequest;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.member.dto.MemberLoginResponse;
import com.moneymanager.security.jwt.JwtAuthenticationFilter;
import com.moneymanager.service.main.CategoryService;
import com.moneymanager.service.main.LedgerService;
import com.moneymanager.service.main.api.GoogleChartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.update<br>
 * 파일이름       : LedgerUpdateControllerTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 27.<br>
 * 설명              : 가계부 수정 요청에 대한 컨트롤러 테스트 클래스
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
@WebMvcTest(
		controllers = LedgerApiController.class,
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
public class LedgerUpdateControllerTest {

	@Autowired	private MockMvc mockMvc;
	@Autowired	private ObjectMapper objectMapper;

	@MockBean	private LedgerService ledgerService;
	@MockBean	private CategoryService categoryService;
	@MockBean	private GoogleChartService chartService;

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
