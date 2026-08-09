package com.moneymanager.ledger.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneymanager.ledger.controller.LedgerApiController;
import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.dto.response.LedgerDetailResponse;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.ledger.service.command.LedgerCommandService;
import com.moneymanager.ledger.service.validation.LedgerValidator;
import com.moneymanager.support.ControllerTestSupport;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import com.moneymanager.support.fixture.file.ImageFixture;
import com.moneymanager.support.fixture.request.LedgerUpdateRequestFixture;
import com.moneymanager.support.fixture.response.LedgerDetailResponseFixture;
import com.moneymanager.support.security.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.controller.api<br>
 * 파일이름       : LedgerApiControllerTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 2<br>
 * 설명              : LedgerApiController 클래스 요청을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 8. 2</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@WebMvcTest(LedgerApiController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LedgerApiControllerTest extends ControllerTestSupport {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private LedgerCommandService commandService;

	@MockBean
	private LedgerValidator validator;

	private final String BASE_URI = "/api/ledgers";

	@Nested
	@DisplayName("가계부 수정")
	@WithMockCustomUser
	class UpdateTest {

		private String URI = BASE_URI + "/{code}";

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("이미지 없는 가계부를 수정한다.")
			void updatesLedger_whenLedgerHasNoImage() throws Exception {
				//given: 요청에 필요한 정보가 주어진다.
				Ledger ledger = LedgerFixture.savedLedger(1L).memberId(MemberTestData.MEMBER_ID).build();

				LedgerUpdateRequest request = LedgerUpdateRequestFixture.from(ledger)
						.paymentType("cash")
						.memo("메모")
						.build();

				LedgerDetailResponse response = LedgerDetailResponseFixture.create()
						.toBuilder()
						.paymentType(PaymentType.CASH)
						.memo("메모")
						.build();

				MockMultipartFile ledgerPart = new MockMultipartFile(
						"ledger",
						"",
						MediaType.APPLICATION_JSON_VALUE,
						objectMapper.writeValueAsBytes(request)
				);

				when(commandService.update(eq(ledger.getCode()), any(LedgerUpdateRequest.class)))
						.thenReturn(response);

				//when: 가계부 수정을 요청한다.
				mockMvc.perform(
						multipart(URI, ledger.getCode())
								.file(ledgerPart)
								.with(requestBuilder -> {
									requestBuilder.setMethod("PATCH");

									return requestBuilder;
								})
								.contentType(MediaType.MULTIPART_FORM_DATA)
				)
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.message").value("가계부 수정 완료했습니다."))
						.andExpect(jsonPath("$.data.paymentType").value("CASH"))
						.andExpect(jsonPath("$.data.memo").value("메모"));

				//then: 검증과 수정 메서드가 호촐된다.
				verify(validator).update(any(LedgerUpdateRequest.class));
				verify(commandService).update(eq(ledger.getCode()), any(LedgerUpdateRequest.class));
			}

			@Test
			@DisplayName("이미지 있는 가계부를 수정한다.")
			void updatesLedger_whenLedgerHasImage() throws Exception {
				//given: 요청에 필요한 정보가 주어진다.
				Ledger ledger = LedgerFixture.savedLedger(1L).build();

				LedgerUpdateRequest request = LedgerUpdateRequestFixture.from(ledger).build();

				LedgerDetailResponse response = LedgerDetailResponseFixture.withImage();

				MockMultipartFile ledgerPart = new MockMultipartFile(
						"ledger",
						"",
						MediaType.APPLICATION_JSON_VALUE,
						objectMapper.writeValueAsBytes(request)
				);

				MockMultipartFile image = ImageFixture.jpg("이미지");

				when(commandService.update(eq(ledger.getCode()), any(LedgerUpdateRequest.class)))
						.thenReturn(response);

				//when: 가계부 수정을 요청한다.
				mockMvc.perform(
								multipart(URI, ledger.getCode())
										.file(ledgerPart)
										.file(image)
										.with(requestBuilder -> {
											requestBuilder.setMethod("PATCH");

											return requestBuilder;
										})
										.contentType(MediaType.MULTIPART_FORM_DATA)
						)
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.message").value("가계부 수정 완료했습니다."))
						.andExpect(jsonPath("$.data.images[0]").value("이미지"));

				//then: 검증과 수정 메서드가 호촐된다.
				verify(validator).update(any(LedgerUpdateRequest.class));
				verify(commandService).update(eq(ledger.getCode()), any(LedgerUpdateRequest.class));
			}

		}

	}


}