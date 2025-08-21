package com.moneymanager.controller.web.main;

import com.moneymanager.dto.budgetBook.CategoryDTO;
import com.moneymanager.dto.budgetBook.request.BudgetBookSearchRequest;
import com.moneymanager.dto.budgetBook.response.BudgetBookDetailResponse;
import com.moneymanager.dto.budgetBook.response.BudgetBookListResponse;
import com.moneymanager.dto.budgetBook.response.BudgetBookSearchResponse;
import com.moneymanager.dto.common.request.DateRequest;
import com.moneymanager.enums.type.DateType;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.service.main.BudgetBookService;
import com.moneymanager.service.main.ImageServiceImpl;
import com.moneymanager.service.main.validation.DateValidationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.controller.web.main<br>
 * * 파일이름       : BudgetBookController<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 15<br>
 * * 설명              : 가계부 관련 화면을 처리하는 클래스
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
 * 		</tbody>
 * </table>
 */
@Controller
@RequestMapping("/budgetBooks")
public class BudgetBookController {

	private final Logger logger = LogManager.getLogger(this);

	private final BudgetBookService budgetBookService;
	private final ImageServiceImpl imageService;

	public BudgetBookController(BudgetBookService budgetBookService, @Qualifier("budgetImage") ImageServiceImpl imageService) {
		this.budgetBookService = budgetBookService;
		this.imageService = imageService;
	}


	/**
	 * 가계부 내역 조회 페이지 요청을 처리합니다.<br>
	 * 가계부 요약 그래프는 날짜기간별로 그래프 유형이 달라집니다. 날짜 기간 내에 등록된 가계부가 없다면 그래프는 별 표시가 없습니다.
	 *
	 * @param session 사용자 식별 및 정보를 저장하는 객체
	 * @param model   뷰에 전달할 객체
	 * @param date    가계부 조회 날짜
	 * @return 내역 조회 페이지
	 */
	@GetMapping("/list/{type}")
	public String getBudgetPage(@PathVariable String type, DateRequest.WeekRange date, HttpSession session, Model model) {
		String memberId = (String) session.getAttribute("mid");

		DateType dateType = DateType.from(type);
		DateRequest resetDate = null;
		switch (dateType) {
			case YEAR:
				resetDate = new DateRequest(
						DateRequest.YearRange.builder()
								.year(DateValidationService.getValidYearOrCurrent(date.getYear()))
								.build()
				);
				break;
			case WEEK:
				resetDate = new DateRequest(
						DateRequest.WeekRange.builder()
								.year(DateValidationService.getValidYearOrCurrent(date.getYear()))
								.month(DateValidationService.getValidMonthOrCurrent(date.getMonth()))
								.week(DateValidationService.getValidWeekOrCurrent(date.getWeek()))
								.build()
				);
				break;
			case MONTH:
			default:
				resetDate = new DateRequest(
						DateRequest.MonthRange.builder()
								.year(DateValidationService.getValidYearOrCurrent(date.getYear()))
								.month(DateValidationService.getValidMonthOrCurrent(date.getMonth()))
								.build()
				);
		}


		BudgetBookSearchRequest search = BudgetBookSearchRequest.builder().date(resetDate).mode("all").keywords(null).build();
		BudgetBookListResponse budgetBookList = budgetBookService.getBudgetBooksForSummary(memberId, search);

		//타임리프에서 나열하기 위한 전환
		Map<String, List<List<BudgetBookListResponse.Card>>> formatSummary = new LinkedHashMap<>();
		for(BudgetBookListResponse.DayCards dayCards  : budgetBookList.getCards() ) {
			List<BudgetBookListResponse.Card> cardList = dayCards.getCardList();
			List<List<BudgetBookListResponse.Card>> cardGroup = new ArrayList<>();

			for( int i=0; i<cardList.size(); i+= 3 ) {
				List<BudgetBookListResponse.Card> cards = new ArrayList<>();

				for( int j=0; j<3; j++ ) {
					int index = i+j;
					cards.add( index < cardList.size() ? cardList.get(index) : null );
				}

				cardGroup.add(cards);
			}

			formatSummary.put(dayCards.getDate(), cardGroup);
		}



		model.addAttribute("type", type);
		model.addAttribute("title", budgetBookService.makeTitleByType(resetDate));
		model.addAttribute("search", BudgetBookSearchResponse.builder().type(type).mode("all").build());
		model.addAttribute("price", budgetBookList.getStats());
		model.addAttribute("summary", formatSummary);


		//차트를 위한 저장
		session.setAttribute("chart", resetDate);

		return "/main/budgetBook_list";
	}


	/**
	 * 가계부 번호에 해당하는 상세정보 페이지를 요청을 처리합니다.
	 *
	 * @param session 사용자 식별 및 정보를 저장하는 객체
	 * @param model   뷰에 전달할 객체
	 * @param id      가계부 식별 번호
	 * @return 가계부 상세 페이지
	 */
	@GetMapping("/{id}")
	public String getBudgetDetailPage(HttpSession session, Model model, @PathVariable Long id, @RequestParam(required = false, defaultValue = "view") String mode) {
		String memberId = (String) session.getAttribute("mid");

		try {
			BudgetBookDetailResponse budgetBook = budgetBookService.getBudgetBookById(memberId, id, mode);
			model.addAttribute("budgetBook", budgetBook);

			if (mode.equals("edit")) {
				String type = budgetBook.getCategory().getCode().startsWith("01") ? "in" : "out";

				List<CategoryDTO> category = budgetBookService.getCategoryByStep(budgetBook.getCategory().getCode());

				model.addAttribute("selectCategory", category);
				model.addAttribute("category", budgetBookService.getCategoriesByCode(category));
				model.addAttribute("max", imageService.getLimitImageCount(memberId));

				return "/main/budgetBook_update";
			} else {
				return "/main/budgetBook_detail";
			}

		} catch (ClientException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("method", "get");
			model.addAttribute("url", "/budgetBooks");

			return "alert";
		}
	}
}
