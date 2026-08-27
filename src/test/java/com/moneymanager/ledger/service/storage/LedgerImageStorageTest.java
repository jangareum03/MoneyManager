package com.moneymanager.ledger.service.storage;

import com.moneymanager.global.config.MutableClock;
import com.moneymanager.global.domain.FileMetadata;
import com.moneymanager.global.exception.code.ErrorCode;
import com.moneymanager.ledger.domain.entity.LedgerImage;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerImageTestFixture;
import com.moneymanager.support.fixture.file.ImageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.storage<br>
 * 파일이름       : ImageStorageTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 20<br>
 * 설명              : LedgerImageStorage 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 8. 20</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class LedgerImageStorageTest {

    private LedgerImageStorage target;

    private MutableClock clock;
    private Path temp;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        clock = new MutableClock();
        temp = tempDir;

        target = new LedgerImageStorage(clock, tempDir.toString());
    }


    @Nested
    @DisplayName("FileMetadata 생성할 때")
    class Create {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("유효한 파일과 회원번호면 파일 정보를 생성한다.")
            void createFileMetadata_whenRequestIsValid() throws IOException {
                //given
                String memberId = MemberTestData.DEFAULT_ID;
                MockMultipartFile file = ImageFixture.jpg("test");

                //when
                FileMetadata result = target.createFileMetadata(memberId, file);

                //then
                assertThat(result).isNotNull();
                assertThat(result.getOriginalFileName()).isEqualTo("test.jpg");
                assertThat(result.getContentType()).isEqualTo(file.getContentType());

                assertThat(result.getStoredFileName())
                        .isNotEqualTo("test.jpg")
                        .endsWith(".jpg");
            }

            @Test
            @DisplayName("저장할 때마다 저장될 파일명은 중복되지 않는다.")
            void generatesUniqueFileName_whenFileIsSaved() throws IOException {
                //given
                String memberId = MemberTestData.DEFAULT_ID;
                MockMultipartFile file = ImageFixture.jpg("test");

                //when
                FileMetadata resultA = target.createFileMetadata(memberId, file);
                FileMetadata resultB = target.createFileMetadata(memberId, file);

                //then
                assertThat(resultA.getStoredFileName())
                        .isNotEqualTo(resultB.getStoredFileName());
            }

            @Test
            @DisplayName("폴더가 없으면 새로운 폴더에 저장한다.")
            void savesFileInNewDirectory_whenDirectoryDoesNotExist() throws IOException {
                //given
                String memberId = MemberTestData.DEFAULT_ID;
                MockMultipartFile file = ImageFixture.jpg("test");

                Path directoryPath = temp.resolve(memberId);
                assertThat(directoryPath).doesNotExist();

                //when
                target.createFileMetadata(memberId, file);

                //then
                assertThat(directoryPath).exists().isDirectory();
            }

            @Test
            @DisplayName("폴더가 있으면 폴더를 생성하지 않는다.")
            void savesFileInExistingDirectory_whenDirectoryExists() throws IOException {
                //given
                String memberId = MemberTestData.DEFAULT_ID;
                MockMultipartFile file = ImageFixture.jpg("test");

                Path directoryPath = temp.resolve(memberId);
                Files.createDirectories(directoryPath);

                //when
                target.createFileMetadata(memberId, file);

                //then
                assertThat(directoryPath).exists().isDirectory();

                try(Stream<Path> files = Files.list(directoryPath)){
                    assertThat(files).hasSize(1);
                }
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("파일이 null이면 예외를 발생시킨다.")
            void throwsInternalException_whenFileIsNull() {
            	//given
                MockMultipartFile file = null;
                String memberId = MemberTestData.DEFAULT_ID;
            	
            	//when
                Throwable throwable = catchThrowable(() -> target.createFileMetadata(memberId, file));
            	
            	//then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(ErrorCode.FILE_NOT_FOUND)
                        .hasWork("FileMetadata 생성");
            }

            @Test
            @DisplayName("파일이 비어있으면 예외를 발생시킨다.")
            void throwsInternalException_whenFileIsEmpty() {
                //given
                MockMultipartFile file = ImageFixture.empty("test");
                String memberId = MemberTestData.DEFAULT_ID;

                //when
                Throwable throwable = catchThrowable(() -> target.createFileMetadata(memberId, file));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(ErrorCode.FILE_NOT_FOUND)
                        .hasWork("FileMetadata 생성");
            }

        }

    }


    @Nested
    @DisplayName("temp 폴더로 이동할 때")
    class MoveToTemp {
        
        @Test
        @DisplayName("기존 파일을 temp폴더로 이동한다.")
        void movesFileToTempFolder_whenFileExists() throws IOException {
        	//given
            Path path = temp.resolve("test.png");
            Files.write(path, "test".getBytes());

            Files.createDirectory(temp.resolve("temp"));
        	
        	//when
            target.moveToTemp(path);
        	
        	//then
        	Path movePath = temp.resolve("temp").resolve("test.png");

            assertThat(Files.exists(path)).isFalse();
            assertThat(Files.exists(movePath)).isTrue();
            assertThat(Files.readString(movePath)).isEqualTo("test");
        }
        
        @Test
        @DisplayName("temp폴더가 없어도 생성 후 이동한다.")
        void createsTempFolderAndMovesFile_whenTempFolderDoesNotExist() throws IOException {
            //given
            Path path = temp.resolve("test.png");
            Files.write(path, "test".getBytes());

            //when
            target.moveToTemp(path);

            //then
            Path movePath = temp.resolve("temp").resolve("test.png");

            assertThat(Files.exists(path)).isFalse();
            assertThat(Files.exists(movePath)).isTrue();
            assertThat(Files.readString(movePath)).isEqualTo("test");
        }
        
        @Test
        @DisplayName("파일이 존재하지 않으면 아무것도 하지 않는다.")
        void doesNothing_whenFileDoesNotExist() throws IOException {
        	//given
            Path path = temp.resolve("test.png");
        	
        	//when
            target.moveToTemp(path);
        	
        	//then
            assertThat(Files.exists(path)).isFalse();
            assertThat(Files.exists(temp.resolve("temp"))).isFalse();
        }

    }


    @Nested
    @DisplayName("절대 경로를 조합할 때")
    class ResolvePath {

        @Test
        @DisplayName("데이터에 저장된 이미지 경로를 바탕으로 절대경로를 반환한다.")
        void createPath_whenLedgerImageIsValid() {
            //given
            Path relativePath = Path.of("member1/images/test.png");
            LedgerImage image = LedgerImageTestFixture.builder(1L, relativePath).build();

            //when
            Path result = target.resolveAbsolutePath(image);

            //then
            assertThat(result.toString()).contains(relativePath.toString());
        }

    }


    @Nested
    @DisplayName("파일 삭제할 때")
    class DeleteOrThrow {

        @Test
        @DisplayName("유효한 파일 경로를 전달하면 파일을 삭제한다.")
        void deletesFile_whenFilePathIsGiven() throws IOException {
            //given
            Path file = temp.resolve("test.jpg");
            Files.createFile(file);

            assertThat(file).exists();

            //when
            target.deleteOrThrow(file);

            //then
            assertThat(file).doesNotExist();
        }

        @Test
        @DisplayName("빈 폴더 경로를 전달하면 폴더를 삭제한다.")
        void deletesDirectory_whenPathIsEmptyDirectory() throws IOException {
            //given
            Path directory = temp.resolve("images");
            Files.createDirectories(directory);

            assertThat(directory).exists().isDirectory();

            //when
            target.deleteOrThrow(directory);

            //then
            assertThat(directory).doesNotExist();
        }

        @Test
        @DisplayName("존재하지 않은 경로면 예외를 발생시킨다.")
        void throwsIOException_whenPathDoesNotExist() {
            //given
            Path notExists = temp.resolve("notExists.jpg");

            assertThat(notExists).doesNotExist();

            //when
            assertThatThrownBy(() -> target.deleteOrThrow(notExists))
                    .isInstanceOf(IOException.class);
        }

    }


    @Nested
    @DisplayName("파일 삭제할 때")
    class Delete {

        @Test
        @DisplayName("존재하지 않는 경로를 전달해도 예외를 던지지 않는다.")
        void doesNothing_whenPathDoesNotExist() {
        	//given
            Path notExists = temp.resolve("notExists.jpg");

            assertThat(notExists).doesNotExist();

            //when
            assertDoesNotThrow(() -> target.deleteFile(notExists));
        }

        @Test
        @DisplayName("파일이 존재하는 폴더를 삭제하면 예외를 던지지 않는다.")
        void doesNothing_whenDirectoryDoesNotEmpty() throws IOException {
        	//given
            Path directory = temp.resolve("images");
            Files.createDirectories(directory);

            Path file = directory.resolve("test.jpg");
            Files.createFile(file);
        	
        	//when
           assertDoesNotThrow(() -> target.deleteFile(directory));
        }

    }

}