package com.moneymanager.domain.member.dto;

import com.moneymanager.domain.member.enums.MemberType;
import lombok.Builder;
import lombok.Getter;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.member.response<br>
 * * 파일이름       : MemberMyPageResponse<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 27.<br>
 * * 설명              : 마이페이지 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 27.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class MemberMyPageResponse {
	private MemberInfo memberInfo;
	private Point point;

	/**
	 * 간단한 회원 정보를 담은 DTO
	 */
	@Builder
	@Getter
	public static class MemberInfo {
		//유형
		private MemberType type;
		//닉네임
		private String nickName;
		//프로필
		private String profile;
		//마지막 접속일
		private String lastLoginDate;
	}

	/**
	 * 회원 포인트 정보를 담은 DTO
	 */
	@Builder
	@Getter
	public static class Point {
		//현재 포인트
		private Long currentPoint;
		//적립된 포인트
		private Long earmPoint;
		//사용된 포인트
		private Long usePoint;
	}

}
