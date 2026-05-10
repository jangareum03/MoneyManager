package com.moneymanager.unit.service.ledger;

import com.moneymanager.domain.ledger.dto.response.ImageSlot;
import com.moneymanager.domain.ledger.enums.SlotStatus;
import com.moneymanager.fixture.ledger.LedgerImageFixture;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.ledger.LedgerImageReadService;
import com.moneymanager.service.member.MemberReadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.ledger<br>
 * 파일이름       : LedgerImageReadServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 26.<br>
 * 설명              : LedgerImageReadService 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 4. 26.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class LedgerImageReadServiceTest {

	@InjectMocks
	private LedgerImageReadService target;

	@Mock	private SecurityUtil securityUtil;
	@Mock	private MemberReadService memberReadService;
	@Mock	private LedgerImageRepository imageRepository;


	@Nested
	@DisplayName("이미지 슬롯 생성")
	class ImageSlotCreateTest {

		@BeforeEach
		void setUp() {
			when(securityUtil.getMemberId()).thenReturn("member");
		}

		@ParameterizedTest
		@MethodSource("provideValidImageSlotCases")
		@DisplayName("신규 작성 시 EMPTY 슬롯과 LOCKED 슬롯이 생성된다.")
		void buildsEmptyAndLockedSlots_whenNewLedgerCreated(int allowedCount, List<SlotStatus> expectedStatuses) {
			//given: 회원의 업로드 가능 개수 설정
			String memberId = "member";

			when(memberReadService.getImageLimit(memberId)).thenReturn(allowedCount);
			
			//when: 이미지 슬롯 생성
			List<ImageSlot> result = target.resolveImageSlots();
			
			//then: 기본 검증
			assertThat(result).hasSize(3);
			assertThat(result)
					.extracting(ImageSlot::getStatus)
					.containsExactlyElementsOf(expectedStatuses);

			//FILLED 슬롯 여부 검증
			assertThat(result)
					.extracting(ImageSlot::getStatus)
					.doesNotContain(SlotStatus.FILLED);
		}

		static Stream<Arguments> provideValidImageSlotCases() {
			return Stream.of(
				Arguments.of(
						0,
						List.of(SlotStatus.LOCKED, SlotStatus.LOCKED, SlotStatus.LOCKED)
				),
				Arguments.of(
						1,
						List.of(SlotStatus.EMPTY, SlotStatus.LOCKED, SlotStatus.LOCKED)
				),
				Arguments.of(
						2,
						List.of(SlotStatus.EMPTY, SlotStatus.EMPTY, SlotStatus.LOCKED)
				),
				Arguments.of(
						3,
						List.of(SlotStatus.EMPTY, SlotStatus.EMPTY, SlotStatus.EMPTY)
				),
				Arguments.of(
						4,
						List.of(SlotStatus.EMPTY, SlotStatus.EMPTY, SlotStatus.EMPTY)
				)
			);
		}

		@Test
		@DisplayName("저장된 이미지는 FILLED 슬롯으로 생성된다.")
		void buildsFilledSlots_whenImagesExist() {
			//given: 가계부 번호에 해당하는 이미지 경로를 가져온다.
			String memberId = "member";

			when(memberReadService.getImageLimit(memberId)).thenReturn(1);
			when(imageRepository.findByLedgerId(any())).thenReturn(
					List.of(
							LedgerImageFixture.defaultImage(1L, 1L).build()
					)
			);
			
			//when: 이미지 슬롯 생성
			List<ImageSlot> result = target.resolveImageSlots(1L);
			
			//then: 기본 검증
			assertThat(result).hasSize(3);
			assertThat(result)
					.extracting(ImageSlot::getStatus)
					.containsExactly(SlotStatus.FILLED, SlotStatus.LOCKED, SlotStatus.LOCKED);
		}
		
		@Test
		@DisplayName("null 또는 blank 이미지는 제외된다.")
		void excludesSlots_whenImageIsNullOrEmpty() {
			//given: 이미지 경로에 빈 문자열이 포함된다.
			String memberId = "member";

			when(memberReadService.getImageLimit(memberId)).thenReturn(2);
			when(imageRepository.findByLedgerId(any())).thenReturn(
					List.of(
							LedgerImageFixture.defaultImage(1L, 1L).build(),
							LedgerImageFixture.defaultImage(2L, 1L).imagePath("").build()
					)
			);
			
			//when: 이미지 슬롯 생성
			List<ImageSlot> result = target.resolveImageSlots(1L);
			
			//then: 기본 검증
			assertThat(result).hasSize(3);
			assertThat(result)
					.extracting(ImageSlot::getStatus)
					.containsExactly(SlotStatus.FILLED, SlotStatus.EMPTY, SlotStatus.LOCKED);
		}

		@Test
		@DisplayName("슬롯 상태별 다른 이미지 경로를 가진다.")
		void resolvesDifferentImagePath_whenBySlotStatus() {
			//given: 가계부 이미지를 설정
			String memberId = "member";

			when(memberReadService.getImageLimit(memberId)).thenReturn(2);
			when(imageRepository.findByLedgerId(any())).thenReturn(
					List.of(
							LedgerImageFixture.defaultImage(1L, 1L).build()
					)
			);

			//when: 이미지 슬롯 생성
			List<ImageSlot> result = target.resolveImageSlots(1L);
			
			//then: 이미지 경로 검증
			assertThat(result).hasSize(3);
			assertThat(result)
					.extracting("filePath")
					.containsExactly("/uploads/ledger/member/images/default.png", "/image/ledger/slot-unlock.svg", "/image/ledger/slot-lock.svg");
		}
		
	}

}
