package com.moneymanager.ledger.service;


import com.moneymanager.domain.ledger.dto.request.LedgerUpdateRequest;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.ledger.vo.Money;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.domain.member.Member;
import com.moneymanager.exception.exception.BusinessException;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.ledger.LedgerCommandService;
import com.moneymanager.support.IntegrationTestSupport;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import com.moneymanager.support.fixture.entity.MemberFixture;
import com.moneymanager.support.fixture.request.LedgerUpdateRequestFixture;
import com.moneymanager.support.fixture.request.LedgerWriteRequestFixture;
import com.moneymanager.support.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
public class LedgerCommandServiceIT extends IntegrationTestSupport {

	@Autowired
	private LedgerCommandService target;

	@MockBean
	private SecurityUtil securityUtil;

	@Autowired
	private LedgerImageRepository imageRepository;

	@Value("${file.image.ledger}")
	String ledgerPath;

	@BeforeEach
	void setUp() {
		Member member = memberRepository.save(
				MemberFixture.builder(MemberTestData.MEMBER_ID).build()
		);

		when(securityUtil.getMemberId()).thenReturn(member.getId());
	}

	@Nested
	@DisplayName("가계부 등록")
	@WithMockCustomUser
	class RegisterTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@BeforeEach
			void setUp() {
				ledgerRepository.deleteAll();
				imageRepository.deleteAll();;
			}
			
			@Test
			@DisplayName("등록 요청으로 가계부 정보가 DB에 저장된다.")
			void savesLedger_whenRequestIsValid() {
				//given: 이미지가 포함되지 않은 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.create();

				//when: 가계부 등록을 요청한다.
				target.register(request);

				//then: 가계부가 저장된다.
				List<Ledger> ledgers = ledgerRepository.findAll();

				assertThat(ledgers).hasSize(1);

				Ledger ledger = ledgers.get(0);

				assertThat(ledger.getId()).isNotNull();
				assertThat(ledger.getCode()).isNotNull();
				assertThat(ledger.getCreatedAt()).isNotNull();
				assertThat(ledger.getDate()).isEqualTo(LedgerTestData.LOCAL_DATE);
				assertThat(ledger.getCategory()).isEqualTo(CategoryTestData.EARNED_CODE);
				assertThat(ledger.getFix().getValue()).isEqualTo(LedgerTestData.FIX_N);

				assertThat(ledger.getMoney())
						.extracting(
								Money::getAmount,
								Money::getPaymentType
						)
						.containsExactly(
								LedgerTestData.AMOUNT,
								PaymentType.NONE
						);

				assertThat(ledger.getFixCycle()).isNull();
				assertThat(ledger.getUpdatedAt()).isNull();

				//then: 이미지 정보는 저장되지 않는다.
				int imageCount = imageRepository.count();

				assertThat(imageCount).isZero();
			}
			
			@Test
			@DisplayName("등록 요청에 이미지가 있으면 이미지 정보도 DB에 저장된다.")
			void savesImagesToDatabase_whenImagesExist() {
				//given: 이미지가 포함된 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.withImages(2).build();

				//when: 가계부 등록을 요청한다.
				target.register(request);

				//then: 가계부가 저장된다.
				List<Ledger> ledgers = ledgerRepository.findAll();

				assertThat(ledgers).hasSize(1);

				//then: 이미지 정보가 저장된다.
				List<LedgerImage> images = imageRepository.findAll();

				assertThat(images).hasSize(2);
				assertThat(images)
						.extracting(LedgerImage::getImagePath)
								.allSatisfy(path ->
										assertThat(path).contains(MemberTestData.MEMBER_ID)
								);
				assertThat(images)
						.extracting(LedgerImage::getSortOrder)
						.containsExactly(1, 2);
			}

			@Test
			@DisplayName("저장된 이미지가 저장된 Ledger를 참조한다.")
			void validatesLedgerReference_whenImagesAreSaved() {
				//given: 이미지가 포함된 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.withImages(2).build();

				//when: 가계부 등록을 요청한다.
				target.register(request);

				//then: 이미지의 가계부 ID는 가계부 ID와 동일하게 설정된다.
				Ledger ledger = ledgerRepository.findAll().get(0);

				List<LedgerImage> images = imageRepository.findAll();

				assertThat(images)
						.extracting(
								LedgerImage::getLedgerId
						)
						.containsOnly(ledger.getId());
			}
			
		}

	}


	@Nested
	@DisplayName("가계부 수정")
	@WithMockCustomUser
	class UpdateTest {

		Ledger savedLedger;

		@BeforeEach
		void setUp() {
			Long ledgerId = ledgerRepository.insert(LedgerFixture.newLedger().build());

			savedLedger = ledgerRepository.findById(ledgerId);
		}

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("요청으로 수정된 가계부 정보가 DB에 저장된다.")
			void savesLedger_whenRequestIsValid() {
				//given: 수정할 가계부 요청이 준비되어 있다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture
						.withPlace()
						.paymentType("card")
						.build();

				String code = savedLedger.getCode();
				
				//when: 가계부 수정을 요청한다.
				target.update(code, request);
				
				//then: 수정된 값으로 가계부가 저장된다.
				assertThat(ledgerRepository.findAll()).hasSize(1);

				Ledger ledger = ledgerRepository.findById(savedLedger.getId());

				assertThat(savedLedger.getUpdatedAt()).isNull();
				assertThat(ledger.getUpdatedAt()).isNotNull();

				assertThat(ledger.getMoney())
						.extracting(
								Money::getAmount,
								Money::getPaymentType
						)
						.containsExactly(
							request.getAmount(),
							PaymentType.from(request.getPaymentType())
						);

				assertThat(ledger.getPlace())
						.extracting(
								Place::getPlaceName,
								Place::getRoadAddress,
								Place::getDetailAddress
						)
						.containsExactly(
								request.getPlaceName(),
								request.getRoadAddress(),
								request.getDetailAddress()
						);
			}
			
			@Test
			@DisplayName("수정 요청에 이미지가 있으면 이미지 정보도 DB에 저장된다.")
			void savesImage_whenImagesExist() {
				//given: 이미지가 포함된 수정할 가계부 요청이 준비되어 있다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.withImages(2).build();
				String code = savedLedger.getCode();

				//when: 가계부 수정을 요청한다.
				target.update(code, request);
				
				//then: 이미지 및 이미지 정보가 저장된다.
				List<LedgerImage> images = imageRepository.findByLedgerId(savedLedger.getId());

				assertThat(images).hasSize(2);

				assertThat(images)
						.extracting(LedgerImage::getLedgerId)
						.containsOnly(savedLedger.getId());

				for(LedgerImage image : images) {
					Path path = Paths.get(ledgerPath, image.getImagePath());

					assertThat(Files.exists(path)).isTrue();
				}

			}

		}


		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("존재하지 않은 가계부를 수정하면 실패한다.")
			void rejectsRequest_whenLedgerDoesNotExist() {
				//given: 존재하지 않은 가계부 코드가 주어진다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
						.memo("메모")
						.build();
				String code = "error";

				//when: 가계부 수정을 요청하면 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.update(code, request))
						.isInstanceOf(BusinessException.class);

				//then: 가계부가 수정되지 않는다.
				Ledger ledger = ledgerRepository.findById(savedLedger.getId());

				assertThat(ledger.getMemo()).isEqualTo(savedLedger.getMemo());
			}

			@Test
			@DisplayName("다른 회원의 가계부를 수정하면 실패한다.")
			void rejectsRequest_whenMemberIsNotOwner() {
				//given: 다른 회원의 가계부가 존재한다.
				Member otherMember = memberRepository.save(MemberFixture.builder().build());

				Long ledgerId = ledgerRepository.insert(
						LedgerFixture.newLedger().memberId(otherMember.getId()).code("other").build()
				);

				Ledger ledger = ledgerRepository.findById(ledgerId);

				LedgerUpdateRequest request = LedgerUpdateRequestFixture.create();

				//when & then: 가계부 수정을 요청하면 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.update(ledger.getCode(), request))
						.isInstanceOf(BusinessException.class);
			}

		}

	}


	@Nested
	@DisplayName("가계부 저장")
	class SaveTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@Test
			@DisplayName("신규 가계부는 DB에 저장된다.")
			void savesLedger_whenRequestIsValid() {
				//given: 신규 가계부가 준비되어 있다.
				Ledger ledger = LedgerFixture.newLedger().build();
				
				//when: 가계부 저장을 요청한다.
				Ledger result = target.save(ledger);
				
				//then: 신규 가계부가 저장된다.
				Long count = ledgerRepository.count();
				assertThat(count).isEqualTo(1);

				assertThat(result.getId()).isNotNull();
				assertThat(result.getCategory()).isEqualTo(CategoryTestData.EARNED_CODE);
			}
			
			@Test
			@DisplayName("기존 가계부는 수정 후 DB에 반영된다.")
			void updatesLedger_whenLedgerExists() {
				//given: 가계부가 저장되어 있다.
				Long ledgerId = ledgerRepository.insert(LedgerFixture.newLedger().build());

				Ledger savedLedger = ledgerRepository.findById(ledgerId);
				savedLedger.changeMemo("수정 완료");

				//when: 가계부를 저장한다.
				Ledger result = target.save(savedLedger);
				
				//then: 기존 가계부 값이 수정된다.
				assertThat(result.getMemo()).isEqualTo("수정 완료");
			}

		}


		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("존재하지 않은 가계부를 수정하면 실패한다.")
			void rejectsRequest_whenLedgerDoesNotExist() {
				//given: 가계부가 저장되어 있다.
				Ledger errorLedger = LedgerFixture.savedLedger(99999999L).build();
				
				//when & then: 가계부 저장 요청하면 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.save(errorLedger))
						.isInstanceOf(BusinessException.class);
			}

		}

	}

}
