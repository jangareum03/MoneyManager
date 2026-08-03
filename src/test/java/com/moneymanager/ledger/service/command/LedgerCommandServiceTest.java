package com.moneymanager.ledger.service.command;

import com.moneymanager.ledger.service.read.LedgerReadService;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.FixedYN;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.global.exception.code.LedgerErrorCode;
import com.moneymanager.global.exception.exception.BusinessException;
import com.moneymanager.global.exception.exception.ExternalException;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.log.DeveloperLogInfo;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import com.moneymanager.support.fixture.request.LedgerUpdateRequestFixture;
import com.moneymanager.support.fixture.request.LedgerWriteRequestFixture;
import com.moneymanager.support.fixture.vo.MoneyFixture;
import com.moneymanager.support.fixture.vo.PlaceFixture;
import com.moneymanager.ledger.repository.LedgerRepository;
import com.moneymanager.global.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import com.moneymanager.support.ApplicationExceptionAssert;

import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
@Slf4j
@ExtendWith(MockitoExtension.class)
public class LedgerCommandServiceTest {

	@Spy
	@InjectMocks
	private LedgerCommandService target;

	@Mock
	private SecurityUtil securityUtil;

	@Mock
	private LedgerReadService ledgerReadService;

	@Mock
	private LedgerImageCommandService imageCommandService;

	@Mock
	private LedgerRepository ledgerRepository;


	@Nested
	@DisplayName("가계부 등록")
	class RegisterTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@BeforeEach
			void setUp() {
				when(securityUtil.getMemberId()).thenReturn(MemberTestData.MEMBER_ID);
			}
			
			@Test
			@DisplayName("등록 요청 정보로 가계부를 저장한다.")
			void savesLedger_whenRequestIsValid() {
				//given: 장소 정보가 포함된 등록 요청과 저장된 가계부가 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.withPlace().build();

				when(ledgerRepository.insert(any(Ledger.class))).thenReturn(1L);
				when(ledgerRepository.findById(1L)).thenReturn(LedgerFixture.savedLedger(1L).build());
				
				//when: 가계부 등록을 요쳥한다.
				target.register(request);
				
				//then: 요청된 가계부가 DB에 저장된다.
				ArgumentCaptor<Ledger> argument = ArgumentCaptor.forClass(Ledger.class);
				verify(ledgerRepository).insert(argument.capture());

				Ledger savedLedger = argument.getValue();
				assertThat(savedLedger.getMemberId()).isEqualTo(MemberTestData.MEMBER_ID);
				assertThat(savedLedger.getDate().format(DateTimeFormatter.BASIC_ISO_DATE)).isEqualTo(request.getDate());
				assertThat(savedLedger.getCategory()).isEqualTo(request.getCategoryCode());
				assertThat(savedLedger.getMoney()).isEqualTo(Money.of(request.getAmount(), PaymentType.from(request.getPaymentType())));
				assertThat(savedLedger.getFix()).isEqualTo(FixedYN.from(request.getFixed()));
				assertThat(savedLedger.getPlace()).isEqualTo(Place.of(request.getPlaceName(), request.getRoadAddress(), request.getDetailAddress()));

				//then: 회원 인증 및 이미지 처리 동작이 요청된다.
				verify(securityUtil).getMemberId();
				verify(imageCommandService).processImages(any(Ledger.class), eq(request));
			}
			
			@Test
			@DisplayName("이미지 정보도 포함된 요청 정보로 가계부를 저장한다.")
			void savesLedger_whenImagesAreGiven() {
				//given: 이미지가 포함된 등록 요청과 저장된 가계부가 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.withImages(2).build();

				when(ledgerRepository.insert(any(Ledger.class))).thenReturn(1L);
				when(ledgerRepository.findById(1L)).thenReturn(LedgerFixture.savedLedger(1L).build());

				//when: 가계부 등록을 요쳥한다.
				target.register(request);
				
				//then: 이미지 처리 메서드에 가계부와 요청 정보가 전달된다.
				ArgumentCaptor<Ledger> ledgerCaptor = ArgumentCaptor.forClass(Ledger.class);

				verify(imageCommandService).processImages(ledgerCaptor.capture(), eq(request));

				assertThat(ledgerCaptor.getValue().getId()).isEqualTo(1L);
			}

		}


		@Nested
		@DisplayName("실패 케이스")
		class Failure {

			@Test
			@DisplayName("회원 인증이 실패하면 가계부 등록에 실패한다.")
			void rejectsRequest_whenAuthenticationFails() {
				//given: 정상적인 가계부 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.create();

				//given: 회원 인증 중 예외가 발생하도록 동작이 정의되어 있다.
				when(securityUtil.getMemberId())
						.thenThrow(BusinessException.class);

				//when & then: 가계부 등록을 요청하면 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.register(request))
						.isInstanceOf(BusinessException.class);

				//then: 가계부 저장 및 이미지 처리가 요청되지 않는다.
				verify(ledgerRepository, never()).insert(any());
				verify(imageCommandService, never()).processImages(any(), any());
			}

			@Test
			@DisplayName("잘못된 요청이면 가계부 등록에 실패한다.")
			void rejectsRequest_whenRequestIsInvalid() {
				//given: 존재하지 않은 카테고리 코드가 포함된 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.builder()
						.categoryCode("error")
						.build();

				//when & then: 가계부 등록을 요청하면 ValidationException이 발생한다.
				assertThatThrownBy(() -> target.register(request))
						.isInstanceOf(ValidationException.class);
				
				//then: 가계부 저장 및 이미지 처리가 요청되지 않는다.
				verify(ledgerRepository, never()).insert(any());
				verify(imageCommandService, never()).processImages(any(), any());
			}
			
			@Test
			@DisplayName("저장 중 문제가 발생하면 등록에 실패한다.")
			void throwsException_whenSaveFails() {
				//given: 정상적인 가계부 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.create();

				//given: 가계부 저장 중 예외가 발생하도록 Repository 동작이 정의되어 있다.
				when(ledgerRepository.insert(any(Ledger.class)))
						.thenThrow(new DataAccessResourceFailureException("DB error"));

				//when & then: 가계부 등록을 요청하면 DataAccessException이 발생한다.
				assertThatThrownBy(() -> target.register(request))
						.isInstanceOf(DataAccessException.class);
				
				//then: 이미지 처리가 요청되지 않는다.
				verify(imageCommandService, never()).processImages(any(), any());
			}
			
			@Test
			@DisplayName("이미지 처리 중 문제가 발생하면 등록에 실패한다.")
			void throwsException_whenImageProcessingFails() {
				//given: 이미지가 포함된 등록 요청이 준비되어 있다.
				LedgerWriteRequest request = LedgerWriteRequestFixture.withImages(1).build();

				//given: 저장된 가계부가 반환되도록 Repository 동작이 정의되어 있다.
				when(ledgerRepository.insert(any(Ledger.class))).thenReturn(1L);
				when(ledgerRepository.findById(1L)).thenReturn(LedgerFixture.savedLedger(1L).build());

				//given: 서버에 이미지를 저장 중 예외가 발생하도록 Service의 동작이 정의되어 있다.
				doThrow(BusinessException.class)
						.when(imageCommandService).processImages(any(), any());

				//when & then: 가계부 등록을 요청하면 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.register(request))
						.isInstanceOf(BusinessException.class);
			}
			
		}

	}


	@Nested
	@DisplayName("가계부 수정")
	class UpdateTest {

		@BeforeEach
		void setUp() {
			when(securityUtil.getMemberId())
					.thenReturn(MemberTestData.MEMBER_ID);
		}
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@Test
			@DisplayName("수정 요청으로 가계부를 수정한다.")
			void updatesLedger_whenRequestIsValid() {
				//given: 기존 가계부가 저장되어 있다.
				Ledger ledger = LedgerFixture.savedLedger(1L).build();
				String code = LedgerTestData.CODE;

				when(ledgerReadService.getLedger(MemberTestData.MEMBER_ID, code))
						.thenReturn(ledger);
				when(ledgerRepository.update(ledger))
						.thenReturn(1);

				LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
						.paymentType("card")
						.build();
				
				//when: 가계부 수정을 요청한다.
				target.update(code, request);
				
				//then: 수정된 요청값으로 저장된다.
				assertThat(ledger.getMoney())
						.extracting(
								Money::getAmount,
								Money::getPaymentType
						)
						.containsExactly(
								request.getAmount(),
								PaymentType.from(request.getPaymentType())
						);

				//then: 가계부 조회 및 수정이 요청된다.
				verify(ledgerReadService).getLedger(eq(MemberTestData.MEMBER_ID), eq(code));
				verify(ledgerRepository).update(eq(ledger));
				verify(ledgerReadService).getDetailData(eq(code));
			}
			
			@Test
			@DisplayName("기존 값과 동일한 값으로 가계부를 수정한다.")
			void updatesLedger_whenValuesAreIdentical() {
				//given: 저장된 기존 가계부와 동일한 수정 요청이 준비되어 있다.
				Ledger ledger = LedgerFixture.savedLedger(1L).build();
				String code = LedgerTestData.CODE;

				when(ledgerReadService.getLedger(MemberTestData.MEMBER_ID, code))
						.thenReturn(ledger);
				when(ledgerRepository.update(ledger))
						.thenReturn(1);

				LedgerUpdateRequest request = LedgerUpdateRequestFixture.from(ledger).build();

				//when: 가계부 수정을 요청한다.
				target.update(code, request);

				//then: 수정된 요청값으로 저장된다.
				assertThat(ledger)
						.extracting(
							Ledger::getCategory,
							Ledger::getMemo,
							Ledger::getFix,
							Ledger::getFixCycle,
							Ledger::getMoney,
							Ledger::getPlace
						)
						.containsExactly(
								request.getCategoryCode(),
								request.getMemo(),
								FixedYN.from(request.getFixed()),
								request.getFixCycle() == null ? null : FixCycle.valueOf(request.getFixCycle()),
								MoneyFixture.from(request),
								PlaceFixture.from(request)
						);

				//then: 가계부 수정이 요청된다.
				verify(ledgerRepository).update(eq(ledger));
			}

			@Test
			@DisplayName("이미지가 포함된 수정 정보로 가계부를 수정한다.")
			void updatesLedger_whenImagesExist() {
				//given: 이미지가 포함되지 않은 가계부가 저장되어 있다.
				Ledger ledger = LedgerFixture.savedLedger(1L).build();
				String code = LedgerTestData.CODE;

				when(ledgerReadService.getLedger(MemberTestData.MEMBER_ID, code))
						.thenReturn(ledger);
				when(ledgerRepository.update(ledger))
						.thenReturn(1);

				LedgerUpdateRequest request = LedgerUpdateRequestFixture
						.withImages(2)
						.build();

				//when: 가계부 수정을 요청한다.
				target.update(code, request);
				
				//then: 가계부 수정 및 이미지 처리가 요청된다.
				verify(ledgerRepository).update(eq(ledger));
				verify(imageCommandService).processImages(eq(ledger), eq(request));
			}

		}
		
		
		@Nested
		@DisplayName("실패 케이스")
		class Failure {
			
			@Test
			@DisplayName("회원 인증에 실패하면 가계부 수정에 실패한다.")
			void rejectsRequest_whenAuthenticationFails() {
				//given: 회원 인증이 실패하도록 동작이 정의되어 있다.
				when(securityUtil.getMemberId())
						.thenThrow(BusinessException.class);

				String code = LedgerTestData.CODE;
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.create();
				
				//when & then: 가계부 수정을 요청하면 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.update(code, request))
						.isInstanceOf(BusinessException.class);

				//then: 가계부 수정 및 이미지 처리가 요청되지 않는다.
				verify(ledgerRepository, never()).update(any());
				verify(imageCommandService, never()).processImages(any(), any());
			}
			
			@Test
			@DisplayName("가계부 조회를 실패하면 가계부 수정에 실패한다.")
			void rejectsRequest_whenLedgerLookupFails() {
				//given: 존재하지 않은 가계부 코드가 주어진다.
				String code = "error";
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.create();

				//given: 가계부 조회 중 예외가 발생하도록 service의 동작이 정의되어 있다.
				when(ledgerReadService.getLedger(any(), eq(code)))
						.thenThrow(BusinessException.class);

				//when & then: 가계부 수정을 요청하면 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.update(code, request))
						.isInstanceOf(BusinessException.class);

				//then: 가계부 저장 및 이미지 처리 요청되지 않는다.
				verify(ledgerRepository, never()).update(any(Ledger.class));
				verify(imageCommandService, never()).processImages(any(Ledger.class), eq(request));
			}
			
			@Test
			@DisplayName("잘못된 요청이면 가계부 수정에 실패한다.")
			void rejectsRequest_whenRequestIsInvalid() {
				//given: 잘못된 값이 포함된 수정 요청이 준비되어 있다.
				String code = LedgerTestData.CODE;
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
						.categoryCode("000000")
						.build();

				Ledger ledger = LedgerFixture.savedLedger(1L).build();

				when(ledgerReadService.getLedger(MemberTestData.MEMBER_ID, code))
						.thenReturn(ledger);
				
				//when & then: 가계부 수정을 요청하면 ValidationException이 발생한다.
				assertThatThrownBy(() -> target.update(code, request))
						.isInstanceOf(ValidationException.class);

				//then: 이미지 처리가 요청되지 않는다.
				verify(imageCommandService, never()).processImages(any(), any());
			}
			
			@Test
			@DisplayName("수정 중 문제가 발생하면 수정에 실패한다.")
			void throwsException_whenUpdateFails() {
				//given: 수정 중 문제가 발생하도록 동작이 정의되어 있다.
				String code = LedgerTestData.CODE;
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.create();
				Ledger ledger = LedgerFixture.savedLedger(1L).build();

				when(ledgerReadService.getLedger(MemberTestData.MEMBER_ID, code))
						.thenReturn(ledger);

				when(ledgerRepository.update(ledger))
						.thenReturn(0);

				//when & then: 가계부 수정을 요청하면 BusinessException이 발생한다.
				assertThatThrownBy(() -> target.update(code, request))
						.isInstanceOf(BusinessException.class);

				//then: 이미지 처리가 요청되지 않는다.
				verify(imageCommandService, never()).processImages(any(), any());
			}
			
			@Test
			@DisplayName("이미지 처리 중 문제가 발생하면 수정에 실패한다.")
			void throwsException_whenImageProcessingFails() {
				//given: 이미지 처리 중 문제가 발생하도록 동작이 정의되어 있다.
				String code = LedgerTestData.CODE;
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.create();
				Ledger ledger = LedgerFixture.savedLedger(1L).build();

				when(ledgerReadService.getLedger(MemberTestData.MEMBER_ID, code))
						.thenReturn(ledger);

				when(ledgerRepository.update(ledger))
						.thenReturn(1);

				doThrow(ExternalException.class)
						.when(imageCommandService).processImages(ledger, request);

				//when & then: 가계부 수정을 요청하면 ExternalException이 발생한다.
				assertThatThrownBy(() -> target.update(code, request))
						.isInstanceOf(ExternalException.class);
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
			@DisplayName("신규 가계부는 저장 후 반환한다.")
			void returnsCreatedLedger_whenRequestIsValid() {
				//given: 신규 가계부가 준비되어 있다.
				Ledger newLedger = LedgerFixture.newLedger().build();
				Ledger savedLedger = LedgerFixture.savedLedger(1L).build();

				when(ledgerRepository.insert(any())).thenReturn(1L);
				when(ledgerRepository.findById(1L)).thenReturn(savedLedger);

				//when: 가계부를 저장한다.
				Ledger result = target.save(newLedger);
				
				//then: 신규 가계부가 저장된다.
				assertThat(result).isEqualTo(savedLedger);

				verify(ledgerRepository).insert(newLedger);
				verify(ledgerRepository).findById(1L);
				verify(ledgerRepository, never()).update(any());
			}
			
			@Test
			@DisplayName("기존 가계부는 수정 후 반환한다.")
			void returnsUpdatedLedger_whenLedgerExists() {
				//given: 수정할 가계부가 저장되어 있다.
				Ledger ledger = LedgerFixture.savedLedger(1L).build();
				Ledger updatedLedger = LedgerFixture.savedLedger(1L)
						.id(ledger.getId())
						.memberId(ledger.getMemberId())
						.build();

				when(ledgerRepository.update(any())).thenReturn(1);
				when(ledgerRepository.findById(1L)).thenReturn(updatedLedger);

				//when: 가계부를 저장한다.
				Ledger result = target.save(ledger);
				
				//then: 기존 가개부가 저장된다.
				assertThat(result).isEqualTo(updatedLedger);

				verify(ledgerRepository).update(ledger);
				verify(ledgerRepository).findById(1L);
				verify(ledgerRepository, never()).insert(any());
			}
			
		}


		@Nested
		@DisplayName("실패 케이스")
		class Failure {
		
			@Test
			@DisplayName("가계부 수정에 실패하면 예외가 발생한다.")
			void throwsException_whenLedgerUpdateFails() {
				//given: 수정할 가계부가 준비되어 있다.
				Ledger ledger = LedgerFixture.savedLedger(1L).build();

				when(ledgerRepository.update(any()))
						.thenReturn(0);
				
				//when & then: 가계부 저장을 요청하면 BusinessException이 발생한다.
				ApplicationExceptionAssert.assertThatApplicationException(catchException(() -> target.save(ledger)))
						.hasErrorCode(LedgerErrorCode.NOT_FOUND_DATA)
						.hasWork("가계부 수정")
						.hasTarget(Ledger.class)
						.hasCauseMessage("데이터 없음")
						.hasValue(
								DeveloperLogInfo.valueOf("memberId", ledger.getMemberId(), "ledgerId", ledger.getId())
						)
						.hasUserMessage("수정할 수 없습니다.");
			}
			
		}
		
	}

}