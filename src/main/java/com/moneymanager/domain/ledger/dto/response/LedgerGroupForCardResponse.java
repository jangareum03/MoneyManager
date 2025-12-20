package com.moneymanager.domain.ledger.dto.response;

import com.moneymanager.domain.ledger.vo.IncomeExpenseSummary;
import com.moneymanager.domain.ledger.vo.LedgerSummary;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto<br>
 * 파일이름       : LedgerGroupForCardResponse<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 3<br>
 * 설명              : 가계부 요약정보를 카드 리스트로 형태로 클라이언트에게 전달하기 위한 클래스
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
 * 		 	  <td>25. 12. 3.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
@Builder
public class LedgerGroupForCardResponse {
	private final String title;
	private final Map<String, List<List<LedgerSummary>>> summary;
	private final IncomeExpenseSummary stats;


	public static LedgerGroupForCardResponse from(LedgerGroupResponse response) {
		return LedgerGroupForCardResponse
				.builder()
				.title(response.getTitle())
				.summary( chunkByDate(response.getSummary().getDateGroups(), 3) )
				.stats(response.getStats())
				.build();
	}

	private static Map<String, List<List<LedgerSummary>>> chunkByDate( Map<String, List<LedgerSummary>> original, int size ) {
		Map<String, List<List<LedgerSummary>>> result = new LinkedHashMap<>();

		original.forEach( (date, list) -> {
			List<List<LedgerSummary>> chunk = new ArrayList<>();

			for( int i=0; i<list.size(); i += size ) {
				chunk.add( list.subList(i, Math.min(i+size, list.size())) );
			}

			result.put(date, chunk);
		});

		return result;
	}
}