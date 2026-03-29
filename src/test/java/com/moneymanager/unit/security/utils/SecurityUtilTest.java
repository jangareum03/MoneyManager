package com.moneymanager.unit.security.utils;

import com.moneymanager.BusinessExceptionAssert;
import com.moneymanager.exception.error.ErrorCode;
import com.moneymanager.security.CustomUserDetails;
import com.moneymanager.security.utils.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.security.utils<br>
 * 파일이름       : SecurityUtilTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 19<br>
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
 * 		 	  <td>26. 3. 19</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class SecurityUtilTest {

	@Mock
	private CustomUserDetails userDetails;

	@Mock
	private Authentication authentication;

	@Mock
	private SecurityContext securityContext;

	private SecurityUtil securityUtil;

	@BeforeEach
	void setUp() {
		securityUtil = new SecurityUtil();

		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(userDetails);

		SecurityContextHolder.setContext(securityContext);
	}

	//==================[ TEST ]==================
	@Test
	@DisplayName("로그인 된 사용자의 회원번호를 가져올 수 있다.")
	void getMemberId_Success() {
		//given
		when(userDetails.getId()).thenReturn("member123");

		//when
		String actual = securityUtil.getMemberId();

		//then
		assertThat(actual).isEqualTo("member123");
	}

	@Test
	@DisplayName("로그인 된 사용자의 회원번호가 null이면 예외가 발생한다.")
	void getMemberId_Failure_MemberIsNull() {
		//given
		when(userDetails.getId()).thenReturn(null);

		//when
		Throwable throwable = catchThrowable(() -> securityUtil.getMemberId());

		//then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(ErrorCode.MEMBER_AUTHORITY_UNAUTHORIZED)
				.hasUserMessage("다시 로그인")
				.hasLogMessage("인증 실패");
	}

	@Test
	@DisplayName("비로그인은 인증된 주체가 null이여서 예외가 발생한다.")
	void getMemberId_Failure_CustomUserDetailsIsNull() {
		//given
		when(authentication.getPrincipal()).thenReturn(null);

		//when
		Throwable throwable = catchThrowable(() -> securityUtil.getMemberId());

		// then
		BusinessExceptionAssert.assertThatBusinessException(throwable)
				.hasErrorCode(ErrorCode.MEMBER_AUTHORITY_FAILED)
				.hasUserMessage("다시 로그인")
				.hasLogMessage("인증 실패", "principal");
	}
}