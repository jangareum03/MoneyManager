package com.moneymanager.service.member.history;

import com.moneymanager.dao.member.history.LoginHistoryDaoImpl;
import com.moneymanager.dto.member.log.LoginLogDTO;
import com.moneymanager.entity.LoginLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;


/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.service.member.history<br>
 * * 파일이름       : LoginLogService<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 15<br>
 * * 설명              : 회원 로그인 내역 관련 비즈니스 로직을 처리하는 클래스
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
@Slf4j
@Service
public class LoginLogService {

	private final LoginHistoryDaoImpl historyDAO;

	public LoginLogService(LoginHistoryDaoImpl loginHistoryDAO) {
		this.historyDAO = loginHistoryDAO;
	}


	/**
	 * 로그인내역을 추가합니다. <br>
	 * 성공한 로그인 내역에는 실패사유(cause)가 없지만, 실패한 로그인 내역에는 반드시 실패 사유가 포함되어야 합니다.
	 *
	 * @param log 로그 내역
	 * @return 로그인 내역
	 */
	public LoginLog createLog(LoginLogDTO log) {
		LoginLog history = log.isSuccess() ? getSuccessLog(log) : getFailureLog(log);

		return historyDAO.saveHistory(history);
	}


	private LoginLog getSuccessLog(LoginLogDTO login) {
		return LoginLog.builder()
				.memberId(login.getId()).userName(login.getId()).type("LOGIN").success('Y').failureReason(null)
				.browser(getBrowser(login.getUserAgent())).ip(login.getIp()).accessAt(Timestamp.valueOf(login.getDateTime()))
				.build();
	}


	private LoginLog getFailureLog(LoginLogDTO login) {
		return LoginLog.builder()
				.userName(login.getId()).type("LOGIN").success('N').failureReason(login.getCause())
				.browser(getBrowser(login.getUserAgent())).ip(login.getIp()).accessAt(Timestamp.valueOf(login.getDateTime()))
				.build();
	}


	/**
	 * 접속한 브라우저를 반환합니다.
	 *
	 * @param userAgent 브라우저 및 운영체제 정보
	 * @return 브라우저 유형
	 */
	private String getBrowser(String userAgent) {
		String browser = "other";

		if (userAgent.contains("Edg")) {
			browser = "Edge";
		} else if (userAgent.contains("Chrome")) {
			browser = "Chrome";
		} else if (userAgent.contains("Safari")) {
			browser = "Safari";
		} else if (userAgent.contains("Firefox")) {
			browser = "Firefox";
		}

		return browser;
	}
}
