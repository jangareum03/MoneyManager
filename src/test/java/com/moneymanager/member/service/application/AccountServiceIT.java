package com.moneymanager.member.service.application;

import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : AccountServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 10<br>
 * 설명              : AccountService 클래스 요청을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 9. 10</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class AccountServiceIT extends IntegrationTest {

    @Autowired
    AccountService target;

    @Nested
    @DisplayName("로그인 진행할 때")
    class Login {
        
        @Test
        @DisplayName("회원가입한 사용자면 회원정보를 반환한다.")
        void returnsUserInfo_whenUserExists() {
        	//given
            String password = MemberTestData.DEFAULT_PASSWORD;

            Member member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .buildWithEncodePassword(passwordEncoder.encode(password));

            insertMember(member);

        	//when
            CustomUserDetails result = target.login(member.getUsername(), password);

        	//then
        	assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(member.getUsername());
            assertThat(result.getPassword()).isEqualTo(member.getPassword());
            assertThat(result.getMemberNumber()).isEqualTo(member.getMemberNumber());
        }
        
        @Test
        @DisplayName("회원가입하지 않은 사용자면 로그인을 실패한다.")
        void throwsException_whenUserDoesNotExist() {
        	//given
            String username = MemberTestData.DEFAULT_USERNAME;
            String password = MemberTestData.DEFAULT_PASSWORD;
        	
        	//when
            assertThatThrownBy(() -> target.login(username, password))
                    .isInstanceOf(UsernameNotFoundException.class);
        }

    }

}