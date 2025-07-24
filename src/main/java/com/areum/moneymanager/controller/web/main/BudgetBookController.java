package com.areum.moneymanager.controller.web.main;

import com.areum.moneymanager.dto.request.main.BudgetBookRequestDTO;
import com.areum.moneymanager.dto.response.main.BudgetBookResponseDTO;
import com.areum.moneymanager.dto.response.main.CategoryResponseDTO;
import com.areum.moneymanager.exception.code.ErrorCode;
import com.areum.moneymanager.exception.ErrorException;
import com.areum.moneymanager.service.main.BudgetBookService;
import com.areum.moneymanager.service.main.ImageServiceImpl;
import com.areum.moneymanager.service.main.validation.DateValidationService;
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
 *  * 패키지이름    : com.areum.moneymanager.controller.web.main<br>
 *  * 파일이름       : BudgetBookController<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 가계부 관련 화면을 처리하는 클래스
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
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Controller
@RequestMapping("/budgetBooks")
public class BudgetBookController {

	private final Logger logger = LogManager.getLogger(this);

	private final BudgetBookService budgetBookService;
	private final ImageServiceImpl imageService;

	public BudgetBookController( BudgetBookService budgetBookService, @Qualifier("budgetImage") ImageServiceImpl imageService ) {
		this.budgetBookService = budgetBookService;
		this.imageService = imageService;
	}



	/**
	 * 가계부 내역 조회 페이지 요청을 처리합니다.<br>
	 * 가계부 요약 그래프는 날짜기간별로 그래프 유형이 달라집니다. 날짜 기간 내에 등록된 가계부가 없다면 그래프는 별 표시가 없습니다.
	 *
 	 * @param session			사용자 식별 및 정보를 저장하는 객체
	 * @param model				뷰에 전달할 객체
	 * @param date					가계부 조회 날짜
	 * @return	내역 조회 페이지
	 */
	@GetMapping("/list/{type}")
	public String getBudgetPage( @PathVariable String type, BudgetBookRequestDTO.DateForm date, HttpSession session, Model model ) {
		String memberId = (String) session.getAttribute("mid");

		//유형별 날짜 설정
		String year, month= null, week = null;
		switch ( type.toLowerCase()) {
			case "week":
				week = DateValidationService.getValidWeekOrCurrent( date.getWeek() );
			case "month":
				month = DateValidationService.getValidMonthOrCurrent( date.getMonth() );
			case "year" :
				year = DateValidationService.getValidYearOrCurrent( date.getYear() );
				break;
			default:
				model.addAttribute("error", ErrorCode.BUDGET_TYPE_NONE.getMessage());
				model.addAttribute("method", "get");
				model.addAttribute("url", "/budgetBooks/list/month");

				return "alert";
		}

		BudgetBookRequestDTO.ChartJson resetDate = new BudgetBookRequestDTO.ChartJson( type, year, month, week );
		BudgetBookRequestDTO.Search search = BudgetBookRequestDTO.Search.builder().range( new BudgetBookRequestDTO.ChartJson( type, resetDate.getYear(), resetDate.getMonth(), resetDate.getWeek() ) ).mode("all").keywords(null).build();
		BudgetBookResponseDTO.Preview summaryInfo = budgetBookService.getBudgetBooksForSummary( memberId, search );

		//타임리프에서 나열하기 위한 전환
		Map<String , List<List<BudgetBookResponseDTO.Summary>>> formatSummary = new LinkedHashMap<>();
		summaryInfo.getInfo().forEach( (key, value) ->  {
			List<List<BudgetBookResponseDTO.Summary>> formatList = new ArrayList<>();

			for( int i=0; i<value.size(); i += 3 ) {
				List<BudgetBookResponseDTO.Summary> format = new ArrayList<>();

				for( int j=0; j<3; j++ ) {
					int index = i+j;
					format.add( index < value.size() ? value.get(index) : null );
				}

				formatList.add(format);
			}

			formatSummary.put(key, formatList);
		});

		model.addAttribute("type", type);
		model.addAttribute("title", budgetBookService.makeTitleByType( type, resetDate ) );
		model.addAttribute("search", BudgetBookResponseDTO.Search.builder().type(type).mode("all").build());
		model.addAttribute("price", summaryInfo.getPrice());
		model.addAttribute("summary", formatSummary);


		//차트를 위한 저장
		session.setAttribute("chart", resetDate);

		return "/main/budgetBook_list";
	}



	/**
	 * 가계부 번호에 해당하는 상세정보 페이지를 요청을 처리합니다.
	 *
	 * @param session			사용자 식별 및 정보를 저장하는 객체
	 * @param  model				뷰에 전달할 객체
	 * @param id							가계부 식별 번호
	 * @return 가계부 상세 페이지
	 */
	@GetMapping("/{id}")
	public String getBudgetDetailPage( HttpSession session, Model model, @PathVariable Long id, @RequestParam(required = false, defaultValue = "view") String mode ) {
		String memberId = (String)session.getAttribute("mid");

		try{
			BudgetBookResponseDTO.Detail budgetBook = budgetBookService.getBudgetBookById( memberId, id, mode );
			model.addAttribute("budgetBook", budgetBook);

			if( mode.equals("edit") ) {
				String type = budgetBook.getCategory().getCode().startsWith("01") ? "in" : "out";

				List<CategoryResponseDTO.Read> selectCategory = budgetBookService.getCategoryByStep(budgetBook.getCategory().getCode());

				model.addAttribute("selectCategory", selectCategory);
				model.addAttribute("category", budgetBookService.getCategoriesByCode( selectCategory ));
				model.addAttribute("max", imageService.getLimitImageCount(memberId));

				return "/main/budgetBook_update";
			}else {
				return "/main/budgetBook_detail";
			}

		}catch ( ErrorException e  ) {
			model.addAttribute("error", e.getErrorMessage());
			model.addAttribute("method", "get");
			model.addAttribute("url", "/budgetBooks");

			return "alert";
		}
	}
}
