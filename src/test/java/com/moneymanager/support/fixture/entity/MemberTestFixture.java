package com.moneymanager.support.fixture.entity;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.enums.MemberGender;

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
	private MemberGender gender = DEFAULT_GENDER;


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

	public MemberTestFixture password(String password) {
		this.password = password;

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

	public Member build() {
		return  Member.createForJoin(
				id,
				number,
				username,
				password,
				name,
				birthdate,
				nickname,
				email,
				gender
		);
	}

}