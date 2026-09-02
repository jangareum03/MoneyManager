package com.moneymanager.ledger.service.policy;

import com.moneymanager.ledger.domain.dto.response.ImageSlot;
import com.moneymanager.ledger.domain.enums.SlotStatus;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.policy<br>
 * 파일이름       : ImageSlotPolicy<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 13<br>
 * 설명              : 이미지 슬롯 정책을 나타내는 클래스
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
 * 		 	  <td>26. 8. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class ImageSlotPolicy {

	@Getter
	private final int MAX_SLOT = 3;

	private final static String PREFIX_IMAGE = "/uploads/ledger/";

	List<ImageSlot> buildCreatableImageSlots(int availImgCnt) {
		List<ImageSlot> slots = new ArrayList<>(MAX_SLOT);

		if(!canRegisterMember(availImgCnt)){
			availImgCnt = MAX_SLOT;
		}

		for(int i=0; i<MAX_SLOT; i++){
			ImageSlot slot = createLockedSlot();

			if(i <= availImgCnt) {
				slot = createEmptySlot("/image/ledger/slot-unlock.svg");
			}

			slots.add(slot);
		}

		return slots;
	}

	List<ImageSlot> buildDisplayImageSlots(List<String> filePath) {
		List<ImageSlot> slots = new ArrayList<>(MAX_SLOT);

		for(int i=0; i<MAX_SLOT; i++){
			ImageSlot slot = createEmptySlot("/image/ledger/image-empty.svg");

			if(i < filePath.size()) {
				slot = createFilledSlot(PREFIX_IMAGE + filePath.get(i));
			}

			slots.add(slot);
		}

		return slots;
	}

	List<ImageSlot> buildEditableImageSlots(int availImgCnt, List<String> filePath) {
		List<ImageSlot> slots = new ArrayList<>(MAX_SLOT);

		for(int i=0; i<MAX_SLOT; i++){
			if(i < filePath.size()) {
				slots.add(
						createFilledSlot(PREFIX_IMAGE + filePath.get(i))
				);
			}else if(i < availImgCnt) {
				slots.add(
						createEmptySlot("/image/ledger/slot-unlock.svg")
				);
			}else{
				slots.add(
						createLockedSlot()
				);
			}
		}

		return slots;
	}


	//===== createSlots 보조 메서드 =====
	private boolean canRegisterMember(int currentCount) {
		return currentCount <= MAX_SLOT;
	}


	//===== 유틸 메서드 =====
	private ImageSlot createEmptySlot(String filePath) {
		return ImageSlot.of(SlotStatus.EMPTY, filePath);
	}

	private ImageSlot createFilledSlot(String filePath) {
		return ImageSlot.of(SlotStatus.FILLED, filePath);
	}

	private ImageSlot createLockedSlot() {
		return ImageSlot.of(SlotStatus.LOCKED, "/image/ledger/slot-lock.svg");
	}

}