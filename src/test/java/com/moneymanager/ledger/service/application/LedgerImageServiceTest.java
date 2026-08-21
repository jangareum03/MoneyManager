package com.moneymanager.ledger.service.application;

import com.moneymanager.global.domain.FileMetadata;
import com.moneymanager.global.exception.code.CommonErrorCode;
import com.moneymanager.global.exception.code.LedgerErrorCode;
import com.moneymanager.global.exception.exception.ExternalException;
import com.moneymanager.ledger.domain.dto.response.ImageSlot;
import com.moneymanager.ledger.domain.entity.LedgerImage;
import com.moneymanager.ledger.repository.LedgerImageRepository;
import com.moneymanager.ledger.service.policy.LedgerPolicy;
import com.moneymanager.ledger.service.storage.LedgerImageStorage;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
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
import java.util.List;

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
public class LedgerImageServiceTest{

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

        String memberId = MemberTestData.MEMBER_ID;
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
                            ImageSlot.ofEmptySlot(),
                            ImageSlot.ofEmptySlot(),
                            ImageSlot.ofLockedSlot()
                    ));
        }

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("SlotStatus가 Empty만큼 이미지를 저장한다.")
            void savesImages_whenSlotStatusIsEmpty() throws IOException {
                //given
                when(imageStorage.store(eq(images.get(0)), eq(memberId)))
                        .thenReturn(FileMetadataFixture.jpg("test1").build());
                when(imageStorage.store(eq(images.get(1)), eq(memberId)))
                        .thenReturn(FileMetadataFixture.png("test2").build());

                //whe
                assertDoesNotThrow(() -> target.processImageUpload(memberId, ledgerId, images));

                //then
                verify(imageStorage, times(2))
                        .store(any(), eq(memberId));

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
                when(imageStorage.store(images.get(0), memberId))
                        .thenThrow(new IOException("파일 저장 실패"));

                //when
                Throwable throwable = catchThrowable(() -> target.processImageUpload(memberId, ledgerId, images));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .isInstanceOf(ExternalException.class)
                        .hasErrorCode(CommonErrorCode.FILE_UPLOAD_FAILED)
                        .hasWork("이미지 파일 저장")
                        .hasTarget(FileMetadata.class)
                        .hasValue("memberId", memberId, "originalFilename", images.get(0).getOriginalFilename());

                verify(imageStorage, times(1)).store(any(), eq(memberId));

                verify(imageRepository, never()).saveAll(any());
                verify(imageStorage, never()).delete(any());
            }

            @Test
            @DisplayName("이미지 저장 중 일부만 실패하면 모두 삭제한다.")
            void deletesAllFiles_whenPartialImageSaveFails() throws IOException {
                //given
                when(imageStorage.store(eq(images.get(0)), eq(memberId)))
                        .thenReturn(FileMetadataFixture.jpg("test1").build());

                when(imageStorage.store(images.get(1), memberId))
                        .thenThrow(new IOException("파일 저장 실패"));

                //when
                Throwable throwable = catchThrowable(() -> target.processImageUpload(memberId, ledgerId, images));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .isInstanceOf(ExternalException.class)
                        .hasErrorCode(CommonErrorCode.FILE_UPLOAD_FAILED)
                        .hasWork("이미지 파일 저장")
                        .hasTarget(FileMetadata.class)
                        .hasValue("memberId", memberId, "originalFilename", images.get(1).getOriginalFilename());

                verify(imageStorage, times(2)).store(any(), eq(memberId));
                verify(imageStorage, times(1)).delete(any());

                verify(imageRepository, never()).saveAll(any());
            }

            @Test
            @DisplayName("이미지 정보 저장 중 실패하면 파일과 정보가 모두 삭제된다.")
            void deletesFilesAndMetaData_whenMetaDataSaveFails() throws IOException {
                //given
                when(imageStorage.store(eq(images.get(0)), eq(memberId)))
                        .thenReturn(FileMetadataFixture.jpg("test1").build());
                when(imageStorage.store(eq(images.get(1)), eq(memberId)))
                        .thenReturn(FileMetadataFixture.png("test2").build());

                doThrow(new DataAccessException("이미지 정보 저장 실패") {})
                        .when(imageRepository).saveAll(any());

                //when
                Throwable throwable = catchThrowable(() -> target.processImageUpload(memberId, ledgerId, images));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .isInstanceOf(ExternalException.class)
                        .hasErrorCode(LedgerErrorCode.DATA_PERSISTENCE_FAILED)
                        .hasWork("가계부 이미지 정보 저장")
                        .hasTarget(LedgerImage.class)
                        .hasValue("memberId", memberId, "ledgerId", ledgerId, "imageCount", images.size());

                verify(imageStorage, times(2)).store(any(), eq(memberId));
                verify(imageRepository, times(1)).saveAll(any());
            }

        }

    }

}