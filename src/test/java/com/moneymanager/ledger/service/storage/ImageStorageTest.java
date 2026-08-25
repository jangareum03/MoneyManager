package com.moneymanager.ledger.service.storage;

import com.moneymanager.global.config.MutableClock;
import com.moneymanager.global.domain.FileMetadata;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


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
    @DisplayName("파일 저장할 때")
    class Save {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("유효한 파일과 회원번호면 서버에 파일을 저장하고 파일 정보를 반환한다.")
            void savesFileAndReturnsFileMetadata_whenRequestIsValid() throws IOException {
                //given
                String memberId = MemberTestData.DEFAULT_ID;
                MockMultipartFile file = ImageFixture.jpg("test");

                //when
                FileMetadata result = target.store(file, memberId);

                //then
                assertThat(result).isNotNull();
                assertThat(result.getOriginalFileName()).isEqualTo("test.jpg");
                assertThat(result.getContentType()).isEqualTo(file.getContentType());

                assertThat(result.getStoredFileName())
                        .isNotEqualTo("test.jpg")
                        .endsWith(".jpg");

                Path savedFile = Path.of(temp.toString())
                        .resolve(result.getAbsolutePath());
                assertThat(savedFile).isRegularFile();  //일반 파일인지 확인

                assertThat(Files.readAllBytes(savedFile))
                        .isEqualTo(file.getBytes());
            }

            @Test
            @DisplayName("저장할 때마다 저장된 파일명은 중복되지 않는다.")
            void generatesUniqueFileName_whenFileIsSaved() throws IOException {
                //given
                String memberId = MemberTestData.DEFAULT_ID;
                MockMultipartFile file = ImageFixture.jpg("test");

                //when
                FileMetadata resultA = target.store(file, memberId);
                FileMetadata resultB = target.store(file, memberId);

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
                target.store(file, memberId);

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
                target.store(file, memberId);

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
                Throwable throwable = catchThrowable(() -> target.store(file, memberId));
            	
            	//then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        
                        .hasWork("파일 업로드")
                        .hasCauseMessage("파일 없음");
            }

            @Test
            @DisplayName("파일이 비어있으면 예외를 발생시킨다.")
            void throwsInternalException_whenFileIsEmpty() {
                //given
                MockMultipartFile file = ImageFixture.empty("test");
                String memberId = MemberTestData.DEFAULT_ID;

                //when
                Throwable throwable = catchThrowable(() -> target.store(file, memberId));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        
                        .hasWork("파일 업로드")
                        .hasCauseMessage("파일 없음");
            }
            
            @Test
            @DisplayName("파일을 저장 중 문제가 발생하면 예외를 전파시킨다.")
            void rethrowsException_whenFileSaveFails() throws IOException {
            	//given
                String memberId = MemberTestData.DEFAULT_ID;
                MockMultipartFile file = mock(MockMultipartFile.class);

                when(file.getOriginalFilename()).thenReturn("test.jpg");

                doThrow(new IOException("저장 실패"))
                        .when(file)
                                .transferTo(any(Path.class));
            	
            	//when
                assertThatThrownBy(() -> target.store(file, memberId))
                        ;
            }

        }

    }


    @Nested
    @DisplayName("파일 삭제할 때")
    class Delete {

        @Test
        @DisplayName("파일 경로를 전달하면 파일을 삭제한다.")
        void deletesFile_whenFilePathIsGiven() throws IOException {
        	//given
            Path file = temp.resolve("test.jpg");
            Files.createFile(file);

            assertThat(file).exists();
        	
        	//when
            target.delete(file);
        	
        	//then
        	assertThat(file).doesNotExist();
        }
        
        @Test
        @DisplayName("빈 디렉터리 경로를 전달하면 디렉터리를 삭제한다.")
        void deletesDirectory_whenPathIsEmptyDirectory() throws IOException {
        	//given
            Path directory = temp.resolve("images");
            Files.createDirectories(directory);

            assertThat(directory).exists().isDirectory();
        	
        	//when
            target.delete(directory);
        	
        	//then
        	assertThat(directory).doesNotExist();
        }

        @Test
        @DisplayName("존재하지 않는 경로를 전달해도 예외를 던지지 않는다.")
        void doesNothing_whenPathDoesNotExist() {
        	//given
            Path notExists = temp.resolve("notExists.jpg");

            assertThat(notExists).doesNotExist();

            //when
            assertDoesNotThrow(() -> target.delete(notExists));
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
           assertDoesNotThrow(() -> target.delete(directory));
        }

    }

}