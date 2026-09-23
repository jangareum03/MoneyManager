package com.moneymanager.member.repository;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.enums.MemberStatus;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : MemberRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 22<br>
 * 설명              : MemberRepository 통합 테스트
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
 * 		 	  <td>26. 9. 22</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@Transactional
class MemberRepositoryIT extends IntegrationTest {

    @Autowired
    private MemberRepository target;

    @Nested
    @DisplayName("아이디 조회하면")
    class FindUsernameById {
        
        @Test
        @DisplayName("회원번호에 해당하는 아이디를 반환한다.")
        void findUsername_whenMemberExists() {
        	//given
            Member member = MemberTestFixture.builder().build();

            insertMember(member);
        	
        	//when
            String result = target.findUsernameByMemberId(member.getId());
        	
        	//then
        	assertThat(result)
                    .isNotNull()
                    .isEqualTo(member.getUsername());
        }
        
    }

    @Nested
    @DisplayName("비밀번호 조회하면")
    class FindPasswordById {

        @Test
        @DisplayName("회원번호에 해당하는 암호화 비밀번호를 반환한다.")
        void findUsername_whenMemberExists() {
            //given
            String password = MemberTestData.DEFAULT_PASSWORD;

            Member member = MemberTestFixture.builder()
                    .password(passwordEncoder.encode(password))
                    .build();
            insertMember(member);

            String memberId = member.getId();

            //when
            String result = target.findPasswordByMemberId(memberId);

            //then
            assertThat(result).isNotEqualTo(password);
            assertThat(passwordEncoder.matches(password, result)).isTrue();
        }
        
        @Test
        @DisplayName("회원번호가 없으면 예외를 전파한다.")
        void throwsException_whenMemberIdDoesNotExist() {
        	//given
            String memberId = "no-member";
        	
        	//when
            assertThatThrownBy(() -> target.findPasswordByMemberId(memberId))
                    .isInstanceOf(EmptyResultDataAccessException.class);
        }

    }


    @Nested
    @DisplayName("회원을 탈퇴상태로 수정하면")
    class UpdateToWithdrawn {

        Member member = MemberTestFixture.builder().build();

        @BeforeEach
        void setUp() {
            member = MemberTestFixture.builder().build();

            insertMember(member);
        }
        
        @Test
        @DisplayName("ACTIVE면 회원의 상태와 탈퇴일을 변경한다.")
        void updateStatusAndResignedAt_whenMemberExists() {
            //기존 회원 상태 확인
            assertThat(member.getStatus()).isSameAs(MemberStatus.ACTIVE);
            assertThat(member.getDeletedAt()).isNull();

        	//when
            target.updateStatusToWithdrawn(member);

        	//then
            Member saved = target.findById(member.getId());

            assertThat(saved.getStatus()).isEqualTo(MemberStatus.DELETED);
            assertThat(saved.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("ACTIVE가 아니면 상태가 변경되지 않는다.")
        void doesNothing_whenMemberIsNotActive() {
        	//given
            jdbcTemplate.update(
                    "UPDATE member SET status = 'L' WHERE id = ?",
                    member.getId()
            );
        	
        	//when
            target.updateStatusToWithdrawn(member);
        	
        	//then
        	assertThat(member.getStatus()).isNotSameAs(MemberStatus.DELETED);
        }

    }

}