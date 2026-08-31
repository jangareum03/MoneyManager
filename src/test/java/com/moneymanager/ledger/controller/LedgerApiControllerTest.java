package com.moneymanager.ledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.ledger.domain.dto.request.LedgerSearchRequest;
import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.service.application.LedgerHistoryService;
import com.moneymanager.ledger.service.application.LedgerService;
import com.moneymanager.ledger.service.read.CategoryReadService;
import com.moneymanager.support.UnitTest;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.fixture.file.ImageFixture;
import com.moneymanager.support.fixture.request.LedgerUpdateRequestFixture;
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

import java.util.List;

import static com.moneymanager.global.exception.code.ErrorCode.POLICY_VIOLATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.controller<br>
 * 파일이름       : LedgerApiControllerTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 23<br>
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
 * 		 	  <td>26. 8. 23</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@WebMvcTest(LedgerApiController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LedgerApiControllerTest extends UnitTest {

    private final String BASE_URI = "/api/ledgers";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LedgerService ledgerService;

    @MockBean
    private LedgerHistoryService ledgerHistoryService;

    @MockBean
    private CategoryReadService categoryReadService;


    @Nested
    @DisplayName("가계부 검색할 때")
    class Search {
        
        @Test
        @DisplayName("정상적인 요청이면 서비스를 호출한다.")
        void invokesService_whenRequestIsValid() throws Exception {
        	//given
            String type = "month";
            String menu = "all";

            when(ledgerHistoryService.searchLedgersByCondition(any(LedgerSearchRequest.class)))
                    .thenReturn(List.of());
        	
        	//when
            mockMvc.perform(
                    get(BASE_URI)
                            .param("type", type)
                            .param("menu", menu)
            )
                    .andDo(print())
                    .andExpect(status().isOk());
        	
        	//then
        	verify(ledgerHistoryService).searchLedgersByCondition(any(LedgerSearchRequest.class));
        }
        
        @Test
        @DisplayName("요청이 누락되면 실패한다.")
        void rejectsRequest_whenRequestIsInvalid() throws Exception {
            //given
            when(ledgerHistoryService.searchLedgersByCondition(any(LedgerSearchRequest.class)))
                    .thenThrow(new ApplicationException(
                            POLICY_VIOLATION,
                            LogContent.of(
                                    "가계부 내역 검색 검증",
                                    LedgerSearchRequest.class,
                                    "memo",
                                    ""
                            ).withCause("메모 누락")
                    ));

            //when
            mockMvc.perform(
                            get(BASE_URI)
                                    .param("type", "month")
                                    .param("menu", "memo")
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

    }

    @Nested
    @DisplayName("가계부 수정할 때")
    class Update {

        @Test
        @DisplayName("정상 요청이면 서비스를 호출한다.")
        void callsService_whenRequestIsValid() throws Exception {
            //given
            LedgerUpdateRequest request = LedgerUpdateRequestFixture
                    .withPlace()
                    .fixed(LedgerTestData.FIXED_REPEAT.getValue())
                    .fixCycle(LedgerTestData.MONTHLY_CYCLE.getValue())
                    .memo(LedgerTestData.MEMO)
                    .build();

            MockMultipartFile ledger = new MockMultipartFile(
                    "ledger",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request)
            );

            List<MockMultipartFile> image = List.of(
                    ImageFixture.jpg("test"),
                    ImageFixture.png("test")
            );

            //when
            mockMvc.perform(
                            multipart(BASE_URI + "/{code}", "code-123")
                                    .file(ledger)
                                    .file(image.get(0))
                                    .file(image.get(1))
                                    .with(r -> {
                                        r.setMethod("PUT");

                                        return r;
                                    })
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("가계부 수정 완료했습니다."))
                    .andExpect(jsonPath("$.next").value("/ledgers/code-123"));

            //then
            verify(ledgerService).processLedgerUpdate(
                    eq("code-123"),
                    any(LedgerUpdateRequest.class)
            );
        }

        @Test
        @DisplayName("이미지가 없어도 서비스를 호출한다.")
        void callsService_whenImageIsNullOrBlank() throws Exception {
            //given
            LedgerUpdateRequest request = LedgerUpdateRequestFixture
                    .withPlace()
                    .fixed(LedgerTestData.FIXED_REPEAT.getValue())
                    .fixCycle(LedgerTestData.MONTHLY_CYCLE.getValue())
                    .memo(LedgerTestData.MEMO)
                    .build();

            MockMultipartFile ledger = new MockMultipartFile(
                    "ledger",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request)
            );

            //when
            mockMvc.perform(
                            multipart(BASE_URI + "/{code}", "code-123")
                                    .file(ledger)
                                    .with(r -> {
                                        r.setMethod("PUT");

                                        return r;
                                    })
                    )
                    .andExpect(status().isOk());

            //then
            verify(ledgerService).processLedgerUpdate(
                    eq("code-123"),
                    any(LedgerUpdateRequest.class)
            );
        }

        @Test
        @DisplayName("요청에서 ledger가 없으면 실패한다.")
        void rejectsRequest_whenLedgerDoesNotExist() throws Exception {
            //when
            mockMvc.perform(
                            multipart(BASE_URI + "/{code}", "code-123")
                                    .with(req -> {
                                        req.setMethod("PUT");

                                        return req;
                                    })
                    )
                    .andExpect(status().isBadRequest());

            //then
            verifyNoInteractions(ledgerService);
        }

    }


    @Nested
    @DisplayName("가계부 삭제 요청할 때")
    class Delete {

        String URI = BASE_URI;

        @Test
        @DisplayName("삭제된 건수가 있으면 건수를 포함한 메서지를 반환한다.")
        void returnsSuccessMessageWithCount_whenAccountBookIsDeleted() throws Exception {
            //given
            List<String> codes = List.of("code1", "code2");

            when(ledgerService.processLedgerDelete(codes))
                    .thenReturn(2);

            //when
            mockMvc.perform(
                            delete(URI)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            ["code1", "code2"]
                                            """)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("2건의 내역이 삭제되었습니다."));

            //then
            verify(ledgerService).processLedgerDelete(codes);
        }

        @Test
        @DisplayName("삭제된 건수가 없으면 삭제된 내역이 없다는 메시지를 반환한다.")
        void returnsNotFoundMessage_whenDeletedCountIsZero() throws Exception {
            //given
            List<String> codes = List.of("code1", "code2");

            when(ledgerService.processLedgerDelete(codes))
                    .thenReturn(0);

            //when
            mockMvc.perform(
                            delete(URI)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            ["code1", "code2"]
                                            """)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("삭제된 내역이 없습니다."));

            //then
            verify(ledgerService).processLedgerDelete(codes);
        }

    }

}