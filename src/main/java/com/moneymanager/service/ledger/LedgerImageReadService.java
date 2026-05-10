package com.moneymanager.service.ledger;

import com.moneymanager.domain.global.Policy;
import com.moneymanager.domain.ledger.dto.response.ImageSlot;
import com.moneymanager.domain.ledger.enums.SlotStatus;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.member.MemberReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.moneymanager.utils.validation.ValidationUtils.isNullOrBlank;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.ledger<br>
 * 파일이름       : LedgerImageReadService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 26<br>
 * 설명              : 가계부 이미지 정보를 조회하는 클래스
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
 * 		 	  <td>26. 4. 26</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class LedgerImageReadService {

	private final SecurityUtil securityUtil;

	private final MemberReadService memberReadService;
	private final LedgerImageRepository imageRepository;


	public List<ImageSlot> resolveImageSlots() {
		//1. 인증된 회원 조회
		String memberId = securityUtil.getMemberId();

		//2. 업로드 가능 개수
		int allowedCount = memberReadService.getImageLimit(memberId);

		//3. 슬롯 상태 생성
		List<SlotStatus> slotStatuses = generateSlotStatuses(allowedCount, 0);

		return createImageSlots(slotStatuses, Collections.emptyList());
	}


	public List<ImageSlot> resolveImageSlots(Long ledgerId) {
		//1. 인증된 회원 조회
		String memberId = securityUtil.getMemberId();

		//2. 업로드 가능 개수
		int allowedCount = memberReadService.getImageLimit(memberId);

		//3. 저장된 이미지 조회
		List<String> savedImages =
				imageRepository.findByLedgerId(ledgerId)
						.stream()
						.filter(i -> i != null && !isNullOrBlank(i.getImagePath()))
						.map(i -> memberId + "/" + i.getImagePath())
						.toList();

		//4. 슬롯 상태 생성
		List<SlotStatus> slotStatuses = generateSlotStatuses(allowedCount, savedImages.size());

		//5. 상태 기반 슬롯 생성
		return createImageSlots(slotStatuses, savedImages);
	}

	private List<SlotStatus> generateSlotStatuses(int allowedCount, int savedCount) {
		List<SlotStatus> slotStatuses = new ArrayList<>();

		for(int i=0; i<Policy.LEDGER_MAX_IMAGE; i++) {
			if( i >= allowedCount) {
				slotStatuses.add(SlotStatus.LOCKED);
			}else if(i < savedCount) {
				slotStatuses.add(SlotStatus.FILLED);
			}else {
				slotStatuses.add(SlotStatus.EMPTY);
			}
		}

		return slotStatuses;
	}

	private List<ImageSlot> createImageSlots(List<SlotStatus> slotStatusList, List<String> savedImageList) {
		List<ImageSlot> imageSlots = new ArrayList<>();

		int savedIndex = 0;

		for(SlotStatus status : slotStatusList) {
			imageSlots.add(
					switch (status) {
						case EMPTY -> ImageSlot.ofEmptySlot();
						case FILLED -> ImageSlot.ofFilledSlot(savedImageList.get(savedIndex++));
						case LOCKED -> ImageSlot.ofLockedSlot();
					}
			);
		}

		return imageSlots;
	}

}
