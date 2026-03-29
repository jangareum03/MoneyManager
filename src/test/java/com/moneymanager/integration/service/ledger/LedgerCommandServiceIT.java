package com.moneymanager.integration.service.ledger;

import com.moneymanager.domain.global.dto.StoredFile;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.domain.ledger.enums.AmountType;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.file.FileCommandService;
import com.moneymanager.service.ledger.LedgerCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

import static com.moneymanager.exception.error.ErrorCode.FILE_ETC_UNKNOWN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.integration.service.ledger<br>
 * 파일이름       : LedgerCommandServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 30<br>
 * 설명              : LedgerCommandService 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 1. 30.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@SpringBootTest
@ActiveProfiles("test")
public class LedgerCommandServiceIT {

	@Autowired	private LedgerCommandService service;

	@Autowired	private LedgerRepository ledgerRepository;
	@Autowired	private LedgerImageRepository ledgerImageRepository;

	@MockBean	private SecurityUtil securityUtil;
	@MockBean	private FileCommandService fileCommandService;

	@BeforeEach
	void setUp() {
		clearData();
	}

	private void clearData() {
		ledgerRepository.deleteAll();
		ledgerImageRepository.deleteAll();
	}

	//==================[ TEST ]==================
	@Test
	@DisplayName("정상 요청이면 DB에 가계부 정보가 저장된다.")
	void registerLedger_Success(){
		//given
		LedgerWriteRequest request = LedgerWriteRequest.builder()
				.date("20260101")
				.categoryCode("010101")
				.amount(10000L)
				.amountType("none")
				.build();

		when(securityUtil.getMemberId()).thenReturn("test");

		//when
		service.registerLedger(request);

		//then
		List<Ledger> ledgers = ledgerRepository.findAll();
		Ledger ledger = ledgers.get(0);

		assertThat("test").isEqualTo(ledger.getMemberId());
		assertThat("20260101").isEqualTo(ledger.getDate());
		assertThat("010101").isEqualTo(ledger.getCategory());
		assertThat(10000L).isEqualTo(ledger.getAmount());
		assertThat(AmountType.NONE).isSameAs(ledger.getAmountType());
	}

	//이미지 포함 요청 시 Ledger + 이미지 저장
	@Test
	@DisplayName("이미지를 포함한 정상 요청이면 DB에 가계부 정보가 저장된다.")
	void registerLedger_Success_withImageFile() throws IOException {
		//given
		ClassPathResource jpgResource = new ClassPathResource("files/test.jpg");
		ClassPathResource pngResource = new ClassPathResource("files/test.png");

		MockMultipartFile file1 = new MockMultipartFile(
				"images",
				"test.jpg",
				"image/jpeg",
				jpgResource.getInputStream()
		);

		MockMultipartFile file2 = new MockMultipartFile(
				"images",
				"test.png",
				"image/png",
				pngResource.getInputStream()
		);

		LedgerWriteRequest request = LedgerWriteRequest.builder()
				.date("20260101")
				.categoryCode("010101")
				.amount(10000L)
				.amountType("none")
				.image(List.of(file1, file2))
				.build();

		when(securityUtil.getMemberId()).thenReturn("test");
		when(fileCommandService.storeFile(any(), any(), any()))
				.thenReturn(new StoredFile("D:\\project\\moneymanager\\src\\test\\resources", "\\2026\\03"));

		//when
		service.registerLedger(request);

		//then
		List<Ledger> ledgers = ledgerRepository.findAll();
		List<LedgerImage> ledgerImages = ledgerImageRepository.findAll();

		assertThat(1).isEqualTo(ledgers.size());
		assertThat(2).isEqualTo(ledgerImages.size());
	}

	@Test
	@DisplayName("인증 사용자 memberId가 Ledger객체 내부 필드에 저장된다.")
	void registerLedger_Success_MemberIdExits() {
		//given
		LedgerWriteRequest request = LedgerWriteRequest.builder()
				.date("20260101")
				.categoryCode("010101")
				.amount(10000L)
				.amountType("none")
				.build();

		when(securityUtil.getMemberId()).thenReturn("test");

		//when
		service.registerLedger(request);

		//then
		Ledger saved = ledgerRepository.findAll().get(0);
		assertThat("test").isEqualTo(saved.getMemberId());
	}

	@Test
	@DisplayName("잘못된 요청으로 예외가 발생하면 가계부 정보가 저장되지 않는다.")
	void registerLedger_Failure_Exception() {
		//given
		LedgerWriteRequest request = LedgerWriteRequest.builder()
				.date("20260101")
				.categoryCode("010101")
				.amount(-1L)
				.amountType("none")
				.build();

		when(securityUtil.getMemberId()).thenReturn("test");

		//when
		assertThrows(
				BusinessException.class,
				() -> service.registerLedger(request)
		);

		//then
		assertThat(ledgerRepository.count()).isZero();
		assertThat(ledgerImageRepository.count()).isZero();
	}

	@Test
	@DisplayName("이미지 처리 실패하면 전체 롤백된다.")
	void registerLedger_Failure_Rollback() throws IOException {
		//given
		ClassPathResource jpgResource = new ClassPathResource("files/test.jpg");
		ClassPathResource pngResource = new ClassPathResource("files/test.png");

		MockMultipartFile file1 = new MockMultipartFile(
				"images",
				"test.jpg",
				"image/jpeg",
				jpgResource.getInputStream()
		);

		MockMultipartFile file2 = new MockMultipartFile(
				"images",
				"test.png",
				"image/png",
				pngResource.getInputStream()
		);

		LedgerWriteRequest request = LedgerWriteRequest.builder()
				.date("20260101")
				.categoryCode("010101")
				.amount(10000L)
				.amountType("none")
				.image(List.of(file1, file2))
				.build();

		when(securityUtil.getMemberId()).thenReturn("test");
		doThrow(
				BusinessException.of(FILE_ETC_UNKNOWN, "이미지 저장 실패")
		).when(fileCommandService).storeFile(any(), any(), any());

		//when
		assertThrows(BusinessException.class, () -> service.registerLedger(request));

		//then
		assertThat(ledgerRepository.count()).isZero();
		assertThat(ledgerImageRepository.count()).isZero();
	}

}
