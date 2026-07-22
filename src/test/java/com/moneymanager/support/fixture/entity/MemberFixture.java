package com.moneymanager.support.fixture.entity;

import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.member.MemberInfo;
import com.moneymanager.domain.member.enums.MemberType;

public final class MemberFixture {

	private static int counter = 0;

	private MemberFixture() {}

	public static Member.MemberBuilder builder() {
		return builder(generateMemberId());
	}

	public static Member.MemberBuilder builder(String memberId) {
		return Member.builder()
				.id(memberId)
				.userName("user" + counter)
				.password(MemberTestData.PASSWORD)
				.role(MemberTestData.ROLE)
				.type(MemberType.NORMAL)
				.name(MemberTestData.NAME)
				.birthDate(MemberTestData.BIRTH_DATE)
				.nickName(MemberTestData.NICK_NAME)
				.email(MemberTestData.EMAIL)
				.createdAt(MemberTestData.CREATE_DATE)
				.memberInfo(createMemberInfo(memberId).build());
	}

	public static MemberInfo.MemberInfoBuilder createMemberInfo(String memberId) {
		return MemberInfo.builder()
				.memberId(memberId)
				.gender(MemberTestData.GENDER);
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
