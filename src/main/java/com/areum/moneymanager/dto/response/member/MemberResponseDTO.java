package com.areum.moneymanager.dto.response.member;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;


public class MemberResponseDTO {


	@Builder
	@Getter
	// 아이디를 찾을 때
	public static class IDFound {
		private String id;
		private char status;
		private LocalDateTime date;
	}




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
