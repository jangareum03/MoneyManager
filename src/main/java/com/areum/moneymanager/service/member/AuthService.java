package com.areum.moneymanager.service.member;

import com.areum.moneymanager.dao.member.MemberInfoDao;
import com.areum.moneymanager.dao.member.MemberInfoDaoImpl;
import com.areum.moneymanager.dto.request.member.LogRequestDTO;
import com.areum.moneymanager.dto.request.member.LoginRequestDTO;
import com.areum.moneymanager.dto.response.member.LoginResponseDTO;
import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.MemberInfo;
import com.areum.moneymanager.exception.ErrorException;
import com.areum.moneymanager.exception.custom.ServerException;
import com.areum.moneymanager.service.member.history.LoginLogService;
import com.areum.moneymanager.service.member.validation.MemberValidationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;

import static com.areum.moneymanager.exception.code.ErrorCode.*;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.service.member<br>
 *  * 파일이름       : AuthService<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7 15<br>
 *  * 설명              : 회원 계정 비즈니스 로직을 처리하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Service
public class AuthService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final MemberServiceImpl memberService;
	private final LoginLogService loginLogService;
	private final MailService mailService;
	private final MemberInfoDao memberInfoDao;
	private final Logger logger = LogManager.getLogger(this);


	@Autowired
	public AuthService(MemberServiceImpl memberService, MemberInfoDaoImpl memberInfoDao, LoginLogService loginHistoryService, MailService mailService ) {
		this.memberService = memberService;
		this.memberInfoDao = memberInfoDao;
		this.loginLogService = loginHistoryService;
		this.mailService = mailService;
	}




	/**
	 * 로그인 정보로 서비스 이용에 필요한 정보를 반환합니다.
	 * <p>
	 * 서비스 접속 가능 여부를 확인하기 위해 로그인 정보를 검증 후 회원 상태가 활성화일때만 회원 정보를 반환합니다.
	 *
	 * @param login			서비스 접속 요청하는 로그인 정보
	 * @param request	 http 요청정보를 담은 객체
	 * @throws ErrorException	비밀번호 불일치면 발생
	 * @return 회원 상태가 활성화일 경우에만 회원 정보, 그 외 상태이면 null
	 */
	public LoginResponseDTO.Login login( LoginRequestDTO.Login login, HttpServletRequest request  ) {
		LoginRequestDTO.Environment environment = getLoginEnvironment(request);

		//아이디와 비밀번호 검증
		MemberValidationService.checkIdAvailability( login.getId() );
		MemberValidationService.checkPasswordAvailability( login.getPassword() );

		String password = memberInfoDao.findPasswordByUsername( login.getId() );

		if( Objects.isNull(password) || ! passwordEncoder.matches( login.getPassword(), password) ) { // 아이디가 존재하지 않거나 비밀번호가 일치하지 않은 경우
			String cause = Objects.isNull(password) ? "아이디 미존재" : "비밀번호 불일치";

			loginLogService.loginFailure( LogRequestDTO.Login.builder().id(login.getId()).success(false).failureReason(cause).environment(environment).build() );
			throw new ErrorException(MEMBER_LOGIN_MISMATCH);
		}



		return loginByStatus( request, login.getId(), environment );
	}



	/**
	 * 회원 상태에 따라 로그인 과정을 다르게 진행합니다. <br>
	 * 정상적으로 로그인 가능한 상태는 서비스 이용에 필요한 객체를 제공하지만, 로그인 불가능한 상태는 로그인 불가 원인을 메시지로 제공합니다.
	 *
	 * @param request		http 요청정보를 담은 객체
	 * @param id						아이디
	 * @param environment	http 요청정보를 담은 객체
	 * @return 회원 상태가 활성화일 경우에만 회원정보, 그 외 상태면 메시지
	 */
	@Transactional
	private LoginResponseDTO.Login loginByStatus( HttpServletRequest request, String id, LoginRequestDTO.Environment environment ) {
		LogRequestDTO.Login history =
						LogRequestDTO.Login.builder().id( id ).environment(environment).build();


		Member member = memberInfoDao.findLoginMemberByUsername( id );
		switch ( member.getStatus().toUpperCase() ) {
			case "A" :
					//로그인 내역 추가 및 마지막 접속일자 수정
					LogRequestDTO.Login successHistory = history.toBuilder().success(true).build();
					loginLogService.loginSuccess( member.getId(), successHistory );
					changeLastLogin( member.getId(), environment.getLoginDate() );


					return LoginResponseDTO.Login.builder().memberId(member.getId()).nickName( Objects.isNull(member.getNickName()) ? "" : member.getNickName() ).build();
			case "D" :
				Period period = Period.between(LocalDate.from(member.getDeletedAt().toLocalDateTime()), LocalDate.now() );

				if( period.getDays() > 30 ) {
					LogRequestDTO.Login restoreHistory = history.toBuilder().success(false).failureReason("미가입한 회원으로 로그인 불가").build();
					loginLogService.loginFailure( restoreHistory );
					throw new ErrorException(MEMBER_LOGIN_RESIGN);
				}
			case "L":
				LoginResponseDTO.SendEmail email = getEmailForPassword( member.getId() );

				//임시 비밀번호를 전송
				String password = mailService.send( "password", email.getName(), email.getEmail() );
				if( Objects.nonNull(password) ) {	//이메일 전송 성공한 경우
					request.getSession().setAttribute( email.getEmail(), password );

					memberInfoDao.updateStatus( Member.builder().id(member.getId()).status("A").build() );
					memberService.changePassword( member.getId(), password );
				}

				if( member.getStatus().equalsIgnoreCase("D") ) {
					LogRequestDTO.Login restoreHistory = history.toBuilder().success(false).failureReason("탈퇴 회원으로 로그인 불가").build();
					loginLogService.loginFailure( restoreHistory );
					throw new ErrorException(MEMBER_LOGIN_RESTORE);
				}else {
					LogRequestDTO.Login lockedHistory = history.toBuilder().success(false).failureReason("로그인 횟수 초과하여 로그인 불가").build();
					loginLogService.loginFailure( lockedHistory );
					throw new ErrorException(MEMBER_LOGIN_LOCKED);
				}
			default:
				LogRequestDTO.Login errorHistory = history.toBuilder().success(false).failureReason("아이디 또는 비밀번호 불일치로 로그인 불가능").build();
				loginLogService.loginFailure( errorHistory );
				throw new ErrorException(MEMBER_LOGIN_UNKNOWN);
		}
	}



	/**
	 * 서비스 접속을 시도한 환경정보를 반환합니다.<p>
	 * 환경정보에는 브라우저, IP 주소, 접속시간이 있습니다.
	 *
	 * @param request		http 요청정보를 담은 객체
	 * @return	접속한 환경 정보
	 */
	private LoginRequestDTO.Environment getLoginEnvironment( HttpServletRequest request ) {
		return LoginRequestDTO.Environment.builder().ip(request.getRemoteAddr()).browser(getBrowser(request.getHeader("USER-AGENT"))).loginDate(LocalDateTime.now()).build();
	}



	/**
	 *	접속한 브라우저를 반환합니다.
	 *
	 * @param userAgent		브라우저 및 운영체제 정보
	 * @return	브라우저 유형
	 */
	private String getBrowser( String userAgent ) {
		String browser = "other";

		if( userAgent.contains("Edg") ) {
			browser = "Edge";
		}else if(userAgent.contains("Chrome")) {
			browser = "Chrome";
		}else if(userAgent.contains("Safari")) {
			browser = "Safari";
		}else if( userAgent.contains("Firefox") ) {
			browser = "Firefox";
		}

		return browser;
	}



	/**
	 * 로그인 성공한 회원의 마지막 접속 시간을 변경합니다.
	 *
	 * @param memberId		회원 번호
	 */
	private void changeLastLogin( String memberId, LocalDateTime date ) {
		MemberInfo entity =
						MemberInfo.builder()
										.member(Member.builder().id(memberId).build()).loginAt(Timestamp.valueOf(date))
										.build();

		memberInfoDao.updateLastLoginDate(entity);
	}



	/**
	 * 회원번호로 이메일과 이름을 반환합니다.
	 *
	 * @param memberId 	회원번호
	 * @return 회원번호가 있으면 이메일과이름, 없으면 null
	 */
	private LoginResponseDTO.SendEmail getEmailForPassword( String memberId ) {
		Member member = memberInfoDao.findEmailAndNameByMemberId(memberId);

		return LoginResponseDTO.SendEmail.builder().name(member.getName()).email(member.getEmail()).build();
	}
}
