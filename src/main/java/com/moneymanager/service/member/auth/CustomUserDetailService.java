package com.moneymanager.service.member.auth;

import com.moneymanager.dao.member.MemberDaoImpl;
import com.moneymanager.dto.common.ErrorDTO;
import com.moneymanager.dto.member.log.LoginLogDTO;
import com.moneymanager.entity.Member;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.service.member.history.LoginLogService;
import com.moneymanager.utils.LoggerUtil;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.service.member.auth<br>
 * * 파일이름       : CustomUserDetailService<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 15<br>
 * * 설명              : 데이터베이스에서 사용자 정보를 조회하는 클래스
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
 * 		 	  <td>25. 7. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

	private final LoginLogService logService;
	private final MemberDaoImpl memberDao;

	public CustomUserDetailService(LoginLogService logService, MemberDaoImpl memberDao) {
		this.logService = logService;
		this.memberDao = memberDao;
	}


	/**
	 * 사용자가 로그인한 아이디가 자격이 있는지 증명합니다.
	 *
	 * @param username 로그인 시도한 아이디
	 * @throws UsernameNotFoundException DB 조회 불가 시
	 * @return 자격증명을 한 사용자 정보
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LoggerUtil.logSystemInfo("로그인 시도 - 사용자ID: {}", username);

		try {
			Member member = memberDao.findAuthMemberByUsername(username);

			return new CustomUserDetails(member);
		} catch (EmptyResultDataAccessException e) {
			LoggerUtil.logSystemInfo("로그인 실패 - 사용자ID: {}, 원인: 가입되지 않은 아이디로 로그인", username);

			ErrorCode errorCode = ErrorCode.LOGIN_ACCOUNT_MISMATCH;

			HttpServletRequest request = getCurrentHttpRequest();
			logService.createLog(new LoginLogDTO(false, request, username, errorCode.getLogMessage()));

			throw createLoginException(errorCode, username);
		}

	}


	/**
	 * 현재 사용중인 HttpServletRequest 반환합니다.
	 *
	 * @return HttpServletRequest 객체
	 */
	private HttpServletRequest getCurrentHttpRequest() {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

		return Objects.nonNull(attrs) ? attrs.getRequest() : null;
	}


	/**
	 * 로그인 실패 상황에서 사용할 {@link ClientException}을 생성합니다.<br>
	 * 전달받은 {@link ErrorCode}와 관련 요청 데이터를 기반으로 {@link ErrorDTO}를 구성합니다.<br>
	 * 이후 {@link ClientException}에 포함 후 반환합니다.
	 *
	 * @param code 에러코드를 담은 객체
	 * @param data 요청 데이터
	 * @return ClientException
	 */
	private ClientException createLoginException(ErrorCode code, String data) {
		ErrorDTO<String> errorDTO = ErrorDTO.<String>builder().errorCode(code).requestData(data).build();
		return new ClientException(errorDTO);
	}
}
