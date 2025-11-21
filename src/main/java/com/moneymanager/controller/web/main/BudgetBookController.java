package com.moneymanager.controller.web.main;

import com.moneymanager.domain.ledger.dto.*;
import com.moneymanager.domain.ledger.dto.LedgerResponse;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.service.main.BudgetBookService;
import com.moneymanager.service.main.ImageServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Controller
@RequestMapping("/budgetBooks")
public class BudgetBookController {

	private final BudgetBookService budgetBookService;
	private final ImageServiceImpl imageService;

	public BudgetBookController(BudgetBookService budgetBookService, @Qualifier("budgetImage") ImageServiceImpl imageService) {
		this.budgetBookService = budgetBookService;
		this.imageService = imageService;
	}


	/**
	 *	회원의 가계부 내역을 조회하여 날짜를 카드 리스트로 그룹화하고, Thymeleaf 뷰에 필요한 모델 속성을 설정한 후 가계부 리스트 페이지를 반환합니다.
	 *
	 * <p>
	 *     처리 과정:
	 *     <ol>
	 *         <li>세션에서 회원ID를 가져옵니다.</li>
	 *         <li>검색 조건(<code>LedgerSearchRequest</code>)이 없으면 기본값을 설정합니다.</li>
	 *         <li>서비스(<code>budgetBookService</>)에서 가계부 내역 요약을 조회합니다.</li>
	 *         <li>Thymeleaf에서 쉽게 나열할 수 있도록 카드 리스트를 3개씩 그룹화하여 날짜별 Map으로 변환합니다.</li>
	 *         <li>모델에 조회 결과, 검색조건, 통계 정보를 추가합니다.</li>
	 *     </ol>
	 * </p>
	 *
	 * @param type				URL 경로에서 전달되는 가계부 타입(예: "month", "week")
	 * @param search			가계부 검색 조건 객체({@link LedgerSearchRequest})
	 * @param session			HTTP 세션에서 회원 ID를 가져오기 위해 사용
	 * @param model			뷰로 전달할 속성을 저장하는 모델
	 * @return	가계부 리스트 페이지 뷰 이름
	 */
	@GetMapping("/list/{type}")
	public String getBudgetPage(@PathVariable String type, LedgerSearchRequest search, HttpSession session, Model model) {
		String memberId = (String) session.getAttribute("mid");

		if( type == null || search == null ) {
			search = LedgerSearchRequest.getDefaultValue();
		}

		LedgerListResponse budgetBookList = budgetBookService.getBudgetBooksForSummary(memberId, search);

		//Thymeleaf에서 나열하기 위한 전환
		Map<String, List<List<LedgerListResponse.Card>>> formatSummary = new LinkedHashMap<>();
		for(LedgerListResponse.DayCards dayCards  : budgetBookList.getCards() ) {
			List<LedgerListResponse.Card> cardList = dayCards.getCardList();
			List<List<LedgerListResponse.Card>> cardGroup = new ArrayList<>();

			for( int i=0; i<cardList.size(); i+= 3 ) {
				List<LedgerListResponse.Card> cards = new ArrayList<>();

				for( int j=0; j<3; j++ ) {
					int index = i+j;
					cards.add( index < cardList.size() ? cardList.get(index) : null );
				}

				cardGroup.add(cards);
			}

			formatSummary.put(dayCards.getDate(), cardGroup);
		}


		model.addAttribute("type", type);
		model.addAttribute("title", budgetBookList.getTitle());
		model.addAttribute("search", LedgerSearchResponse.builder().type(type).mode("all").build());
		model.addAttribute("price", budgetBookList.getStats());
		model.addAttribute("summary", formatSummary);

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
			LedgerResponse budgetBook = budgetBookService.getBudgetBookById(memberId, id, mode);
			model.addAttribute("budgetBook", budgetBook);

			if (mode.equals("edit")) {
				List<LedgerCategoryResponse> category = budgetBookService.getCategoryByStep(budgetBook.getCategory().getCode());

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
