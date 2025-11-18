package com.moneymanager.domain.ledger;

import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.domain.ledger<br>
 *  * 파일이름       : Ledger<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 11. 15<br>
 *  * 설명              : TB_BUDGET_BOOK 테이블과 매칭되는 클래스
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
 *		 	  <td>22. 11. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>최초 생성(버전 1.0)</td>
 *		 	</tr>
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
public class Ledger {
    private Long id;											//가계부 번호(식별자)
    private Member member;							//작성자
    private Category category;						//카테고리
    private String fix;										//고정여부
    private String fixCycle;								//고정주기
    private String bookDate;							//가계부 날짜
    private String memo;									//내용
    private Long price;									//가격
    private PaymentType paymentType;			//결제 유형
    private String image1;								//이미지1
    private String image2;								//이미지2
    private String image3;								//이미지3
    private String placeName;							//장소명
    private String roadAddress;						//도로명 주소
    private String address;								//지번주소
    private LocalDateTime createdAt;			//등록일
    private LocalDateTime updatedAt;			//수정일
}
