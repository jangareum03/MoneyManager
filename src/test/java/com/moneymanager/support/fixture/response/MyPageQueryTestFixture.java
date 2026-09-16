package com.moneymanager.support.fixture.response;

import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.domain.enums.MemberType;
import com.moneymanager.member.domain.query.MyPageQuery;
import com.moneymanager.support.data.MemberTestData;

public final class MyPageQueryTestFixture {

    private MemberType type;
    private MemberGender gender;

    private MyPageQueryTestFixture() {}

    public static MyPageQueryTestFixture builder() {
        return new MyPageQueryTestFixture();
    }

    public MyPageQueryTestFixture type(MemberType type) {
        this.type = type;

        return this;
    }

    public MyPageQueryTestFixture gender(MemberGender gender) {
        this.gender = gender;

        return this;
    }

    public MyPageQuery build() {
        return MyPageQuery.of(
                MemberTestData.DEFAULT_ID,
                type.getValue(),
                MemberTestData.DEFAULT_PASSWORD,
                MemberTestData.DEFAULT_NAME,
                MemberTestData.DEFAULT_NICKNAME,
                gender.getValue(),
                MemberTestData.DEFAULT_EMAIL,
                MemberTestData.LAST_LOGIN_DATE,
                MemberTestData.DEFAULT_CREATE_DATE,
                1L
        );
    }

}