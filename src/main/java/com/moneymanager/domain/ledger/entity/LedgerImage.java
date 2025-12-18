package com.moneymanager.domain.ledger.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.entity<br>
 * 파일이름       : LedgerImage<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 17<br>
 * 설명              : LEDGER_IMAGE 테이블과 매칭되는 클래스
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
 * 		 	  <td>25. 12. 17.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class LedgerImage {
	private Long id;										//이미지 식별번호
	private Ledger ledgerId;						//가계부 정보
	private String imagePath;						//이미지 상대경로
	private int sortOrder;							//나열 순서
	private LocalDateTime createdAt;		//등록일
	private LocalDateTime updatedAt;		//수정일
}
