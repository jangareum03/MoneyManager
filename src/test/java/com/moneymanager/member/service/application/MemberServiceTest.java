package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.member.domain.dto.request.MemberSignUpRequest;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.service.command.MemberCommandService;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.member.service.validation.MemberValidator;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    MemberCommandService memberCommandService;

    @Mock
    MemberReadService memberReadService;

    @Mock
    EmailVerificationService emailVerificationService;

    @Mock
    MemberValidator memberValidator;


    @Nested
    @DisplayName("회원가입 진행할 때")
    class ProcessSignUp {

        MemberSignUpRequest request = MemberSignUpRequest.of(
                MemberTestData.DEFAULT_USERNAME,
                MemberTestData.DEFAULT_PASSWORD,
                MemberTestData.DEFAULT_NAME,
                MemberTestData.DEFAULT_BIRTHDATE,
                MemberTestData.DEFAULT_NICKNAME,
                MemberTestData.DEFAULT_EMAIL,
                "token",
                MemberTestData.DEFAULT_GENDER.getValue()
        );

        @Test
        @DisplayName("요청 검증 → 이메일 검증 여부 → 중복 검증 → 객체 변환 → 회원 저장 → 인증토큰 삭제 순서로 진행한다.")
        void processesSignUp_whenRequestIsValid() {
            //given
            Member member = mock(Member.class);

            when(memberCommandService.create(request))
                    .thenReturn(member);

            //when
            target.processSignUp(request);

            //then
            InOrder inOrder = Mockito.inOrder(memberCommandService, memberReadService, emailVerificationService, memberValidator);

            inOrder.verify(memberValidator).signUp(request);
            inOrder.verify(emailVerificationService).verifyEmailCode(request.getEmail(), request.getToken());
            inOrder.verify(memberReadService).validateSignUpEligibility(request.getUsername(), request.getNickname());
            inOrder.verify(memberCommandService).create(request);
            inOrder.verify(memberCommandService).save(member);
            inOrder.verify(emailVerificationService).deleteTokenByEmail(member.getEmail());
        }

        @Test
        @DisplayName("요청 정보 검증에 실패하면 이메일 검증 여부를 수행하지 않는다.")
        void doesNothing_whenRequestIsInvalid() {
        	//given
            doThrow(ApplicationException.class)
                    .when(memberValidator)
                    .signUp(request);
        	
        	//when
            assertThatThrownBy(() -> target.processSignUp(request));
        	
        	//then
            verify(emailVerificationService, never()).verifyEmailCode(request.getEmail(), request.getToken());
        }

        @Test
        @DisplayName("이메일 검증 여부가 실패하면 회원 중복 검증을 수행하지 않는다.")
        void doesNothing_whenTokenIsInvalid() {
            //given
            doThrow(ApplicationException.class)
                    .when(emailVerificationService)
                    .verifyEmailCode(request.getEmail(), request.getToken());

            //when
            assertThatThrownBy(() -> target.processSignUp(request));

            //then
            verify(memberReadService, never()).validateSignUpEligibility(anyString(), anyString());
        }
        
        @Test
        @DisplayName("중복 검증에 실패하면 객체 변환을 수행하지 않는다.")
        void doesNothing_whenUserAlreadyExists() {
        	//given
            doThrow(ApplicationException.class)
                    .when(memberReadService)
                    .validateSignUpEligibility(request.getUsername(), request.getNickname());
        	
        	//when
            assertThatThrownBy(() -> target.processSignUp(request));

            //then
            verify(memberCommandService, never()).create(request);
        }
        
        @Test
        @DisplayName("요청 객체 변환에 실패하면 저장하지 않는다.")
        void doesNothing_whenMappingFails() {
            //given
            when(memberCommandService.create(request))
                    .thenThrow(ApplicationException.class);

            //when
            assertThatThrownBy(() -> target.processSignUp(request));

            //then
            verify(memberCommandService, never()).save(any(Member.class));
        }

        @Test
        @DisplayName("회원 저장에 실패하면 토큰을 삭제하지 않는다.")
        void throwsException_whenSaveFails() {
            //given
            Member member = mock(Member.class);

            when(memberCommandService.create(request))
                    .thenReturn(member);

            doThrow(ApplicationException.class)
                    .when(memberCommandService)
                    .save(member);

            //when
            assertThatThrownBy(() -> target.processSignUp(request))
                    .isInstanceOf(ApplicationException.class);

            //then
            verify(emailVerificationService, never()).deleteTokenByEmail(request.getEmail());
        }

    }

}