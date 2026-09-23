package com.moneymanager.member.service.validation;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.member.domain.dto.request.MemberWithdrawalRequest;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.enums.WithdrawalReason;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.moneymanager.global.exception.code.ErrorCode.INVALID_VALUE;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.validation<br>
 * 파일이름       : MemberValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 23<br>
 * 설명              : MemberValidator 단위 테스트
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
class MemberValidatorTest {

    @InjectMocks
    private MemberValidator target;

    @Mock
    private MemberFieldValidator fieldValidator;

    @Mock
    private MemberProfileValidator profileValidator;

    @Nested
    @DisplayName("탈퇴 요청 검증할 때")
    class ValidationWithdrawal {
        String password = MemberTestData.DEFAULT_PASSWORD;
        String reason = WithdrawalReason.CANNOT_ID_OR_NICKNAME.getMessageKey();
        String other = null;

        @Test
        @DisplayName("정상적인 요청이면 예외가 발생하지 않는다.")
        void successWithdraw_doesNotThrowException() {
        	//given
            MemberWithdrawalRequest request = new MemberWithdrawalRequest(password, reason, other);
        	
        	//when
            assertThatCode(() -> target.validateWithdrawal(request))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("비밀번호가 유효하지 않으면 검증에 실패한다.")
        void failWithdraw_whenMemberPasswordIsInvalid() {
        	//given
            MemberWithdrawalRequest request = new MemberWithdrawalRequest("", reason, other);

            doThrow(ApplicationException.class)
                    .when(fieldValidator)
                    .validatePassword(eq(request.password()), anyString());

        	//when
            assertThatThrownBy(() -> target.validateWithdrawal(request))
                    .isInstanceOf(ApplicationException.class);
        }

        @Test
        @DisplayName("탈퇴 유형이 유효하지 않으면 검증에 실패한다.")
        void throwsException_whenMemberWithdrawalTypeIsInvalid() {
        	//given
            MemberWithdrawalRequest request = new MemberWithdrawalRequest(password, "no-exist", other);
        	
        	//when
        	Throwable throwable = catchThrowable(() -> target.validateWithdrawal(request));

        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(INVALID_VALUE)
                    .hasWork("WithdrawalReason 변환")
                    .hasTarget(Member.class)
                    .hasValue("reason", "no-exist")
                    .hasMessageKey("member.reason.invalid");
        }
    }

}