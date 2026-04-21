package com.moneymanager.unit.service.file;

import com.moneymanager.BusinessExceptionAssert;
import com.moneymanager.domain.global.dto.StoredFile;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ErrorCode;
import com.moneymanager.exception.error.ErrorInfo;
import com.moneymanager.service.file.FileCommandService;
import com.moneymanager.service.file.FileNamingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.moneymanager.exception.error.ErrorCode.FILE_COLLISION_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.file<br>
 * 파일이름       : FileCommandServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 17<br>
 * 설명              : FileCommandService 클래스 로직을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 3. 17</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class FileCommandServiceTest {

	@Mock
	private FileNamingStrategy strategy;

	@Mock
	private MultipartFile file;

	@TempDir
	Path tempDir;

	private final FileCommandService service = new FileCommandService();


//	@Nested
//	@DisplayName("파일 저장")
//	class StoreFileTest {
//
//		@Test
//		@DisplayName("정상적인 파일이면 저장 후 StoredFile을 반환한다.")
//		void returnsStoredFile_whenSaveSucceeds() throws IOException {
//			//given
//			when(strategy.getRootPath()).thenReturn("base");
//			when(strategy.generateRelativePath()).thenReturn(Path.of("2026", "03").toString());
//			when(strategy.generateStoredName(anyString())).thenReturn("test.png");
//
//			when(file.getOriginalFilename()).thenReturn("original.png");
//
//			//when
//			StoredFile result = service.storeFile(tempDir, file, strategy);
//
//			//then
//			assertThat(result).isNotNull();
//
//			assertThat(result.getRelativePath()).isEqualTo(Path.of("2026", "03", "test.png").toString());
//			assertThat(result.getFullPath()).isEqualTo(tempDir.resolve(Path.of("base", "2026", "03", "test.png")).toString());
//
//			verify(file).transferTo(any(File.class));
//			verify(strategy).generateStoredName("original.png");
//		}
//
//		@Test
//		@DisplayName("폴더가 이미 존재해도 파일이 저장된다.")
//		void savesFile_whenDirectoryExists() throws IOException {
//			//given
//			Path existingDirectory = tempDir.resolve(Path.of("base", "2026", "03"));
//			Files.createDirectories(existingDirectory);
//
//			when(strategy.getRootPath()).thenReturn("base");
//			when(strategy.generateRelativePath()).thenReturn(Path.of("2026", "03").toString());
//			when(strategy.generateStoredName(anyString())).thenReturn("test.png");
//
//			when(file.getOriginalFilename()).thenReturn("original.png");
//
//			//when
//			StoredFile result = service.storeFile(tempDir, file, strategy);
//
//			//then
//			assertThat(result).isNotNull();
//
//			assertThat(result.getRelativePath()).isEqualTo(Path.of("2026", "03", "test.png").toString());
//			assertThat(result.getFullPath()).isEqualTo(tempDir.resolve(Path.of("base", "2026", "03", "test.png")).toString());
//
//			verify(file).transferTo(any(File.class));
//		}
//
//	}
//
//
//	@Nested
//	@DisplayName("폴더 생성")
//	class CreateDirectoryTest {
//
//		@Test
//		@DisplayName("폴더가 없으면 폴더를 생성한다.")
//		void createsDirectory_whenNoDirectory() {
//			//given
//			when(strategy.getRootPath()).thenReturn("base");
//			when(strategy.generateRelativePath()).thenReturn(Path.of("2026", "03").toString());
//			when(strategy.generateStoredName(anyString())).thenReturn("test.png");
//
//			when(file.getOriginalFilename()).thenReturn("original.png");
//
//			//when
//			StoredFile result = service.storeFile(tempDir, file, strategy);
//
//			//then
//			assertThat(Files.exists(tempDir.resolve(Path.of("base", "2026", "03")))).isTrue();
//			assertThat(Files.isDirectory(tempDir.resolve(Path.of("base", "2026", "03")))).isTrue();
//
//		}
//
//		@Test
//		@DisplayName("폴더 경로에 파일이 있으면 예외가 발생한다.")
//		void throwsException_whenDirectoryHasFile() throws IOException {
//			//given
//			Path path = tempDir.resolve(Path.of("base", "2026", "03"));
//			Files.createDirectories(path.getParent());
//			Files.createFile(path);
//
//			when(strategy.getRootPath()).thenReturn("base");
//			when(strategy.generateRelativePath()).thenReturn(Path.of("2026", "03").toString());
//			when(strategy.generateStoredName(anyString())).thenReturn("test.png");
//
//			when(file.getOriginalFilename()).thenReturn("original.png");
//
//			//when & then
//			BusinessExceptionAssert.assertThatBusinessException(catchThrowable(() -> service.storeFile(tempDir, file, strategy)))
//					.hasErrorCode(FILE_COLLISION_ALREADY_EXISTS)
//					.hasLogMessage("폴더 생성", "중복데이터", "file");
//		}
//
//	}
//
//
//	@Nested
//	@DisplayName("파일 업로드")
//	class UploadTest {
//
//		@Test
//		@DisplayName("파일을 저장할 수 없으면 예외가 발생한다.")
//		void throwsException_whenSaveFails() throws IOException {
//			//given
//			when(strategy.getRootPath()).thenReturn("base");
//			when(strategy.generateRelativePath()).thenReturn(Path.of("2026", "03").toString());
//			when(strategy.generateStoredName(anyString())).thenReturn("test.png");
//
//			when(file.getOriginalFilename()).thenReturn("original.png");
//
//			doThrow(new IOException())
//					.when(file)
//					.transferTo(any(File.class));
//
//			//when & then
//			assertThatExceptionOfType(BusinessException.class)
//					.isThrownBy(()-> service.storeFile(tempDir, file, strategy))
//					.satisfies(e -> {
//						ErrorInfo errorDTO = e.getErrorInfo();
//
//						assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.FILE_ETC_RESOURCE_ERROR);
//						assertThat(errorDTO.getLogMessage()).contains("파일저장불가");
//					});
//		}
//
//	}


	@Nested
	@DisplayName("파일 삭제")
	class DeleteTest {

		@Test
		@DisplayName("파일이 정상적으로 삭제된다.")
		void deleteFiles_whenSuccessfully() throws IOException {
			//given
			File tempFile = File.createTempFile("test", ".png");

			assertThat(tempFile).exists();

			//when
			service.deleteFiles(List.of(tempFile));

			//then
			assertThat(tempFile.exists()).isFalse();
		}

	}

}
