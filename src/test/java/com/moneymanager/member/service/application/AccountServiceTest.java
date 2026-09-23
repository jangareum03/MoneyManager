package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.member.domain.dto.request.MemberWithdrawalRequest;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.enums.WithdrawalReason;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.command.MemberUpdater;
import com.moneymanager.member.service.email.EmailSender;
import com.moneymanager.member.service.email.PasswordResetTokenManager;
import com.moneymanager.member.service.validation.MemberValidator;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;

import static com.moneymanager.global.exception.code.ErrorCode.MISMATCH;
import static com.moneymanager.global.exception.code.ErrorCode.REQUIRED_VALUE;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : AccountServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 23<br>
 * 설명              : AccountService 단위 테스트
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
 * 		 	  <td>26. 9. 23</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    AccountService target;

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberUpdater memberUpdater;

    @Mock
    MemberValidator memberValidator;

    @Mock
    EmailSender emailSender;

    @Mock
    PasswordResetTokenManager tokenManager;

    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        target = new AccountService(
                memberRepository,
                memberUpdater,
                memberValidator,
                emailSender,
                passwordEncoder,
                tokenManager
        );
    }

    @Nested
    @DisplayName("회원탈퇴 진행할 때")
    class Withdrawal {

        String memberId = MemberTestData.DEFAULT_ID;
        MemberWithdrawalRequest request;
        
        Member member = mock(Member.class);
        
        @Test
        @DisplayName("정상 요청이면  회원 탈퇴 처리가 수행한다.")
        void withdrawsMember_whenRequestIsValid() {
        	//given
            request= new MemberWithdrawalRequest(
                    MemberTestData.DEFAULT_PASSWORD,
                    WithdrawalReason.OTHER.getMessageKey(),
                    "기타사유"
            );
            
            when(member.getPassword())
                    .thenReturn(MemberTestData.DEFAULT_PASSWORD);

            when(memberRepository.findById(memberId))
                    .thenReturn(member);

            when(passwordEncoder.matches(request.password(), member.getPassword()))
                    .thenReturn(true);
        	
        	//when
            assertThatCode(() -> target.withdrawal(memberId, request))
                    .doesNotThrowAnyException();
        	
        	//then
        	verify(memberValidator).validateWithdrawal(request);
            verify(memberUpdater).changeToWithdrawn(member);
        }
        
        @Test
        @DisplayName("요청 검증에 실패하면 이후 탈퇴 로직이 수행되지 않는다.")
        void doesNothing_whenRequestIsInvalid() {
        	//given:
            doThrow(ApplicationException.class)
                    .when(memberValidator)
                    .validateWithdrawal(request);
        	
        	//when
            assertThatThrownBy(() -> target.withdrawal(memberId, request))
                    .isInstanceOf(ApplicationException.class);
        	
        	//then
        	verify(memberValidator).validateWithdrawal(request);
            verify(memberRepository, never()).findById(memberId);
        }
        
        @Test
        @DisplayName("유효하지 않은 탈퇴사유면 예외가 발생한다.")
        void throwsException_whenWithdrawalReasonIsInvalid() {
        	//given
            request= new MemberWithdrawalRequest(
                    MemberTestData.DEFAULT_PASSWORD,
                    null,
                    null
            );

        	//when
            assertThatThrownBy(() -> target.withdrawal(memberId, request))
                    .isInstanceOf(NoSuchElementException.class);
        	
        	//then
            verify(memberValidator).validateWithdrawal(request);
            verify(memberRepository, never()).findById(memberId);
        }
        
        @Test
        @DisplayName("기타 탈퇴사유인데 상세 사유가 없으면 예외가 발생한다.")
        void throwsException_whenDetailReasonIsNull() {
        	//given
            request = new MemberWithdrawalRequest(
                    MemberTestData.DEFAULT_PASSWORD,
                    WithdrawalReason.OTHER.getMessageKey(),
                    null
            );
        	
        	//when
            Throwable throwable = catchThrowable(() -> target.withdrawal(memberId, request));
        	
        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasWork("탈퇴사유 확인")
                    .hasTarget(MemberWithdrawalRequest.class)
                    .hasValue("other")
                    .hasCauseMessage("기타 미입력")
                    .hasMessageKey("member.withdrawal.other");
            
            verify(memberRepository, never()).findById(memberId);
        }
        
        @Test
        @DisplayName("비밀번호가 불일치하면 예외가 발생한다.")
        void throwsException_whenPasswordDoesNotMatch() {
        	//given
            request = new MemberWithdrawalRequest(
                    MemberTestData.DEFAULT_PASSWORD,
                    WithdrawalReason.CANNOT_ID_OR_NICKNAME.getMessageKey(),
                    null
            );
            
            when(memberRepository.findById(memberId))
                    .thenReturn(member);
            
            when(passwordEncoder.matches(request.password(), member.getPassword()))
                .thenReturn(false);

            //when
            Throwable throwable = catchThrowable(() -> target.withdrawal(memberId, request));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(MISMATCH)
                    .hasWork("비밀번호 일치 확인")
                    .hasTarget(MemberWithdrawalRequest.class)
                    .hasValue("password", "p******!")
                    .hasMessageKey("member.password.mismatch");

            verify(memberUpdater, never()).changeToWithdrawn(member);
        }
        
        @Test
        @DisplayName("회원 탈퇴 처리 중 예외가 발생하면 예외를 전파한다.")
        void throwsException_whenExceptionOccurs() {
        	//given
            request= new MemberWithdrawalRequest(
                    MemberTestData.DEFAULT_PASSWORD,
                    WithdrawalReason.PRIVACY_CONCERN.getMessageKey(),
                    null
            );

            when(member.getPassword())
                    .thenReturn(MemberTestData.DEFAULT_PASSWORD);

            when(memberRepository.findById(memberId))
                    .thenReturn(member);

            when(passwordEncoder.matches(request.password(), member.getPassword()))
                    .thenReturn(true);

            doThrow(new RuntimeException("탈퇴 처리 실패"))
                    .when(memberUpdater)
                    .changeToWithdrawn(member);

        	//when
            assertThatThrownBy(() -> target.withdrawal(memberId, request))
                    .isInstanceOf(RuntimeException.class);
        }

    }

}