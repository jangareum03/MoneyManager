package com.moneymanager.member.service.command;

import com.moneymanager.global.config.MutableClock;
import com.moneymanager.member.domain.dto.request.MemberSignUpRequest;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberHistory;
import com.moneymanager.member.domain.entity.MemberInfo;
import com.moneymanager.member.domain.enums.HistoryType;
import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.domain.enums.MemberType;
import com.moneymanager.member.repository.MemberHistoryRepository;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.generator.RandomCodeGenerator;
import com.moneymanager.member.service.generator.UuidGenerator;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Named.named;
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

    MemberCommandService target;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    RandomCodeGenerator codeGenerator;

    @Mock
    UuidGenerator uuidGenerator;

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberHistoryRepository historyRepository;

    @Autowired
    Clock clock;

    @BeforeEach
    void setUp() {
        clock = new MutableClock();

        target = new MemberCommandService(
                memberRepository,
                historyRepository,
                clock,
                passwordEncoder,
                codeGenerator,
                uuidGenerator
        );
    }


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

                doThrow(DuplicateKeyException.class)
                        .doThrow(DuplicateKeyException.class)
                        .doNothing()
                        .when(memberRepository)
                        .insert(any(Member.class));
            	
            	//when
                target.save(member);
            	
            	//then
            	verify(memberRepository, times(3)).insert(member);
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

            doThrow(DuplicateKeyException.class)
                    .doThrow(DuplicateKeyException.class)
                    .doThrow(DuplicateKeyException.class)
                    .when(memberRepository)
                    .insert(member);

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
            when(uuidGenerator.generate())
                    .thenReturn("id");

            when(codeGenerator.generateAlphanumeric(11))
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
            assertThat(result.getId()).isEqualTo("id");

            assertThat(result.getMemberNumber()).isEqualTo("Mnumber");
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


    @Nested
    @DisplayName("회원정보 수정할 때")
    class UpdateMember {

        String memberId = MemberTestData.DEFAULT_ID;
        
        @Nested
        @DisplayName("성공")
        class Success {
            
            @Test
            @DisplayName("비밀번호 수정 성공하면 내역 작성 메서드을 호출한다.")
            void savesMemberHistory_whenPasswordUpdateSucceeds() {
            	//given
                String newPassword = "password";
                String encodePassword = "encodePassword";

                when(passwordEncoder.encode(newPassword))
                        .thenReturn(encodePassword);

                when(memberRepository.updatePassword(memberId, encodePassword))
                        .thenReturn(true);
            	
            	//when
                assertDoesNotThrow(() -> target.updatePassword(memberId, newPassword));
            	
            	//then
                ArgumentCaptor<MemberHistory>  captor = ArgumentCaptor.forClass(MemberHistory.class);

            	verify(historyRepository).insertHistory(captor.capture());

                MemberHistory history = captor.getValue();

                assertThat(history.getMemberId()).isEqualTo(memberId);
                assertThat(history.getType()).isSameAs(HistoryType.UPDATE);
                assertThat(history.getItem()).isEqualTo("비밀번호");
                assertThat(history.getBeforeInfo()).isNull();
                assertThat(history.getAfterInfo()).isNull();
            }

            @Test
            @DisplayName("이메일 수정 성공하면 내역 작성 메서드을 호출한다.")
            void savesMemberHistory_whenEmailUpdateSucceeds() {
                //given
                String oldEmail = "old-email@test.com";
                String newEmail = "new-email@test.com";

                when(memberRepository.updateEmail(memberId, newEmail))
                        .thenReturn(true);

                //when
                assertDoesNotThrow(() -> target.updateEmail(memberId, oldEmail, newEmail));

                //then
                ArgumentCaptor<MemberHistory>  captor = ArgumentCaptor.forClass(MemberHistory.class);

                verify(historyRepository).insertHistory(captor.capture());

                MemberHistory history = captor.getValue();

                assertThat(history.getMemberId()).isEqualTo(memberId);
                assertThat(history.getType()).isSameAs(HistoryType.UPDATE);
                assertThat(history.getItem()).isEqualTo("이메일");
                assertThat(history.getBeforeInfo()).contains("*");
                assertThat(history.getAfterInfo()).contains("*");
            }

            @Test
            @DisplayName("이름 수정 성공하면 내역 작성 메서드을 호출한다.")
            void savesMemberHistory_whenNameUpdateSucceeds() {
                //given
                String oldName = "old-name";
                String newName = "new-name";

                when(memberRepository.updateName(memberId, newName))
                        .thenReturn(true);

                //when
                assertDoesNotThrow(() -> target.updateName(memberId, oldName, newName));

                //then
                ArgumentCaptor<MemberHistory>  captor = ArgumentCaptor.forClass(MemberHistory.class);

                verify(historyRepository).insertHistory(captor.capture());

                MemberHistory history = captor.getValue();

                assertThat(history.getMemberId()).isEqualTo(memberId);
                assertThat(history.getType()).isSameAs(HistoryType.UPDATE);
                assertThat(history.getItem()).isEqualTo("이름");
                assertThat(history.getBeforeInfo()).isEqualTo(oldName);
                assertThat(history.getAfterInfo()).isEqualTo(newName);
            }

            @Test
            @DisplayName("성별 수정 성공하면 내역 작성 메서드을 호출한다.")
            void savesMemberHistory_whenGenderUpdateSucceeds() {
                //given
                MemberGender oldGender = MemberGender.NORMAL;
                MemberGender newGender = MemberGender.FEMALE;

                when(memberRepository.updateGender(memberId, newGender.getValue()))
                        .thenReturn(true);

                //when
                assertDoesNotThrow(() -> target.updateGender(memberId, oldGender, newGender));

                //then
                ArgumentCaptor<MemberHistory>  captor = ArgumentCaptor.forClass(MemberHistory.class);

                verify(historyRepository).insertHistory(captor.capture());

                MemberHistory history = captor.getValue();

                assertThat(history.getMemberId()).isEqualTo(memberId);
                assertThat(history.getType()).isSameAs(HistoryType.UPDATE);
                assertThat(history.getItem()).isEqualTo("성별");
                assertThat(history.getBeforeInfo()).isEqualTo(oldGender.getValue());
                assertThat(history.getAfterInfo()).isEqualTo(newGender.getValue());
            }

            @Test
            @DisplayName("프로필 수정 성공하면 내역 작성 메서드을 호출한다.")
            void savesMemberHistory_whenProfileUpdateSucceeds() {
                //given
                String oldProfile = "old.jpg";
                String newProfile = "new.png";

                when(memberRepository.updateProfile(memberId, newProfile))
                        .thenReturn(true);

                //when
                assertDoesNotThrow(() -> target.updateProfile(memberId, oldProfile, newProfile));

                //then
                ArgumentCaptor<MemberHistory>  captor = ArgumentCaptor.forClass(MemberHistory.class);

                verify(historyRepository).insertHistory(captor.capture());

                MemberHistory history = captor.getValue();

                assertThat(history.getMemberId()).isEqualTo(memberId);
                assertThat(history.getType()).isSameAs(HistoryType.UPDATE);
                assertThat(history.getItem()).isEqualTo("프로필");
                assertThat(history.getBeforeInfo()).isEqualTo(oldProfile);
                assertThat(history.getAfterInfo()).isEqualTo(newProfile);
            }

        }
        
        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("비밀번호 수정 실패하면 예외를 발생시킨다.")
            void throwsException_whenPasswordSaveFails() {
            	//given
                when(passwordEncoder.encode("password"))
                        .thenReturn("encode-password");
                when(memberRepository.updatePassword(memberId, "encode-password"))
                        .thenReturn(false);

            	//when
                Throwable throwable = catchThrowable(() -> target.updatePassword(memberId, "password"));

            	//then
            	ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(DATA_PERSISTENCE_FAILED)
                        .hasWork("회원 정보 수정")
                        .hasTarget(Member.class)
                        .hasValue("password", "p******d")
                        .hasCauseMessage("비밀번호 수정 실패");
            }

            @Test
            @DisplayName("이메일 수정 실패하면 예외를 발생시킨다.")
            void throwsException_whenEmailSaveFails() {
                //given
                when(memberRepository.updateEmail(memberId, "email@test.com"))
                        .thenReturn(false);

                //when
                Throwable throwable = catchThrowable(() -> target.updateEmail(memberId, "old@test.com", "email@test.com"));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(DATA_PERSISTENCE_FAILED)
                        .hasWork("회원 정보 수정")
                        .hasTarget(Member.class)
                        .hasValue("email", "e****@test.com")
                        .hasCauseMessage("이메일 수정 실패");
            }

            @Test
            @DisplayName("이름 수정 실패하면 예외를 발생시킨다.")
            void throwsException_whenNameSaveFails() {
                //given
                when(memberRepository.updateName(memberId, "이름"))
                        .thenReturn(false);

                //when
                Throwable throwable = catchThrowable(() -> target.updateName(memberId, "철수", "이름"));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(DATA_PERSISTENCE_FAILED)
                        .hasWork("회원 정보 수정")
                        .hasTarget(Member.class)
                        .hasValue("name", "이*")
                        .hasCauseMessage("이름 수정 실패");
            }

            @Test
            @DisplayName("성별 수정 실패하면 예외를 발생시킨다.")
            void throwsException_whenGenderSaveFails() {
                //given
                when(memberRepository.updateGender(memberId, MemberGender.FEMALE.getValue()))
                        .thenReturn(false);

                //when
                Throwable throwable = catchThrowable(() -> target.updateGender(memberId, MemberGender.NORMAL, MemberGender.FEMALE));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(DATA_PERSISTENCE_FAILED)
                        .hasWork("회원 정보 수정")
                        .hasTarget(Member.class)
                        .hasValue("gender", "F")
                        .hasCauseMessage("성별 수정 실패");
            }

            @Test
            @DisplayName("프로필 수정 실패하면 예외를 발생시킨다.")
            void throwsException_whenProfileSaveFails() {
                //given
                when(memberRepository.updateProfile(memberId, "profile"))
                        .thenReturn(false);

                //when
                Throwable throwable = catchThrowable(() -> target.updateProfile(memberId, "before", "profile"));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(DATA_PERSISTENCE_FAILED)
                        .hasWork("회원 정보 수정")
                        .hasTarget(Member.class)
                        .hasValue("profile", "profile")
                        .hasCauseMessage("프로필 수정 실패");
            }

        }
        
    }


    @Nested
    @DisplayName("이메일 마스킹 처리할 때")
    class MaskedEmail {
        
        @ParameterizedTest
        @MethodSource("validEmailLengthLessThan5")
        @DisplayName("로컬 파트 길이가 5이하면 두번째 글자부터 마스킹 처리한다.")
        void returnsMaskedEmail_whenLocalPartLengthIsFiveOrLess(String email, String expected) {
        	//when
            String result = target.getMaskedEmail(email);
        	
        	//then
            assertThat(result).isEqualTo(expected);
        }

        static Stream<Arguments> validEmailLengthLessThan5() {
            return Stream.of(
                    Arguments.of(
                            named("1글자인 경우", "a@test.com"),
                            "*@test.com"
                    ),
                    Arguments.of(
                            named("2글자인 경우", "ab@test.com"),
                            "a*@test.com"
                    ),
                    Arguments.of(
                            named("3글자인 경우", "abc@test.com"),
                            "a**@test.com"
                    ),
                    Arguments.of(
                            named("4글자인 경우", "abcd@test.com"),
                            "a***@test.com"
                    ),
                    Arguments.of(
                            named("5글자인 경우(경계값)", "abced@test.com"),
                            "a****@test.com"
                    )
            );
        }

        @ParameterizedTest
        @MethodSource("validEmailLengthLeast6")
        @DisplayName("로컬 파트 길이가 6이상 이면 다섯번째 글자부터 마스킹 처리한다.")
        void returnsMaskedEmail_whenLocalPartLengthIsGreaterThanFive(String email, String expected) {
        	//when
            String result = target.getMaskedEmail(email);
        	
        	//then
        	assertThat(result).isEqualTo(expected);
        }

        static Stream<Arguments> validEmailLengthLeast6() {
            return Stream.of(
                    Arguments.of(
                            named("6글자인 경우(경계값)", "abcdef@test.com"),
                            "abcd**@test.com"
                    ),
                    Arguments.of(
                            named("7글자인 경우", "abcdefg@test.com"),
                            "abcd***@test.com"
                    ),
                    Arguments.of(
                            named("10글자인 경우", "a1b2c3d4f5@test.com"),
                            "a1b2******@test.com"
                    )
            );
        }
        
    }

}