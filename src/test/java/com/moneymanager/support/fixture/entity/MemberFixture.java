package com.moneymanager.support.fixture.entity;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberInfo;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.moneymanager.support.data.MemberTestData.*;

public final class MemberFixture {

	private MemberFixture() {}

	public static Member.MemberBuilder member(PasswordEncoder encoder) {
		String memberId = MEMBER_ID;

		return Member.builder()
				.id(memberId)
				.userName(USERNAME)
				.password(encoder.encode(PASSWORD))
				.name(NAME)
				.birthDate(BIRTH_DATE)
				.createdAt(CREATE_DATE)
				.type(TYPE)
				.status(STATUS)
				.role(ROLE)
				.nickName(NICK_NAME)
				.email(EMAIL)
				.info(getMemberInfo(memberId));
	}

	private static MemberInfo	getMemberInfo(String id) {
		return MemberInfo.builder()
				.id(id)
				.gender(GENDER)
				.loginAt(LAST_LOGIN_DATE)
				.build();
	}

}