package com.moneymanager.ledger.service.application;

import com.moneymanager.global.domain.FileMetadata;
import com.moneymanager.ledger.domain.dto.response.ImageSlot;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.entity.LedgerImage;
import com.moneymanager.ledger.domain.enums.SlotStatus;
import com.moneymanager.ledger.repository.LedgerImageRepository;
import com.moneymanager.ledger.service.policy.LedgerPolicy;
import com.moneymanager.ledger.service.storage.LedgerImageStorage;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerImageTestFixture;
import com.moneymanager.support.fixture.entity.LedgerTestFixture;
import com.moneymanager.support.fixture.file.FileMetadataFixture;
import com.moneymanager.support.fixture.file.ImageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.moneymanager.global.exception.code.ErrorCode.FILE_UPLOAD_FAILED;
import static com.moneymanager.global.exception.code.ErrorCode.INTERVAL_SERVER_ERROR;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.application<br>
 * 파일이름       : LedgerImageServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 21<br>
 * 설명              : LedgerImageService 클래스 로직을 검증하는 단위 테스트 클래스
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
@ExtendWith(MockitoExtension.class)
public class LedgerImageServiceTest {

    @InjectMocks
    LedgerImageService target;

    @Mock
    MemberReadService memberReadService;

    @Mock
    LedgerPolicy ledgerPolicy;

    @Mock
    LedgerImageStorage imageStorage;

    @Mock
    LedgerImageRepository imageRepository;

    @Nested
    @DisplayName("이미지 업로드 진행할 때")
    class ImageUpload {

        String memberId = MemberTestData.DEFAULT_ID;
        Long ledgerId = 1L;
        List<MultipartFile> images = List.of(
                ImageFixture.jpg("test1"),
                ImageFixture.png("test2")
        );

        @BeforeEach
        void setUp() {
            when(memberReadService.getAvailableImageCount(memberId))
                    .thenReturn(2);

            when(ledgerPolicy.imageSlots(2))
                    .thenReturn(List.of(
                            ImageSlot.of(SlotStatus.EMPTY, "/image/ledger/slot-unlock.svg"),
                            ImageSlot.of(SlotStatus.EMPTY, "/image/ledger/slot-unlock.svg"),
                            ImageSlot.of(SlotStatus.LOCKED, "/image/ledger/slot-lock.svg")
                    ));
        }

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("SlotStatus가 Empty만큼 이미지를 저장한다.")
            void savesImages_whenSlotStatusIsEmpty() throws IOException {
                //given
                when(imageStorage.createFileMetadata(eq(memberId), eq(images.get(0))))
                        .thenReturn(FileMetadataFixture.jpg(memberId, "test1"));
                when(imageStorage.createFileMetadata(eq(memberId), eq(images.get(1))))
                        .thenReturn(FileMetadataFixture.png(memberId, "test2"));

                //whe
                assertDoesNotThrow(() -> target.processImageUpload(memberId, ledgerId, images));

                //then
                verify(imageStorage, times(2))
                        .createFileMetadata(eq(memberId), any(MultipartFile.class));

                verify(imageRepository).saveAll(any());
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("이미지 저장이 실패하면 파일을 삭제한다.")
            void deletesFile_whenImageSaveFails() throws IOException {
                //given
                when(imageStorage.createFileMetadata(memberId, images.get(0)))
                        .thenThrow(new IOException("파일 저장 실패"));

                //when
                Throwable throwable = catchThrowable(() -> target.processImageUpload(memberId, ledgerId, images));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)

                        .hasErrorCode(FILE_UPLOAD_FAILED)
                        .hasWork("이미지 파일 저장")
                        .hasTarget(FileMetadata.class)
                        .hasValue("memberId", memberId, "originalFilename", images.get(0).getOriginalFilename());

                verify(imageStorage, times(1)).createFileMetadata(eq(memberId), any(MultipartFile.class));

                verify(imageRepository, never()).saveAll(any());
                verify(imageStorage, never()).deleteFile(any());
            }

            @Test
            @DisplayName("이미지 저장 중 일부만 실패하면 모두 삭제한다.")
            void deletesAllFiles_whenPartialImageSaveFails() throws IOException {
                //given
                when(imageStorage.createFileMetadata(eq(memberId), eq(images.get(0))))
                        .thenReturn(FileMetadataFixture.jpg(memberId, "test1"));

                when(imageStorage.createFileMetadata(memberId, images.get(1)))
                        .thenThrow(new IOException("파일 저장 실패"));

                //when
                Throwable throwable = catchThrowable(() -> target.processImageUpload(memberId, ledgerId, images));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)

                        .hasErrorCode(FILE_UPLOAD_FAILED)
                        .hasWork("이미지 파일 저장")
                        .hasTarget(FileMetadata.class)
                        .hasValue("memberId", memberId, "originalFilename", images.get(1).getOriginalFilename());

                verify(imageStorage, times(2)).createFileMetadata(eq(memberId), any(MultipartFile.class));
                verify(imageStorage, times(1)).deleteFile(any());

                verify(imageRepository, never()).saveAll(any());
            }

            @Test
            @DisplayName("이미지 정보 저장 중 실패하면 파일과 정보가 모두 삭제된다.")
            void deletesFilesAndMetaData_whenMetaDataSaveFails() throws IOException {
                //given
                when(imageStorage.createFileMetadata(eq(memberId), eq(images.get(0))))
                        .thenReturn(FileMetadataFixture.jpg(memberId, "test1"));
                when(imageStorage.createFileMetadata(eq(memberId), eq(images.get(1))))
                        .thenReturn(FileMetadataFixture.png(memberId, "test2"));

                doThrow(new DataAccessException("이미지 정보 저장 실패") {
                })
                        .when(imageRepository).saveAll(any());

                //when
                Throwable throwable = catchThrowable(() -> target.processImageUpload(memberId, ledgerId, images));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)

                        .hasErrorCode(INTERVAL_SERVER_ERROR)
                        .hasWork("가계부 이미지 정보 저장")
                        .hasTarget(LedgerImage.class)
                        .hasValue("memberId", memberId, "ledgerId", ledgerId, "imageCount", images.size());

                verify(imageStorage, times(2)).createFileMetadata(eq(memberId), any(MultipartFile.class));
                verify(imageRepository, times(1)).saveAll(any());
            }

        }

    }


    @Nested
    @DisplayName("이미지 삭제를 진행할 때")
    class ImageDelete {

        @Test
        @DisplayName("여러 가계부와 이미지가 존재하면 각 가계부와 이미지에 대해 정상적으로 처리한다.")
        void processesAccountBooksAndImages_whenMultipleAccountBooksAndImagesExist() throws IOException {
            //given
            List<Ledger> ledgers = List.of(
                    LedgerTestFixture.builder().buildExisting(1L, "code1"),
                    LedgerTestFixture.builder().buildExisting(2L, "code2")
            );

            List<LedgerImage> firstLedgerImages = List.of(
                    LedgerImageTestFixture.builder(1L, Path.of("images/member/test01.png")).build(),
                    LedgerImageTestFixture.builder(1L, Path.of("images/member/test02.png")).build()
            );

            List<LedgerImage> secondLedgerImages = List.of(
                    LedgerImageTestFixture.builder(2L, Path.of("images/member/test03.jpg")).build()
            );

            when(imageRepository.findByLedgerId(1L))
                    .thenReturn(firstLedgerImages);

            when(imageRepository.findByLedgerId(2L))
                    .thenReturn(secondLedgerImages);

            for (LedgerImage image : List.of(
                    firstLedgerImages.get(0),
                    firstLedgerImages.get(1),
                    secondLedgerImages.get(0)
            )) {
                when(imageStorage.resolveAbsolutePath(image))
                        .thenReturn(Path.of("root").resolve(image.getImagePath()));
            }

            //when
            target.processImagesDelete(ledgers);

            //then
            verify(imageRepository, times(2)).findByLedgerId(anyLong());

            verify(imageStorage).resolveAbsolutePath(firstLedgerImages.get(0));
            verify(imageStorage).resolveAbsolutePath(firstLedgerImages.get(1));
            verify(imageStorage).resolveAbsolutePath(secondLedgerImages.get(0));

            verify(imageStorage).deleteOrThrow(Path.of("root/images/member/test01.png"));
            verify(imageStorage).deleteOrThrow(Path.of("root/images/member/test02.png"));
            verify(imageStorage).deleteOrThrow(Path.of("root/images/member/test03.jpg"));
        }

        @Test
        @DisplayName("이미지가 존재하지 않으면 파일 경로 조회 및 파일 삭제 메서드가 호출되지 않는다.")
        void doesNotCallFilePathAndDeleteMethods_whenImageDoesNotExist() throws IOException {
            //given
            List<Ledger> ledgers = List.of(
                    LedgerTestFixture.builder().buildExisting(1L, "code1"),
                    LedgerTestFixture.builder().buildExisting(2L, "code2")
            );

            List<LedgerImage> firstLedgerImages = List.of(
                    LedgerImageTestFixture.builder(2L, Path.of("images/member/test03.jpg")).build()
            );

            when(imageRepository.findByLedgerId(1L))
                    .thenReturn(List.of());

            when(imageRepository.findByLedgerId(2L))
                    .thenReturn(firstLedgerImages);

            for (LedgerImage image : firstLedgerImages) {
                when(imageStorage.resolveAbsolutePath(image))
                        .thenReturn(Path.of("root").resolve(image.getImagePath()));
            }

            //when
            target.processImagesDelete(ledgers);

            //then
            verify(imageRepository, times(2)).findByLedgerId(anyLong());

            verify(imageStorage).resolveAbsolutePath(firstLedgerImages.get(0));
            verify(imageStorage).deleteOrThrow(any());
        }

        @Test
        @DisplayName("파일 삭제 실패하면 temp폴더로 이동하는 메서드를 호출한다.")
        void movesFileToTempFolder_whenFileDeletionFails() throws IOException {
            //given
            List<Ledger> ledgers = List.of(
                    LedgerTestFixture.builder().buildExisting(1L, "code1")
            );

            List<LedgerImage> images = List.of(
                    LedgerImageTestFixture.builder(1L, Path.of("member/images/test1.png")).build(),
                    LedgerImageTestFixture.builder(1L, Path.of("member/images/test2.png")).build()
            );

            when(imageRepository.findByLedgerId(1L))
                    .thenReturn(images);

            Path root = Path.of("root");
            for (LedgerImage image : images) {
                when(imageStorage.resolveAbsolutePath(image))
                        .thenReturn(root.resolve(image.getImagePath()));
            }

            doThrow(new IOException("파일 저장 실패"))
                    .when(imageStorage).deleteOrThrow(root.resolve(images.get(0).getImagePath()));

            //when
            target.processImagesDelete(ledgers);

            //then
            verify(imageStorage).resolveAbsolutePath(images.get(0));
            verify(imageStorage).resolveAbsolutePath(images.get(1));

            verify(imageStorage, times(2)).deleteOrThrow(any(Path.class));

            verify(imageStorage).moveToTemp(eq(root.resolve(images.get(0).getImagePath())));
            verify(imageStorage, never()).moveToTemp(root.resolve(images.get(1).getImagePath()));
        }

        @Test
        @DisplayName("파일 삭제 및 temp폴더 이동에 실패하면 로그를 출력한다.")
        void logsError_whenFileDeletionAndTempMoveFail() throws IOException {
            //given
            Ledger ledger = LedgerTestFixture.builder().buildExisting(1L, "code1");

            LedgerImage image = LedgerImageTestFixture.builder(1L, Path.of("member/images/test1.png")).build();

            Path path = Path.of("root").resolve(image.getImagePath());

            when(imageRepository.findByLedgerId(1L))
                    .thenReturn(List.of(image));

            when(imageStorage.resolveAbsolutePath(image))
                    .thenReturn(path);

            doThrow(new IOException("파일 저장 실패"))
                    .when(imageStorage)
                    .deleteOrThrow(path);

            doThrow(new IOException("파일 이동 실패"))
                    .when(imageStorage)
                    .moveToTemp(path);

            //when
            assertDoesNotThrow(() -> target.processImagesDelete(List.of(ledger)));

            //then
            verify(imageStorage).deleteOrThrow(path);
            verify(imageStorage).moveToTemp(path);
        }

    }

}