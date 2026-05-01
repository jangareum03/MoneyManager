package com.moneymanager.fixture;


import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.member.MemberInfo;
import com.moneymanager.domain.member.enums.MemberGender;
import com.moneymanager.domain.member.enums.MemberStatus;
import com.moneymanager.domain.member.enums.MemberType;

import java.time.LocalDateTime;

public class MemberFixture {

	private final static String USERNAME = "user1";
	private final static String PASSWORD = "pw1234!!";
	private final static String ROLE = "ROLE_USER";

	private static int counter = 0;

	public static Member.MemberBuilder defaultMember() {
		String memberId = generateMemberId();

		return Member.builder()
				.id(memberId)
				.userName(USERNAME)
				.password(PASSWORD)
				.role(ROLE)
				.type(MemberType.NORMAL)
				.status(MemberStatus.ACTIVE)
				.name("홍길동")
				.birthDate("20020202")
				.nickName("홍길동전")
				.email(generateEmail())
				.createdAt(LocalDateTime.of(2026, 1, 1, 10, 0, 0))
				.memberInfo(createMemberInfo(memberId));
	}

	public static Member create() {
		return defaultMember().build();
	}

	public static MemberInfo createMemberInfo(String memberId) {
		return MemberInfo.builder()
				.memberId(memberId)
				.gender(MemberGender.DEFAULT)
				.build();
	}

	private static String generateMemberId() {
		long millis = System.currentTimeMillis();
		int value = (int) ((millis + (counter++)) % 100_000);

		return "id-" + String.format("%05d", value);
	}

	private static String generateEmail() {
		long millis = System.currentTimeMillis();
		int value = (int) ((millis + (counter++)) % 100_000);

		return "test" + String.format("%03d", value) + "@test.com";
	}

}
