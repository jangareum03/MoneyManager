package com.moneymanager.support.fixture.entity;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberInfo;
import com.moneymanager.member.domain.enums.MemberGender;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.moneymanager.support.data.MemberTestData.*;

public final class MemberFixture {

	private MemberFixture() {}

	public static Member.MemberBuilder	member() {
		return member(null);
	}

	public static Member.MemberBuilder member(PasswordEncoder encoder) {
		return Member.builder()
				.id(MEMBER_ID)
				.username(USERNAME)
				.password(encoder == null ? PASSWORD : encoder.encode(PASSWORD))
				.name(NAME)
				.birthdate(BIRTH_DATE)
				.createdAt(CREATE_DATE)
				.type(TYPE)
				.status(STATUS)
				.role(ROLE)
				.nickname(NICK_NAME)
				.email(EMAIL)
				.info(getMemberInfo());
	}

	public static Member.MemberBuilder member(PasswordEncoder encoder, MemberInfo info) {
		return Member.builder()
				.id(MEMBER_ID)
				.username(USERNAME)
				.password(encoder == null ? PASSWORD : encoder.encode(PASSWORD))
				.name(NAME)
				.birthdate(BIRTH_DATE)
				.createdAt(CREATE_DATE)
				.type(TYPE)
				.status(STATUS)
				.role(ROLE)
				.nickname(NICK_NAME)
				.email(EMAIL)
				.info(info);
	}

	public static MemberInfo.MemberInfoBuilder memberInfo(String memberId) {
		return MemberInfo.builder()
				.id(memberId)
				.gender(MemberGender.NORMAL);
	}


	//===== member 보조 메서드 =====
	private static MemberInfo	getMemberInfo() {
		return MemberInfo.builder()
				.id(MEMBER_ID)
				.gender(GENDER)
				.loginAt(LAST_LOGIN_DATE)
				.build();
	}

}