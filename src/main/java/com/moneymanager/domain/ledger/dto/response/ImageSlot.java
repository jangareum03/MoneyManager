package com.moneymanager.domain.ledger.dto.response;

import com.moneymanager.domain.ledger.enums.SlotStatus;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto.response<br>
 * 파일이름       : ImageSlot<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 5. 6.<br>
 * 설명              : 가계부 이미지 슬롯에 필요한 정보를 담은 객체
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
 * 		 	  <td>26. 5. 6.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class ImageSlot {
	private final SlotStatus status;				//슬롯 상태
	private final String filePath;				//이미지 경로

	private ImageSlot(SlotStatus status, String filePath) {
		this.status = status;
		this.filePath = filePath;
	}

	public static ImageSlot ofLockedSlot() {
		return new ImageSlot(SlotStatus.LOCKED, "/image/ledger/slot-lock.svg");
	}

	public static ImageSlot ofEmptySlot() {
		return new ImageSlot(SlotStatus.EMPTY, "/image/ledger/slot-unlock.svg");
	}

	public static ImageSlot ofFilledSlot(String fileName) {
		return new ImageSlot(SlotStatus.FILLED, "/uploads/ledger/" + fileName);
	}
}
