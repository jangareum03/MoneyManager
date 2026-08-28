package com.moneymanager.ledger.service.application;

import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep2Response;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.entity.LedgerImage;
import com.moneymanager.ledger.domain.enums.LedgerType;
import com.moneymanager.ledger.repository.LedgerImageRepository;
import com.moneymanager.ledger.service.storage.LedgerImageStorage;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import com.moneymanager.support.fixture.file.ImageFixture;
import com.moneymanager.support.fixture.request.LedgerUpdateRequestFixture;
import com.moneymanager.support.fixture.request.LedgerWriteRequestFixture;
import com.moneymanager.support.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

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

    @SpyBean
    private LedgerImageStorage imageStorage;

    @Nested
    @WithMockCustomUser
    @DisplayName("작성 2단계에 필요한 정보를 조회할 때")
    class GetWriteStep2Data {

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
                assertThat(result.getType()).isEqualTo(LedgerType.INCOME);

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
                    .fixed(LedgerTestData.FIXED_REPEAT.getValue().toLowerCase())
                    .fixCycle(LedgerTestData.MONTHLY_CYCLE.getValue().toLowerCase())
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
            assertThat(saved.getMemberId()).isEqualTo(MemberTestData.DEFAULT_ID);

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

            //given: 회원의 이미지 개수를 2로 변경
            jdbcTemplate.update(
                    "UPDATE member_info SET image_limit = 2 WHERE id = ?",
                    MemberTestData.DEFAULT_ID
            );

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


    @Nested
    @WithMockCustomUser
    @DisplayName("가계부를 수정할 때")
    class Update {

        Ledger ledger;

        @BeforeEach
        void setUp() throws IOException {
            Long id = ledgerRepository.save(
                    LedgerTestFixture.builder().build()
            );

            ledger = ledgerRepository.findById(id);
        }

        @Test
        @DisplayName("정상적인 가계부 수정 요청이면 수정한다.")
        void updatesLedger_whenRequestIsFromOwner() {
            //given
            String code = ledger.getCode();
            LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
                    .categoryCode("010201")
                    .memo("메모")
                    .build();

            assertThat(ledger.getUpdatedAt()).isNull();

            //when
            target.processLedgerUpdate(code, request);

            //then
            Ledger updated = ledgerRepository.findById(ledger.getId());

            assertThat(updated.getCategory()).isEqualTo(request.getCategoryCode());
            assertThat(updated.getMemo()).isEqualTo(request.getMemo());
            assertThat(updated.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("이미지를 포함한 수정 요청이면 가계부 수정 후 이미지도 저장한다.")
        void updatesLedgerAndImage_whenImageIsProvided() throws IOException {
            //given
            String code = ledger.getCode();
            LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
                    .images(List.of(
                            ImageFixture.jpg("test")
                    ))
                    .build();

            assertThat(ledger.getUpdatedAt()).isNull();

            //when
            target.processLedgerUpdate(code, request);

            //then
            List<LedgerImage> images = imageRepository.findByLedgerId(ledger.getId());

            assertThat(images).hasSize(1);
            assertThat(images.get(0).getLedgerId()).isEqualTo(ledger.getId());

            try (Stream<Path> paths = Files.walk(tempDir)) {
                long count = paths
                        .filter(Files::isRegularFile)
                        .filter(path -> {
                            String fileName = path.getFileName().toString();

                            return fileName.endsWith("jpg") || fileName.endsWith("png");
                        })
                        .count();

                assertThat(count).isEqualTo(1);
            }
        }

        @Test
        @DisplayName("타인의 가계부를 수정 요청하면 수정하지 않는다.")
        void throwsException_whenUserIsNotOwner() {
            //given: 다른 회원의 가계부가 주어진다
            String memberId = "other";

            memberRepository.save(
                    MemberTestFixture.builder()
                            .id(memberId)
                            .username("other")
                            .nickName("other")
                            .email("other@test.com")
                            .build(passwordEncoder)
            );

            Member other = memberRepository.findById(memberId);

            Long id = ledgerRepository.save(
                    LedgerTestFixture.builder()
                            .memberId(other.getId())
                            .build()
            );

            Ledger otherLedger = ledgerRepository.findById(id);

            LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
                    .memo("수정")
                    .build();

            //when & then
            assertThatThrownBy(() -> target.processLedgerUpdate(otherLedger.getCode(), request))
                    .isInstanceOf(ApplicationException.class);
        }

    }


    @Nested
    @WithMockCustomUser(memberId = "member1")
    @Sql("/sql/ledger-delete-test.sql")
    @DisplayName("가계부를 삭제할 때")
    class Delete {
        
        @Test
        @DisplayName("이미지가 있는 가계부는 이미지와 함께 삭제한다.")
        void deletesLedgerAndImage_whenImageExists() throws IOException {
        	//given
            List<String> codes = List.of("code-1", "code-2", "code-4");
            LedgerImage image = imageRepository.findByLedgerCode("code-1").get(0);

            Path path = tempDir.resolve(image.getImagePath());
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        	
        	//when
            int result = target.processLedgerDelete(codes);
        	
        	//then
        	assertThat(result).isEqualTo(3);
            assertThat(ledgerRepository.findByCodeIn(codes)).hasSize(0);
            assertThat(imageRepository.count()).isZero();

            //파일 삭제 검증
            assertThat(Files.exists(path)).isFalse();
        }
        
        @Test
        @DisplayName("이미지가 없는 가계부는 정보만 삭제한다.")
        void deletesLedgerOnly_whenImageDoesNotExist() {
            //given
            List<String> codes = List.of("code-2", "code-4");

            assertThat(ledgerRepository.count()).isEqualTo(4);
            assertThat(imageRepository.count()).isEqualTo(1);

            //when
            int result = target.processLedgerDelete(codes);

            //then
            assertThat(result).isEqualTo(2);

            assertThat(ledgerRepository.count()).isEqualTo(2);
            assertThat(imageRepository.count()).isEqualTo(1);
        }
        
        @Test
        @DisplayName("타인의 가계부는 삭제되지 않는다.")
        void rejectsDeletion_whenUserIsNotOwner() {
        	//given
            List<String> codes = List.of("code-2", "code-3", "code-4");
        	
        	//when
            int result = target.processLedgerDelete(codes);
        	
        	//then
        	assertThat(result).isEqualTo(2);

            assertThat(ledgerRepository.findByCodeIn(codes)).hasSize(1);
        }

        @Test
        @DisplayName("이미지 삭제가 실패해도 가계부와 이미지 정보는 삭제한다.")
        void deletesLedgerAndImage_whenImageDeletionFails() throws IOException {
        	//given
            List<String> codes = List.of("code-1");

            doThrow(new IOException("파일 삭제 실패"))
                    .when(imageStorage)
                    .deleteOrThrow(any());
        	
        	//when
            int result = target.processLedgerDelete(codes);
        	
        	//then
        	assertThat(result).isEqualTo(1);

            assertThat(ledgerRepository.findByCodeIn(codes)).hasSize(0);
        }

    }

}