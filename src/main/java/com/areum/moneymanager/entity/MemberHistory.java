package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;
import java.sql.Timestamp;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.entity<br>
 *  * 파일이름       : MemberHistory<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 23. 1. 28<br>
 *  * 설명              : TB_MEMBER_HISTORY 테이블과 매칭되는 엔티티 클래스
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
 *		 	  <td>23. 1. 28</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>최초 생성(버전 1.0)</td>
 *		 	</tr>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>22. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Getter
@Builder
public class MemberHistory {
    /* 수정내역번호(PK) */
    private Long id;
    /* 회원번호(FK: member_id) */
    private Member member;
    /* 성공여부 */
    private char success;
    /* 수정유형 */
    private String type;
    /* 수정항목 */
    private String item;
    /* 기존정보 */
    private String beforeInfo;
    /* 변경정보 */
    private String afterInfo;
    /* 실패사유 */
    private String failureReason;
    /* 변경날짜 */
    private Timestamp updatedAt;
}
