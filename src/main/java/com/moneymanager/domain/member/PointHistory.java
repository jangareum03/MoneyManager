package com.moneymanager.domain.member;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;



/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.entity<br>
 *  * 파일이름       : PointHistory<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : TB_POINT_LOGS 테이블과 매칭되는 엔티티 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>22. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Builder
@Getter
public class PointHistory {
	/* 사용내역 번호(PK) */
	private Long id;
	/* 회원번호(FK: member_id) */
	private Member member;
	/* 포인트 유형 */
	private String type;
	/* 포인트 값 */
	private Long points;
	/* 변경이유 */
	private String reason;
	/* 잔여 포인트 */
	private Long balancePoints;
	/* 사용된 날짜 */
	private Timestamp usedAt;
}
