package com.moneymanager.controller.api.members;


import com.moneymanager.domain.global.dto.ApiResultDTO;
import com.moneymanager.domain.member.dto.MemberAttendanceRequest;
import com.moneymanager.domain.member.dto.MemberAttendanceResponse;
import com.moneymanager.service.member.AttendanceService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping("/api/attendance")
public class AttendanceApiController {

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
//		YearMonthVO vo = YearMonthVO.builder().year(new YearVO(year)).month(month).build();
//
//		return attendanceService.getDayByCompleteAttend((String) session.getAttribute("mid"), vo);
		return null;
	}


	/**
	 * 특정 회원의 출석 요청을 처리합니다.<br>
	 * 세션에서 현재 로그인된 회원 ID를 가져와서 지정된 날짜에 출석을 진행합니다.
	 * <ul>
	 *     <li>출석이 성공하면 출석 완료 메시지를 반환합니다.</li>
	 *     <li>출석이 실패하면 예외 메시지를 반환합니다.</li>
	 * </ul>
	 *
	 * @param session 			사용자 식별 및 정보를 저장하는 객체
	 * @param date				   	출석체크 진행할 날짜
	 */
	@PostMapping
	public ResponseEntity<ApiResultDTO> postAttendance(HttpSession session, @RequestBody MemberAttendanceRequest date) {
		String memberId = (String) session.getAttribute("mid");
//		YearMonthDayVO vo = YearMonthDayVO.builder()
//				.vo(YearMonthVO.builder().year(new YearVO(date.getYear())).month(date.getMonth()).build())
//				.day(date.getDay()).build();
//
//		attendanceService.createAttendance(memberId, vo);

		return ResponseEntity.ok(ApiResultDTO.builder().success(true).message("출석 완료했습니다.").build());

	}


}

