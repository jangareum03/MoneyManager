package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.entity<br>
 *  * 파일이름       : Category<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 11. 17<br>
 *  * 설명              : TB_CATEGORY 테이블과 매칭되는 엔티티 클래스
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
 *		 	  <td>22. 11. 17</td>
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
public class Category {
    /* 카테고리번호(PK) */
    private String code;
    /* 부모카테고리 */
    private String parentCode;
    /* 카테고리이름 */
    private String name;
}
