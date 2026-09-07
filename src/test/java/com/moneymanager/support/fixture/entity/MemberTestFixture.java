package com.moneymanager.support.fixture.entity;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.enums.MemberStatus;
import com.moneymanager.member.domain.enums.MemberType;

import java.time.LocalDateTime;

import static com.moneymanager.support.data.MemberTestData.*;

public final class MemberTestFixture {

	private String id = DEFAULT_ID;
	private String number = DEFAULT_NUMBER;
	private String username = DEFAULT_USERNAME;
	private String password = DEFAULT_PASSWORD;
	private String name = DEFAULT_NAME;
	private String birthdate =  DEFAULT_BIRTHDATE;
	private String nickname = DEFAULT_NICKNAME;
	private String email = DEFAULT_EMAIL;
	private String role =  DEFAULT_ROLE;
	private MemberType type = DEFAULT_TYPE;
	private MemberStatus status = DEFAULT_STATUS;
	private LocalDateTime createAt = DEFAULT_CREATE_DATE;
	private LocalDateTime deleteAt;

	//회원 부가정보
	private MemberInfoTestFixture memberInfo;

	private MemberTestFixture() {}

	public static MemberTestFixture builder() {
		return new MemberTestFixture();
	}

	public MemberTestFixture id(String id) {
		this.id = id;

		return this;
	}

	public MemberTestFixture number(String number) {
		this.number = number;

		return this;
	}

	public MemberTestFixture username(String username) {
		this.username = username;

		return this;
	}

	public MemberTestFixture nickName(String nickname) {
		this.nickname = nickname;

		return this;
	}

	public MemberTestFixture email(String email) {
		this.email = email;

		return this;
	}

	public MemberTestFixture withMemberInfo(MemberInfoTestFixture info) {
		this.memberInfo = info;

		return this;
	}

	public Member build() {
		return  Member.of(
				id,
				number,
				username,
				password,
				name,
				birthdate,
				nickname,
				email,
				type,
				memberInfo == null ? null : memberInfo.build()
		);
	}

	public Member buildWithEncodePassword(String encodePassword) {
		return  Member.of(
				id,
				number,
				username,
				encodePassword,
				name,
				birthdate,
				nickname,
				email,
				type,
				memberInfo == null ? null : memberInfo.build()
		);
	}

}