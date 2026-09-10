package com.moneymanager.member.repository;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : MemberTokenRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 11<br>
 * 설명              : MemberTokenRepository 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 9. 11</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class MemberTokenRepositoryIT extends IntegrationTest {

    @Autowired
    MemberTokenRepository target;

    @Nested
    @DisplayName("회원번호로 Refresh 토큰 조회할 때")
    class FindRefreshToken {
        
        @Test
        @DisplayName("회원번호가 존재하면 해당하는 토큰을 반환한다.")
        void returnsToken_whenMemberExists() {
        	//given
            Member member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .build();

            insertMember(member);

            target.saveToken(member.getId(), "refreshToken", new Date());
        	
        	//when
            String result = target.findRefreshTokenByMemberNumber(member.getMemberNumber());
        	
        	//then
        	assertThat(result)
                    .isNotNull()
                    .isEqualTo("refreshToken");
        }

        @Test
        @DisplayName("회원번호가 존재하지 않으면 null을 반환한다.")
        void returnsNull_whenUserDoesNotExist() {
            //given
            Member member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .build();

            insertMember(member);

            //when
            String result = target.findRefreshTokenByMemberNumber(member.getMemberNumber());

            //then
            assertThat(result).isNull();
        }

    }
    
    
    @Nested
    @DisplayName("Refresh Token 삭제할 때")
    class Delete {

        Member member;
        
        @BeforeEach
        void setUp() {
             member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .build();

            insertMember(member);

        }
        
        @Test
        @DisplayName("토큰이 존재하면 삭제 후 1을 반환한다.")
        void deletesToken_whenTokenExists() {
        	//given
            target.saveToken(member.getId(), "refreshToken", new Date());
        	
        	//when
        	int result = target.deleteRefreshToken("refreshToken");
            
        	//then
        	assertThat(result).isEqualTo(1);
        }
        
        @Test
        @DisplayName("토큰이 존재하지 않으면 0을 반환한다.")
        void doesNothing_whenTokenDoesNotExist() {
            //when
            int result = target.deleteRefreshToken("refreshToken");

            //then
            assertThat(result).isZero();
        }

    }

}