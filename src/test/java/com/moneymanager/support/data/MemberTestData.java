package com.moneymanager.support.data;

import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.domain.enums.MemberStatus;
import com.moneymanager.member.domain.enums.MemberType;

import java.time.LocalDateTime;

public final class MemberTestData {

	private MemberTestData() {}

	public static final String MEMBER_ID = "UCt01001";
	public static final String USERNAME = "test123";
	public static final String PASSWORD = "pw1234!!";
	public static final String ROLE = "ROLE_USER";

	public static final String NAME = "테스트";
	public static final String EMAIL = "test@test.com";
	public static final String BIRTH_DATE = "20020202";
	public static final String NICK_NAME = "닉네임";
	public static final MemberGender GENDER = MemberGender.NORMAL;
	public static final MemberType TYPE = MemberType.COMMON;
	public static final MemberStatus STATUS = MemberStatus.ACTIVE;
	public static final LocalDateTime CREATE_DATE = LocalDateTime.of(2026, 6, 12, 7, 20);
	public static final LocalDateTime LAST_LOGIN_DATE = CREATE_DATE.plusDays(3).plusHours(3);

}
