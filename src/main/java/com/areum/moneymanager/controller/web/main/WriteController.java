package com.areum.moneymanager.controller.web.main;

import com.areum.moneymanager.dto.request.main.BudgetBookRequestDTO;
import com.areum.moneymanager.dto.response.main.BudgetBookResponseDTO;
import com.areum.moneymanager.exception.ErrorException;
import com.areum.moneymanager.service.main.BudgetBookService;
import com.areum.moneymanager.service.main.validation.DateValidationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * 가계부 작성을 담당하는 클래스</br>
 * 가계부 작성, 수정 등의 기능을 처리
 *
 * @version 1.0
 */
@Controller
@RequestMapping("/budgetBook/write")
public class WriteController {

	private final Logger logger = LogManager.getLogger(this);

	private final BudgetBookService budgetBookService;

	public WriteController( BudgetBookService budgetBookService ) {
		this.budgetBookService = budgetBookService;
	}




	/**
	 * 가계부 작성을 위한 가계부 유형과 날짜 선택 페이지 요청을 처리합니다.
	 *
	 * @param  model		뷰에 전달할 객체
	 * @return 수입 또는 지출 작성 페이지
	 */
	@GetMapping
	public String getStep1Page( Model model ) {
		LocalDate today = LocalDate.now();

		//사용자에게 전달할 정보
		model.addAttribute("year", today.getYear());
		model.addAttribute("month", today.getMonthValue());
		model.addAttribute("day", today.getDayOfMonth());
		model.addAttribute("lastDay", today.lengthOfMonth());
		model.addAttribute("today", today.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));


		return "/main/budgetBook_writeStep1";
	}



	/**
	 * 가계부 유형(수입/지출)에 따른 작성 페이지 요청을 처리합니다.<p>
	 * 가계부 유형에 따른 카테고리 리스트와 회원마다 등록할 수 있는 이미지 개수를 전달합니다.
	 *
	 * @param type				가계부 유형
	 * @param date				가계부 날짜
	 * @param session	사용자 식별 및 정보를 저장하는 객체
	 * @param model		뷰에 전달할 객체
	 * @return	수입 또는 지출 작성 페이지
	 */
	@PostMapping("/{type}")
	public String getStep2Page( @PathVariable String type, @RequestParam  String date, HttpSession session, Model model ) {
		try {
			//날짜 검증
			if( date.length() != 8 ) {
				throw new IllegalArgumentException("날짜 값의 길이가 맞지 않습니다.");
			}
			DateValidationService.checkDateAvailability( date );


			BudgetBookRequestDTO.Setting set = BudgetBookRequestDTO.Setting.builder().type(type).date(date).build();
			BudgetBookResponseDTO.Write write = budgetBookService.getWriteByData( (String)session.getAttribute("mid"), set );

			model.addAttribute("write", write);
			model.addAttribute("budgetBook", BudgetBookRequestDTO.Create.builder().fix( new BudgetBookRequestDTO.Fix() ).place( new BudgetBookRequestDTO.Place() ).build());

			return "/main/budgetBook_writeStep2";
		} catch ( IllegalArgumentException | ParseException e) {
			if( e instanceof ParseException ) {
				model.addAttribute("error", "날짜 형식이 맞지 않습니다.");
				logger.debug("가계부 날짜가 잘못된 형식입니다.");
			}else {
				model.addAttribute("error", e.getMessage());
				logger.debug("가계부 날짜가 잘못되었습니다. (원인: {})", e.getMessage());
			}

			model.addAttribute("method", "get");
			model.addAttribute("url", "/budgetBook/write");


			return "alert";
		}
	}



	/**
	 *	가계부 위치를 알기위한 검색 페이지 요청을 처리합니다.
	 * @return	카카오 장소 검색 페이지
	 */
	@GetMapping("/map")
	public String getMapPage() {
		return "/main/kakaoMap";
	}



	/**
	 * 입력한 정보로 가계부 저장 요청을 처리합니다.<p>
	 * 정상적으로 가계가 등록되면 가계부 정보를 한번에 볼 수 있는 가계부 리스트 페이로 이동하며, <br>
	 * 등록되지 않으면 안내문구 노출 후 가계부 작성 페이지로 이동됩니다.
	 *
	 * @param create	가계부 정보
	 * @param session	사용자 식별 및 정보를 저장하는 객체
	 * @param model  	뷰에 전달할 객체
	 * @return	가계부 리스트 페이지
	 */
	@PostMapping
	public String postWrite( @ModelAttribute("budgetBook") BudgetBookRequestDTO.Create create, HttpSession session, Model model ) {
		String memberId = (String)session.getAttribute("mid");

		try{
			budgetBookService.createBudgetBook( memberId, create );

			return "redirect:/budgetBook/write";
		}catch ( ErrorException  e ) {
			model.addAttribute("error", e.getErrorMessage());
			model.addAttribute("method", "get");
			model.addAttribute("url", "/budgetBook/write");


			return "/alert";
		}
	}
}
