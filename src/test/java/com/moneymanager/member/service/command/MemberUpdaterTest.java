package com.moneymanager.member.service.command;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberHistory;
import com.moneymanager.member.domain.enums.MemberStatus;
import com.moneymanager.member.repository.MemberHistoryRepository;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.application.EmailVerificationService;
import com.moneymanager.support.ApplicationExceptionAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.moneymanager.global.exception.code.ErrorCode.STATUS_NOT_ALLOWED;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.command<br>
 * 파일이름       : MemberUpdaterTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 23<br>
 * 설명              : MemberUpdater 단위 테스트
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
class MemberUpdaterTest {

    @InjectMocks
    private MemberUpdater target;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberHistoryRepository historyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("회원 탈퇴 상태로 변경할 때")
    class ChangeWithdrawn {

        @Test
        @DisplayName("탈퇴 가능하면 회원상태를 변경하고 이력을 저장한다.")
        void changesMemberStatusAndSavesHistory_whenMemberCanWithdraw() {
            //given
            Member member = mock(Member.class);

            when(member.canWithdraw())
                    .thenReturn(true);

            //when
            target.changeToWithdrawn(member);

            //then
            verify(memberRepository).updateStatusToWithdrawn(member);
            verify(historyRepository).insertHistory(any(MemberHistory.class));
        }

        @Test
        @DisplayName("탈퇴 불가능하면 예외가 발생한다.")
        void throwsException_whenMemberCannotWithdraw() {
            //given
            Member member = mock(Member.class);

            when(member.canWithdraw())
                    .thenReturn(false);

            when(member.getStatus())
                    .thenReturn(MemberStatus.LOCKED);

            //when
            Throwable throwable = catchThrowable(() -> target.changeToWithdrawn(member));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(STATUS_NOT_ALLOWED)
                    .hasWork("회원 탈퇴")
                    .hasTarget(Member.class)
                    .hasValue("status")
                    .hasValue("L")
                    .hasMessageKey("member.withdrawal.failed");
        }

    }

}