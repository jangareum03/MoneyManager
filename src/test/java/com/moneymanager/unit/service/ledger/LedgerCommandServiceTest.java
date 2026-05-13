package com.moneymanager.unit.service.ledger;


import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ErrorInfo;
import com.moneymanager.exception.error.ServiceAction;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.file.FileCommandService;
import com.moneymanager.service.ledger.LedgerCommandService;
import com.moneymanager.fixture.LedgerRequestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.util.ReflectionTestUtils;

import static com.moneymanager.exception.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.ledger<br>
 * 파일이름       : LedgerCommandServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 15<br>
 * 설명              : LedgerCommandService 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 1. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class LedgerCommandServiceTest {

	@InjectMocks
	private LedgerCommandService target;

	@Mock
	private SecurityUtil securityUtil;

	@Mock
	private LedgerRepository ledgerRepository;

	@Mock
	private LedgerImageRepository imageRepository;

	@Mock
	private FileCommandService fileCommandService;


	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(target, "rootPath", "/test-root");
	}


	@Nested
	@DisplayName("가계부 등록")
	class RegisterTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("이미지가 없는 요청이면 이미지 저장 로직을 수행하지 않는다.")
			void savesOnlyLedger_whenRequestHasNoImage() {
				//given
				LedgerWriteRequest request = LedgerRequestFixture.defaultLedgerWriteRequest().build();
				Ledger savedLedger = mock(Ledger.class);

				when(securityUtil.getMemberId()).thenReturn("test");
				when(ledgerRepository.save(any())).thenReturn(1L);
				when(ledgerRepository.findById(1L)).thenReturn(savedLedger);

				//when
				target.registerLedger(request);

				//then
				verify(ledgerRepository).save(any(Ledger.class));
				verify(ledgerRepository).findById(1L);
				verify(fileCommandService, never()).storeFile(any(), any(), any());
				verify(imageRepository, never()).saveAll(anyList());
			}

//			@Test
//			@DisplayName("이미지가 있는 요청이면 파일 저장 후 이미지를 저장한다.")
//			void savesFileAndImage_whenRequestHasImage() throws IOException {
//				//given
//				LedgerWriteRequest request = LedgerRequestFixture.withImage().build();
//				Ledger savedLedger = mock(Ledger.class);
//
//				when(securityUtil.getMemberId()).thenReturn("test");
//				when(ledgerRepository.save(any(Ledger.class))).thenReturn(1L);
//				when(ledgerRepository.findById(1L)).thenReturn(savedLedger);
//
//				when(savedLedger.getId()).thenReturn(1L);
//				when(savedLedger.getMemberId()).thenReturn("test");
//
//				when(fileCommandService.storeFile(any(Path.class), any(MultipartFile.class), any()))
//						.thenReturn(
//								new StoredFile("/test-root/test/img1.jpg", "img1.jpg"),
//								new StoredFile("/test-root/test/img2.jpg", "img2.jpg")
//						);
//
//				//when
//				service.registerLedger(request);
//
//				//then
//				ArgumentCaptor<List<LedgerImage>> captor = ArgumentCaptor.forClass(List.class);
//				verify(fileCommandService, times(2)).storeFile(any(Path.class), any(MultipartFile.class), any());
//				verify(imageRepository).saveAll(captor.capture());
//
//				List<LedgerImage> savedImages = captor.getValue();
//				assertThat(savedImages).hasSize(2);
//				assertThat(savedImages)
//						.extracting(LedgerImage::getLedgerId)
//						.containsOnly(1L);
//				assertThat(savedImages)
//						.extracting(LedgerImage::getImagePath)
//						.containsExactly("img1.jpg", "img2.jpg");
//				assertThat(savedImages)
//						.extracting(LedgerImage::getSortOrder)
//						.containsExactly(1, 2);
//			}

		}


		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("회원ID가 없으면 예외가 발생한다.")
			void throwsException_whenMemberIdIsNull() {
				//given
				LedgerWriteRequest request = LedgerRequestFixture.defaultLedgerWriteRequest().build();

				when(securityUtil.getMemberId()).thenThrow(
						BusinessException.of(MEMBER_AUTHORITY_UNAUTHORIZED,  "인증 실패")
								.withUserMessage("로그인이 실패합니다.")
				);

				//when & then
				assertThatThrownBy(() -> target.registerLedger(request))
						.isInstanceOfSatisfying(BusinessException.class, e -> {
							assertThat(e.getErrorInfo().getService()).isEqualTo(ServiceAction.LEDGER_REGISTER);
						});

				verifyNoInteractions(ledgerRepository, imageRepository, fileCommandService);
			}

			@Test
			@DisplayName("가계부 저장 후 조회되지 않으면 예외가 발생한다.")
			void throwsException_whenLedgerIdDoesNotExist() {
				//given
				LedgerWriteRequest request = LedgerRequestFixture.defaultLedgerWriteRequest().build();

				when(securityUtil.getMemberId()).thenReturn("test");
				when(ledgerRepository.save(any(Ledger.class))).thenReturn(1L);
				when(ledgerRepository.findById(1L)).thenReturn(null);

				//when & then
				assertThatThrownBy(() -> target.registerLedger(request))
						.isInstanceOfSatisfying(BusinessException.class, e -> {
							assertThat(e.getErrorInfo().getErrorCode()).isSameAs(LEDGER_TARGET_NOT_FOUND);
							assertThat(e.getErrorInfo().getService()).isEqualTo(ServiceAction.LEDGER_REGISTER);
						});

				verify(fileCommandService, never()).storeFile(any(), any(), any());
				verify(imageRepository, never()).saveAll(anyList());
			}

			@Test
			@DisplayName("가계부 저장 중 중복키 예외가 발생하면 BusinessException으로 변환한다.")
			void throwsException_whenDuplicateKeyOccurs() {
				//given
				LedgerWriteRequest request = LedgerRequestFixture.defaultLedgerWriteRequest().build();

				when(securityUtil.getMemberId()).thenReturn("test");
				when(ledgerRepository.save(any(Ledger.class)))
						.thenThrow(new DuplicateKeyException("중복키 발생") {});

				//when & then
				assertThatThrownBy(() -> target.registerLedger(request))
						.isInstanceOfSatisfying(BusinessException.class, e -> {
							ErrorInfo errorInfo = e.getErrorInfo();

							assertThat(errorInfo.getErrorCode()).isSameAs(LEDGER_COLLISION_UNIQUE_CONSTRAINT);
							assertThat(errorInfo.getUserMessage()).contains("등록 중 문제");
							assertThat(errorInfo.getService()).isEqualTo(ServiceAction.LEDGER_REGISTER);
						});
			}

			@Test
			@DisplayName("가계부 저장 중 데이터 무결성 위반이 발생하면 BusinessException으로 변환한다.")
			void throwsException_whenDataIntegrityViolationOccurs() {
				//given
				LedgerWriteRequest request = LedgerRequestFixture.defaultLedgerWriteRequest().build();

				when(securityUtil.getMemberId()).thenReturn("test");
				when(ledgerRepository.save(any(Ledger.class)))
						.thenThrow(new DataIntegrityViolationException("위반 발생"));

				//when & then
				assertThatThrownBy(() -> target.registerLedger(request))
						.isInstanceOfSatisfying(BusinessException.class, e -> {
							ErrorInfo errorInfo = e.getErrorInfo();

							assertThat(errorInfo.getErrorCode()).isSameAs(LEDGER_COLLISION_UNIQUE_CONSTRAINT);
							assertThat(errorInfo.getUserMessage()).contains("등록 중 문제");
							assertThat(errorInfo.getLogMessage()).contains("데이터 무결성 위반");
							assertThat(errorInfo.getService()).isEqualTo(ServiceAction.LEDGER_REGISTER);
						});

				verify(ledgerRepository, never()).findById(anyLong());
				verifyNoInteractions(imageRepository);
			}

//			@Test
//			@DisplayName("이미지 저장 중 예외가 발생하면 저장된 파일을 삭제하고 사용자메시지를 변경한다.")
//			void deletesFilesAndChangesMessage_whenStoreFileThrowsBusinessException() throws IOException {
//				//given
//				LedgerWriteRequest request = LedgerRequestFixture.withImage().build();
//				Ledger savedLedger = mock(Ledger.class);
//				Path path = Path.of("test-root", "test/img1.jpg");
//
//				when(securityUtil.getMemberId()).thenReturn("test");
//				when(ledgerRepository.save(any(Ledger.class))).thenReturn(1L);
//				when(ledgerRepository.findById(1L)).thenReturn(savedLedger);
//
//				when(savedLedger.getId()).thenReturn(1L);
//				when(savedLedger.getMemberId()).thenReturn("test");
//
//				when(fileCommandService.storeFile(any(Path.class), any(MultipartFile.class), any()))
//						.thenReturn(new StoredFile(path.toString(), "img1.jpg"))
//						.thenThrow(BusinessException.of(FILE_ETC_RESOURCE_ERROR, "업로드에 실패했습니다.", "파일 저장 실패"));
//
//				//when & then
//				assertThatThrownBy(() -> service.registerLedger(request))
//						.isInstanceOfSatisfying(BusinessException.class, e -> {
//							ErrorInfo errorInfo = e.getErrorInfo();
//
//							assertThat(errorInfo.getErrorCode()).isSameAs(FILE_ETC_RESOURCE_ERROR);
//							assertThat(errorInfo.getUserMessage()).isEqualTo("이미지 저장 중 문제가 발생했습니다. 다시 시도해주세요.");
//							assertThat(errorInfo.getService()).isEqualTo(ServiceAction.LEDGER_REGISTER);
//						});
//
//				ArgumentCaptor<List<File>> captor = ArgumentCaptor.forClass(List.class);
//				verify(fileCommandService).deleteFiles(captor.capture());
//
//				List<File> deletedFiles = captor.getValue();
//				assertThat(deletedFiles).hasSize(1);
//				assertThat(deletedFiles.get(0).getPath()).isEqualTo(path.toString());
//
//				verify(imageRepository, never()).saveAll(anyList());
//			}

		}

	}


	@Nested
	@DisplayName("이미지 경로")
	class ImagePathTest {

//		@Test
//		@DisplayName("이미지 저장 경로는 회원ID 기준으로 생성된다.")
//		void createsRootPath_whenMemberIdExist() throws IOException {
//			//given
//			LedgerWriteRequest request = LedgerRequestFixture.withImage().build();
//			Ledger savedLedger = mock(Ledger.class);
//
//			when(securityUtil.getMemberId()).thenReturn("test");
//			when(ledgerRepository.save(any(Ledger.class))).thenReturn(1L);
//			when(ledgerRepository.findById(1L)).thenReturn(savedLedger);
//
//			when(savedLedger.getMemberId()).thenReturn("test");
//			when(savedLedger.getId()).thenReturn(1L);
//
//			when(fileCommandService.storeFile(any(Path.class), any(MockMultipartFile.class), any()))
//					.thenReturn(
//							new StoredFile(Path.of("/test-root","test", "img1.jpg").toString(), "img1.jpg"),
//							new StoredFile(Path.of("/test-root","test", "img2.jpg").toString(), "img2.jpg")
//					);
//
//			//when
//			service.registerLedger(request);
//
//			//then
//			verify(fileCommandService, times(2))
//					.storeFile(
//							eq(Path.of("/test-root", "test")),
//							any(MultipartFile.class),
//							any()
//					);
//		}

	}

}