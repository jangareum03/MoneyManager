package com.areum.moneymanager.service.member;

import com.areum.moneymanager.dao.member.MemberInfoDao;
import com.areum.moneymanager.dao.member.MemberInfoDaoImpl;
import com.areum.moneymanager.dto.member.log.LoginLogDTO;
import com.areum.moneymanager.dto.member.request.MemberLoginRequest;
import com.areum.moneymanager.dto.member.response.MemberLoginResponse;
import com.areum.moneymanager.entity.LoginLog;
import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.MemberInfo;
import com.areum.moneymanager.exception.ErrorException;
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
 * * 패키지이름    : com.areum.moneymanager.service.member<br>
 * * 파일이름       : AuthService<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7 15<br>
 * * 설명              : 회원 계정 비즈니스 로직을 처리하는 클래스
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
public class AuthService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final MemberServiceImpl memberService;
	private final LoginLogService loginLogService;
	private final MailService mailService;
	private final MemberInfoDao memberInfoDao;
	private final Logger logger = LogManager.getLogger(this);


	@Autowired
	public AuthService(MemberServiceImpl memberService, MemberInfoDaoImpl memberInfoDao, LoginLogService loginHistoryService, MailService mailService) {
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
	 * @param login   서비스 접속 요청하는 로그인 정보
	 * @param request http 요청정보를 담은 객체
	 * @return 회원 상태가 활성화일 경우에만 회원 정보, 그 외 상태이면 null
	 * @throws ErrorException 비밀번호 불일치면 발생
	 */
	public MemberLoginResponse.Success login(MemberLoginRequest login, HttpServletRequest request) {
		//아이디와 비밀번호 검증
		MemberValidationService.checkIdAvailability(login.getId());
		MemberValidationService.checkPasswordAvailability(login.getPassword());

		String password = memberInfoDao.findPasswordByUsername(login.getId());

		if (Objects.isNull(password) || !passwordEncoder.matches(login.getPassword(), password)) { // 아이디가 존재하지 않거나 비밀번호가 일치하지 않은 경우
			String cause = Objects.isNull(password) ? "아이디 미존재" : "비밀번호 불일치";

			loginLogService.createLog( new LoginLogDTO( false, request, login.getId(), cause) );

			throw new ErrorException(MEMBER_LOGIN_MISMATCH);
		}


		return loginByStatus(request, login.getId());
	}


	/**
	 * 회원 상태에 따라 로그인 과정을 다르게 진행합니다. <br>
	 * 정상적으로 로그인 가능한 상태는 서비스 이용에 필요한 객체를 제공하지만, 로그인 불가능한 상태는 로그인 불가 원인을 메시지로 제공합니다.
	 *
	 * @param request     http 요청정보를 담은 객체
	 * @param id          아이디
	 * @return 회원 상태가 활성화일 경우에만 회원정보, 그 외 상태면 메시지
	 */
	@Transactional
	private MemberLoginResponse.Success loginByStatus(HttpServletRequest request, String id) {
		Member member = memberInfoDao.findLoginMemberByUsername(id);

		switch (member.getStatus().toUpperCase()) {
			case "A":
				//로그인 내역 추가 및 마지막 접속일자 수정
				LoginLog loginLog = loginLogService.createLog(new LoginLogDTO(true, request, id, null));
				changeLastLogin(member.getId(), loginLog.getAccessAt().toLocalDateTime());


				return MemberLoginResponse.Success.builder().memberId(member.getId()).nickName(Objects.isNull(member.getNickName()) ? "" : member.getNickName()).build();
			case "D":
				Period period = Period.between(LocalDate.from(member.getDeletedAt().toLocalDateTime()), LocalDate.now());

				if (period.getDays() > 30) {
					loginLogService.createLog( new LoginLogDTO(false, request, id, "미가입한 회원으로 로그인 불가") );

					throw new ErrorException(MEMBER_LOGIN_RESIGN);
				}
			case "L":
				MemberLoginResponse.Failure login = getEmailForPassword(member.getId());

				//임시 비밀번호를 전송
				String password = mailService.send("password", login.getName(), login.getEmail());
				if (Objects.nonNull(password)) {    //이메일 전송 성공한 경우
					request.getSession().setAttribute(login.getEmail(), password);

					memberInfoDao.updateStatus(Member.builder().id(member.getId()).status("A").build());
					memberService.changePassword(member.getId(), password);
				}

				if (member.getStatus().equalsIgnoreCase("D")) {
					loginLogService.createLog( new LoginLogDTO(false, request, id, "탈퇴 회원으로 로그인 불가") );

					throw new ErrorException(MEMBER_LOGIN_RESTORE);
				} else {
					loginLogService.createLog( new LoginLogDTO(false, request, id, "로그인 횟수 초과하여 로그인 불가") );

					throw new ErrorException(MEMBER_LOGIN_LOCKED);
				}
			default:
				loginLogService.createLog( new LoginLogDTO(false, request, id, "아이디 또는 비밀번호 불일치로 로그인 불가") );

				throw new ErrorException(MEMBER_LOGIN_UNKNOWN);
		}
	}



	/**
	 * 로그인 성공한 회원의 마지막 접속 시간을 변경합니다.
	 *
	 * @param memberId 회원 번호
	 */
	private void changeLastLogin(String memberId, LocalDateTime date) {
		MemberInfo entity =
				MemberInfo.builder()
						.member(Member.builder().id(memberId).build()).loginAt(Timestamp.valueOf(date))
						.build();

		memberInfoDao.updateLastLoginDate(entity);
	}


	/**
	 * 회원번호로 이메일과 이름을 반환합니다.
	 *
	 * @param memberId 회원번호
	 * @return 회원번호가 있으면 이메일과이름, 없으면 null
	 */
	private MemberLoginResponse.Failure getEmailForPassword(String memberId) {
		Member member = memberInfoDao.findEmailAndNameByMemberId(memberId);

		return MemberLoginResponse.Failure.builder().name(member.getName()).email(member.getEmail()).build();
	}
}
