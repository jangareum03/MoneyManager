package com.moneymanager.service.member;

import com.moneymanager.dao.member.MemberInfoDaoImpl;
import com.moneymanager.dao.member.history.PointHistoryDaoImpl;
import com.moneymanager.domain.member.dto.MemberMyPageResponse;
import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.member.MemberInfo;
import com.moneymanager.domain.member.PointHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



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
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Slf4j
@Service
public class PointService {

	private final MemberInfoDaoImpl memberInfoDao;
	private final PointHistoryDaoImpl historyDAO;

	public PointService(MemberInfoDaoImpl memberInfoDao, PointHistoryDaoImpl historyDAO ) {
		this.memberInfoDao = memberInfoDao;
		this.historyDAO = historyDAO;
	}



	/**
	 * 출석체크 완료된 회원의 포인트에 point만큼 추가 후 내역에 추가합니다.<br>
	 *
	 * @param memberId	회원번호
	 * @param point				변경할 포인트 값
	 */
	@Transactional
	public void addPointForAttendance( String memberId, long point ) {
		if(point == 0) {
			log.debug("{} 회원에 추가할 포인트가 없습니다.", memberId);
			throw new RuntimeException("");
		}

		//포인트 및 내역 추가
		Member member = Member.builder().id(memberId).memberInfo( MemberInfo.builder().point(point).build() ).build();
		Long memberPoint = memberInfoDao.updatePointAndReturn( member );
		if( memberPoint >= 0) {
			PointHistory entity = PointHistory.builder().member(Member.builder().id(memberId).build())
							.type("EARM").points(point).reason("출석체크").balancePoints(memberPoint).build();

			PointHistory pointHistory =  historyDAO.saveHistory(entity);
			log.debug("{} 회원 포인트 변경 성공 - 추가할 포인트: {}, 현재 사용자 포인트: {}", pointHistory.getMember().getId(), pointHistory.getPoints(), pointHistory.getBalancePoints());
		}else  {
			throw new RuntimeException("");
		}
	}



	/**
	 * 회원번호에 해당하는 포인트 정보를 반환합니다.
	 *
	 * @param memberId		회원 식별번호
	 * @return	현재, 누적, 사용된 포인트 정보를 반환합니다.
	 */
	public MemberMyPageResponse.Point getMemberPointSummary(String memberId ) {
		MemberMyPageResponse.Point daoResult = historyDAO.findSumPointByType( memberId );
		Long point = memberInfoDao.findPointByMemberId( memberId );


		return MemberMyPageResponse.Point.builder()
				.currentPoint(point).earmPoint(daoResult.getEarmPoint()).usePoint(daoResult.getUsePoint())
				.build();
	}

}
