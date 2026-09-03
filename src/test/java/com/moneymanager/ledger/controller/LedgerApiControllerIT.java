package com.moneymanager.ledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneymanager.global.config.MutableClock;
import com.moneymanager.ledger.domain.dto.request.LedgerSearchRequest;
import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.entity.LedgerImage;
import com.moneymanager.ledger.repository.LedgerImageRepository;
import com.moneymanager.ledger.service.storage.LedgerImageStorage;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import com.moneymanager.support.fixture.file.ImageFixture;
import com.moneymanager.support.fixture.request.LedgerUpdateRequestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
public class LedgerApiControllerIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LedgerImageRepository imageRepository;

    @Autowired
    private MutableClock clock;

    @SpyBean
    private LedgerImageStorage imageStorage;

    private final String BASE_URI = "/api/ledgers";
    
    @BeforeEach
    void setUp() {
        insertMember(MemberTestFixture.builder().build(passwordEncoder));
    }


    @Nested
    @Sql("/sql/ledger-history-test.sql")
    @DisplayName("가계부 검색할 때")
    class Search {
        
        @Test
        @DisplayName("정상적인 검색 요청이면 내역 목록을 반환한다.")
        void returnsHistoryList_whenSearchRequestIsValid() throws Exception {
        	//given
            clock.set(LocalDate.of(2026, 1, 1));

            LedgerSearchRequest request = LedgerSearchRequest.builder()
                    .type("month")
                    .menu("memo")
                    .memo("이")
                    .build();
        	
        	//when
            mockMvc.perform(
                    get(BASE_URI)
                            .param("type", request.getType())
                            .param("menu", request.getMenu())
                            .param("memo", request.getMemo())
                            .cookie(accessTokenCookie("member1"))
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").isEmpty())
                    .andExpect(jsonPath("$.data").isNotEmpty());
        }
        
        @Test
        @DisplayName("내역이 없으면 빈 목록을 반환한다.")
        void returnsEmptyList_whenHistoryDoesNotExist() throws Exception {
            //given
            LedgerSearchRequest request = LedgerSearchRequest.builder()
                    .type("month")
                    .menu("memo")
                    .memo("가")
                    .build();

            //when
            mockMvc.perform(
                            get(BASE_URI)
                                    .param("type", request.getType())
                                    .param("menu", request.getMenu())
                                    .param("memo", request.getMemo())
                                    .cookie(accessTokenCookie("member1"))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").isEmpty())
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        @DisplayName("검색에 실패하면 안내 메시지를 반환한다.")
        void returnsErrorMessage_whenSearchFails() throws Exception {
            //given
            LedgerSearchRequest request = LedgerSearchRequest.builder()
                    .type("month")
                    .menu("memo")
                    .build();

            //when
            mockMvc.perform(
                            get(BASE_URI)
                                    .param("type", request.getType())
                                    .param("menu", request.getMenu())
                                    .param("memo", request.getMemo())
                                    .cookie(accessTokenCookie("member1"))
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").isEmpty());
        }

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
                                get(URI, CategoryTestData.EARNED_CODE)
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
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
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
                                        .contentType(MediaType.TEXT_PLAIN)
                        )
                        .andExpect(status().isMethodNotAllowed());
            }

            @Test
            @DisplayName("코드가 존재하지 않으면 이전 화면으로 이동한다.")
            void redirectsToPreviousPage_whenCodeDoesNotExist() throws Exception {
                //when
                mockMvc.perform(
                                get(URI, "error")
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .header("referer", "/ledgers/new/step2")
                        )
                        .andExpect(status().isNotFound())
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
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
                                        .param("unit", unit)
                                        .param("value", date)
                        );
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
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
                                        .param("unit", unit)
                                        .param("value", value)
                        )
                        .andExpect(status().isBadRequest());
            }

            @Test
            @DisplayName("요청 파라미터에 날짜 단위가 누락되면 에러 페이지로 이동한다.")
            void redirectsToErrorPage_whenDateUnitIsMissing() throws Exception {
                //when
                mockMvc.perform(
                                get(URI)
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
                                        .param("value", "2026")
                        )
                        .andExpect(status().isBadRequest());
            }

            @Test
            @DisplayName("요청 파라미터에 날짜가 누락되면 에러 페이지로 이동한다.")
            void redirectsToErrorPage_whenBaseDateIsMissing() throws Exception {
                //when
                mockMvc.perform(
                                get(URI)
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
                                        .param("unit", "year")
                        )
                        .andExpect(status().isBadRequest());

            }

            @Test
            @DisplayName("요청 파라미터 모두 누락되면 에러 페이지로 이동한다.")
            void redirectsToErrorPage_whenAllParametersAreMissing() throws Exception {
                mockMvc.perform(
                                get(URI)
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
                        )
                        .andExpect(status().isBadRequest());
            }

            @Test
            @DisplayName("허용되지 않은 메서드 요청하면 에러 페이지로 이동한다.")
            void redirectsToErrorPage_whenHttpMethodIsNotAllowed() throws Exception {
                //when
                mockMvc.perform(
                                post(URI)
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
                                        .param("unit", "month")
                                        .param("value", "202601")
                        )
                        .andExpect(status().isMethodNotAllowed());
            }

        }

    }


    @Nested
    @DisplayName("가계부 코드로 수정 요청할 때")
    class Update {

        private final String URI = BASE_URI + "/{code}";
        private Ledger ledger;

        @BeforeEach
        void setUp() {
            Long Id = ledgerRepository.save(
                    LedgerTestFixture.builder().build()
            );

            ledger = ledgerRepository.findById(Id);
        }

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("이미지가 없는 요청이면 가계부 정보만 수정한다.")
            void returnsSuccess_whenCodeExistsAndRequestIsValid() throws Exception {
                //given
                LedgerUpdateRequest request = LedgerUpdateRequestFixture.withPlace().build();

                MockMultipartFile ledger = new MockMultipartFile(
                        "ledger",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(request)
                );

                //when
                mockMvc.perform(
                                multipart(URI, Update.this.ledger.getCode())
                                        .file(ledger)
                                        .with(req -> {
                                            req.setMethod("PUT");

                                            return req;
                                        })
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message").value("가계부 수정 완료했습니다."))
                        .andExpect(jsonPath("$.next").value("/ledgers/" + Update.this.ledger.getCode()));

                //then
                Ledger updated = ledgerRepository.findById(Update.this.ledger.getId());

                assertThat(updated.getPlace().getPlaceName()).isEqualTo(request.getPlaceName());
                assertThat(updated.getPlace().getRoadAddress()).isEqualTo(request.getRoadAddress());
                assertThat(updated.getPlace().getDetailAddress()).isEqualTo(request.getDetailAddress());
            }

            @Test
            @DisplayName("이미지가 있는 요청이면 파일이 저장되고 이미지 정보를 수정한다.")
            void updatesLedgerAndSavesImage_whenSingleImageIsGiven() throws Exception {
                //given
                LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
                        .memo("배고파")
                        .build();

                MockMultipartFile ledger = new MockMultipartFile(
                        "ledger",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(request)
                );

                MockMultipartFile image = ImageFixture.jpg("test");

                //when
                mockMvc.perform(
                                multipart(URI, Update.this.ledger.getCode())
                                        .file(ledger)
                                        .file(image)
                                        .with(req -> {
                                            req.setMethod("PUT");

                                            return req;
                                        })
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
                        )
                        .andExpect(status().isOk());

                //then: 데이터베이스에 이미지 정보가 저장된다.
                List<LedgerImage> savedImage = imageRepository.findByLedgerId(Update.this.ledger.getId());
                assertThat(savedImage).hasSize(1);

                //then: 이미지 파일이 서버에 저장된다.
                try (Stream<Path> paths = Files.walk(tempDir)) {
                    long count = paths.filter(Files::isRegularFile)
                            .filter(path -> {
                                String fileName = path.getFileName().toString().toLowerCase();

                                return fileName.endsWith("jpg") || fileName.endsWith("png");
                            })
                            .count();

                    assertThat(count).isEqualTo(1);
                }
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("존재하지 않은 가계부면 수정에 실패한다.")
            void throwsException_whenLedgerDoesNotExist() throws Exception {
                //given
                LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder().build();

                MockMultipartFile ledger = new MockMultipartFile(
                        "ledger",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(request)
                );

                //when
                mockMvc.perform(
                                multipart(URI, "noExist")
                                        .file(ledger)
                                        .with(req -> {
                                            req.setMethod("PUT");

                                            return req;
                                        })
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
                        )
                        .andExpect(status().isNotFound());
            }

            @Test
            @DisplayName("잘못된 수정 요청이면 수정에 실패한다.")
            void throwsException_whenRequestIsInvalid() throws Exception {
                //given
                LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
                        .fixed(LedgerTestData.FIXED_REPEAT.getValue())
                        .fixCycle("month")
                        .build();

                MockMultipartFile ledger = new MockMultipartFile(
                        "ledger",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(request)
                );

                //when
                mockMvc.perform(
                                multipart(URI, Update.this.ledger.getCode())
                                        .file(ledger)
                                        .with(req -> {
                                            req.setMethod("PUT");

                                            return req;
                                        })
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
                        )
                        .andDo(print())
                        .andExpect(status().isBadRequest());
            }

            @Test
            @DisplayName("요청에 ledger가 누락되면 수정에 실패한다.")
            void throwsException_whenLedgerIsNull() throws Exception {
                //when
                mockMvc.perform(
                                multipart(URI, ledger.getCode())
                                        .with(req -> {
                                            req.setMethod("PUT");

                                            return req;
                                        })
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
                        )
                        .andExpect(status().isBadRequest());
            }

            @Test
            @DisplayName("지원하지 않은 이미지 형식이면 수정에 실패한다.")
            void throwsException_whenImageTypeIsInvalid() throws Exception {
                //given
                LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
                        .memo("배고파")
                        .build();

                MockMultipartFile ledger = new MockMultipartFile(
                        "ledger",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(request)
                );

                MockMultipartFile pdf = new MockMultipartFile(
                        "images",
                        "test.pdf",
                        MediaType.APPLICATION_PDF_VALUE,
                        "content".getBytes(StandardCharsets.UTF_8)
                );

                //when
                mockMvc.perform(
                                multipart(URI, Update.this.ledger.getCode())
                                        .file(ledger)
                                        .file(pdf)
                                        .with(req -> {
                                            req.setMethod("PUT");

                                            return req;
                                        })
                                        .cookie(accessTokenCookie(MemberTestData.DEFAULT_USERNAME))
                        )
                        .andExpect(status().isUnsupportedMediaType());
            }

        }

    }


    @Nested
    @Sql("/sql/ledger-delete-test.sql")
    @DisplayName("가계부를 삭제할 때")
    class Delete {

        String URI = BASE_URI;
        
        @Test
        @DisplayName("자신의 가계부를 삭제하면 데이터와 이미지가 함께 삭제한다.")
        void returnsSuccessMessage_whenUserDeletesOwnAccountBook() throws Exception {
        	//given
            LedgerImage image = imageRepository.findByLedgerId(1L).get(0);

            Path path = tempDir.resolve(image.getImagePath());
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        	
        	//when
            mockMvc.perform(
                    delete(URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    ["code-1", "code-2"]
                                    """)
                            .cookie(accessTokenCookie("member1"))
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("2건의 내역이 삭제되었습니다."));
        	
        	//then
        	assertThat(ledgerRepository.findByCodeIn(List.of("code-1", "code-2")).size())
                    .isZero();

            assertThat(imageRepository.findByLedgerId(1L).size())
                    .isZero();

            assertThat(Files.exists(path)).isFalse();
        }
        
        @Test
        @DisplayName("이미지 삭제에 실패해도 데이터는 삭제한다.")
        void returnsSuccessMessage_whenImageDeletionFails() throws Exception {
            //given
            LedgerImage image = imageRepository.findByLedgerId(1L).get(0);

            Path path = tempDir.resolve(image.getImagePath());
            Files.createDirectories(path.getParent());
            Files.createFile(path);

            doThrow(IOException.class)
                    .when(imageStorage)
                            .deleteOrThrow(any());

            //when
            mockMvc.perform(
                            delete(URI)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                    ["code-1", "code-2"]
                                    """)
                                    .cookie(accessTokenCookie("member1"))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("2건의 내역이 삭제되었습니다."));

            //then
            assertThat(ledgerRepository.findByCodeIn(List.of("code-1", "code-2")).size())
                    .isZero();

            assertThat(imageRepository.findByLedgerId(1L).size())
                    .isZero();

            assertThat(Files.exists(path)).isFalse();
            assertThat(Files.exists(tempDir.resolve("temp").resolve("test.png"))).isTrue();
        }
        
        @Test
        @DisplayName("존재하지 않는 가계부면 아무것도 삭제되지 않는다.")
        void returnsNotFoundMessage_whenAccountBookDoesNotExist() throws Exception {
            //when
            mockMvc.perform(
                            delete(URI)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                    ["no-exist", "code-2"]
                                    """)
                                    .cookie(accessTokenCookie("member1"))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("1건의 내역이 삭제되었습니다."));
        }
        
        @Test
        @DisplayName("타인의 가계부는 아무것도 삭제되지 않는다.")
        void rejectsRequest_whenUserIsNotOwner() throws Exception {
            //when
            mockMvc.perform(
                            delete(URI)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                    ["code-2", "code-3"]
                                    """)
                                    .cookie(accessTokenCookie("member1"))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("1건의 내역이 삭제되었습니다."));
        }

    }

}