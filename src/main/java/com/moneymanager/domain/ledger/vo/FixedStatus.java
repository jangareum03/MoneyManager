package com.moneymanager.domain.ledger.vo;

import com.moneymanager.domain.ledger.enums.FixedPeriod;
import com.moneymanager.exception.ErrorCode;
import lombok.Getter;
import lombok.Value;

import static com.moneymanager.exception.ErrorUtil.createClientException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : FixedStatus<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 24.<br>
 * 설명              : 가계부 고정주기 값을 나타내는 클래스
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
 * 		 	  <td>25. 11. 24.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
@Getter
public class FixedStatus {
	boolean fixed;				//고정 여부(true: 고정, false: 변동)
	FixedPeriod period;		//고정 주기

	public FixedStatus() {
		this.fixed = false;
		this.period = null;
	}

	public FixedStatus(boolean fixed, FixedPeriod period) {
		if( fixed && period == null ) {
			throw createClientException(ErrorCode.LEDGER_FIX_MISSING, "고정 주기는 필수입니다.");
		}

		if( !fixed && period != null ) {
			throw createClientException(ErrorCode.LEDGER_FIX_INVALID, "고정 여부를 확인해주세요.", period);
		}

		this.fixed = fixed;
		this.period = period;
	}

}