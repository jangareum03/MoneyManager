package com.moneymanager.service.member.history;

import com.moneymanager.dao.member.history.MemberHistoryDaoImpl;
import com.moneymanager.dto.member.log.UpdateLogDTO;
import com.moneymanager.entity.Member;
import com.moneymanager.entity.MemberLog;
import com.moneymanager.exception.ErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;


/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.service.member.history<br>
 * * 파일이름       : UpdateLogService<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 15<br>
 * * 설명              : 회원정보 수정내역 관련 비즈니스 로직을 처리하는 클래스
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
public class UpdateLogService {

	private final MemberHistoryDaoImpl historyDAO;

	private final Logger logger = LogManager.getLogger(this);

	public UpdateLogService(MemberHistoryDaoImpl historyDAO) {
		this.historyDAO = historyDAO;
	}


	/**
	 * 회원 정보의 변경사항을 수정내역을 추가합니다.
	 *
	 * @param log 수정내역 정보
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void createLog(UpdateLogDTO log) {
		try {
			MemberLog history = log.isSuccess() ? getSuccessLog(log) : getFailureLog(log);

			historyDAO.saveHistory(history);

			logger.debug("{} 회원의 {} 정보에 대한 내역을 등록했습니다. (전: {}, 후: {})", history.getMember().getId(), history.getItem(), history.getBeforeInfo(), history.getAfterInfo());
		}catch ( ErrorException e ) {
			logger.debug("{} 회원의 {} 정보에 대한 내역이 등록되지 않았습니다. (전: {}, 후: {})", log.getMemberId(), log.getItem(), log.getBeforeInfo(), log.getAfterInfo());
			logger.debug("내역등록이 되지 않은 원인은 다음과 같습니다. ({}: {})", e.getErrorCode(), e.getMessage());
		}
	}


	private MemberLog getSuccessLog(UpdateLogDTO update) {
		return MemberLog.builder()
				.member(Member.builder().id(update.getMemberId()).build())
				.success('Y').type(update.getType()).item(update.getItem().getText())
				.beforeInfo(update.getBeforeInfo()).afterInfo(update.getAfterInfo())
				.failureReason(null).updatedAt(Timestamp.valueOf(update.getDateTime()))
				.build();
	}


	private MemberLog getFailureLog(UpdateLogDTO update) {
		return MemberLog.builder()
				.member(Member.builder().id(update.getMemberId()).build())
				.success('N').type(update.getType()).item(update.getItem().getText())
				.beforeInfo(null).afterInfo(null)
				.failureReason(update.getCause()).updatedAt(Timestamp.valueOf(update.getDateTime()))
				.build();
	}
}
