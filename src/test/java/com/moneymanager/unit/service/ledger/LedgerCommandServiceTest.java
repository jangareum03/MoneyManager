package com.moneymanager.unit.service.ledger;


import com.moneymanager.domain.global.dto.StoredFile;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ErrorCode;
import com.moneymanager.exception.error.ServiceAction;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.file.FileCommandService;
import com.moneymanager.service.ledger.LedgerCommandService;
import com.moneymanager.service.validation.LedgerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.moneymanager.exception.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.ledger<br>
 * 파일이름       : LedgerCommandServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 15<br>
 * 설명              : LedgerCommandService 클래스 로직을 검증하는 테스트 클래스
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

	@InjectMocks	private LedgerCommandService service;

	@Mock	private SecurityUtil securityUtil;
	@Mock	private LedgerValidator ledgerValidator;
	@Mock	private LedgerRepository ledgerRepository;
	@Mock	private LedgerImageRepository imageRepository;
	@Mock	private FileCommandService fileService;

	private final String memberId = "member";
	private LedgerWriteRequest request;
	private Ledger savedLedger;

	@BeforeEach
	void setUp() {
		request = LedgerWriteRequest.builder()
				.date("20260101")
				.categoryCode("010101")
				.amount(10000L)
				.amountType("none")
				.image(List.of(mock(MultipartFile.class)))
				.build();

		savedLedger = Ledger.builder()
				.id(1L)
				.memberId(memberId)
				.build();
	}

	//==================[ registerLedger ]==================
	@Test
	@DisplayName("정상 요청이면 가계부 등록이 성공한다.")
	void registerLedger_success() {
		//given
		when(securityUtil.getMemberId()).thenReturn(memberId);
		when(ledgerRepository.save(any())).thenReturn(1L);
		when(ledgerRepository.findById(1L)).thenReturn(savedLedger);
		when(fileService.storeFile(any(), any(), any())).thenReturn(new StoredFile("full/path/rel/image", "/rel/image"));

		//when
		service.registerLedger(request);

		//then
		verify(ledgerValidator).register(request);
		verify(ledgerRepository).save(any());
		verify(imageRepository).saveAll(anyList());
	}


	@Test
	@DisplayName("예외가 발생하면 ServiceAction이 있다.")
	void registerLedger_Failure_ServiceActionExists() {
		//given
		when(securityUtil.getMemberId()).thenReturn(memberId);
		doThrow(BusinessException.of(
				LEDGER_INPUT_MISSING,
				"예외 던짐"
		)).when(ledgerValidator).register(request);

		//when
		BusinessException result = assertThrows(BusinessException.class, () -> service.registerLedger(request));

		//then
		assertThat(result.getErrorInfo().getService()).isEqualTo(ServiceAction.LEDGER_REGISTER);
	}


	@Test
	@DisplayName("회원번호가 없으면 그 후 로직을 실행하지 않는다.")
	void registerLedger_failure_memberIdIsNull() {
		//given
		BusinessException exception = BusinessException.of(
				ErrorCode.MEMBER_AUTHORITY_UNAUTHORIZED,
				"회원 인증 실패"
		);

		when(securityUtil.getMemberId()).thenThrow(exception);

		//when
		assertThrows(BusinessException.class, () -> service.registerLedger(request));

		//then
		verify(securityUtil).getMemberId();
		verify(ledgerValidator, never()).register(any());
		verify(ledgerRepository, never()).save(any());
		verify(ledgerValidator, never()).validateImage(any());
		verify(imageRepository, never()).saveAll(anyList());
	}


	@Test
	@DisplayName("요청 정보 검증 실패하면 그 후 로직을 실행하지 않는다")
	void registerLedger_failure_validate() {
		//given
		LedgerWriteRequest request = mock(LedgerWriteRequest.class);

		when(securityUtil.getMemberId()).thenReturn("member");
		doThrow(
				BusinessException.of(
						LEDGER_INPUT_MISSING,
						"검증 실패"
				)
		).when(ledgerValidator).register(request);

		//when
		assertThrows(BusinessException.class, () -> service.registerLedger(request));

		//then
		verify(securityUtil).getMemberId();
		verify(ledgerValidator).register(request);
		verify(ledgerRepository, never()).save(any());
		verify(ledgerValidator, never()).validateImage(any());
		verify(imageRepository, never()).saveAll(anyList());
	}


	@Test
	@DisplayName("DB 저장 중 중복키가 발생하면 그 후 로직을 실행하지 않는다.")
	void registerLedger_failure_duplicateKey() {
		//given
		when(securityUtil.getMemberId()).thenReturn(memberId);
		when(ledgerRepository.save(any()))
				.thenThrow(new DuplicateKeyException("중복키 발생"));

		//when
		assertThrows(BusinessException.class, () -> service.registerLedger(request));

		//then
		verify(securityUtil).getMemberId();
		verify(ledgerValidator).register(request);
		verify(ledgerRepository).save(any());

		verify(ledgerRepository, never()).findById(anyLong());
	}


	@Test
	@DisplayName("DB 저장 중 무결성 위반이 발생하면 그 후 로직을 실행하지 않는다.")
	void registerLedger_failure_integrity() {
		//given
		when(securityUtil.getMemberId()).thenReturn(memberId);
		when(ledgerRepository.save(any()))
				.thenThrow(new DataIntegrityViolationException("무결성 위반"));

		//when
		assertThrows(BusinessException.class, () -> service.registerLedger(request));

		//then
		verify(securityUtil).getMemberId();
		verify(ledgerValidator).register(request);
		verify(ledgerRepository).save(any());

		verify(ledgerRepository, never()).findById(anyLong());
		verify(imageRepository, never()).saveAll(anyList());
	}


	@Test
	@DisplayName("DB 저장 후 조회가 실패하면 그 후 로직을 실행하지 않는다.")
	void registerLedger_failure_notFound() {
		//given
		when(securityUtil.getMemberId()).thenReturn(memberId);
		when(ledgerRepository.save(any())).thenReturn(1L);
		when(ledgerRepository.findById(1L)).thenReturn(null);

		//when
		assertThrows(BusinessException.class, () -> service.registerLedger(request));

		//then
		verify(securityUtil).getMemberId();
		verify(ledgerValidator).register(request);
		verify(ledgerRepository).save(any());
		verify(ledgerRepository).findById(anyLong());

		verify(ledgerValidator, never()).validateImage(any());
		verify(fileService, never()).storeFile(any(), any(), any());
	}


	@Test
	@DisplayName("이미지 검증에 실패하면 그 후 로직을 실행하지 않는다.")
	void registerLedger_failure_imageValidate() {
		//given
		when(securityUtil.getMemberId()).thenReturn(memberId);
		when(ledgerRepository.save(any())).thenReturn(1L);
		when(ledgerRepository.findById(1L)).thenReturn(savedLedger);

		doThrow(BusinessException.of(
				FILE_INPUT_EMPTY,
				"이미지 검증 실패"
		)).when(ledgerValidator).validateImage(request.getImage().get(0));

		//when
		assertThrows(BusinessException.class, () -> service.registerLedger(request));

		//then
		verify(securityUtil).getMemberId();
		verify(ledgerValidator).register(request);
		verify(ledgerRepository).save(any());
		verify(ledgerRepository).findById(anyLong());
		verify(ledgerValidator).validateImage(any());

		verify(fileService, never()).storeFile(any(), any(), any());
		verify(imageRepository, never()).saveAll(any());
	}


	@Test
	@DisplayName("이미지 저장에 실패하면 그 후 로직을 실행하지 않는다")
	void registerLedger_failure_storeFile() {
		//given
		when(securityUtil.getMemberId()).thenReturn(memberId);
		when(ledgerRepository.save(any())).thenReturn(1L);
		when(ledgerRepository.findById(1L)).thenReturn(savedLedger);

		doThrow(BusinessException.of(
				FILE_ETC_UNKNOWN,
				"이미지 저장 실패"
		)).when(fileService).storeFile(any(), any(), any());

		//when
		assertThrows(BusinessException.class, () -> service.registerLedger(request));

		//then
		verify(securityUtil).getMemberId();
		verify(ledgerValidator).register(request);
		verify(ledgerRepository).save(any());
		verify(ledgerRepository).findById(anyLong());
		verify(ledgerValidator).validateImage(any());
		verify(fileService).storeFile(any(), any(), any());
		verify(fileService).deleteFiles(anyList());

		verify(imageRepository, never()).saveAll(any());
	}


	@Test
	@DisplayName("이미지 정보를 DB에 저장에 실패하면 그 후 로직을 실행하지 않는다.")
	void registerLedger_failure_imageInfoNotSaved() {
		//given
		when(securityUtil.getMemberId()).thenReturn(memberId);
		when(ledgerRepository.save(any())).thenReturn(1L);
		when(ledgerRepository.findById(1L)).thenReturn(savedLedger);
		when(fileService.storeFile(any(), any(), any())).thenReturn(new StoredFile("full/path/rel/image", "/rel/image"));

		doThrow(new DataAccessException("저장 실패") {})
				.when(imageRepository).saveAll(anyList());

		//when
		assertThrows(BusinessException.class, () -> service.registerLedger(request));

		//then
		verify(securityUtil).getMemberId();
		verify(ledgerValidator).register(request);
		verify(ledgerRepository).save(any());
		verify(ledgerRepository).findById(anyLong());
		verify(ledgerValidator).validateImage(any());
		verify(fileService).storeFile(any(), any(), any());
		verify(fileService).deleteFiles(anyList());
		verify(imageRepository).saveAll(any());
	}
}