package com.moneymanager.member.service.validation;

import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.validation<br>
 * 파일이름       : MemberDuplicateValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-10-30<br>
 * 역할             : MemberDuplicateValidator 단위 테스트
 * </p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MemberDuplicateValidator 테스트")
class MemberDuplicateValidatorTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberDuplicateValidator target;

    @Nested
    @DisplayName("아이디 중복 메서드는")
    class ExistenceUsername {

        @Test
        @DisplayName("아이디가 존재하면 true를 반환한다.")
        void returnTure_whenUsernameExist() {
        	//given
            String username = MemberTestData.DEFAULT_USERNAME;
            
            given(memberRepository.existsByUsername(username))
                    .willReturn(1);
        	
        	//when
            boolean result = target.existsUsername(username);
        	
        	//then
        	assertThat(result).isTrue();
        }
        
        @Test
        @DisplayName("아이디가 존재하지 않으면 false를 반환한다.")
        void returnsFalse_whenUsernameDoesNotExist() {
            //given
            String username = MemberTestData.DEFAULT_USERNAME;

            given(memberRepository.existsByUsername(username))
                    .willReturn(0);

            //when
            boolean result = target.existsUsername(username);

            //then
            assertThat(result).isFalse();
        }

    }

}