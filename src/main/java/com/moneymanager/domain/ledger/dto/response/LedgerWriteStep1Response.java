package com.moneymanager.domain.ledger.dto.response;

import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.utils.DateTimeUtils;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto<br>
 * 파일이름       : LedgerWriteStep1Response<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 18<br>
 * 설명              : 가계부 초기 작성 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 12. 18</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LedgerWriteStep1Response {
	private final int year;								//연도
	private final int month;								//월
	private final int day;									//일
	private final int lastDay;							//월의 마지막 일
	private final String today;						//오늘 날짜를 문자열로 표현
	private final List<LedgerType> types;		//가계부 유형

	public LedgerWriteStep1Response(LocalDate date) {
		this.year = date.getYear();
		this.month = date.getMonthValue();
		this.day = date.getDayOfMonth();
		this.lastDay = date.lengthOfMonth();

		this.today = DateTimeUtils.formatDateAsString(date, "yyyy년 MM월 dd일");
		this.types = List.of(LedgerType.values());
	}
}