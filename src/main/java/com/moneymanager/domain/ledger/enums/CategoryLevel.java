package com.moneymanager.domain.ledger.enums;

import com.moneymanager.exception.ErrorCode;
import lombok.Getter;

import static com.moneymanager.exception.ErrorUtil.createClientException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.enums<br>
 * 파일이름       : CategoryLevel<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 26.<br>
 * 설명              : 가계부 카테고리 단계를 정의한 클래스
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
 * 		 	  <td>25. 11. 26.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public enum CategoryLevel {
	TOP,
	MIDDLE,
	LOW;

	public static CategoryLevel from(String level) {
		for( CategoryLevel e : CategoryLevel.values() ) {
			if( level.equalsIgnoreCase(e.name()) ) {
				return e;
			}
		}

		throw createClientException(ErrorCode.LEDGER_CATEGORY_INVALID, "지원하지 않은 카테고리 단계입니다.", level);
	}
}