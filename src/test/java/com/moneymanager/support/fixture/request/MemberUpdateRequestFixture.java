package com.moneymanager.support.fixture.request;

import com.moneymanager.member.domain.dto.request.MemberUpdateRequest;
import com.moneymanager.support.data.MemberTestData;

public final class MemberUpdateRequestFixture {

    private MemberUpdateRequestFixture() {}

    public static MemberUpdateRequest.MemberUpdateRequestBuilder builder() {
        return MemberUpdateRequest.builder()
                .name(MemberTestData.DEFAULT_NAME)
                .gender(MemberTestData.DEFAULT_GENDER.getValue())
                .password(MemberTestData.DEFAULT_PASSWORD);
    }

}