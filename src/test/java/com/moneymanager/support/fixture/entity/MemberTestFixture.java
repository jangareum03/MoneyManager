package com.moneymanager.support.fixture.entity;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberInfo;
import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.domain.enums.MemberStatus;
import com.moneymanager.member.domain.enums.MemberType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static com.moneymanager.support.data.MemberTestData.*;

public final class MemberTestFixture {

	private String id = DEFAULT_ID;
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

	//회원 부가정보
	private MemberGender gender =  DEFAULT_GENDER;
	private String profile;
	private Long point = 0L;
	private Long consecutiveDays = 0L;
	private Integer imageLimit = 1;
	private Integer failureCount = 1;
	private LocalDateTime loginAt;
	private LocalDateTime deleteAt;

	private MemberTestFixture() {}

	public static MemberTestFixture builder() {
		return new MemberTestFixture();
	}

	public MemberTestFixture id(String id) {
		this.id = id;

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

	public Member build(PasswordEncoder passwordEncoder) {
		return  Member.of(
				id, username, passwordEncoder.encode(password), name, birthdate, nickname, email, type, gender
		);
	}

	public Member buildExisting(String id, PasswordEncoder passwordEncoder) {
		return Member.restore(
				id, username, passwordEncoder.encode(password),
				name, birthdate, nickname, email, role, type, status,
				createAt, deleteAt,
				MemberInfo.restore(id, gender, profile, point, consecutiveDays, imageLimit, failureCount, loginAt)
		);
	}

}