package com.moneymanager.controller.api.members;


import com.moneymanager.dto.common.ApiResultDTO;
import com.moneymanager.dto.common.request.DateRequest;
import com.moneymanager.dto.member.request.MemberAttendanceRequest;
import com.moneymanager.dto.member.response.MemberAttendanceResponse;
import com.moneymanager.service.member.AttendanceService;
import com.moneymanager.vo.YearMonthDayVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.controller.api.members<br>
 * * 파일이름       : AttendanceApiController<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 15<br>
 * * 설명              : 회원의 출석체크 API를 제공하는 클래스
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
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 8. 12</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 수정] getCompletedList 매개변수를 MemberAttendanceRequest.CalendarView → year/month 로 수정</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@RestController
@RequestMapping("/api/attendance")
public class AttendanceApiController {

	private final Logger logger = LogManager.getLogger(this);
	private final AttendanceService attendanceService;

	public AttendanceApiController(AttendanceService attendanceService) {
		this.attendanceService = attendanceService;
	}


	/**
	 * 특정 회원의 출석 완료 리스트 조회 요청을 처리합니다. <br>
	 * <p>
	 *		요청 파라미터로 전달된 년도(year)와 월(month)으로 조회하며,
	 *		만약 year 또는 month 값이  없거나 유효하지 않으면 현재 년도와 월을 기본값으로 사용합니다.
	 * </p>
	 *
	 * @param session			사용자 식별 및 정보를 저장하는 객체
	 * @param year				조회할 연도
	 * @param month			조회할 월
	 * @return	출석 완료된 일자 목록이 포함된 {@link MemberAttendanceResponse} 객체
	 */
	@GetMapping
	public MemberAttendanceResponse getCompletedList(HttpSession session, @RequestParam String year, @RequestParam String month) {
		//요청값 → VO 변환
		YearMonthDayVO vo = YearMonthDayVO.builder().year(year).month(month).build();

		return attendanceService.getDayByCompleteAttend((String) session.getAttribute("mid"), vo);
	}


	/**
	 * 오늘 날짜의 출석 요청을 처리합니다.<br>
	 * 출석 완료여부에 따라 안내 메시지가 달라지며, 출석하지 않았다면 출석 완료합니다.
	 *
	 * @param session 사용자 식별 및 정보를 저장하는 객체
	 * @param attendToday   출석체크 진행할 날짜
	 */
	@PostMapping
	public ResponseEntity<ApiResultDTO> postAttendance(HttpSession session, @RequestBody MemberAttendanceRequest.AttendToday attendToday) {
		String memberId = (String) session.getAttribute("mid");

		try {
			DateRequest date = new DateRequest( attendToday.getToday() );
			attendanceService.createAttendance(memberId, date);

			return ResponseEntity.ok(ApiResultDTO.builder().success(true).message("출석 완료했습니다.").build());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.ok(ApiResultDTO.builder().success(false).message(e.getMessage()).build());
		}
	}


}

