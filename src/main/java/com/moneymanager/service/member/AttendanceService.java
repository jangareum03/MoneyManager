package com.moneymanager.service.member;

import com.moneymanager.dao.member.AttendanceDao;
import com.moneymanager.domain.member.dto.MemberAttendanceResponse;
import com.moneymanager.domain.member.Attendance;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.service.member<br>
 * * 파일이름       : AttendanceService<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 22. 11. 7<br>
 * * 설명              : 회원 출석 비즈니스 로직을 처리하는 클래스
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
 * 		 	  <td>22. 11. 7</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성(버전 1.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 7. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 11. 17</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[매개변수 수정] YearMonthVO → AttendanceDate</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@Service
public class AttendanceService {

	private final PointService pointService;
	private final AttendanceDao attendanceDAO;

	public AttendanceService(AttendanceDao attendanceDAO, PointService pointService) {
		this.attendanceDAO = attendanceDAO;
		this.pointService = pointService;
	}

	private final Logger logger = LogManager.getLogger(this);

//
//	/**
//	 * 주어진 날짜에 맞춰 달력을 반환합니다.<p>
//	 * 달력은 일요일이 시작일이고 토요일이 끝일이며, 주의 빈 날짜는 null로 설정됩니다.<br>
//	 * 각 주는 List로 저장되며, 주의 각 날짜는 Integer로 저장됩니다.
//	 *
//	 * @param vo 	달력 날짜
//	 * @return 달력 날짜를 담은 리스트
//	 */
//	public List<List<Integer>> createCalendar(YearMonthVO vo) {
//		List<List<Integer>> resultList = new ArrayList<>();
//
//		/*
//			- start	: 달의 1일의 시작 요일	(1: 월요일 ~ 7:일요일)
//			- end	:	달의 마지막 일
//		*/
//		int start = vo.getFirstDate().get(ChronoField.DAY_OF_WEEK);
//		start = (start == 7) ? 0 : start;
//		int last = vo.getLastDate().lengthOfMonth();
//
//		//첫주 시작요일까지 빈칸 채우기
//		List<Integer> currentWeek = new ArrayList<>();
//		for (int i = 0; i < start; i++) {
//			currentWeek.add(null);
//		}
//
//		//날짜 채우기
//		for (int day = 1; day <= last; day++) {
//			currentWeek.add(day);
//
//			if (currentWeek.size() == 7) {
//				resultList.add(new ArrayList<>(currentWeek));
//				currentWeek.clear();
//			}
//		}
//
//		//마지막 주
//		if ((!currentWeek.isEmpty() && currentWeek.size() != 7)) {
//			while (currentWeek.size() < 7) {
//				currentWeek.add(null);
//			}
//			resultList.add(currentWeek);
//		}
//
//		return resultList;
//	}
//
//
//	/**
//	 * 회원({@code id})의 해당 연월({@link YearMonthDayVO})에 출석이 완료된 날짜들의 목록을 조회힙니다.
//	 * <p>
//	 *		내부적으로 해당 연월의 첫째날부터 마지막 날까지의 기간을 기준으로 출석 완료된 날짜 데이터를 조회합니다. <br>
//	 *		그 후, 출석한 날짜(=일자)만 문자열 리스트로 추출하여 {@link MemberAttendanceResponse}로 반환합니다. <br>
//	 *		예를 들어, N월의 1일부터 3일까지 출석이 완료되었다면 1, 2, 3을 리스트에 저장합니다.
//	 * </p>
//	 *
//	 *
//	 * @param id			 	회원 식별번호
//	 * @param vo     		완료된 출석 리스트를 조회하기 위한 날짜 객체
//	 * @return 출석 완료된 일자를 리스트로 담은 {@link MemberAttendanceResponse} 객체
//	 */
//	public MemberAttendanceResponse getDayByCompleteAttend(String id, YearMonthVO vo) {
//		//회원의 출석완료 리스트 조회
//		List<Attendance> entityList = attendanceDAO.findCompleteAttendBetweenDate(id, vo.getFirstDate(), vo.getLastDate());
//
//		List<String> dayList = entityList.stream()
//				.map(entity -> String.valueOf(entity.getAttendanceDate().getDayOfMonth()))
//				.collect(Collectors.toList());
//
//		return MemberAttendanceResponse.builder().dates(dayList).build();
//	}
//
//
//	/**
//	 * 회원<code>id</code>의 오늘 날짜로 출석을 등록합니다.
//	 * <p>
//	 *     이미 출석을 완료한 상태라면 예외가 발생합니다. <br>
//	 *     출석 완료 후 포인트룰 추가 후 연속 출석이라면 연속출석일에 1을 추가합니다.
//	 * </p>
//	 *
//	 * @param id 		회원 식별번호
//	 * @param vo     출석 진행할 날짜
//	 */
//	@Transactional
//	public void createAttendance(String id, YearMonthDayVO vo) {
//		validateToday(id, vo.getDate());
//		validateDuplication(id, vo.getDate());
//
//		//출석 저장 및 포인트 추가
//		int point = 5;
//		attendanceDAO.saveContinuousAttend(id);
//		pointService.addPointForAttendance(id, point);
//
//		//연속 출석
//		updateContinuousAttendance(id, vo.getDate().minusDays(1));
//	}


	/**
	 * 날짜(date)가 오늘인지 확인합니다.
	 * <p>
	 *     오늘 날짜가 아니라면 예외가 발생합니다.
	 * </p>
	 *
	 * @param id			회원 식별번호
	 * @param date		검증할 출석날짜
	 */
	private void validateToday( String id, LocalDate date ) {
		LocalDate today = LocalDate.now();
		if (!today.isEqual(date)) {
			//ErrorDTO<String> errorDTO = ErrorDTO.<String>builder().errorCode(ErrorCode.ATTEND_TODAY_INVALID).requestData(String.format("id=%s, date=%s", id, date)).build();
		}
	}


	/**
	 * 회원의 출석완료 리스트 중에서 날짜(date)가 있는지 확인합니다.
	 * <p>
	 *     날짜로 회원이 출석을 이미 완료했다면 예외가 발생합니다.
	 * </p>
	 *
	 * @param id			회원 식별번호
	 * @param date		확인할 출석날짜
	 */
	private void validateDuplication(String id, LocalDate date) {
		if(attendanceDAO.hasCheckedInDate(id, date) ) {
			//ErrorDTO<String> errorDTO = ErrorDTO.<String>builder().errorCode(ErrorCode.ATTEND_TODAY_DUPLICATE).requestData(String.format("id=%s, date=%s", id, date)).build();
		}
	}


	/**
	 * 회원<code>id</code>의 완료된 출석 리스트 중에서 날짜<code>date</code>가 존재하는지 확인합니다.
	 * <p>
	 *     날짜(=어제 날짜)가 존재하지 않는다면, 연속 출석이 아니므로 연속출석일자를 0으로 초기화합니다.<br>
	 *     예를 들어, 회원의 출석 리스트에서 8월 2일(=어제 날짜)이 존재하지 않는다면 연속출석이 아니므로 0으로 초기화가 됩니다.
	 * </p>
	 *
	 * @param id			회원 식별번호
	 * @param date		연속 출석 확인할 날짜(=어제 날짜)
	 */
	private void updateContinuousAttendance(String id, LocalDate date) {
		boolean isContinuous = attendanceDAO.hasCheckedInDate(id, date);
		if(!isContinuous) {
			attendanceDAO.resetContinuousDate(id);
		}
	}
}
