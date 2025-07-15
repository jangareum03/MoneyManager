package com.areum.moneymanager.controller.api.members;


import com.areum.moneymanager.dto.request.member.AttendanceRequestDTO;
import com.areum.moneymanager.dto.response.ApiResponseDTO;
import com.areum.moneymanager.dto.response.member.AttendanceResponseDTO;
import com.areum.moneymanager.service.member.AttendanceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;

/**
 * 회원 출석체크에 대한 데이터 요청을 담당하는 클래스<br>
 * 출석체크 확인등의 기능을 처리
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/attendance")
public class AttendanceApiController {

	private final Logger logger = LogManager.getLogger(this);
	private final AttendanceService attendanceService;

	public AttendanceApiController( AttendanceService attendanceService ) {
		this.attendanceService = attendanceService;
	}


	@GetMapping
	public AttendanceResponseDTO.Complete getCompletedList( HttpSession session, AttendanceRequestDTO.Calendar date) {
		int year = (date.getYear() == 0) ? LocalDate.now().getYear() : date.getYear();
		int month = (date.getMonth() == 0) ? LocalDate.now().getMonthValue() : date.getMonth();

		return attendanceService.getDayByCompleteAttend( (String)session.getAttribute("mid"), AttendanceRequestDTO.Calendar.builder().year(year).month(month).build() );
	}


	/**
	 * 오늘 날짜의 출석 요청을 처리합니다.<br>
	 * 출석 완료여부에 따라 안내 메시지가 달라지며, 출석하지 않았다면 출석 완료합니다.
	 *
	 * @param session	사용자 식별 및 정보를 저장하는 객체
	 * @param today	출석체크 진행할 날짜
	 */
	@PostMapping
	public ResponseEntity<ApiResponseDTO> postAttendance(HttpSession session, @RequestBody AttendanceRequestDTO.Date today ) {
		String memberId = (String)session.getAttribute("mid");

		try{
			attendanceService.createAttendance(memberId, today);

			return ResponseEntity.ok(ApiResponseDTO.builder().success(true).message("출석 완료했습니다.").build());
		}catch( IllegalArgumentException e ) {
			return ResponseEntity.ok( ApiResponseDTO.builder().success(false).message(e.getMessage()).build() );
		}
	}




}

