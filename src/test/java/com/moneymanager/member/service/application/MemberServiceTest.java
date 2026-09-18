package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.security.CurrentUser;
import com.moneymanager.member.domain.dto.request.EmailUpdateRequest;
import com.moneymanager.member.domain.dto.request.MemberUpdateRequest;
import com.moneymanager.member.domain.dto.response.MemberUpdateResponse;
import com.moneymanager.member.domain.dto.response.MyPageResponse;
import com.moneymanager.member.domain.dto.response.SideBarUser;
import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.domain.enums.MemberType;
import com.moneymanager.member.domain.query.MyPageQuery;
import com.moneymanager.member.service.command.MemberCommandService;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.member.service.validation.MemberValidator;
import com.moneymanager.redis.service.EmailVerificationService;
import com.moneymanager.redis.service.SideBarMemberService;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.response.MyPageQueryTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.moneymanager.global.exception.code.ErrorCode.INTERVAL_SERVER_ERROR;
import static com.moneymanager.global.exception.code.ErrorCode.REQUIRED_NOT_EXIST;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : MemberServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 6<br>
 * 설명              : MemberService 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 9. 6</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService target;

    @Mock
    CurrentUser currentUser;

    @Mock
    MemberReadService memberReadService;

    @Mock
    MemberCommandService memberCommandService;

    @Mock
    MemberProfileImageService memberProfileImageService;

    @Mock
    SideBarMemberService sideBarMemberService;

    @Mock
    EmailVerificationService emailVerificationService;

    @Mock
    MemberValidator memberValidator;


    @Nested
    @DisplayName("사이드바 정보 저장할 때")
    class Sidebar {

        @Test
        @DisplayName("회원번호에 대한 정보가 있으면 Redis에 닉네임과 프로필 정보가 저장된다.")
        void savesMemberProfileToRedis_whenMemberExists() {
            //given
            String memberNumber = "memberNumber";
            SideBarUser sideBarUser = mock(SideBarUser.class);

            when(memberReadService.getSideBarUser(memberNumber))
                    .thenReturn(sideBarUser);

            //when
            assertDoesNotThrow(() -> target.processSaveSideBar(memberNumber));

            //then
            verify(sideBarMemberService).saveNickname(memberNumber, sideBarUser.getNickname());
            verify(sideBarMemberService).saveProfile(memberNumber, sideBarUser.getProfile());
        }

        @Test
        @DisplayName("회원번호에 대한 정보가 없으면 예외를 전파시킨다.")
        void throwsException_whenUserDoesNotExist() {
            //given
            String memberNumber = "memberNumber";

            when(memberReadService.getSideBarUser(memberNumber))
                    .thenThrow(ApplicationException.class);

            //when
            assertThatThrownBy(() -> target.processSaveSideBar(memberNumber))
                    .isInstanceOf(ApplicationException.class);
        }

    }


    @Nested
    @DisplayName("마이페이지 정보 조회할 때")
    class MyPage {

        MyPageQuery myPageQuery;

        @BeforeEach
        void setUp() {
            myPageQuery = MyPageQueryTestFixture.builder()
                    .type(MemberType.COMMON)
                    .gender(MemberGender.NORMAL)
                    .build();
        }

        @Test
        @DisplayName("회원 정보를 반환한다.")
        void returnsDefaultProfile_whenMemberExists() {
            //given
            when(currentUser.getMemberId())
                    .thenReturn(MemberTestData.DEFAULT_NUMBER);

            when(memberReadService.getMyProfile(MemberTestData.DEFAULT_NUMBER))
                    .thenReturn(myPageQuery);

            when(memberProfileImageService.get(MemberTestData.DEFAULT_NUMBER))
                    .thenReturn("member/profile.test.png");

            //when
            MyPageResponse result = target.getMemberProfile();

            //then
            assertThat(result.getType()).isEqualTo(MemberTestData.DEFAULT_TYPE);
            assertThat(result.getName()).isEqualTo(MemberTestData.DEFAULT_NAME);
            assertThat(result.getNickname()).isEqualTo(MemberTestData.DEFAULT_NICKNAME);
            assertThat(result.getGender()).isEqualTo(MemberTestData.DEFAULT_GENDER);
            assertThat(result.getEmail()).isEqualTo(MemberTestData.DEFAULT_EMAIL);
            assertThat(result.getProfile()).isEqualTo("member/profile.test.png");

            assertThat(result.getAttendanceDays()).isEqualTo("1");
        }

        @Test
        @DisplayName("날짜는 지정된 포맷으로 저장한다.")
        void returnsFormattedDate_whenMemberExists() {
            //given
            when(currentUser.getMemberId())
                    .thenReturn(MemberTestData.DEFAULT_NUMBER);

            when(memberReadService.getMyProfile(MemberTestData.DEFAULT_NUMBER))
                    .thenReturn(myPageQuery);

            when(memberProfileImageService.get(MemberTestData.DEFAULT_NUMBER))
                    .thenReturn("member/profile.test.png");

            //when
            MyPageResponse result = target.getMemberProfile();

            //then
            assertThat(result.getLastLogin()).isEqualTo("2026년 06월 15일 월요일");
            assertThat(result.getJoinDate()).isEqualTo("2026년 06월 12일");
        }

        @Test
        @DisplayName("인증된 사용자 조회가 실패하면 예외를 전파한다.")
        void throwsException_whenAuthenticationFails() {
            //given
            when(currentUser.getMemberId())
                    .thenThrow(ApplicationException.class);

            //when
            assertThatThrownBy(() -> target.getMemberProfile())
                    .isInstanceOf(ApplicationException.class);
        }

        @Test
        @DisplayName("회원정보 조회를 실패하면 예외를 전파한다.")
        void throwsException_whenMemberNotFound() {
            //given
            when(currentUser.getMemberId())
                    .thenReturn(MemberTestData.DEFAULT_NUMBER);

            when(memberReadService.getMyProfile(MemberTestData.DEFAULT_NUMBER))
                    .thenThrow(ApplicationException.class);

            //when
            assertThatThrownBy(() -> target.getMemberProfile())
                    .isInstanceOf(ApplicationException.class);
        }

    }


    @Nested
    @DisplayName("회원정보 수정할 때")
    class Update {

        MemberUpdateRequest request = mock(MemberUpdateRequest.class);
        MyPageQuery member = mock(MyPageQuery.class);

        @Nested
        @DisplayName("성공")
        class Success {

            @BeforeEach
            void setUp() {
                when(currentUser.getMemberId())
                        .thenReturn(MemberTestData.DEFAULT_NUMBER);
            }

            @Test
            @DisplayName("기존 이름과 다르면 이름을 수정한다.")
            void updateMemberName_whenNameIsDifferent() {
                //given
                when(request.getName())
                        .thenReturn("변경");

                when(memberReadService.getMyProfile(MemberTestData.DEFAULT_NUMBER))
                        .thenReturn(member);

                when(member.getName())
                        .thenReturn("기존");

                //when
                MemberUpdateResponse result = target.processMemberUpdate(request);

                //then
                assertThat(result.getType()).isEqualTo("name");
                assertThat(result.getValue()).isEqualTo("변경");
            }

            @Test
            @DisplayName("기존 성별과 다르면 이름을 수정한다.")
            void updateMemberName_whenGenderIsDifferent() {
                //given
                when(request.getGender())
                        .thenReturn("F");

                when(memberReadService.getMyProfile(MemberTestData.DEFAULT_NUMBER))
                        .thenReturn(member);

                when(member.getGender())
                        .thenReturn(MemberGender.MALE);

                //when
                MemberUpdateResponse result = target.processMemberUpdate(request);

                //then
                assertThat(result.getType()).isEqualTo("gender");
                assertThat(result.getValue()).isEqualTo("F");
            }

            @Test
            @DisplayName("기존 비밀번호과 다르면 이름을 수정한다.")
            void updateMemberName_whenPasswordIsDifferent() {
                //given
                when(request.getPassword())
                        .thenReturn("변경");

                when(memberReadService.getMyProfile(MemberTestData.DEFAULT_NUMBER))
                        .thenReturn(member);

                when(member.getPassword())
                        .thenReturn("기존");

                //when
                MemberUpdateResponse result = target.processMemberUpdate(request);

                //then
                assertThat(result.getType()).isEqualTo("password");
                assertThat(result.getValue()).isEqualTo("********");
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {
            
            @Test
            @DisplayName("요청 객체가 null이면 예외를 발생시킨다.")
            void throwException_whenRequestIsNull() {
            	//when
                Throwable throwable = catchThrowable(() -> target.processMemberUpdate(null));
            	
            	//then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(REQUIRED_NOT_EXIST)
                        .hasWork("회원 정보 수정")
                        .hasTarget(MemberUpdateRequest.class)
                        .hasMessageKey("member.update.failed");
            }
            
            @Test
            @DisplayName("수정 요청값이 모두 null이면 예외를 발생시킨다.")
            void throwException_whenAllUpdateFieldsAreNull() {
            	//given
                request = MemberUpdateRequest.of(null, null, null);

                when(currentUser.getMemberId())
                        .thenReturn(MemberTestData.DEFAULT_NUMBER);

                //when
                Throwable throwable = catchThrowable(() -> target.processMemberUpdate(request));

                //then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                        .hasErrorCode(INTERVAL_SERVER_ERROR)
                        .hasWork("회원 정보 수정")
                        .hasTarget(MemberUpdateRequest.class)
                        .hasValue("memberNumber", MemberTestData.DEFAULT_NUMBER)
                        .hasMessageKey("member.update.failed");
            }
            
            @Test
            @DisplayName("인증된 사용자가 아니면 예외를 전파한다.")
            void throwException_whenMemberIsNotAuthenticated() {
            	//given
                when(currentUser.getMemberId())
                        .thenThrow(ApplicationException.class);
            	
            	//when
                assertThatThrownBy(() -> target.processMemberUpdate(request))
                        .isInstanceOf(ApplicationException.class);
            }
            
            @Test
            @DisplayName("요청값이 유효하지 않으면 예외를 전파한다.")
            void throwsException_whenRequestIsInvalid() {
            	//given
                doThrow(ApplicationException.class)
                        .when(memberValidator)
                        .validateMemberUpdate(request);

                //when
                assertThatThrownBy(() -> target.processMemberUpdate(request))
                        .isInstanceOf(ApplicationException.class);
            }
        }
    }


    @Nested
    @DisplayName("이메일 수정할 때")
    class UpdateEmail {

        EmailUpdateRequest request = mock(EmailUpdateRequest.class);
        MyPageQuery member = mock(MyPageQuery.class);

        @BeforeEach
        void setUp() {
            when(currentUser.getMemberId())
                    .thenReturn(MemberTestData.DEFAULT_NUMBER);
        }

        @Test
        @DisplayName("기존 이메일과 다르면 이메일을 수정한다.")
        void updateMemberEmail_whenEmailIsDifferentFromCurrentEmail() {
            //given
            when(request.getEmail())
                    .thenReturn("change@test.com");

            when(request.getToken())
                    .thenReturn("token");

            when(memberReadService.getMyProfile(MemberTestData.DEFAULT_NUMBER))
                    .thenReturn(member);

            when(member.getId()).thenReturn(MemberTestData.DEFAULT_ID);
            when(member.getEmail()).thenReturn(MemberTestData.DEFAULT_EMAIL);

            //when
            MemberUpdateResponse result = target.changeEmail(request);

            //the
            assertThat(result.getType()).isEqualTo("email");
            assertThat(result.getValue()).isEqualTo("change@test.com");
        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("인증된 사용자가 아니면 이메일 검증 메서드를 진행하지 않는다.")
            void doesNotExecuteEmailVerification_whenMemberIsNotAuthenticated() {
            	//given
                when(currentUser.getMemberId())
                        .thenThrow(ApplicationException.class);
            	
            	//when
                assertThatThrownBy(() -> target.changeEmail(request))
                        .isInstanceOf(ApplicationException.class);
            	
            	//then
            	verify(emailVerificationService, never()).validateEmailToken(request.getEmail(), request.getToken());
            }
            
            @Test
            @DisplayName("이메일 검증에 실패하면 회원정보 조회 메서드를 진행하지 않는다.")
            void doesNotFetchMemberInfo_whenEmailVerificationFails() {
            	//given
                String email = "change@test.com";
                String token = "token";

                when(currentUser.getMemberId())
                        .thenReturn(MemberTestData.DEFAULT_NUMBER);

                when(request.getEmail())
                        .thenReturn(email);

                when(request.getToken())
                        .thenReturn(token);
                
                doThrow(ApplicationException.class)
                        .when(emailVerificationService)
                                .validateEmailToken(email, token);

                //when
                Throwable throwable = catchThrowable(() -> target.changeEmail(request));
            	
            	//then
                ApplicationExceptionAssert.assertThatApplicationException(throwable)
                                .hasMessageKey("member.update.failed");

            	verify(memberReadService, never()).getMyProfile(MemberTestData.DEFAULT_NUMBER);
            }

            @Test
            @DisplayName("이메일 수정에 실패하면 인증토큰이 삭제 메서드를 진행하지 않는다.")
            void doesNotDeleteAuthToken_whenEmailUpdateFails() {
            	//given
                String email = "change@test.com";
                String token = "token";

                when(currentUser.getMemberId())
                        .thenReturn(MemberTestData.DEFAULT_NUMBER);

                when(request.getEmail())
                        .thenReturn(email);

                when(request.getToken())
                        .thenReturn(token);

                when(memberReadService.getMyProfile(MemberTestData.DEFAULT_NUMBER))
                        .thenReturn(member);

                when(member.getId()).thenReturn(MemberTestData.DEFAULT_ID);

                when(member.getEmail())
                        .thenReturn(MemberTestData.DEFAULT_EMAIL);

                doThrow(ApplicationException.class)
                        .when(memberCommandService)
                                .updateEmail(MemberTestData.DEFAULT_ID, MemberTestData.DEFAULT_EMAIL, email);

                //when
                assertThatThrownBy(() -> target.changeEmail(request))
                        .isInstanceOf(ApplicationException.class);

                //then
                verify(emailVerificationService, never()).deleteToken(email);
            }

        }

    }

}