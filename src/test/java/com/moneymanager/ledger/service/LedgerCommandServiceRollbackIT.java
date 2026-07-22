package com.moneymanager.ledger.service;

import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.domain.ledger.dto.request.LedgerUpdateRequest;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.member.Member;
import com.moneymanager.exception.exception.BusinessException;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import com.moneymanager.support.fixture.entity.MemberFixture;
import com.moneymanager.support.fixture.request.LedgerUpdateRequestFixture;
import com.moneymanager.support.fixture.request.LedgerWriteRequestFixture;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.repository.member.MemberRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.ledger.LedgerCommandService;
import com.moneymanager.service.ledger.LedgerImageCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import com.moneymanager.support.security.WithMockCustomUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
	private LedgerCommandService target;

	@Autowired
	private LedgerRepository ledgerRepository;

	@SpyBean
	private LedgerImageCommandService imageCommandService;


	@MockBean
	private SecurityUtil securityUtil;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private LedgerImageRepository imageRepository;


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

		@Test
		@DisplayName("이미지 처리 저장 중 예외가 발생하면 가계부 등록은 롤백된다.")
		void rollback_whenProcessImagesFails() {
			//given: 이미지가 포함된 가계부 등록 요청을 준비한다.
			LedgerWriteRequest request = LedgerWriteRequestFixture.withImages(3).build();

			doThrow(BusinessException.class).when(imageCommandService).processImages(any(), any());

			//when & then: 가계부를 등록을 요청하면 BusinessException이 발생한다.
			assertThatThrownBy(() -> target.register(request))
					.isInstanceOf(BusinessException.class);

			//then: 가계부와 이미지 정보는 저장되지 않는다.
			assertThat(ledgerRepository.count()).isZero();
			assertThat(imageRepository.count()).isZero();
		}

	}


	@Nested
	@DisplayName("가계부 수정")
	class UpdateTest {

		@Test
		@DisplayName("이미지 처리 중 실패하면 전체 롤백한다.")
		void rollback_whenProcessImageFails() {
			//given: 수정할 가계부가 준비되어 있다.
			Long ledgerId = ledgerRepository.insert(
					LedgerFixture.newLedger(MemberTestData.MEMBER_ID)
			);

			Ledger savedLedger = ledgerRepository.findById(ledgerId);

			LedgerUpdateRequest request = LedgerUpdateRequestFixture.withImages(2)
							.memo("수정된 메모")
							.categoryCode(CategoryTestData.FOOD_CODE)
							.build();

			doThrow(
					BusinessException.class
			).when(imageCommandService).processImages(any(), any());

			//when: 가계부를 수정한다.
			assertThatThrownBy(
					() -> target.update(
							savedLedger.getCode(),
							request
					)
			).isInstanceOf(BusinessException.class);

			//then: 요청한 가계부 값이 반영되지 않는다.
			Ledger rollbackLedger = ledgerRepository.findById(ledgerId);

			assertThat(rollbackLedger.getCategory()).isEqualTo(CategoryTestData.EARNED_CODE);
			assertThat(rollbackLedger.getMemo()).isNull();;
			assertThat(rollbackLedger.getUpdatedAt()).isNull();

			assertThat(imageRepository.count()).isZero();
		}

	}

}
