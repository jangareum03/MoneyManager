package com.moneymanager.ledger.controller;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.support.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.controller<br>
 * 파일이름       : LedgerApiControllerIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 14<br>
 * 설명              : LedgerApiController 클래스 요청을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 8. 14</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Transactional
public class LedgerApiControllerIT extends IntegrationTest {

	private final String BASE_URI = "/api/ledgers";

	private Member member;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		member = saveMember();
	}

	@Nested
	@DisplayName("카테고리 목록을 조회할 때")
	class GetCategories {

		String URI = BASE_URI + "/category/{code}/children";

		@Nested
		@DisplayName("성공")
		class Success {
		
			@Test
			@DisplayName("코드가 존재하면 하위 카테고리 목록을 반환한다.")
			void returnsSubcategories_whenCodeExists() throws Exception {
				//when
				mockMvc.perform(
						get(URI, "010100")
								.cookie(accessTokenCookie(member.getUsername()))
								.contentType(MediaType.TEXT_PLAIN)
						)
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.length()").value(3));
			}
			
		}
		
		@Nested
		@DisplayName("실패")
		class Failure {
			
			@Test
			@DisplayName("POST로 요청하면 에러 페이지를 반환한다.")
			void returnsErrorPage_whenRequestIsPost() throws Exception {
				//when
				mockMvc.perform(
						post(URI, "010100")
								.cookie(accessTokenCookie(member.getUsername()))
								.contentType(MediaType.TEXT_PLAIN)
				)
						.andExpect(status().isOk())
						.andExpect(view().name("error/400"))
						.andExpect(model().attribute("message", "잘못된 요청입니다."))
						.andDo(print());
			}

			@Test
			@DisplayName("코드가 존재하지 않으면 이전 화면으로 이동한다.")
			void redirectsToPreviousPage_whenCodeDoesNotExist() throws Exception {
				//when
				mockMvc.perform(
						get(URI, "error")
								.cookie(accessTokenCookie(member.getUsername()))
								.contentType(MediaType.TEXT_PLAIN)
								.header("referer", "/ledgers/new/step2")
				)
						.andExpect(status().is3xxRedirection())
						.andDo(print());
			}
			
		}
		
	}


	@Nested
	@DisplayName("날짜 단위별 선택박스 리스트를 요청할 때")
	class GetDateList {

		private final String URI = BASE_URI + "/dates";

		@Nested
		@DisplayName("성공")
		class Success {
			
			@Test
			@DisplayName("날짜 단위와 기준 날짜가 유효하면 리스트를 반환한다.")
			void returnsList_whenDateUnitAndBaseDateAreValid() throws Exception {
				//given
				String unit = "year";
				String date = "2026";
				
				//when
				mockMvc.perform(
						get(URI)
								.cookie(accessTokenCookie(member.getUsername()))
								.param("unit", unit)
								.param("value", date)
				)
						.andExpect(status().isOk());
			}
			
		}

		@Nested
		@DisplayName("실패")
		class Failure {
			
			@Test
			@DisplayName("요청이 잘못되면 가계부 작성 2단계 화면으로 이동한다.")
			void redirectsToStepTwo_whenRequestIsInvalid() throws Exception {
				//given
				String unit = "year";
				String value = "202601";
				
				//when
				mockMvc.perform(
						get(URI)
								.cookie(accessTokenCookie(member.getUsername()))
								.param("unit", unit)
								.param("value", value)
				)
						.andExpect(status().isOk())
						.andExpect(view().name("error/400"));
			}

			@Test
			@DisplayName("요청 파라미터에 날짜 단위가 누락되면 에러 페이지로 이동한다.")
			void redirectsToErrorPage_whenDateUnitIsMissing() throws Exception {
				//when
				mockMvc.perform(
						get(URI)
								.cookie(accessTokenCookie(member.getUsername()))
								.param("value", "2026")
				)
						.andExpect(status().isOk())
						.andExpect(view().name("error/400"))
						.andExpect(model().attribute("message", "필수값이 없습니다."));
			}
			
			@Test
			@DisplayName("요청 파라미터에 날짜가 누락되면 에러 페이지로 이동한다.")
			void redirectsToErrorPage_whenBaseDateIsMissing() throws Exception {
				//when
				mockMvc.perform(
								get(URI)
										.cookie(accessTokenCookie(member.getUsername()))
										.param("unit", "year")
						)
						.andExpect(status().isOk())
						.andExpect(view().name("error/400"));
				
			}
			
			@Test
			@DisplayName("요청 파라미터 모두 누락되면 에러 페이지로 이동한다.")
			void redirectsToErrorPage_whenAllParametersAreMissing() throws Exception {
				mockMvc.perform(
								get(URI)
										.cookie(accessTokenCookie(member.getUsername()))
						)
						.andExpect(status().isOk())
						.andExpect(view().name("error/400"));
			}
			
			@Test
			@DisplayName("허용되지 않은 메서드 요청하면 에러 페이지로 이동한다.")
			void redirectsToErrorPage_whenHttpMethodIsNotAllowed() throws Exception {
				//when
				mockMvc.perform(
						post(URI)
								.cookie(accessTokenCookie(member.getUsername()))
								.param("unit", "month")
								.param("value", "202601")
				)
						.andExpect(view().name("error/400"))
						.andExpect(model().attribute("message", "잘못된 요청입니다."));
			}

		}

	}

}