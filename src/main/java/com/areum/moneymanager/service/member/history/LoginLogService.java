package com.areum.moneymanager.service.member.history;

import com.areum.moneymanager.dao.member.history.LoginHistoryDaoImpl;
import com.areum.moneymanager.dto.request.member.LogRequestDTO;
import com.areum.moneymanager.entity.LoginLog;
import com.areum.moneymanager.exception.ErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;


/**
 * 회원 로그인 관리를 위한 이력 작업(데이터 수정 및 삭제)을 처리하는 클래스</br>
 * 로그인 이력내역의 등록,수정, 삭제등의 메서드를 구현
 *
 * @version 1.0
 */

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.service.member.history<br>
 *  * 파일이름       : LoginLogService<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 로그인 내역 관련 비즈니스 로직을 처리하는 클래스
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
public class LoginLogService {

	private final LoginHistoryDaoImpl historyDAO;
	private final Logger logger = LogManager.getLogger(this);

	public LoginLogService(LoginHistoryDaoImpl loginHistoryDAO ) {
		this.historyDAO = loginHistoryDAO;
	}



	public void loginSuccess( String memberId, LogRequestDTO.Login login ) {
		//DTO → Entity 변환
		LoginLog loginLog = LoginLog.builder()
				.memberId(memberId).userName(login.getId()).type("LOGIN").success('Y').failureReason(null)
				.browser(login.getEnvironment().getBrowser()).ip(login.getEnvironment().getIp()).accessAt(Timestamp.valueOf(login.getEnvironment().getLoginDate()))
				.build();

		createLog(loginLog);
	}



	public void loginFailure( LogRequestDTO.Login login ) {
		//DTO → Entity 변환
		LoginLog loginLog = LoginLog.builder()
				.userName(login.getId()).type("LOGIN").success('N').failureReason(login.getFailureReason())
				.browser(login.getEnvironment().getBrowser()).ip(login.getEnvironment().getIp()).accessAt(Timestamp.valueOf(login.getEnvironment().getLoginDate()))
				.build();

		createLog(loginLog);
	}


	/**
	 * 로그인내역을 추가합니다. <br>
	 * 성공한 로그인 내역에는 실패사유(cause)가 없지만, 실패한 로그인 내역에는 반드시 실패 사유가 포함되어야 합니다.
	 *
	 * @param log	로그 내역
	 */
	private void createLog( LoginLog log ) {
		try{
			//회원 로그인내역 추가
			LoginLog loginHistory = historyDAO.saveHistory( log );

			logger.debug("{}시간에 접속한 {} 아이디의 로그인 내역을 추가했습니다. (내역번호: {}, 성공여부: {}, 브라우저: {}, IP: {})", loginHistory.getAccessAt(), loginHistory.getUserName(), loginHistory.getId(), loginHistory.getSuccess(), loginHistory.getBrowser(), loginHistory.getIp());
		}catch ( ErrorException e ) {
			logger.debug("{} IP로 접속한 로그인 내역을 추가하지 못 했습니다. ({} :{})", log.getIp(), e.getErrorCode(), e.getErrorMessage());
		}
	}


}
