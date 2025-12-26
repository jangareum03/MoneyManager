package com.moneymanager.ledger.service;

import com.moneymanager.dao.main.LedgerImageDao;
import com.moneymanager.dao.member.MemberInfoDaoImpl;
import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ServerException;
import com.moneymanager.service.main.FileService;
import com.moneymanager.service.main.ImageServiceImpl;
import com.moneymanager.service.main.validation.ImageValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.image<br>
 * 파일이름       : LedgerImageServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 10<br>
 * 설명              : 가계부 이미지와 관련된 기능을 확인하는 테스트 클래스
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
 * 		 	  <td>25. 12. 10.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
	public class LedgerImageServiceTest {

	@Spy
	@InjectMocks	private ImageServiceImpl imageService;

	@Mock				private LedgerImageDao imageDao;
	@Mock				private MemberInfoDaoImpl memberInfoDao;
	@Mock				private FileService	fileService;

	@Mock				private MockMultipartFile	multipartFile;


	//=================================================
	// getLimitImageCount() 테스트
	//=================================================
	@DisplayName("회원번호가 없으면 기본값으로 반환한다.")
	@Test
	void 회원번호_없으면_기본값반환(){
		//given
		String memberId = "not";
		when(memberInfoDao.findImageLimit(memberId)).thenThrow(EmptyResultDataAccessException.class);

		//when
		int result = imageService.getLimitImageCount(memberId);

		//then
		assertThat(result).isEqualTo(1);
	}

	@DisplayName("회원번호가 있으면 설정된 개수를 반환한다.")
	@Test
	void 회원번호_있으면_개수반환(){
		//given
		String memberId = "member";
		when(memberInfoDao.findImageLimit(memberId)).thenReturn(3);

		//when
		int result = imageService.getLimitImageCount(memberId);

		//then
		assertThat(result).isEqualTo(3);
	}



	//=================================================
	// getImageListByLedger() 테스트
	//=================================================
	@DisplayName("없는 가계부는 모든 요소가 Null인 리스트를 반환한다.")
	@Test
	void 가계부에_이미지_없으면_null리스트_반환(){
		//given
		Long id = 5L;
		when(imageDao.findImageListByLedger(id)).thenReturn(Collections.emptyList());

		//when
		List<LedgerImage> result = imageService.getImageListByLedger(id, 1);

		//then
		assertThat(result).hasSize(3);
		assertThat(result).allMatch(Objects::isNull);
	}

	@DisplayName("가계부에 저장된 이미지 리스트를 반환한다.")
	@Test
	void 가계부에_이미지_있으면_File리스트_반환(){
		//given
		Long id = 2L;
		when(imageDao.findImageListByLedger(id))
				.thenReturn(
						List.of(
								LedgerImage.builder()
										.id(1L)
										.ledgerId(1L)
										.imagePath("/2025/11/23/b8d3c9a1-4f8e-4a7b-8c6d-5e9f2a1b0c4d.png")
										.sortOrder(2)
										.createdAt(LocalDateTime.of(2025, 11, 23, 11,11,11))
										.build(),
								LedgerImage.builder()
										.id(2L)
										.ledgerId(1L)
										.imagePath("/2025/11/25/3c2a8f0e-7d6b-4e1c-9f5a-0d4b3c2e1f0d.jpg")
										.sortOrder(1)
										.createdAt(LocalDateTime.of(2025, 11, 23, 11,11,11))
										.updatedAt(LocalDateTime.of(2025, 11, 25, 11,11,11))
										.build()
						)
				);

		List<LedgerImage> mockResult = new ArrayList<>();
		mockResult.add(
				LedgerImage.builder()
						.id(2L)
						.ledgerId(1L)
						.imagePath("/2025/11/25/3c2a8f0e-7d6b-4e1c-9f5a-0d4b3c2e1f0d.jpg")
						.sortOrder(1)
						.createdAt(LocalDateTime.of(2025, 11, 23, 11,11,11))
						.updatedAt(LocalDateTime.of(2025, 11, 25, 11,11,11))
						.build()
		);
		mockResult.add(
				LedgerImage.builder()
						.id(1L)
						.ledgerId(1L)
						.imagePath("/2025/11/23/b8d3c9a1-4f8e-4a7b-8c6d-5e9f2a1b0c4d.png")
						.sortOrder(2)
						.createdAt(LocalDateTime.of(2025, 11, 23, 11,11,11))
						.build()
		);
		mockResult.add(null);

		//when
		List<LedgerImage> result = imageService.getImageListByLedger(id, 2);

		//then
		assertThat(result).hasSize(3);

		assertThat(result.get(0)).usingRecursiveComparison()
				.isEqualTo(mockResult.get(0));

		assertThat(result.get(1)).usingRecursiveComparison()
				.isEqualTo(mockResult.get(1));

		assertThat(result.get(2)).isNull();
	}



	//=================================================
	// getImageSlots() 테스트
	//=================================================
	@DisplayName("이미지 슬롯은 최소 1개부터 최대 3개까지 사용 가능하다.")
	@ParameterizedTest
	@CsvSource({
			"1, true, false, false",
			"2, true, true, false",
			"3, true, true, true",
	})
	void 이미지슬롯_경계값(int limit, boolean first, boolean second, boolean third){
		//given
		String memberId = "member123";
		when(memberInfoDao.findImageLimit(memberId)).thenReturn(limit);

		//when
		List<Boolean> result = imageService.getImageSlots(memberId);

		//then
		assertThat(result)
				.isNotNull()
				.hasSize(3)
				.containsExactly(first, second, third);
	}


	//=================================================
	// saveImages() 테스트
	//=================================================
	@DisplayName("파일이 잘못되면 IOException 예외가 발생한다.")
	@Test
	void 파일_비정상이면_예외발생() throws IOException {
		//given
		Ledger ledger = Ledger.builder()
				.id(1L)
				.memberId("member")
				.date("20251101")
				.build();
		List<MultipartFile> files = List.of(multipartFile, multipartFile);

		//when
		when(fileService.getFolder(anyString(), any())).thenReturn(new File("test-folder"));
		doThrow(IOException.class).when(fileService).saveFile( eq(files.get(0)), any(File.class) );


		//then
		try(MockedStatic<ImageValidator> mocked = mockStatic(ImageValidator.class)) {
			mocked.when(() -> ImageValidator.validate(any())).thenAnswer(invocationOnMock -> null);

			assertThatExceptionOfType(ServerException.class)
					.isThrownBy(() -> imageService.saveImages(ledger, files))
					.satisfies(e -> {
						ErrorDTO errorDTO = e.getErrorDTO();

						assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.STORAGE_FILE_INTERNAL);

					});
		}

		verify(fileService, times(2)).saveFile(eq(multipartFile), any(File.class));
		verify(imageDao, never()).insertAll(any());
	}

	@DisplayName("파일들 중 저장이 하나라도 실패하면 이전에 생성된 파일들이 모두 삭제된다.")
	@Test
	void 파일저장_실패하면_이전에_생성된_파일들은_삭제() throws IOException {
		//given
		Ledger ledger = Ledger.builder()
				.id(1L)
				.memberId("member")
				.date("20251101")
				.build();

		MultipartFile file1 = mock(MultipartFile.class);
		MultipartFile file2 =mock(MultipartFile.class);
		List<MultipartFile> files = List.of(file1, file2);

		File spyFile1 = spy(new File("file1"));
		File spyFile2 = spy(new File("file2"));

		when(fileService.createFile(any(), any())).thenReturn(spyFile1, spyFile2);

		doNothing().doThrow(IOException.class)
				.when(fileService).saveFile(any(), any());

		when(spyFile1.exists()).thenReturn(true);

		try( MockedStatic<ImageValidator> mockedStatic = mockStatic(ImageValidator.class) ) {
			mockedStatic.when( () -> ImageValidator.validate(any()) )
					.thenAnswer( invocation -> null );

			//when
			assertThatExceptionOfType(ServerException.class)
					.isThrownBy(() -> imageService.saveImages(ledger, files));
		}

		verify(spyFile2, never()).delete();

		verify(imageDao, never()).insertAll(any());
	}

	@DisplayName("정상적으로 파일이 저장되고 데이터베이스에 추가된다.")
	@Test
	void 파일_정상이면_저장하고_DB_반영완료() throws IOException {
		//given
		Ledger ledger = Ledger.builder()
				.id(1L)
				.memberId("member")
				.date("20251101")
				.build();

		MultipartFile file1 = mock(MultipartFile.class);
		MultipartFile file2 = mock(MultipartFile.class);
		List<MultipartFile> files = List.of(file1, file2);

		File spyFile1 = spy(new File("file1"));
		File spyFile2 = spy(new File("file2"));

		when(fileService.getFolder(any(), any()))
				.thenReturn(new File("folder"));

		when(fileService.createFile(any(), anyString()))
				.thenReturn(spyFile1, spyFile1);

		doNothing()
				.when(fileService)
				.saveFile(any(), any());

		try( MockedStatic<ImageValidator> mockedStatic = mockStatic(ImageValidator.class) ) {
			mockedStatic.when(() -> ImageValidator.validate(any()))
					.thenAnswer(invocation -> null);

			//when
			imageService.saveImages(ledger, files);
		}

		//then
		verify(fileService, times(2)).saveFile(any(), any());
		verify(imageDao, times(1))
				.insertAll(argThat(
						images ->
							images.size() == 2 && images.get(0).getSortOrder() == 1 && images.get(1).getSortOrder() == 2
				));

		verify(spyFile1, never()).delete();
		verify(spyFile2, never()).delete();
	}



	//=================================================
	// changeImages() 테스트
	//=================================================
	@DisplayName("기존 이미지와 변경될 이미지가 모두 있으면 기존 이미지는 삭제되고 변경될 이미지는 저장된다.")
	@Test
	void 기존이미지와_새이미지_모두_있으면_저장및삭제(){
		//given
		Ledger ledger = Ledger.builder().id(1L).build();

		List<MultipartFile> newImages = List.of(mock(MultipartFile.class));
		List<LedgerImage> oldImages = List.of(mock(LedgerImage.class));

		when(imageDao.findImageListByLedger(1L)).thenReturn(oldImages);

		doNothing().when(imageService).saveImages(any(), any());
		doNothing().when(imageService).deleteImages(any(), any());

		//when
		imageService.changeImages( ledger, newImages );

		//then
		verify(imageService).saveImages(ledger, newImages);
		verify(imageService).deleteImages(ledger, oldImages);
	}

	@DisplayName("기존 이미지는 있지만, 새 이미지가 없으면 기존 이미지를 삭제만 한다.")
	@Test
	void 기존이미지_있고_새이미지_없으면_삭제() {
		//given
		Ledger ledger = Ledger.builder().id(1L).build();
		List<LedgerImage> oldFiles = List.of(mock(LedgerImage.class));

		when(imageDao.findImageListByLedger(1L)).thenReturn(oldFiles);
		doNothing().when(imageService).deleteImages(any(), any());

		//when
		imageService.changeImages(ledger, Collections.emptyList());

		//then
		verify(imageService, never()).saveImages(any(), any());
		verify(imageService).deleteImages(eq(ledger), eq(oldFiles));
	}

	@DisplayName("기존 이미지가 없고, 새 이미지만 있다면 저장만 한다.")
	@Test
	void 기존이미지_없고_새이미지_있으면_저장() {
		//given
		Ledger ledger = Ledger.builder().id(1L).build();
		List<MultipartFile> newImages = List.of(mock(MultipartFile.class));

		when( imageDao.findImageListByLedger(1L) ).thenReturn(Collections.emptyList());
		doNothing().when(imageService).saveImages(any(), any());

		//when
		imageService.changeImages(ledger, newImages);

		//then
		verify(imageService, never()).deleteImages(any(), any());
		verify(imageService).saveImages(eq(ledger), eq(newImages));
	}
}