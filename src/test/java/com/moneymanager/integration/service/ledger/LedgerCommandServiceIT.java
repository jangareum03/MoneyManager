package com.moneymanager.integration.service.ledger;


import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.member.enums.MemberType;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.repository.member.MemberRepository;
import com.moneymanager.security.support.WithMockCustomUser;
import com.moneymanager.service.file.FileCommandService;
import com.moneymanager.service.ledger.LedgerCommandService;
import com.moneymanager.unit.fixture.LedgerRequestFixture;
import com.moneymanager.unit.fixture.MemberFixture;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


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
@Transactional
@ActiveProfiles("test")
@Import(LedgerCommandServiceIT.TestClockConfig.class)
public class LedgerCommandServiceIT {

	@Autowired
	private LedgerCommandService service;

	@Autowired
	private LedgerRepository ledgerRepository;

	@Autowired
	private LedgerImageRepository ledgerImageRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private FileCommandService	fileCommandService;


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


	@Nested
	@DisplayName("가계부 등록")
	class RegisterTest {

		@WithMockCustomUser
		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("이미지가 없는 요청이면 가계부만 저장된다.")
			void savesLedger_whenRequestHasNoImage() {
				//given
				LedgerWriteRequest request = LedgerRequestFixture.defaultLedgerWriteRequest().build();

				//when
				service.registerLedger(request);

				//then
				List<Ledger> ledgers = ledgerRepository.findAll();
				assertThat(ledgers).hasSize(1);

				Ledger ledger = ledgers.get(0);
				assertThat(ledger.getMemberId()).isEqualTo("UCt01001");
				assertThat(ledger.getDate()).isEqualTo(LocalDate.of(2026,1,1));
				assertThat(ledger.getCategory()).isEqualTo("010101");
				assertThat(ledger.getAmount()).isEqualTo(10000L);
			}

			@Test
			@DisplayName("이미지가 1개 있는 요청이면 가계부와 이미지 정보가 저장된다.")
			void savesLedgerAndImage_whenRequestHasImage() throws IOException {
				//given
				LedgerWriteRequest request = LedgerRequestFixture.withImage().build();

				//when
				service.registerLedger(request);

				//then
				List<Ledger> ledgers = ledgerRepository.findAll();
				assertThat(ledgers).hasSize(1);
				Ledger ledger = ledgers.get(0);

				List<LedgerImage> images = ledgerImageRepository.findAll();
				assertThat(images).hasSize(1);

				LedgerImage image = images.get(0);
				assertThat(image.getLedgerId()).isEqualTo(ledger.getId());
				assertThat(image.getImagePath()).startsWith("/2026/03").endsWith(".jpg");
				assertThat(image.getImagePath()).doesNotContain("test");
				assertThat(image.getSortOrder()).isEqualTo(1);
			}

			@Test
			@DisplayName("이미지가 여러개 있는 요청이면 가계부와 이미지 정보가 저장된다.")
			void savesLedgerAndImage_whenRequestHasImages() throws IOException {
				//given
				LedgerWriteRequest request = LedgerRequestFixture.withImages().build();

				//when
				service.registerLedger(request);

				//then
				//가계부 검증
				List<Ledger> ledgers = ledgerRepository.findAll();
				assertThat(ledgers).hasSize(1);
				Ledger ledger = ledgers.get(0);

				//가계부 이미지 검증
				List<LedgerImage> images = ledgerImageRepository.findAll();
				assertThat(images).hasSize(2);
				assertThat(images).allSatisfy(image -> {
					assertThat(image.getLedgerId()).isEqualTo(ledger.getId());
					assertThat(image.getImagePath()).startsWith("/2026/03");
					assertThat(image.getImagePath()).doesNotContain("test");
				});

				//이미지 순서 검증
				LedgerImage firstImage = images.get(0);
				assertThat(firstImage.getImagePath()).endsWith(".jpg");
				assertThat(firstImage.getSortOrder()).isEqualTo(1);

				LedgerImage secondImage = images.get(1);
				assertThat(secondImage.getImagePath()).endsWith(".png");
				assertThat(secondImage.getSortOrder()).isEqualTo(2);
			}

		}


		@WithMockCustomUser
		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@WithMockCustomUser(memberId = "noMember")
			@Test
			@DisplayName("존재하지 않는 회원이면 예외가 발생한다.")
			void throwsException_whenMemberDoesNotExist() {
				//given
				LedgerWriteRequest request = LedgerRequestFixture.defaultLedgerWriteRequest().build();

				//when & then
				assertThatThrownBy(() -> service.registerLedger(request))
						.isInstanceOf(BusinessException.class);

				assertThat(ledgerRepository.findAll()).isEmpty();
				assertThat(ledgerImageRepository.findAll()).isEmpty();
			}

			@Test
			@DisplayName("잘못된 요청이면 예외가 발생한다..")
			void throwsException_whenRequestIsInvalid() {
				//given
				LedgerWriteRequest request = LedgerRequestFixture.defaultLedgerWriteRequest()
						.date("202601011")
						.build();

				//when & then
				assertThatThrownBy(() -> service.registerLedger(request))
						.isInstanceOf(BusinessException.class);

				//then
				assertThat(ledgerRepository.findAll()).isEmpty();
				assertThat(ledgerImageRepository.findAll()).isEmpty();
			}

		}

	}

}
