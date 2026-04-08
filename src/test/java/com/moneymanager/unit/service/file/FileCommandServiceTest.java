package com.moneymanager.unit.service.file;

import com.moneymanager.domain.global.dto.StoredFile;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ErrorCode;
import com.moneymanager.exception.error.ErrorInfo;
import com.moneymanager.service.file.FileCommandService;
import com.moneymanager.service.file.FileNamingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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

	@InjectMocks
	private FileCommandService service;

	@Mock
	private FileNamingStrategy strategy;

	@Mock
	private MultipartFile file;

	@TempDir
	Path tempDir;

	@BeforeEach
	void setUp() {
		service = new FileCommandService();
	}

	//==================[ storeFile ]==================
	@Test
	@DisplayName("정상적인 파일이면 저장에 성공한다.")
	void storeFile_success() {
		//given
		when(strategy.getBasePath()).thenReturn("base");
		when(strategy.generateRelativePath()).thenReturn("2026\\03");
		when(strategy.generateStoredName(anyString())).thenReturn("test.png");

		when(file.getOriginalFilename()).thenReturn("original.png");

		//when
		StoredFile result = service.storeFile(tempDir, file, strategy);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getRelativePath()).contains("2026\\03\\test.png");
		assertThat(result.getFullPath()).contains("\\base\\2026\\03\\test.png");
	}


	@Test
	@DisplayName("중복된 폴더면 생성이 불가능하고 예외가 발생한다.")
	void storeFile_failure_duplicateDirectory() throws IOException {
		//given
		when(strategy.getBasePath()).thenReturn("base");
		when(strategy.generateRelativePath()).thenReturn("2026\\01");
		when(strategy.generateStoredName(anyString())).thenReturn("test.png");
		when(file.getOriginalFilename()).thenReturn("original.png");

		File existDir = Path.of(tempDir.toString(), "base", "2026", "01").toFile();
		existDir.getParentFile().mkdirs();
		existDir.createNewFile();

		//when & then
		assertThatExceptionOfType(BusinessException.class)
				.isThrownBy(()-> service.storeFile(tempDir, file, strategy))
				.satisfies(e -> {
					ErrorInfo errorDTO = e.getErrorInfo();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.FILE_COLLISION_ALREADY_EXISTS);
					assertThat(errorDTO.getLogMessage()).contains("중복");
				});
	}


	@Test
	@DisplayName("파일을 저장할 수 없으면 예외가 발생한다.")
	void storeFile_failure_spaceIsFull() throws IOException {
		//given
		when(strategy.getBasePath()).thenReturn("base");
		when(strategy.generateRelativePath()).thenReturn("2026\\01");
		when(strategy.generateStoredName(anyString())).thenReturn("test.png");
		when(file.getOriginalFilename()).thenReturn("original.png");

		doThrow(new IOException())
				.when(file)
				.transferTo(any(File.class));

		//when & then
		assertThatExceptionOfType(BusinessException.class)
				.isThrownBy(()-> service.storeFile(tempDir, file, strategy))
				.satisfies(e -> {
					ErrorInfo errorDTO = e.getErrorInfo();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.FILE_ETC_RESOURCE_ERROR);
					assertThat(errorDTO.getLogMessage()).contains("파일저장불가");
				});
	}


	//==================[ deleteFiles ]==================
	@Test
	@DisplayName("파일이 정상적으로 삭제된다.")
	void deleteFiles_success() throws IOException {
		//given
		File tempFile = File.createTempFile("test", ".png");

		assertThat(tempFile).exists();

		//when
		service.deleteFiles(List.of(tempFile));

		//then
		assertThat(tempFile.exists()).isFalse();
	}
}
