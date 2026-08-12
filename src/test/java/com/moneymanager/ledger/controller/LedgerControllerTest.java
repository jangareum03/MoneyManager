package com.moneymanager.ledger.controller;

import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep1Response;
import com.moneymanager.ledger.service.command.LedgerCommandService;
import com.moneymanager.ledger.service.read.LedgerReadService;
import com.moneymanager.ledger.service.validation.LedgerValidator;
import com.moneymanager.support.ControllerTestSupport;
import com.moneymanager.support.security.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.controller<br>
 * 파일이름       : LedgerControllerTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 16<br>
 * 설명              : LedgerController 클래스 요청을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 7. 16</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@WebMvcTest(LedgerController.class)
@AutoConfigureMockMvc(addFilters = false)
class LedgerControllerTest extends ControllerTestSupport {

	private static final String BASE_URI = "/ledgers";

	@Autowired	private MockMvc mockMvc;

	@MockBean	private LedgerReadService readService;
	@MockBean	private LedgerCommandService  commandService;
	@MockBean	private LedgerValidator validator;


	@Nested
	@DisplayName("가계부 작성 1단계 요청할 때")
	@WithMockCustomUser
	class Step1ViewTest {

		private static final String URI = BASE_URI + "/new/step1";

		@Nested
		@DisplayName("성공")
		class Success {

			@Test
			@DisplayName("가계부 작성 1단계 페이지를 반환한다.")
			@WithMockCustomUser
			void returnView_whenGetRequestIsGiven() throws Exception {
				//given: 화면에 필요한 데이터를 조회하는 동작이 정의되어 있다.
				LedgerWriteStep1Response response = LedgerWriteStep1Response.of(
						List.of(2026, 2025),
						List.of(1, 2),
						List.of(1, 2, 3, 4, 5)
				);

				when(readService.getWriteStep1Data()).thenReturn(response);

				//when: 가계부 작성 1단계 페이지를 요청한다.
				mockMvc.perform(
						get(URI)
				)
						.andExpect(status().isOk());

				//then: 서비스를 호출한다.
				verify(readService).getWriteStep1Data();
			}

		}

	}

}