package com.moneymanager.integration.service.ledger;

import com.moneymanager.BusinessExceptionAssert;
import com.moneymanager.domain.global.dto.StoredFile;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.member.enums.MemberType;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.repository.member.MemberRepository;
import com.moneymanager.security.support.WithMockCustomUser;
import com.moneymanager.service.file.FileCommandService;
import com.moneymanager.service.file.FileNamingStrategy;
import com.moneymanager.service.ledger.LedgerCommandService;
import com.moneymanager.unit.fixture.LedgerRequestFixture;
import com.moneymanager.unit.fixture.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static com.moneymanager.exception.error.ErrorCode.FILE_ETC_RESOURCE_ERROR;
import static com.moneymanager.exception.error.ErrorCode.LEDGER_ETC_DB_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.ledger<br>
 * 파일이름       : LedgerCommandServiceRollbackIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 18<br>
 * 설명              : LedgerCommandService 클래스 롤백을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 4. 18</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@SpringBootTest
@ActiveProfiles("test")
public class LedgerCommandServiceRollbackIT {

	@Autowired
	private LedgerCommandService service;

	@Autowired
	private LedgerRepository ledgerRepository;

	@SpyBean
	private LedgerImageRepository ledgerImageRepository;

	@Autowired
	private MemberRepository memberRepository;

	@MockBean
	private FileCommandService fileCommandService;


	@TestConfiguration
	static class TestClockConfig {
		@Bean
		public Clock clock() {
			LocalDate fixedDate = LocalDate.of(2026, 3, 20);

			return Clock.fixed(
					fixedDate.atStartOfDay().atZone(ZoneId.of("Asia/Seoul")).toInstant(),
					ZoneId.of("Asia/Seoul")
			);
		}
	}


	@BeforeEach
	void setUp() {
		memberRepository.deleteAll();

		Member member = MemberFixture.defaultMember()
				.id("UCt01001")
				.type(MemberType.NORMAL)
				.name("김철수")
				.birthDate("20001010")
				.nickName("철수")
				.email("cheolsu@test.com")
				.memberInfo(MemberFixture.defaultMemberInfo())
				.build();

		memberRepository.save(member);
	}


	@WithMockCustomUser
	@Nested
	@DisplayName("가계부 등록")
	class RegisterTest {

		@Test
		@DisplayName("이미지 파일 저장 중 실패하면 트랜잭션이 롤백된다.")
		void rollback_whenStoringImageFileFails() throws IOException {
			//given
			LedgerWriteRequest request = LedgerRequestFixture.withImages().build();

			when(fileCommandService.storeFile(any(MultipartFile.class), any(Ledger.class), any(FileNamingStrategy.class)))
					.thenThrow(BusinessException.of(FILE_ETC_RESOURCE_ERROR, "파일 저장 실패"));

			//when & then
			assertThatThrownBy(() -> service.registerLedger(request))
					.isInstanceOf(BusinessException.class)
					.satisfies(e -> {
						BusinessExceptionAssert.assertThatBusinessException(e)
								.hasErrorCode(FILE_ETC_RESOURCE_ERROR)
								.hasLogMessage("파일 저장");
					});

			//롤백 검증
			assertThat(ledgerRepository.count()).isZero();
		}

		@Test
		@DisplayName("이미지 정보 저장 중 예외가 발생하면 트랜잭션이 롤백된다.")
		void rollback_whenSavingLedgerImageFails() throws IOException {
			//given
			LedgerWriteRequest request = LedgerRequestFixture.withImages().build();

			when(fileCommandService.storeFile(any(), any(), any()))
					.thenReturn(new StoredFile("full", "full/image.jpg"));

			doThrow(BusinessException.of(LEDGER_ETC_DB_ERROR, "이미지 저장 실패"))
					.when(ledgerImageRepository).saveAll(anyList());

			//when & then
			assertThatThrownBy(() -> service.registerLedger(request))
					.isInstanceOf(BusinessException.class)
					.satisfies(e -> {
						BusinessExceptionAssert.assertThatBusinessException(e)
								.hasErrorCode(LEDGER_ETC_DB_ERROR)
								.hasLogMessage("이미지 저장");
					});

			assertThat(ledgerRepository.count()).isZero();
			assertThat(ledgerImageRepository.count()).isZero();
		}

		@Test
		@DisplayName("RuntimeException 발생 시에도 트랜잭션은 롤백된다")
		void rollback_whenUnexpectedRuntimeExceptionOccurs() throws IOException {
			//given
			LedgerWriteRequest request = LedgerRequestFixture.withImages().build();

			when(fileCommandService.storeFile(any(MultipartFile.class), any(Ledger.class), any(FileNamingStrategy.class)))
					.thenThrow(new RuntimeException("예상치 못한 오류"));

			//when & then
			assertThatThrownBy(() -> service.registerLedger(request))
					.isInstanceOf(RuntimeException.class)
					.hasMessage("예상치 못한 오류");

			assertThat(ledgerRepository.count()).isZero();
			assertThat(ledgerImageRepository.count()).isZero();
		}

	}

}
