package com.moneymanager.member.repository;


import com.moneymanager.support.RepositoryIntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : MemberRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-09-22<br>
 * 역할             : MemberRepository 통합 테스트
 * </p>
 */
@Transactional
@DisplayName("MemberRepository 테스트")
class MemberRepositoryIT extends RepositoryIntegrationTest {

    @Autowired
    private MemberRepository target;

    @Nested
    @DisplayName("아이디 존재 여부 확인 메서드는")
    class ExistsUsernameTest {

        @Test
        @DisplayName("가입된 아이디면 1을 반환한다.")
        void returnsOne_whenUsernameExist() {
        	//given
            String username = MemberTestData.DEFAULT_USERNAME;

        	//when
            Integer result = target.existsByUsername(username);

        	//then
            assertThat(result).isEqualTo(1);
        }
        
        @ParameterizedTest
        @ValueSource(strings = {"test", "test12", "no-username"})
        @DisplayName("가입된 아이디가 아니면 0을 반환한다.")
        void returnZero_whenUsernameDoesNotExist(String username) {
            //when
            Integer result = target.existsByUsername(username);

            //then
            assertThat(result).isEqualTo(0);
        }

    }

}