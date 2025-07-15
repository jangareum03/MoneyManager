package com.areum.moneymanager.service.member.history;

import com.areum.moneymanager.dao.member.history.MemberHistoryDaoImpl;
import com.areum.moneymanager.dto.request.member.LogRequestDTO;
import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.MemberHistory;
import com.areum.moneymanager.exception.ErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;


/**
 * 회원 데이터의 변경이력 작업(데이터 수정 및 삭제)을 처리하는 클래스</br>
 * 변경이력 생성, 조회, 삭제 등의 메서드를 구현
 *
 * @version 1.0
 */
@Service
public class UpdateLogService {

	private final MemberHistoryDaoImpl historyDAO;

	private final Logger logger = LogManager.getLogger(this);

	public UpdateLogService(MemberHistoryDaoImpl historyDAO ) {
		this.historyDAO = historyDAO;
	}



	/**
	 * 회원 정보의 변경사항을 수정내역을 추가합니다.
	 *
	 * @param member	수정내역 정보
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void createHistory( LogRequestDTO.Member member ) {
		MemberHistory memberInfoHistory;

		//DTO → Entity 변환
		MemberHistory entity;
		if( member.isSuccess() ) {
			if( member.getItem().equals("프로필") ) {
				entity = MemberHistory.builder()
								.member(Member.builder().id(member.getMemberId()).build())
								.success('Y').type(member.getType()).item(member.getItem())
								.beforeInfo(member.getBeforeInfo()).afterInfo(member.getAfterInfo())
								.failureReason(null)
								.updatedAt(Timestamp.valueOf(member.getToday()))
								.build();
			}else{
				entity = MemberHistory.builder()
								.member(Member.builder().id(member.getMemberId()).build())
								.success('Y').type(member.getType()).item(member.getItem())
								.beforeInfo(member.getBeforeInfo()).afterInfo(member.getAfterInfo())
								.failureReason(null)
								.build();
			}
		}else {
			logger.debug("🍑[실패내역] 전: {}, 후: {}", member.getBeforeInfo(), member.getAfterInfo());
			entity = MemberHistory.builder()
							.member(com.areum.moneymanager.entity.Member.builder().id(member.getMemberId()).build())
							.success('N').type(member.getType()).item(member.getItem())
							.beforeInfo(null).afterInfo(null)
							.failureReason(member.getFailureReason())
							.build();
		}

		try{
			//회원정보 수정내역 추가
			memberInfoHistory = historyDAO.saveHistory( entity );

			logger.debug("{} 회원의 {} 정보에 대한 내역을 등록했습니다. (전: {}, 후: {})", memberInfoHistory.getMember().getId(), memberInfoHistory.getItem(), memberInfoHistory.getBeforeInfo(), memberInfoHistory.getAfterInfo());
		} catch ( ErrorException e ) {
			logger.debug("{} 회원의 {} 정보에 대한 내역이 등록되지 않았습니다. (전: {}, 후: {})", member.getMemberId(), member.getItem(), member.getBeforeInfo(), member.getAfterInfo());
			logger.debug("내역등록이 되지 않은 원인은 다음과 같습니다. ({}: {})", e.getErrorCode(), e.getMessage());
		}
	}
}
