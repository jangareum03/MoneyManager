package com.moneymanager.domain.ledger.vo;

import com.moneymanager.domain.global.enums.DatePatterns;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.utils.date.DateTimeUtils;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : LedgerByDate<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 29<br>
 * 설명              : 가계부를 날짜별로 묶어 놓은 그룹을 나타내는 클래스
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
 * 		 	  <td>25. 11. 29.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
@Getter
public class LedgerByDate {
	Map<String, List<LedgerSummary>> dateGroups;

	public LedgerByDate(List<Ledger> ledgers) {
		Map<String, List<LedgerSummary>> map = new LinkedHashMap<>();

		for( Ledger ledger : ledgers ) {
			String key = DateTimeUtils.formatDate(LocalDate.parse(ledger.getDate()), DatePatterns.DATE_DOT_WITH_DAY.getPattern());

			//키가 없으면 새로운 리스트 추가
			map.putIfAbsent(key, new ArrayList<>());

			//동일한 객체가 아닌 경우에만 추가
			LedgerSummary summary = LedgerSummary.from(ledger);
			if( !map.get(key).contains(summary) ) {
				map.get(key).add(summary);
			}

		}

		this.dateGroups = map;
	}
}