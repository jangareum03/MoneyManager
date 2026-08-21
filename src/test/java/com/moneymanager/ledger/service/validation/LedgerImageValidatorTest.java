package com.moneymanager.ledger.service.validation;

import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.fixture.file.ImageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.moneymanager.global.exception.code.CommonErrorCode.*;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.validation<br>
 * 파일이름       : LedgerImageValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 18<br>
 * 설명              : LedgerImageValidator 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 8. 18</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@DisplayName("이미지 파일을 검증할 때")
class LedgerImageValidatorTest {

    private LedgerImageValidator target;

    @BeforeEach
    void setUp() {
        this.target = new LedgerImageValidator();
    }

    @Nested
    class ValidMultipartFile {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("정상적인 파일이면 검증에 성공한다.")
            void validatesSuccessfully_whenFileIsValid() {
            	//given
                MultipartFile file = ImageFixture.jpg("test");

            	//when
                assertDoesNotThrow(
                        () -> target.validate(file)
                );
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @ParameterizedTest
            @NullSource
            @DisplayName("요청 객체가 null이면 예외를 발생시킨다.")
            void throwsValidationException_whenRequestIsNull(MultipartFile file) {
                //when & then
                ApplicationExceptionAssert.assertThatApplicationException(
                                catchThrowable(() -> target.validate(file))
                        ).isInstanceOf(ValidationException.class)
                        .hasErrorCode(FILE_NOT_FOUND)
                        .hasWork("가계부 이미지 검증")
                        .hasCauseMessage("파일 없음");
            }

            @Test
            @DisplayName("파일을 읽는 도중 문제가 발생하면 예외를 발생시킨다.")
            void throwsExternalException_whenFileReadFails() throws IOException {
                //given
                MultipartFile file = mock(MultipartFile.class);
                when(file.getOriginalFilename()).thenReturn("테스트.png");

                when(file.getInputStream())
                        .thenThrow(new IOException("파일 읽기 실패"));

                //when
                Throwable throwable = catchThrowable(() -> target.validate(file));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(FILE_READ_FAILED)
                        .hasWork("가계부 이미지 검증")
                        .hasTarget(LedgerWriteRequest.class)
                        .hasValue("images", "테스트.png");
            }

        }

    }


    @Nested
    class HeaderValidation {

        @Test
        @DisplayName("Header가 4byte보다 짧으면 예외가 발생한다.")
        void throwsValidationException_whenHeaderIsShorterThanFourBytes() throws IOException {
            //given
            MultipartFile file = mock(MultipartFile.class);

            when(file.getInputStream())
                    .thenReturn(new ByteArrayInputStream(
                            new byte[] {
                                    (byte) 0x25,
                                    (byte) 0x50
                            }
                    ));

            //when
            Throwable throwable = catchThrowable(() -> target.validate(file));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(UNSUPPORTED_FILE_TYPE)
                    .hasWork("이미지 파일 검증")
                    .hasTarget(MultipartFile.class)
                    .hasValue("header", "2550");
        }

        @Test
        @DisplayName("허용되지 않은 파일이면 예외가 발생한다.")
        void throwsException_whenFileIsNotAllowed() throws IOException {
            //given
            MultipartFile file = mock(MultipartFile.class);

            when(file.getInputStream())
                    .thenReturn(new ByteArrayInputStream(
                            new byte[] {
                                    (byte) 0x00,
                                    (byte) 0x00,
                                    (byte) 0xFE,
                                    (byte) 0xFF
                            }
                    ));

            //when
            Throwable throwable = catchThrowable(() -> target.validate(file));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(UNSUPPORTED_FILE_TYPE)
                    .hasWork("이미지 파일 검증")
                    .hasTarget(MultipartFile.class)
                    .hasValue("header", "0000FEFF");
        }

    }


    @Nested
    class ContentTypeValidation {

        private MultipartFile file;

        @Test
        @DisplayName("contentType이 이미지가 아니면 예외가 발생한다.")
        void throwsValidationException_whenContentTypeIsNotImage() {
            //given
            MockMultipartFile file = new MockMultipartFile(
                    "images",
                    "test.jpg",
                    "text/plain",
                    getFileContent()
            );

            //when
            Throwable throwable = catchThrowable(() -> target.validate(file));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(UNSUPPORTED_FILE_TYPE)
                    .hasWork("이미지 파일 검증")
                    .hasTarget(MultipartFile.class)
                    .hasValue("contentType", "text/plain");
        }

    }


    @Nested
    class ExtensionValidation {

        @Test
        @DisplayName("파일 이름이 null이거나 비어있으면 예외를 발생시킨다.")
        void throwsValidationException_whenFileNameIsBlank() {
        	//given
            MockMultipartFile file = new MockMultipartFile(
                    "images",
                    null,
                    "image/jpeg",
                    getFileContent()
            );
        	
        	//when
            Throwable throwable = catchThrowable(() -> target.validate(file));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasWork("이미지 파일 검증")
                    .hasTarget(MultipartFile.class)
                    .hasValue("originalFilename");
        }
        
        @Test
        @DisplayName("파일 이름에 확장자가 없으면 예외를 발생시킨다.")
        void throwsValidationException_whenFileNameHasNoExtension() {
            //given
            MockMultipartFile file = new MockMultipartFile(
                    "images",
                    "test",
                    "image/jpeg",
                    getFileContent()
            );

            //when
            Throwable throwable = catchThrowable(() -> target.validate(file));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(UNSUPPORTED_FILE_TYPE)
                    .hasWork("이미지 파일 검증")
                    .hasTarget(MultipartFile.class)
                    .hasValue("originalFilename", file.getOriginalFilename())
                    .hasOption("allowed", "jpg, jpeg, png");
        }
        
        @Test
        @DisplayName("허용하는 확장자가 아니면 예외를 발생시킨다.")
        void throwsValidationException_whenExtensionIsNotAllowed() {
            //given
            MockMultipartFile file = new MockMultipartFile(
                    "images",
                    "test.pdf",
                    "image/jpeg",
                    getFileContent()
            );

            //when
            Throwable throwable = catchThrowable(() -> target.validate(file));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(UNSUPPORTED_FILE_TYPE)
                    .hasWork("이미지 파일 검증")
                    .hasTarget(MultipartFile.class)
                    .hasValue("originalFilename", file.getOriginalFilename())
                    .hasOption("allowed", "jpg, jpeg, png");
        }

    }


    @Nested
    class SizeValidation {

        @Test
        @DisplayName("파일 크기가 허용 크기보다 크면 예외가 발생한다.")
        void throwsValidationException_whenFileSizeIsExceeded() {
        	//given
            byte[] largeFile = new byte[1024 * 1024 * 6];
            largeFile[0] = (byte) 0xFF;
            largeFile[1] = (byte) 0xD8;
            largeFile[2] = (byte) 0xFF;
            largeFile[3] = (byte) 0xE0;

            MockMultipartFile file = new MockMultipartFile(
                    "images",
                    "test.png",
                    "image/jpeg",
                    largeFile
            );

            //when
            Throwable throwable = catchThrowable(() -> target.validate(file));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(ValidationException.class)
                    .hasErrorCode(FILE_TOO_LARGE)
                    .hasWork("이미지 파일 검증")
                    .hasTarget(MultipartFile.class)
                    .hasValue("size", file.getSize())
                    .hasOption("max", String.valueOf(5 * 1024 * 1024));
        }

    }

    //===== 보조 메서드 =====
    private byte[] getFileContent() {
        byte[] content = new byte[1024];

        content[0] = (byte) 0xFF;
        content[1] = (byte) 0xD8;
        content[2] = (byte) 0xFF;
        content[3] = (byte) 0xE0;

        return content;
    }
}