package com.moneymanager.domain.notice;

import com.moneymanager.domain.admin.Admin;
import lombok.Builder;
import lombok.Getter;

import java.sql.Date;



/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.entity<br>
 *  * 파일이름       : Notice<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 23. 2. 6<br>
 *  * 설명              : TB_NOTICE 테이블과 매칭되는 엔티티 클래스
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
 *		 	  <td>23. 2. 6</td>
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
@Getter
@Builder
public class Notice {
    /* 공지사항 번호(PK) */
    private String id;
    /* 등록자(FK: admin_id) */
    private Admin admin;
    /* 유형 */
    private char type;
    /* 상태 */
    private String status;
    /* 제목 */
    private String title;
    /* 내용 */
    private String content;
    /* 등록일 */
    private Date createdDate;
    /* 수정일 */
    private Date updatedDate;
    /* 조회수 */
    private Long viewCount;
    /* 우선순위 */
    private int rank;
}
