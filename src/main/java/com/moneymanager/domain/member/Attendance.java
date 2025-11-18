package com.moneymanager.domain.member;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.domain.member<br>
 *  * 파일이름       : Attendance<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 11. 7<br>
 *  * 설명              : TB_MEMBER_ATTENDANCE 테이블과 매칭되는 엔티티 클래스
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
 *		 	  <td>22. 11. 7</td>
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
public class Attendance {
    private Long id;										//출석번호(식별자)
    private Member member;						//회원정보
    private LocalDate attendanceDate;		//출석체크 날짜
}
