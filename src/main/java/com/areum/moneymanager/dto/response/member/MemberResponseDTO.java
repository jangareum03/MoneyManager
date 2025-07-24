package com.areum.moneymanager.dto.response.member;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dto.response.member<br>
 *  * 파일이름       : MemberResponseDTO<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 사용자에게 회원 데이터를 전달하기 위한 클래스
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
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
public class MemberResponseDTO {


	@Builder
	@Getter
	//회원 정보를 가져올 때
	public static class Info {
		private String name;
		private String nickName;
		private String gender;
		private String type;
		private String email;
		private String profile;
		private String joinDate;
		private String lastLoginDate;
		private Long attendCount;
	}



	@Builder
	@Getter
	//마이페이지에서 정보를 가져올 때
	public static class MyPage {
		private String profile;
		private MemberType memberShip;
		private String nickName;
		private String lastLoginDate;
	}


	@Builder
	@Getter
	public static class MemberType {
		private char type;
		private String text;
	}


	@Builder
	@Getter
	//전체 총합 포인트 정보를 가져올 때
	public static class Point {
		private Long currentPoint;
		private Long earmPoint;
		private Long usePoint;
	}



}
