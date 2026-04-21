package com.moneymanager.unit.fixture;


import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.member.MemberInfo;
import com.moneymanager.domain.member.enums.MemberGender;

public class MemberFixture {

	private final static String MEMBER_ID = "UCt01001";
	private final static String USERNAME = "testuser";
	private final static String PASSWORD = "pw1234!!";
	private final static String ROLE = "ROLE_USER";

	public static Member.MemberBuilder defaultMember() {
		return Member.builder()
				.id(MEMBER_ID)
				.userName(USERNAME)
				.password(PASSWORD)
				.role(ROLE);
	}

	public static Member.MemberBuilder defaultMember(String userName, String password) {
		return defaultMember()
				.userName(userName)
				.password(password);
	}

	public static MemberInfo defaultMemberInfo() {
		return MemberInfo.builder()
				.gender(MemberGender.DEFAULT)
				.imageLimit(1)
				.build();
	}

	public static MemberInfo defaultMemberInfo(int imageLimit) {
		return MemberInfo.builder()
				.gender(MemberGender.DEFAULT)
				.imageLimit(imageLimit)
				.build();
	}
}
