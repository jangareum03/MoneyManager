package com.moneymanager.ledger.service.application;

import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep2Response;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.CategoryType;
import com.moneymanager.ledger.repository.LedgerImageRepository;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.request.LedgerWriteRequestFixture;
import com.moneymanager.support.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.application<br>
 * 파일이름       : LedgerServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 13<br>
 * 설명              : LedgerService 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 8. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Transactional
class LedgerServiceIT extends IntegrationTest {

    @Autowired
    private LedgerService target;

    @Autowired
    private LedgerImageRepository imageRepository;

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("file.root", () -> tempDir.toString());
    }

    @Nested
    @WithMockCustomUser
    @DisplayName("작성 2단계에 필요한 정보를 조회할 때")
    class GetWriteStep2Data {

        @BeforeEach
        void setUp() {
            saveMember();
        }

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("장상적인 요청이면 LedgerWriteStep2Response 객체를 반환한다.")
            void returnsLedgerWriteStep2Response_whenRequestIsValid() {
                //when
                LedgerWriteStep2Response result = target.getStep2("income", "20260115");

                //then
                assertThat(result).isNotNull();

                assertThat(result.getTitle()).isEqualTo("2026년 01월 15일 목요일");
                assertThat(result.getType()).isEqualTo(CategoryType.INCOME);

                assertThat(result.getFixed()).hasSize(2);
                assertThat(result.getPaymentTypes()).hasSize(4);

                assertThat(result.getCategories())
                        .allMatch(item -> item.getCode().startsWith("01"));

                assertThat(result.getImageSlot()).hasSize(3);
            }

        }

    }


    @Nested
    @WithMockCustomUser
    @DisplayName("가계부 등록 처리 진행할 때")
    class ProcessLedgerRegistration {

        @Test
        @DisplayName("유효한 요청 정보면 가계부를 저장한다.")
        void savesLedger_whenRequestIsValid() {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture
                    .withPlace()
                    .fixed(LedgerTestData.FIX_Y.getValue().toLowerCase())
                    .fixCycle(LedgerTestData.FIX_CYCLE.getValue().toLowerCase())
                    .memo(LedgerTestData.MEMO)
                    .build();

            Long before = ledgerRepository.count();

            //when
            target.processLedgerRegistration(request);

            //then
            Long after = ledgerRepository.count();

            assertThat(after).isGreaterThan(before);

            Ledger saved = ledgerRepository.findAll().get(0);

            assertThat(saved.getUpdatedAt()).isNull();

            assertThat(saved)
                    .extracting(
                            Ledger::getId,
                            Ledger::getCode
                    )
                    .isNotNull();

            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(saved.getMemberId()).isEqualTo(MemberTestData.MEMBER_ID);

            assertThat(saved.getDate())
                    .isEqualTo(LocalDate.parse(request.getDate(), DateTimeFormatter.ofPattern("yyyyMMdd")));

            assertThat(saved.getCategory()).isEqualTo(request.getCategoryCode());
            assertThat(saved.getMemo()).isEqualTo(request.getMemo());

            assertThat(saved.getPlace())
                    .isEqualTo(Place.ofOrNull(request.getPlaceName(), request.getRoadAddress(), request.getDetailAddress()));

            assertThat(saved.getFix().getValue().toLowerCase()).isEqualTo(request.getFixed());
            assertThat(saved.getFixCycle().getValue().toLowerCase()).isEqualTo(request.getFixCycle());
        }

        @Test
        @DisplayName("이미지가 포함된 요청 정보면 파일 저장 및 정보를 저장한다.")
        void savesLedgerAndFiles_whenRequestContainsImages() {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture
                    .withImages(2)
                    .build();

            //when
            target.processLedgerRegistration(request);

            //then
            assertThat(ledgerRepository.count()).isEqualTo(1);
            assertThat(imageRepository.count()).isEqualTo(2);
        }

        @Test
        @DisplayName("가계부 정보를 저장에 실패하면 아무것도 저장되지 않는다.")
        void doesNotSaveAnything_whenLedgerSaveFails() {
            //given
            LedgerWriteRequest request = LedgerWriteRequestFixture
                    .builder()
                    .date("260111")
                    .build();

            //when
            assertThatThrownBy(() -> target.processLedgerRegistration(request))
                    ;

            //then
            Long count = ledgerRepository.count();

            assertThat(count).isZero();
        }

    }

}