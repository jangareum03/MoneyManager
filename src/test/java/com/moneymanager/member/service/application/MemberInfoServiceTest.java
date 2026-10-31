package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.member.domain.dto.response.UsernameAvailabilityResponse;
import com.moneymanager.member.service.validation.MemberDuplicateValidator;
import com.moneymanager.member.service.validation.MemberFieldValidator;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : MemberInfoServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-10-30<br>
 * 역할             : MemberInfoService 단위 테스트
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class MemberInfoServiceTest {

    @Mock
    private MemberFieldValidator fieldValidator;

    @Mock
    private MemberDuplicateValidator duplicateValidator;

    @InjectMocks
    private MemberInfoService target;

    @Nested
    @DisplayName("아이디 사용 가능 확인 메서드는")
    class VerificationUsername {

        @Test
        @DisplayName("요청값이 유효하지 않으면 예외가 발생한다.")
        void throwsException_whenRequestIsInvalid() {
        	//given
            String username = "test";

            willThrow(ApplicationException.class)
                    .given(fieldValidator)
                    .validateUsername(username);
        	
        	//when
            assertThatThrownBy(() -> {
                target.processUsernameVerification(username);
            })
                    .isInstanceOf(ApplicationException.class);
        	
        	//then
        	verifyNoInteractions(duplicateValidator);
        }
        
        @Test
        @DisplayName("사용 가능한 아이디면 사용 가능을 반환한다.")
        void returnsResponse_whenUsernameIsValid() {
        	//given
            String username = MemberTestData.DEFAULT_USERNAME;

            willDoNothing()
                    .given(fieldValidator)
                    .validateUsername(username);

            given(duplicateValidator.existsUsername(username))
                    .willReturn(false);

        	//when
            UsernameAvailabilityResponse result =
                    target.processUsernameVerification(username);
        	
        	//then
        	assertThat(result)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("available", true);
        }

        @Test
        @DisplayName("이미 존재하는 아이디면 사용 불가를 반환한다.")
        void returnsResponse_whenUsernameIsDuplicate() {
            //given
            String username = MemberTestData.DEFAULT_USERNAME;

            willDoNothing()
                    .given(fieldValidator)
                    .validateUsername(username);

            given(duplicateValidator.existsUsername(username))
                    .willReturn(true);

            //when
            UsernameAvailabilityResponse result =
                    target.processUsernameVerification(username);

            //then
            assertThat(result)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("available", false);
        }

    }

}