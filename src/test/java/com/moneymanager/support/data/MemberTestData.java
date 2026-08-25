package com.moneymanager.support.data;

import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.domain.enums.MemberStatus;
import com.moneymanager.member.domain.enums.MemberType;

import java.time.LocalDateTime;

public final class MemberTestData {

	private MemberTestData() {}

	//기본값
	public static final String DEFAULT_ID = "UCt01001";
	public static final String DEFAULT_USERNAME = "test123";
	public static final String DEFAULT_PASSWORD = "pw1234!!";
	public static final String DEFAULT_ROLE = "ROLE_USER";
	public static final String DEFAULT_NAME = "테스트";
	public static final String DEFAULT_EMAIL = "test@test.com";
	public static final String DEFAULT_BIRTHDATE = "20020202";
	public static final String DEFAULT_NICKNAME = "닉네임";
	public static final MemberGender DEFAULT_GENDER = MemberGender.NORMAL;
	public static final MemberType DEFAULT_TYPE = MemberType.COMMON;
	public static final MemberStatus DEFAULT_STATUS = MemberStatus.ACTIVE;
	public static final LocalDateTime DEFAULT_CREATE_DATE = LocalDateTime.of(2026, 6, 12, 7, 20);

	//선택값
	public static final LocalDateTime LAST_LOGIN_DATE = DEFAULT_CREATE_DATE.plusDays(3).plusHours(3);

	//타인의 회원정보
	public static final String OTHER_MEMBER_ID = "UCo02001";
	public static final String OTHER_USERNAME = "other007";
}
