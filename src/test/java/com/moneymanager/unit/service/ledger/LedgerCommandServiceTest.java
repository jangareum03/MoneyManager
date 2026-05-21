package com.moneymanager.unit.service.ledger;


import com.moneymanager.domain.ledger.dto.request.LedgerUpdateRequest;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ErrorInfo;
import com.moneymanager.exception.error.ServiceAction;
import com.moneymanager.fixture.LedgerRequestFixture;
import com.moneymanager.fixture.ledger.LedgerFixture;
import com.moneymanager.fixture.ledger.LedgerUpdateRequestFixture;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.file.FileCommandService;
import com.moneymanager.service.ledger.LedgerCommandService;
import com.moneymanager.service.ledger.LedgerReadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

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

	@Mock
	private LedgerReadService ledgerReadService;


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
				when(ledgerRepository.insert(any())).thenReturn(1L);
				when(ledgerRepository.findById(1L)).thenReturn(savedLedger);

				//when
				target.registerLedger(request);

				//then
				verify(ledgerRepository).insert(any(Ledger.class));
				verify(ledgerRepository).findById(1L);
				verify(fileCommandService, never()).storeFile(any(), any(), any());
				verify(imageRepository, never()).saveAll(anyList());
			}

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
				when(ledgerRepository.insert(any(Ledger.class))).thenReturn(1L);
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
				when(ledgerRepository.insert(any(Ledger.class)))
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
				when(ledgerRepository.insert(any(Ledger.class)))
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

		}

	}


	@Nested
	@DisplayName("가계부 수정 테스트")
	class UpdateTest {

		@BeforeEach
		void setUp() {
			when(securityUtil.getMemberId()).thenReturn("test-mid");
		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {
		
			@Test
			@DisplayName("조회할 수 없는 가계부 수정 시 BusinessException이 발생한다.")
			void throwsException_whenLedgerDoesNotExist() {
				//given: 조회되지 않는 가계부 상황을 설정한다.
				String code = "no-code";
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.createRequest().build();
				List<MultipartFile> fileList = Collections.emptyList();

				when(ledgerReadService.getLedger(anyString(), anyString()))
						.thenThrow(BusinessException.of(
								LEDGER_TARGET_NOT_FOUND,
								"DB 조회 실패"
						));
				
				//when & then: 존재하지 않은 가계부 수정 시 예외가 발생한다.
				assertThatThrownBy(() -> target.update(code, request, fileList))
						.isInstanceOf(BusinessException.class);
			}

			@Test
			@DisplayName("카테고리 코드가 비즈니스 규칙에 벗어나면 BusinessException이 발생한다.")
			void throwsException_whenCategoryCodeIsInvalid() {
				//given: 비즈니스 규칙(01/02로 시작) 벗어난 카테고리 코드로 가계부를 설정한다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.createRequest()
						.categoryCode("030101")
						.build();

				Ledger ledger = LedgerFixture.defaultLedger().build();

				when(ledgerReadService.getLedger(anyString(), anyString()))
						.thenReturn(ledger);

				//when & then: 규칙에 벗어난 가계부 수정 시 예외가 발생한다.
				assertThatCode(() -> target.update("code", request, Collections.emptyList()))
						.isInstanceOf(BusinessException.class);
			}

			@Test
			@DisplayName("고정이 비즈니스 규칙에 벗어나면 BusinessException이 발생한다.")
			void throwsException_whenFixedStatusIsInvalid() {
				//given: 비즈니스 규칙에 벗어난 고정정보로 가계부를 설정한다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.createRequest()
						.fixed(true)
						.fixCycle(null)
						.build();

				Ledger ledger = LedgerFixture.defaultLedger().build();

				when(ledgerReadService.getLedger(anyString(), anyString()))
						.thenReturn(ledger);

				//when & then: 규칙에 벗어난 가계부 수정 시 예외가 발생한다.
				assertThatCode(() -> target.update("code", request, Collections.emptyList()))
						.isInstanceOf(BusinessException.class);
			}

			@ParameterizedTest(name = "[{index}] amount={0}")
			@ValueSource(longs = {0, -1, -1000})
			@DisplayName("금액이 비즈니스 규칙에 벗어나면 BusinessException이 발생한다.,")
			void throwException_whenAmountIsInvalid(Long amount) {
				//given: 비즈니스 규칙(0 이하) 벗어난 금액으로 가계부를 설정한다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.createRequest()
						.amount(amount)
						.build();

				Ledger ledger = LedgerFixture.defaultLedger().build();

				when(ledgerReadService.getLedger(anyString(), anyString()))
						.thenReturn(ledger);

				//when & then: 규칙에 벗어난 가계부 수정 시 예외가 발생한다.
				assertThatCode(() -> target.update("code", request, Collections.emptyList()))
						.isInstanceOf(BusinessException.class);
			}
			
			@Test
			@DisplayName("금액유형이 비즈니스 규칙에 벗어나면 BusinessException이 발생한다.,")
			void throwsException_whenAmountTypeIsInvalid() {
				//given: 비즈니스 규칙(0 이하) 벗어난 금액으로 가계부를 설정한다.
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.createRequest()
						.paymentType("no")
						.build();

				Ledger ledger = LedgerFixture.defaultLedger().build();

				when(ledgerReadService.getLedger(anyString(), anyString()))
						.thenReturn(ledger);

				//when & then: 규칙에 벗어난 가계부 수정 시 예외가 발생한다.
				assertThatCode(() -> target.update("code", request, Collections.emptyList()))
						.isInstanceOf(BusinessException.class);
				
			}

			@Test
			@DisplayName("수정이 적용이 안되면 BusinessException이 발생한다.")
			void throwsException_whenUpdateFails() {
				//given: DB에 적용되지 않도록 설정
				LedgerUpdateRequest request = LedgerUpdateRequestFixture.createRequest().build();

				when(ledgerReadService.getLedger(anyString(), anyString()))
						.thenReturn(LedgerFixture.defaultLedger().build());
				when(ledgerRepository.update(any())).thenReturn(0);

				//when & then: 수정된 내용이 저장이 안되면 예외가 발생한다.
				assertThatCode(() -> target.update("code", request, Collections.emptyList()))
						.isInstanceOf(BusinessException.class);
			}
		}
		
	}
}