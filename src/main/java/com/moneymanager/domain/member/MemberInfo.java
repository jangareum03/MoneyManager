package com.moneymanager.domain.member;

import com.moneymanager.domain.member.enums.MemberGender;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.domain.member<br>
 *  * 파일이름       : MemberInfo<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 10. 24<br>
 *  * 설명              : TB_MEMBER_INFO 테이블과 매칭되는 클래스
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
 *		 	  <td>22. 10. 24</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>최초 생성(버전 1.0)</td>
 *		 	</tr>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>22. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 8. 10</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[필드 추가] failureCount</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Builder
@Getter
public class MemberInfo {
    private MemberGender gender;		//성별(N, M, F)
    private String profile;						//프로필
    private Long point;							//포인트
    private Long consecutiveDays;			//연속 출석일자
    private int imageLimit;						//등록 가능한 이미지 수
    private LocalDateTime loginAt;			//마지막 접속일
    private int failureCount;					//실패횟수
}
