package com.moneymanager.ledger.service;

import com.moneymanager.ledger.domain.dto.response.ImageSlot;
import com.moneymanager.ledger.domain.enums.SlotStatus;
import com.moneymanager.global.exception.exception.BusinessException;
import com.moneymanager.ledger.repository.LedgerImageRepository;
import com.moneymanager.global.security.utils.SecurityUtil;
import com.moneymanager.ledger.service.read.LedgerImageReadService;
import com.moneymanager.delete.service.member.MemberReadService;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerImageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service<br>
 * 파일이름       : LedgerImageReadServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 29<br>
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
 * 		 	  <td>26. 7. 29</td>
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

	@Mock
	private SecurityUtil securityUtil;

	@Mock
	private MemberReadService memberReadService;

	@Mock
	private LedgerImageRepository imageRepository;

	@BeforeEach
	void setUp() {
		when(securityUtil.getMemberId()).thenReturn(MemberTestData.MEMBER_ID);
	}

	@Nested
	@DisplayName("이미지 슬롯 반환")
	class GetImageSlotTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("저장된 이미지가 없으면 EMPTY와 LOCKED 슬롯을 반환된다.")
			void returnsEmptyAndLockedSlots_whenImagesDoesNotExist() {
				//given: 저장된 이미지가 없는 상태이다.
				Long ledgerId = 1L;

				when(memberReadService.getImageLimit(any()))
						.thenReturn(1);
				when(imageRepository.findByLedgerId(any()))
						.thenReturn(Collections.emptyList());

				//when: 이미지 슬롯을 요청한다.
				List<ImageSlot> result = target.resolveImageSlots(ledgerId);

				//then: 이미지 슬롯 리스트가 반환된다.
				assertThat(result).hasSize(3);

				assertThat(result)
						.filteredOn(slot -> slot.getStatus().equals(SlotStatus.EMPTY))
						.hasSize(1);

				assertThat(result)
						.filteredOn(slot -> slot.getStatus().equals(SlotStatus.LOCKED))
						.hasSize(2);
			}

			@Test
			@DisplayName("저장된 이미지 수가 업로드 가능 개수보다 작으면 EMPTY 슬롯을 추가한다.")
			void returnsEmptySlots_whenImageCountIsLessThanUploadLimit() {
				//given: 이미지가 한 개가 저장되어 있다.
				Long ledgerId = 1L;

				when(memberReadService.getImageLimit(any()))
						.thenReturn(2);
				when(imageRepository.findByLedgerId(any()))
						.thenReturn(List.of(
								LedgerImageFixture.savedImage(1L, ledgerId, 1)
						));

				//when: 이미지 슬롯을 요청한다.
				List<ImageSlot> result = target.resolveImageSlots(ledgerId);

				//then: 이미지 슬롯 리스트가 반환된다.
				assertThat(result).hasSize(3);

				assertThat(result)
						.filteredOn(slot -> slot.getStatus().equals(SlotStatus.FILLED))
						.hasSize(1);

				assertThat(result)
						.filteredOn(slot -> slot.getStatus().equals(SlotStatus.EMPTY))
						.hasSize(1);

				assertThat(result)
						.filteredOn(slot -> slot.getStatus().equals(SlotStatus.LOCKED))
						.hasSize(1);
			}
			
			@Test
			@DisplayName("저장된 이미지 수가 업로드 가능 개수와 같으면 모두 FILLED 슬롯을 반환한다.")
			void returnsAllFilledSlots_whenImageCountIsEqualToUploadLimit() {
				//given: 이미지가 한 개가 저장되어 있다.
				Long ledgerId = 1L;

				when(memberReadService.getImageLimit(any()))
						.thenReturn(2);
				when(imageRepository.findByLedgerId(any()))
						.thenReturn(List.of(
								LedgerImageFixture.savedImage(1L, ledgerId, 1),
								LedgerImageFixture.savedImage(2L, ledgerId, 2)
						));

				//when: 이미지 슬롯을 요청한다.
				List<ImageSlot> result = target.resolveImageSlots(ledgerId);

				//then: 이미지 슬롯 리스트가 반환된다.
				assertThat(result).hasSize(3);

				assertThat(result)
						.filteredOn(slot -> slot.getStatus().equals(SlotStatus.FILLED))
						.hasSize(2);

				assertThat(result)
						.filteredOn(slot -> slot.getStatus().equals(SlotStatus.LOCKED))
						.hasSize(1);
			}
			
			@Test
			@DisplayName("저장된 이미지 수가 업로드 가능 개수보다 많으면 업로드 가능 개수만큼 FILLED 슬롯을 반환한다.")
			void returnsFilledSlotsUpToUploadLimit_whenImageCountExceedsUploadLimit() {
				//given: 이미지가 한 개가 저장되어 있다.
				String memberId = MemberTestData.MEMBER_ID;
				Long ledgerId = 1L;

				when(memberReadService.getImageLimit(any()))
						.thenReturn(2);
				when(imageRepository.findByLedgerId(any()))
						.thenReturn(List.of(
								LedgerImageFixture.savedImage(1L, ledgerId, 1),
								LedgerImageFixture.savedImage(2L, ledgerId, 2),
								LedgerImageFixture.savedImage(3L, ledgerId, 3)
						));

				//when: 이미지 슬롯을 요청한다.
				List<ImageSlot> result = target.resolveImageSlots(ledgerId);

				//then: 이미지 슬롯 리스트가 반환된다.
				assertThat(result).hasSize(3);

				assertThat(result)
						.filteredOn(slot -> slot.getStatus().equals(SlotStatus.FILLED))
						.hasSize(2)
						.extracting(ImageSlot::getFilePath)
						.containsExactly(
								"/uploads/ledger/" + memberId + "/image1.jpg",
								"/uploads/ledger/" + memberId + "/image2.jpg"
						);

				assertThat(result)
						.filteredOn(slot -> slot.getStatus().equals(SlotStatus.LOCKED))
						.hasSize(1);
			}
			
			@Test
			@DisplayName("저장된 이미지가 null이면 제거된다.")
			void filtersImages_whenImageIsNull() {
				//given: 이미지가 한 개가 저장되어 있다.
				Long ledgerId = 1L;

				when(memberReadService.getImageLimit(any()))
						.thenReturn(2);
				when(imageRepository.findByLedgerId(any()))
						.thenReturn(Arrays.asList(
								LedgerImageFixture.savedImage(1L, ledgerId, 1),
								null
						));

				//when: 이미지 슬롯을 요청한다.
				List<ImageSlot> result = target.resolveImageSlots(ledgerId);

				//then: 이미지 슬롯 리스트가 반환된다.
				assertThat(result).hasSize(3);

				assertThat(result)
						.extracting(ImageSlot::getStatus)
						.containsExactly(SlotStatus.FILLED, SlotStatus.EMPTY, SlotStatus.LOCKED);
			}
			
			@Test
			@DisplayName("저장된 이미지 경로가 null이면 제거된다.")
			void filtersImages_whenImagePathIsNull() {
				//given: 이미지가 한 개가 저장되어 있다.
				Long ledgerId = 1L;

				when(memberReadService.getImageLimit(any()))
						.thenReturn(2);
				when(imageRepository.findByLedgerId(any()))
						.thenReturn(List.of(
								LedgerImageFixture.savedImage(1L, ledgerId, 1),
								LedgerImageFixture.builder(2L, ledgerId, 2).imagePath(null).build()
						));

				//when: 이미지 슬롯을 요청한다.
				List<ImageSlot> result = target.resolveImageSlots(ledgerId);

				//then: 이미지 슬롯 리스트가 반환된다.
				assertThat(result).hasSize(3);

				assertThat(result)
						.extracting(ImageSlot::getStatus)
						.containsExactly(SlotStatus.FILLED, SlotStatus.EMPTY, SlotStatus.LOCKED);
			}
			
			@ParameterizedTest
			@MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
			@DisplayName("저장된 이미지 경로가 비어있으면 제거된다.")
			void filtersImages_whenImagePathIsEmpty(String path) {
				//given: 이미지가 한 개가 저장되어 있다.
				Long ledgerId = 1L;

				when(memberReadService.getImageLimit(any()))
						.thenReturn(2);
				when(imageRepository.findByLedgerId(any()))
						.thenReturn(List.of(
								LedgerImageFixture.savedImage(1L, ledgerId, 1),
								LedgerImageFixture.builder(2L, ledgerId, 2).imagePath(path).build()
						));

				//when: 이미지 슬롯을 요청한다.
				List<ImageSlot> result = target.resolveImageSlots(ledgerId);

				//then: 이미지 슬롯 리스트가 반환된다.
				assertThat(result).hasSize(3);

				assertThat(result)
						.extracting(ImageSlot::getStatus)
						.containsExactly(SlotStatus.FILLED, SlotStatus.EMPTY, SlotStatus.LOCKED);
			}

		}

		@Nested
		@DisplayName("실패 케이스")
		class Failure {
			
			@Test
			@DisplayName("존재하지 않은 회원은 예외가 발생한다.")
			void throwsException_whenUserDoesNotExist() {
				//given: 회원 인증에 실패하도록 동작이 정의되어 있다.
				when(securityUtil.getMemberId())
						.thenThrow(BusinessException.class);

				//when & then: 이미지 슬롯을 요청하면 예외가 발생한다.
				assertThatThrownBy(() -> target.resolveImageSlots(1L))
						.isInstanceOf(BusinessException.class);
			}

		}

	}

}