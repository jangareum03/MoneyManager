package com.moneymanager.member.service.command;

import com.moneymanager.member.domain.dto.request.MemberSignUpRequest;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberInfo;
import com.moneymanager.member.domain.enums.MemberType;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.generator.MemberNumberGenerator;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.moneymanager.global.exception.code.ErrorCode.CONSTRAINT_VIOLATION;
import static com.moneymanager.global.exception.code.ErrorCode.DATA_INTEGRITY;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.command<br>
 * 파일이름       : MemberCommandServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 4.<br>
 * 설명              :
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
 * 		 	  <td>26. 9. 4.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class MemberCommandServiceTest {

    @InjectMocks
    MemberCommandService target;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    MemberNumberGenerator numberGenerator;

    @Mock
    MemberRepository memberRepository;


    @Nested
    @DisplayName("회원 저장할 때")
    class Save {

        @Nested
        @DisplayName("성공")
        class Success {
            
            @Test
            @DisplayName("정상적인 member면 member 저장 메서드를 호출한다.")
            void insertsMember_whenMemberIsValid() {
            	//given
                Member member = MemberTestFixture.builder()
                        .withMemberInfo(MemberInfoTestFixture.builder())
                        .build();
            	
            	//when
                target.save(member);
            	
            	//then
            	verify(memberRepository, times(1)).insert(member);
            }
            
            @Test
            @DisplayName("정상적인 member info면 member info 저장 메서드를 호출한다.")
            void insertsMemberInfo_whenMemberInfoIsValid() {
                //given
                Member member = MemberTestFixture.builder()
                        .withMemberInfo(MemberInfoTestFixture.builder())
                        .build();

                //when
                target.save(member);

                //then
                InOrder inOrder = Mockito.inOrder(memberRepository);

                inOrder.verify(memberRepository).insert(member);
                inOrder.verify(memberRepository).insert(member.getInfo());
            }

            @Test
            @DisplayName("회원번호가 중복이면 3번까지 새로운 값으로 저장을 다시 진행한다.")
            void retriesUpToThreeTimes_whenUserNumberIsDuplicated() {
            	//given
                Member member = MemberTestFixture.builder()
                        .withMemberInfo(MemberInfoTestFixture.builder())
                        .build();

                when(numberGenerator.generate())
                        .thenReturn("NEW-001", "NEW-002");

                doThrow(DuplicateKeyException.class)
                        .doThrow(DuplicateKeyException.class)
                        .doNothing()
                        .when(memberRepository)
                        .insert(any(Member.class));
            	
            	//when
                target.save(member);
            	
            	//then
                assertThat(member.getMemberNumber()).isEqualTo("NEW-002");

            	verify(memberRepository, times(3)).insert(member);
                verify(numberGenerator, times(2)).generate();
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {
            
            @Test
            @DisplayName("memberInfo가 null이면 예외를 발생시킨다.")
            void throwsException_whenMemberInfoIsNull() {
            	//given
                Member member = MemberTestFixture.builder()
                        .withMemberInfo(null)
                        .build();
            	
            	//when
                Throwable throwable = catchThrowable(() ->  target.save(member));
            	
            	//then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(DATA_INTEGRITY)
                        .hasWork("Member 저장")
                        .hasCauseMessage("Member 와 MemberInfo 1:1 관계 필요")
                        .hasTarget(MemberInfo.class)
                        .hasValue(null);
            }
            
            @Test
            @DisplayName("member 저장에 실패하면 member info 저장 메서드는 호출하지 않는다.")
            void doesNotInsertMemberInfo_whenMemberInsertionFails() {
                //given
                Member member = MemberTestFixture.builder()
                        .withMemberInfo(MemberInfoTestFixture.builder())
                        .build();

                doThrow(new DataAccessException("저장 실패") {})
                        .when(memberRepository)
                        .insert(eq(member));
            	
            	//when
                assertThatThrownBy(() -> target.save(member))
                        .isInstanceOf(DataAccessException.class);
            	
            	//then
            	verify(memberRepository, never()).insert(member.getInfo());
            }
        }

        @Test
        @DisplayName("회원번호가 재생성이 3번 초과하면 예외를 발생시킨다.")
        void throwsException_whenUserNumberRegenerationExceedsThreeTimes() {
            //given
            Member member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .build();

            when(numberGenerator.generate())
                    .thenReturn("NEW-001", "NEW-002");

            doThrow(DuplicateKeyException.class)
                    .doThrow(DuplicateKeyException.class)
                    .doThrow(DuplicateKeyException.class)
                    .when(memberRepository)
                    .insert(any(Member.class));

            //when
            Throwable throwable = catchThrowable(() ->  target.save(member));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(CONSTRAINT_VIOLATION)
                    .hasWork("Member 저장")
                    .hasCauseMessage("회원번호 중복")
                    .hasTarget(Member.class);
        }

    }


    @Nested
    @DisplayName("Member 생성할 때")
    class CreateMember {
        
        @Test
        @DisplayName("회원가입 요청정보로 생성한다.")
        void creates_whenRegistrationRequestIsValid() {
        	//given
            when(numberGenerator.generate())
                    .thenReturn("number");

            when(passwordEncoder.encode(MemberTestData.DEFAULT_PASSWORD))
                    .thenReturn("encodePassword");

            MemberSignUpRequest request = MemberSignUpRequest.builder()
                    .username(MemberTestData.DEFAULT_USERNAME)
                    .password(MemberTestData.DEFAULT_PASSWORD)
                    .name(MemberTestData.DEFAULT_NAME)
                    .birthdate(MemberTestData.DEFAULT_BIRTHDATE)
                    .nickname(MemberTestData.DEFAULT_NICKNAME)
                    .email(MemberTestData.DEFAULT_EMAIL)
                    .gender(MemberTestData.DEFAULT_GENDER.getValue())
                    .build();
        	
        	//when
            Member result = target.create(request);
        	
        	//the
            assertThat(result.getId()).isNotNull();

            assertThat(result.getMemberNumber()).isEqualTo("number");
        	assertThat(result.getUsername()).isEqualTo(request.getUsername());
        	assertThat(result.getPassword()).isEqualTo("encodePassword");
            assertThat(result.getName()).isEqualTo(request.getName());
            assertThat(result.getBirthdate()).isEqualTo(request.getBirthdate());
            assertThat(result.getNickname()).isEqualTo(request.getNickname());
            assertThat(result.getEmail()).isEqualTo(request.getEmail());
            assertThat(result.getType()).isEqualTo(MemberType.COMMON);

            assertThat(result.getInfo().getGender()).isEqualTo(MemberTestData.DEFAULT_GENDER);

        }
        
    }

}