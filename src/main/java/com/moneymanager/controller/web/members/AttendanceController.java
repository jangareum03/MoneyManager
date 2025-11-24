package com.moneymanager.controller.web.members;

import com.moneymanager.domain.budgetBook.vo.YearMonthVO;
import com.moneymanager.domain.budgetBook.vo.YearVO;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.service.member.AttendanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.controller.web.main<br>
 *  * 파일이름       : AccountRecoveryController<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 출석체크 관련 화면을 처리하는 클래스
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
@Controller
@RequestMapping
public class AttendanceController {

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
	 * @param model					뷰에 전달할 객체
	 * @param year						이동할 날짜 년도
	 * @param month					이동할 날짜 월
	 * @return	홈 페이지
	 */
	@GetMapping("/attendance")
	public String getAttendancePage( Model model, @RequestParam(required = false) String year, @RequestParam(required = false) String month ) {
		YearMonthVO vo = null;

		try{
			vo = YearMonthVO.builder().year(new YearVO(year)).month(month).build();

			//년도와 월이 현재와 동일한 지 확인
			LocalDate today = LocalDate.now();
			if( today.getYear() == vo.getYear() && today.getMonthValue() == vo.getMonth() ) {
				model.addAttribute("today", today.getDayOfMonth());
			}
		}catch ( ClientException e ) {
			//기본값(=오늘 날짜)으로 설정
			LocalDate today = LocalDate.now();

			vo = YearMonthVO.builder()
							.year(new YearVO(year))
									.month(month)
											.build();

			model.addAttribute("today", today.getDayOfMonth());
		}


		//달력 생성
		List<List<Integer>> calendar = attendanceService.createCalendar( vo );

		//사용자에게 전달할 정보
		model.addAttribute("year", vo.getYear());
		model.addAttribute("month", vo.getMonth());
		model.addAttribute("calendar", calendar);

		return "/main/home";
	}


}
