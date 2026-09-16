package com.moneymanager.redis.service;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.member.service.validation.MemberValidator;
import com.moneymanager.redis.MemberRedisKey;
import com.moneymanager.redis.RedisService;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.moneymanager.global.exception.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : EmailVerificationServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 6<br>
 * 설명              : EmailVerificationService 클래스 로직을 검증하는 단위 테스트 클래스
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
class EmailVerificationServiceTest {

    @InjectMocks
    EmailVerificationService target;
    
    @Mock
    MemberReadService memberReadService;
    
    @Mock
    MemberValidator memberValidator;
    
    @Mock
    RedisService redisService;

    @Mock
    MemberRedisKey redisKey;


    @Nested
    @DisplayName("이메일 검증할 때")
    class Validation {
        
        @Test
        @DisplayName("형식 검증 → 중복 여부 확인 순서로 진행한다.")
        void doesNotThrowAnyException_whenValueIsValidAndUnique() {
        	//given
            String email = MemberTestData.DEFAULT_EMAIL;
        	
        	//when
            assertDoesNotThrow(() -> target.validateEmail(email));
        	
        	//then
        	InOrder inOrder = inOrder(memberValidator, memberReadService);
            
            inOrder.verify(memberValidator).validateEmail(email);
            inOrder.verify(memberReadService).checkEmailExists(email);
        }
        
        @Test
        @DisplayName("형식 검증 실패하면 중복 여부를 수행하지 않는다.")
        void throwsException_whenFormatIsInvalid() {
        	//given
            String email = MemberTestData.DEFAULT_EMAIL;
            
            doThrow(ApplicationException.class)
                .when(memberValidator)
                    .validateEmail(email);
        	
        	//when
            assertThatThrownBy(() -> target.validateEmail(email))
                    .isInstanceOf(ApplicationException.class);
        	
        	//then
        	verify(memberReadService, never()).checkEmailExists(email);
        }
        
        @Test
        @DisplayName("중복 여부에서 TRUE를 반환하면 예외를 발생시킨다.")
        void throwsException_whenValueIsDuplicated() {
        	//given
            String email = MemberTestData.DEFAULT_EMAIL;

            when(memberReadService.checkEmailExists(email))
                    .thenReturn(true);
        	
        	//when
            Throwable throwable = catchThrowable(() -> target.validateEmail(email));
        	
        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(DUPLICATE_DATA)
                    .hasWork("이메일 검증")
                    .hasCauseMessage("중복 이메일")
                    .hasField("email")
                    .hasValue("te**@test.com");
        }
    }


    @Nested
    @DisplayName("이메일 토큰 검증할 때")
    class ValidateEmailToken {

        String email = MemberTestData.DEFAULT_EMAIL;
        String token = "token";
        
        @Test
        @DisplayName("이메일에 해당하는 토큰과 일치하면 예외가 발생하지 않는다.")
        void verifyMemberToken_success_whenTokenMatchesEmail() {
        	//given
            when(redisKey.emailVerificationToken(email))
                    .thenReturn("email-token");

            when(redisService.get("email-token"))
                    .thenReturn(token);
        	
        	//when
            assertDoesNotThrow(() -> target.validateEmailToken(email, token));
        }
        
        @Test
        @DisplayName("저장된 토큰이 없다면 예외를 발생시킨다.")
        void verifyMemberToken_throwsException_whenTokenNotFound() {
        	//given
            when(redisKey.emailVerificationToken(email))
                    .thenReturn("email-token");

            when(redisService.get("email-token"))
                    .thenReturn(null);
        	
        	//when
            Throwable throwable = catchThrowable(() -> target.validateEmailToken(email, token));
        	
        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(DATA_NOT_FOUND)
                    .hasWork("이메일 토큰 검증")
                    .hasField("email")
                    .hasValue("*", "@test.com")
                    .hasMessageKey("member.signup.failed");
        }
        
        @Test
        @DisplayName("저장된 토큰과 일치하지 않으면 예외를 발생시킨다.")
        void verifyMemberToken_throwsException_whenTokenMismatched() {
            //given
            when(redisKey.emailVerificationToken(email))
                    .thenReturn("email-token");

            when(redisService.get("email-token"))
                    .thenReturn("token12token");

            //when
            Throwable throwable = catchThrowable(() -> target.validateEmailToken(email, token));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(MISMATCH)
                    .hasWork("이메일 토큰 검증")
                    .hasField("token")
                    .hasValue("tok**")
                    .hasMessageKey("member.signup.failed");
        }
        
    }

}