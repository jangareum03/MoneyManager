package com.moneymanager.ledger.controller;

import com.moneymanager.ledger.service.application.LedgerService;
import com.moneymanager.ledger.service.read.LedgerReadService;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.fixture.file.ImageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.controller<br>
 * 파일이름       : LedgerControllerIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 16<br>
 * 설명              : LedgerController 클래스 요청을 검증하는 통합 테스트 클래스
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
@Transactional
public class LedgerControllerIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

	@Autowired
	LedgerService ledgerService;

	@Autowired
	LedgerReadService ledgerReadService;

    private Member member;
    private final Path path = Path.of("src/test/resources/temp");

	@BeforeEach
	void setUp() throws IOException {
		member = saveMember();

        if(Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
	}

    @Nested
    @DisplayName("가계부 작성 1단계 화면 요청할 때")
    class Step1ViewTest {

        private final String URI = "/ledgers/new/step1";

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            @DisplayName("현재 날짜 기준 가계부 작성 페이지를 조회한다.")
            void returnsLedgerWritePage_whenCurrentDateIsGiven() throws Exception {
                //when & then
                mockMvc.perform(
                                get(URI)
                                        .cookie(accessTokenCookie(member.getUsername()))
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("/ledger/ledger_writeStep1"))
                        .andExpect(model().attributeExists("ledger"));

            }

        }

    }


    @Nested
    @DisplayName("가계부 작성 2단계 화면 요청할 때")
    class Step2ViewTest {

        private final String URI = "/ledgers/new/step2";

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("필수인 요청 파라미터가 모두 있으면 모델에 ledger 속성과 Step2 페이지를 반환한다.")
            void returnsStep2Page_whenRequestIsValid() throws Exception {
                //given: 정상적인 가계부 유형과 거래날짜가 주어진다.
                String type = "income";
                String date = "20260101";

                //when: 가계부 작성 2단계 페이지를 요청한다.
                mockMvc.perform(
                                get(URI)
                                        .param("type", type)
                                        .param("date", date)
                                        .cookie(accessTokenCookie(member.getUsername()))
                        )
                        .andExpect(status().isOk())
                        .andExpect(model().attributeExists("ledger"))
                        .andExpect(view().name("/ledger/ledger_writeStep2"))
                        .andDo(print())
                        .andReturn();
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("요청 파라미터에 가계부 유형이 누락되면 200 코드 반환하고 에러 페이지를 반환한다.")
            void returns200AndErrorPage_whenLedgerTypeIsMissing() throws Exception {
                //when
                mockMvc.perform(
                                get(URI)
                                        .param("date", "20260101")
                                        .cookie(accessTokenCookie(member.getUsername()))
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("error/400"));
            }

            @Test
            @DisplayName("요청 파라미터에 가계부 날짜가 누락되면 200 코드 반환하고 에러 페이지를 반환한다.")
            void returns200AndErrorPage_whenLedgerDateIsMissing() throws Exception {
                //when
                mockMvc.perform(
                                get(URI)
                                        .param("type", "income")
                                        .cookie(accessTokenCookie(member.getUsername()))
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("error/400"));
            }

            @Test
            @DisplayName("요청 파라미터가 모두 누락되면 400 코드 반환하고 에러 페이지를 반환한다.")
            void returns400AndErrorPage_whenAllParametersAreMissing() throws Exception {
                //when
                mockMvc.perform(
                                get(URI)
                                        .cookie(accessTokenCookie(member.getUsername()))
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("error/400"));
            }

            @Test
            @DisplayName("POST로 요청하면 200 코드 반환하고 에러 페이지를 반환한다.")
            void returns405AndErrorPage_whenRequestMethodIsPost() throws Exception {
                mockMvc.perform(
                                post(URI)
                                        .param("type", "income")
                                        .param("date", "20260101")
                                        .cookie(accessTokenCookie(member.getUsername()))
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("error/400"));
            }

            @Test
            @Sql(statements = "DELETE FROM ledger_category", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            @DisplayName("요청 중 예외가 발생하면 가계부 2단계 작성 화면으로 리디렉션한다.")
            void redirectsToLedgerStep2_whenExceptionOccurs() throws Exception {
                mockMvc.perform(
                                get(URI)
                                        .param("type", "income")
                                        .param("date", "20260101")
                                        .cookie(accessTokenCookie(member.getUsername()))
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("/ledger/ledger_writeStep2"))
                        .andDo(print());
            }

        }

    }


    @Nested
    @DisplayName("가계부 등록 요청할 때")
    class Create {

        final String URI = "/ledgers";

        @Test
        @DisplayName("등록 요청으로 가계부를 저장한다.")
        void savesLedger_whenRequestIsValid() throws Exception {
            //given
            MockMultipartFile file = ImageFixture.jpg("test");

            //when
            mockMvc.perform(
                            multipart(URI)
                                    .file(file)
                                    .param("date", LedgerTestData.DATE)
                                    .param("categoryCode", CategoryTestData.SALARY_CODE)
                                    .param("fixed", LedgerTestData.FIX_N.getValue().toLowerCase())
                                    .param("amount", LedgerTestData.AMOUNT.toString())
                                    .param("paymentType", LedgerTestData.PAYMENT_TYPE.name().toLowerCase())
									.cookie(accessTokenCookie(member.getUsername()))
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/ledgers"));

            //then
            assertThat(ledgerRepository.findAll())
                    .anyMatch(ledger ->
                            ledger.getCategory().equals(CategoryTestData.SALARY_CODE)
                    );

            try(Stream<Path> files = Files.walk(path)) {
                assertThat(files).anyMatch(p ->
                        p.getFileName().toString().endsWith(".jpg"));
            }
        }

        @Test
        @DisplayName("카테고리 코드가 없으면 등록하지 않는다.")
        void rejectsRequest_whenCategoryCodeIsNull() throws Exception {
            //when
            mockMvc.perform(
                            multipart(URI)
                                    .param("date", LedgerTestData.DATE)
                                    .param("fixed", LedgerTestData.FIX_N.getValue().toLowerCase())
                                    .param("amount", LedgerTestData.AMOUNT.toString())
                                    .param("paymentType", LedgerTestData.PAYMENT_TYPE.name().toLowerCase())
                                    .cookie(accessTokenCookie(member.getUsername()))
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/ledgers/new/step2"));

            //then
            assertThat(ledgerRepository.count()).isEqualTo(0);
            assertThat(Files.exists(path)).isFalse();
        }

        @Test
        @DisplayName("서버에 파일 저장 실패하면 등록하지 않는다.")
        void doesNotSaveLedger_whenFileUploadFails() throws Exception {
            //when
            MockMultipartFile file = ImageFixture.emptyFile();

            //when
            mockMvc.perform(
                            multipart(URI)
                                    .file(file)
                                    .param("date", LedgerTestData.DATE)
                                    .param("categoryCode", CategoryTestData.SALARY_CODE)
                                    .param("fixed", LedgerTestData.FIX_N.getValue().toLowerCase())
                                    .param("amount", LedgerTestData.AMOUNT.toString())
                                    .param("paymentType", LedgerTestData.PAYMENT_TYPE.name().toLowerCase())
                                    .cookie(accessTokenCookie(member.getUsername()))
                    )
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/ledgers/new/step2"));

            //then
            assertThat(ledgerRepository.count()).isEqualTo(0);
            assertThat(Files.exists(path)).isFalse();
        }
    }

}