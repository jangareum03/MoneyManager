package com.moneymanager.global.security;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.security<br>
 * 파일이름       : CustomAuthenticationProviderIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 8<br>
 * 설명              : EmailVerificationRedisKey 클래스 로직을 검증하는 통합 테스트 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 * 		<thead>
 * 		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 * 		 	  	<td>날짜</td>
 * 		 	  	<td>작성자</td>
 * 		 	  	<td>변경내용</td>
 * 		 	</tr>
 * 		</thead>
 * 		<tbody>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 9. 8</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class CustomAuthenticationProviderIT extends IntegrationTest {

    @Autowired
    CustomAuthenticationProvider target;

    @Nested
    @DisplayName("사용자 조회할 때")
    class Authenticate {

        Member member;

        @BeforeEach
        void setUp() {
            member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .buildWithEncodePassword(passwordEncoder.encode(MemberTestData.DEFAULT_PASSWORD));

            insertMember(member);
        }

        @Test
        @DisplayName("존재하는 회원이면 토큰을 발급한다.")
        void generatesToken_whenUserExists() {
        	//given
            Authentication authentication = new UsernamePasswordAuthenticationToken(MemberTestData.DEFAULT_USERNAME, MemberTestData.DEFAULT_PASSWORD);

        	//when
            Authentication result = target.authenticate(authentication);

        	//then
            assertThat(result).isNotNull();
            assertThat(result.getCredentials()).isNull();
            assertThat(result.isAuthenticated()).isTrue();
            assertThat(result.getAuthorities()).isNotEmpty();

            assertThat(result.getName()).isEqualTo(MemberTestData.DEFAULT_USERNAME);
            assertInstanceOf(
                    UsernamePasswordAuthenticationToken.class,
                    result
            );

            assertThat(result.getName()).isEqualTo(MemberTestData.DEFAULT_USERNAME);

        }
        
        @Test
        @DisplayName("존재하지 않은 회원이면 예외를 발생시킨다.")
        void throwsException_whenUserDoesNotExist() {
        	//given
            Authentication authentication = new UsernamePasswordAuthenticationToken("noExist", MemberTestData.DEFAULT_PASSWORD);
        	
        	//when
            assertThatThrownBy(() -> target.authenticate(authentication))
                    .isInstanceOf(UsernameNotFoundException.class);
        }

    }

    

}