package com.moneymanager.support.fixture.entity;

import com.moneymanager.member.domain.entity.MemberInfo;
import com.moneymanager.member.domain.enums.MemberGender;

import java.time.LocalDateTime;

import static com.moneymanager.support.data.MemberTestData.DEFAULT_GENDER;
import static com.moneymanager.support.data.MemberTestData.DEFAULT_ID;

public final class MemberInfoTestFixture {

    private String id = DEFAULT_ID;
    private MemberGender gender =  DEFAULT_GENDER;
    private String profile;
    private Long point = 0L;
    private Long consecutiveDays = 0L;
    private Integer imageLimit = 1;
    private Integer failureCount = 1;
    private LocalDateTime loginAt;

    private MemberInfoTestFixture() {}

    public static MemberInfoTestFixture builder() {
        return new MemberInfoTestFixture();
    }

    public MemberInfoTestFixture id(String id) {
        this.id = id;

        return this;
    }

    public MemberInfoTestFixture profile(String profile) {
        this.profile = profile;

        return this;
    }

    public MemberInfo build() {
        return MemberInfo.of(id, gender);
    }

}