package com.areum.moneymanager.service.member;

import com.areum.moneymanager.dao.member.AttendanceDao;
import com.areum.moneymanager.dto.common.request.DateRequest;
import com.areum.moneymanager.dto.member.request.MemberAttendanceRequest;
import com.areum.moneymanager.dto.member.response.MemberAttendanceResponse;
import com.areum.moneymanager.entity.Attendance;
import com.areum.moneymanager.exception.code.ErrorCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;


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
 * 		</tbody>
 * </table>
 */
@Service
public class AttendanceService {

	private final int DEFAULT_POINT = 5;

	private final PointService pointService;
	private final AttendanceDao attendanceDAO;
	;

	public AttendanceService(AttendanceDao attendanceDAO, PointService pointService) {
		this.attendanceDAO = attendanceDAO;
		this.pointService = pointService;
	}

	private final Logger logger = LogManager.getLogger(this);


	/**
	 * 주어진 날짜에 맞춰 달력을 반환합니다.<p>
	 * 달력은 일요일이 시작일이고 토요일이 끝일이며, 주의 빈 날짜는 null로 설정됩니다.<br>
	 * 각 주는 List로 저장되며, 주의 각 날짜는 Integer로 저장됩니다.
	 *
	 * @param date 달력 날짜
	 * @return 달력 날짜를 담은 리스트
	 */
	public List<List<Integer>> createCalendar(LocalDate date) {
		List<List<Integer>> resultList = new ArrayList<>();

		/*
			- start	: 달의 1일의 시작 요일	(1: 월요일 ~ 7:일요일)
			- end	:	달의 마지막 일
		*/
		int start = date.withDayOfMonth(1).get(ChronoField.DAY_OF_WEEK);
		start = (start == 7) ? 0 : start;
		int last = date.lengthOfMonth();

		//첫주 시작요일까지 빈칸 채우기
		List<Integer> currentWeek = new ArrayList<>();
		for (int i = 0; i < start; i++) {
			currentWeek.add(null);
		}

		//날짜 채우기
		for (int day = 1; day <= last; day++) {
			currentWeek.add(day);

			if (currentWeek.size() == 7) {
				resultList.add(new ArrayList<>(currentWeek));
				currentWeek.clear();
			}
		}

		//마지막 주
		if ((!currentWeek.isEmpty() && currentWeek.size() != 7)) {
			while (currentWeek.size() < 7) {
				currentWeek.add(null);
			}
			resultList.add(currentWeek);
		}

		return resultList;
	}


	/**
	 * 날짜 범위 내에서 회원이 출석을 완료한 날짜들을 반환합니다.<p>
	 * 예를 들어, N월의 1일부터 3일까지 출석이 완료되었다면 1, 2, 3을 리스트에 저장합니다.
	 *
	 * @param memberId 회원 식별번호
	 * @param view     달력 표시를 위한 날짜정보를 담은 객체
	 * @return 회원의 완료된 출석 일자 리스트
	 */
	public MemberAttendanceResponse getDayByCompleteAttend(String memberId, MemberAttendanceRequest.CalendarView view) {
		DateRequest date = new DateRequest( view.getDate() );

		LocalDate start = LocalDate.of(date.getYear(), date.getMonth(), 1);
		LocalDate end = LocalDate.of(date.getYear(), date.getMonth(), start.lengthOfMonth()).plusDays(1);

		//회원의 출석완료 리스트 조회
		List<Attendance> entityList = attendanceDAO.findCompleteAttendBetweenDate(memberId, start.toString(), end.toString());

		List<String> dayList = new ArrayList<>();
		for (Attendance entity : entityList) {
			String day = String.valueOf(entity.getAttendanceDate().toLocalDate().getDayOfMonth());
			dayList.add(day);
		}

		return MemberAttendanceResponse.builder().dates(dayList).build();
	}


	/**
	 * 회원의 출석을 진행합니다. <br>
	 * 이미 출석 완료했을 경우 {@link IllegalArgumentException} 예외가 발생합니다.<br>
	 * 어제 출석을 햇으면 연속 출석 일수를 증가시키고, 아니라면 초기화합니다.
	 * 출석 완료 후 회원의 포인트가 추가됩니다.
	 *
	 * @param memberId 회원번호
	 * @param date     출석 진행할 날짜
	 */
	@Transactional
	public void createAttendance(String memberId, DateRequest date) {
		LocalDate clientDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
		LocalDate today = LocalDate.now();

		if (!clientDate.equals(today)) {
			throw new IllegalArgumentException(ErrorCode.MEMBER_DATE_FORMAT.getMessage());
		}

		//오늘 날짜 출석여부 확인
		boolean isTodayComplete = attendanceDAO.hasCheckedInDate(memberId, clientDate);
		if (isTodayComplete) {
			throw new IllegalArgumentException(ErrorCode.MEMBER_ATTENDANCE_COMPLETE.getMessage());
		}

		attendanceDAO.saveContinuousAttend(memberId);

		//연속출석 확인
		boolean isContinuous = attendanceDAO.hasCheckedInDate(memberId, clientDate.minusDays(1));
		if (!isContinuous) {
			attendanceDAO.resetContinuousDate(memberId);
		}

		//포인트 추가
		pointService.addPointForAttendance(memberId, DEFAULT_POINT);

		logger.debug("{} 회원 출석 성공", memberId);
	}


	/**
	 * 이동할 달력이 현재 날짜인지 확인합니다.<p>
	 * 현재 날짜와 같다면 true를 반환하고, 그렇지 않다면 false를 반환합니다.
	 *
	 * @param moveDate 이동할 달력 날짜
	 * @return 현재 날짜면 true, 아니라면 false
	 */
	public boolean isToday(MemberAttendanceRequest.CalendarMove moveDate) {
		DateRequest date = new DateRequest( moveDate.getDate() );
		LocalDate today = LocalDate.now();

		return date.getYear() == today.getYear() && date.getMonth() == today.getMonthValue();
	}

}
