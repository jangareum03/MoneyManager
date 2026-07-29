package com.moneymanager.ledger.service;

import com.moneymanager.domain.ledger.dto.response.ImageSlot;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.SlotStatus;
import com.moneymanager.domain.member.Member;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.service.ledger.LedgerImageReadService;
import com.moneymanager.support.IntegrationTestSupport;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import com.moneymanager.support.fixture.entity.LedgerImageFixture;
import com.moneymanager.support.fixture.entity.MemberFixture;
import com.moneymanager.support.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service<br>
 * 파일이름       : LedgerImageReadServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 29<br>
 * 설명              : LedgerImageReadService 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 7. 29</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class LedgerImageReadServiceIT extends IntegrationTestSupport {

	@Autowired
	private LedgerImageReadService target;

	@Autowired
	private LedgerImageRepository imageRepository;

	private Member member;
	private Ledger ledger;

	@Nested
	@DisplayName("이미지 슬롯 반환")
	@WithMockCustomUser
	class GetImageSlotTest {

		@BeforeEach
		void setUp() {
			member = memberRepository.save(MemberFixture.builder(MemberTestData.MEMBER_ID).build());

			Long id = ledgerRepository.insert(LedgerFixture.newLedger().memberId(member.getId()).build());
			ledger = ledgerRepository.findById(id);
		}
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@Test
			@DisplayName("저장된 이미지가 없으면 EMPTY 슬롯이 반환된다.")
			void returnsEmptySlots_whenImagesDoesNotExist() {
				//given: 가계부 id가 주어진다.
				Long ledgerId = ledger.getId();

				//when: 이미지 슬롯을 요청한다.
				List<ImageSlot> result = target.resolveImageSlots(ledgerId);

				//then: 이미지 슬롯 리스트가 반환된다.
				assertThat(result).hasSize(3);

				assertThat(result)
						.extracting(ImageSlot::getStatus)
						.containsOnly(SlotStatus.EMPTY, SlotStatus.LOCKED);
			}
			
			@Test
			@DisplayName("저장된 이미지가 있으면 이미지 개수에 따라 슬롯이 반환된다.")
			void returnsSlotsByImageCount_whenImagesExist() {
				//given: 이미지가 저장되어 있다.
				imageRepository.saveAll(
						List.of(
								LedgerImageFixture.newImage(ledger.getId(), 1)
						)
				);

				//when: 이미지 슬롯을 요청한다.
				List<ImageSlot> result = target.resolveImageSlots(ledger.getId());

				//then: 이미지 슬롯 리스트가 반환된다.
				assertThat(result).hasSize(3);

				assertThat(result)
						.filteredOn(slot -> slot.getStatus().equals(SlotStatus.LOCKED))
						.hasSize(2);

				assertThat(result)
						.filteredOn(slot -> slot.getStatus().equals(SlotStatus.FILLED))
						.hasSize(1);
			}
		
		}

	}

}