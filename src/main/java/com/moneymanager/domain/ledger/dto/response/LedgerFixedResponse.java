package com.moneymanager.domain.ledger.dto.response;

import com.moneymanager.domain.ledger.entity.Ledger;
import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto<br>
 * 파일이름       : LedgerFixedResponse<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 19<br>
 * 설명              : 가계부 고정 정보를 클라이언트에게 전달하기 위한 클래스
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
 * 		 	  <td>25. 12. 19</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LedgerFixedResponse {
	private final boolean isFix;			//고정적으로 가계부 작성할지 여부
	private final String period;			//일회성이면 null, 반복이면 선택한 주기

	private LedgerFixedResponse(boolean isFixed, String period) {
		this.isFix = isFixed;
		this.period = period;
	}

	public static LedgerFixedResponse from(Ledger ledger) {
		boolean isRepeat = ledger.getFix().isFixed();

		return new LedgerFixedResponse( isRepeat, isRepeat ? ledger.getFixCycle().getValue() : null );
	}
}