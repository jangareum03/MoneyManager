package com.areum.moneymanager.controller.web.members;

import com.areum.moneymanager.dto.request.member.AttendanceRequestDTO;
import com.areum.moneymanager.service.main.validation.DateValidationService;
import com.areum.moneymanager.service.member.AttendanceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;


/**
 * 회원 출석체크와 관련된 화면을 담당하는 클래스</br>
 * 달력이동, 달력표시 등의 화면을 처리
 *
 * @version 1.0
 */
@Controller
@RequestMapping("/attendance")
public class AttendanceController {

	private final Logger logger = LogManager.getLogger(this);
	private final AttendanceService attendanceService;

	public AttendanceController( AttendanceService attendanceService ) {
		this.attendanceService = attendanceService;
	}



	/**
	 * 선택한 달력 날짜의 정보와 출석 정보 요청을 처리합니다.
	 * <p>
	 * 달력의 년(month)과 월(year)이 0이면 현재 날짜로 설정되며, 선택한 달력이 현재 년도와 월과 같다면 오늘 날짜가 포함됩니다.<br>
	 * 달력 날짜를 기준으로 해당 월의 첫째 날부터 마지막 날까지 회원의 출석 날짜와 달력 리스트를 전달합니다.
	 *
	 * @param moveDate		이동할 달력 날짜
	 * @param model					뷰에 전달할 객체
	 * @return	홈 페이지
	 */
	@GetMapping
	public String getAttendancePage( Model model, @ModelAttribute AttendanceRequestDTO.Move moveDate ) {
		LocalDate today = LocalDate.now();

		//파라미터에서 날짜값이 없는 경우
		if( DateValidationService.isNullOrZero( moveDate.getYear() ) || DateValidationService.isNullOrZero( moveDate.getMonth() ) ) {
			return "redirect:/attendance?year=" + today.getYear() + "&month=" + today.getMonthValue();
		}

		LocalDate date = LocalDate.of(moveDate.getYear(), moveDate.getMonth(), 1);

		//달력 생성
		List<List<Integer>> calendar = attendanceService.createCalendar( date );


		if( moveDate.getYear() == today.getYear() && moveDate.getMonth() == today.getMonthValue() ) {
			model.addAttribute("today", today.getDayOfMonth());
		}


		//사용자에게 전달할 정보
		model.addAttribute("year", date.getYear());
		model.addAttribute("month", date.getMonthValue());
		model.addAttribute("calendar", calendar);


		return "/main/home";
	}


}
