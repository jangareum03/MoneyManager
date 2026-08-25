package com.moneymanager.ledger.service.application;

import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.entity.LedgerImage;
import com.moneymanager.ledger.repository.LedgerImageRepository;
import com.moneymanager.ledger.service.storage.LedgerImageStorage;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerTestFixture;
import com.moneymanager.support.fixture.file.ImageFixture;
import com.moneymanager.support.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.application<br>
 * 파일이름       : LedgerImageServiceRollbackIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 21<br>
 * 설명              : LedgerImageService 클래스 롤백을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 8. 21</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class LedgerImageServiceRollbackIT extends IntegrationTest {

    @Autowired
    private LedgerImageService target;

    @SpyBean
    LedgerImageRepository imageRepository;

    @SpyBean
    private LedgerImageStorage imageStorage;

    Ledger ledger;

    @BeforeEach
    void setUp() {
        ledgerRepository.deleteAll();

        Long id = ledgerRepository.save(
                LedgerTestFixture.builder().build()
        );

        ledger = ledgerRepository.findById(id);
    }

    @Nested
    @WithMockCustomUser
    @DisplayName("이미지 업로드 진행할 때")
    class ImageUpload {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("요청한 이미지만큼 이미지를 저장한다.")
            void savesImages_whenCountIsWithinLimit() throws IOException {
                //given
                String memberId = MemberTestData.DEFAULT_ID;
                List<MultipartFile> images = List.of(
                        ImageFixture.jpg("test")
                );

                //when
                assertDoesNotThrow(() -> target.processImageUpload(memberId, ledger.getId(), images));

                //then
                assertThat(imageRepository.findByLedgerId(ledger.getId()).size())
                        .isEqualTo(1);

                try (Stream<Path> paths = Files.walk(tempDir)) {
                    long size = paths
                            .filter(Files::isRegularFile)
                            .filter(path -> path.getFileName().toString().endsWith("jpg"))
                            .count();

                    assertThat(size).isEqualTo(1);
                }
            }

            @Test
            @DisplayName("등록 가능한 개수보다 많은 파일을 업로드해도 가능한 개수만큼만 저장한다.")
            void savesImagesOnlyUpToLimit_whenExceedsMaxUploadLimit() throws IOException {
                //given
                String memberId = MemberTestData.DEFAULT_ID;
                List<MultipartFile> images = List.of(
                        ImageFixture.jpg("test1"),
                        ImageFixture.jpg("test2"),
                        ImageFixture.png("test3")
                );

                //given: 회원의 이미지 개수를 2로 변경
                jdbcTemplate.update(
                        "UPDATE member_info SET image_limit = 2 WHERE id = ?",
                        memberId
                );

                //when
                assertDoesNotThrow(() -> target.processImageUpload(memberId, ledger.getId(), images));

                //then
                assertThat(imageRepository.findByLedgerId(ledger.getId()).size()).isEqualTo(2);

                try (Stream<Path> paths = Files.walk(tempDir)) {
                    long size = paths
                            .filter(Files::isRegularFile)
                            .filter(f -> f.getFileName().toString().endsWith("jpg"))
                            .count();

                    assertThat(size).isEqualTo(2);
                }
            }

            @Test
            @DisplayName("파일을 저장하면 이미지 정보를 데이터베이스에 저장한다.")
            void savesImageMetaData_whenFileSaveIsSuccessful() {
                //given
                String memberId = MemberTestData.DEFAULT_ID;
                List<MultipartFile> images = List.of(
                        ImageFixture.jpg("test1"),
                        ImageFixture.png("test2")
                );

                //given: 회원의 이미지 개수를 2로 변경
                jdbcTemplate.update(
                        "UPDATE member_info SET image_limit = 2 WHERE id = ?",
                        memberId
                );

                //when
                assertDoesNotThrow(() -> target.processImageUpload(memberId, ledger.getId(), images));

                //then
                List<LedgerImage> ledgerImages = imageRepository.findByLedgerId(ledger.getId());

                assertThat(ledgerImages.size()).isEqualTo(2);

                assertThat(ledgerImages)
                        .extracting(LedgerImage::getLedgerId)
                        .containsOnly(ledger.getId());

                assertThat(ledgerImages)
                        .extracting(LedgerImage::getSortOrder)
                        .containsExactly(1, 2);
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("파일 저장에 실패하면 저장된 모든 파일을 삭제한다.")
            void deletesAllSavedFiles_whenFileSaveFails() throws IOException {
                //given
                String memberId = MemberTestData.DEFAULT_ID;
                List<MultipartFile> images = List.of(
                        ImageFixture.jpg("test1"),
                        ImageFixture.png("tes2")
                );

                //given: 회원의 이미지 개수를 2로 변경
                jdbcTemplate.update(
                        "UPDATE member_info SET image_limit = 2 WHERE id = ?",
                        memberId
                );

                doThrow(new IOException("파일 저장 실패"))
                        .when(imageStorage)
                        .store(images.get(1), memberId);

                //when
                assertThatThrownBy(() -> target.processImageUpload(memberId, ledger.getId(), images));

                //then
                assertThat(imageRepository.findByLedgerId(ledger.getId()).size()).isZero();

                try(Stream<Path> paths =  Files.walk(tempDir)) {
                    boolean hasFile = paths
                            .anyMatch(path -> path.getFileName().toString().endsWith(".jpg") || path.getFileName().toString().endsWith(".png"));

                    assertThat(hasFile).isFalse();
                }
            }

            @Test
            @DisplayName("이미지 정보 저장에 실패하면 저장된 모든 파일을 삭제한다.")
            void deletesAllSavedFiles_whenDatabaseSaveFails() throws IOException {
                //given
                String memberId = MemberTestData.DEFAULT_ID;
                List<MultipartFile> images = List.of(
                        ImageFixture.jpg("test1"),
                        ImageFixture.png("tes2")
                );

                doThrow(new DataAccessException("실패") {})
                        .when(imageRepository)
                        .saveAll(any());

                //when
                assertThatThrownBy(() -> target.processImageUpload(memberId, ledger.getId(), images));

                //then:
                assertThat(imageRepository.findByLedgerId(ledger.getId()).size()).isZero();

                try(Stream<Path> paths =  Files.walk(tempDir)) {
                    boolean hasFile = paths
                            .anyMatch(path -> path.getFileName().toString().endsWith(".jpg") || path.getFileName().toString().endsWith(".png"));

                    assertThat(hasFile).isFalse();
                }
            }

        }

    }

}