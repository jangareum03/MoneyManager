package com.areum.moneymanager.service.member;

import com.areum.moneymanager.dao.member.MemberInfoDao;
import com.areum.moneymanager.dao.member.MemberInfoDaoImpl;
import com.areum.moneymanager.dao.member.history.PointHistoryDaoImpl;
import com.areum.moneymanager.dto.response.member.MemberResponseDTO;
import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.PointHistory;
import com.areum.moneymanager.exception.code.ErrorCode;
import com.areum.moneymanager.exception.ErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;



/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.service.member<br>
 *  * 파일이름       : PointService<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 포인트 관련 비즈니스 로직을 처리하는 클래스
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
public class PointService {

	private Logger logger = LogManager.getLogger(this);

	private final MemberInfoDao memberInfoDao;
	private final PointHistoryDaoImpl historyDAO;

	public PointService( MemberInfoDaoImpl memberInfoDao, PointHistoryDaoImpl historyDAO ) {
		this.memberInfoDao = memberInfoDao;
		this.historyDAO = historyDAO;
	}



	/**
	 * 출석체크 완료된 회원의 포인트에 point만큼 추가 후 내역에 추가합니다.<br>
	 * point값이 0이라면 {@link ErrorException} 이 발생합니다.
	 *
	 * @param memberId	회원번호
	 * @param point				변경할 포인트 값
	 */
	@Transactional
	public void addPointForAttendance( String memberId, int point ) {
		if(point == 0) {
			logger.debug("{} 회원에 추가할 포인트가 없습니다.", memberId);
			throw new  ErrorException(ErrorCode.POINT_ADD_NONE);
		}

		//포인트 및 내역 추가
		Long memberPoint = memberInfoDao.updatePointAndReturn( memberId, point );
		if( Objects.nonNull(memberPoint)) {
			PointHistory entity = PointHistory.builder().member(Member.builder().id(memberId).build())
							.type("EARM").points((long) point).reason("출석체크").balancePoints(memberPoint).build();

			PointHistory pointHistory =  historyDAO.saveHistory(entity);
			logger.debug("{} 회원 포인트 변경 성공 - 추가할 포인트: {}, 현재 사용자 포인트: {}", pointHistory.getMember().getId(), pointHistory.getPoints(), pointHistory.getBalancePoints());
		}else  {
			throw new ErrorException(ErrorCode.MEMBER_ATTENDANCE_UNKNOWN);
		}
	}



	/**
	 * 회원번호에 해당하는 포인트 정보를 반환합니다.
	 *
	 * @param memberId		회원 식별번호
	 * @return	현재, 누적, 사용된 포인트 정보를 반환합니다.
	 */
	public MemberResponseDTO.Point getMemberPointSummary( String memberId ) {
		 MemberResponseDTO.Point daoResult = historyDAO.findSumPointByType( memberId );
		Long point = memberInfoDao.findPointByMemberId( memberId );


		return MemberResponseDTO.Point.builder()
				.currentPoint(point).earmPoint(daoResult.getEarmPoint()).usePoint(daoResult.getUsePoint())
				.build();
	}

}
