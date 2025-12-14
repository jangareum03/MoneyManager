package com.moneymanager.controller.web.main;

import com.moneymanager.domain.ledger.dto.*;
import com.moneymanager.domain.ledger.dto.LedgerDetailResponse;
import com.moneymanager.domain.ledger.enums.DateType;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.service.main.CategoryService;
import com.moneymanager.service.main.LedgerService;
import com.moneymanager.service.main.ImageServiceImpl;
import lombok.RequiredArgsConstructor;
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
 * * 파일이름       : LedgerController<br>
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
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 11. 25</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>
 * 		 	      [클래스 이름] BudgetBookController → LedgerController<br>
 * 		 	      [메서드 이름] getBudgetPage → getLedgerPage
 * 		 	  </td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@Controller
@RequestMapping("/ledgers")
@RequiredArgsConstructor
public class LedgerController {

	private final LedgerService ledgerService;
	private final CategoryService categoryService;


	/**
	 *	회원의 가계부 내역을 조회하여 날짜를 카드 리스트로 그룹화하고, Thymeleaf 뷰에 필요한 모델 속성을 설정한 후 가계부 리스트 페이지를 반환합니다.
	 *
	 * <p>
	 *     처리 과정:
	 *     <ol>
	 *         <li>세션에서 회원ID를 가져옵니다.</li>
	 *         <li>검색 조건(<code>LedgerSearchRequest</code>)이 없으면 기본값을 설정합니다.</li>
	 *         <li>서비스(<code>ledgerService</>)에서 가계부 내역 요약을 조회합니다.</li>
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
	public String getLedgerPage(@PathVariable String type, LedgerSearchRequest search, HttpSession session, Model model) {
		String memberId = (String) session.getAttribute("mid");

		search.changeType(DateType.from(type));

		LedgerGroupResponse ledgerGroupResponse = ledgerService.getLedgersForSummary(memberId, search);
		LedgerGroupForCardResponse cardResponse = LedgerGroupForCardResponse.from(ledgerGroupResponse);

		model.addAttribute("cards", cardResponse);
		model.addAttribute("search", LedgerSearchResponse.builder().type(type).mode("all").build());

		return "/main/ledger_list";
	}


	/**
	 * 회원의 가계부 내역을 조회하여 가계부 상세 페이지 또는 수정 페이지를 반환합니다.
	 * <p>
	 *     URL에 포함된 가계부 ID({@code id})를 이용해 가계부 정보를 조회하고, 모드({@code mode})에 따라 보여줄 화면을 다르게 처리합니다.
	 *     세션에 저장된 사용자 ID를 가져와서 조회를 요청한 가계부 작성자인지 확인합니다. 작성자가 아니라면 {@link com.moneymanager.exception.custom.ClientException} 예외가 발생합니다.
	 * </p>
	 * <p>
	 *     <b>모드 설명:</b>
	 *     <ul>
	 *         <li>{@code view} (기본값) : 가계부 상세 조회 화면 요청</li>
	 *         <li>{@code edit} : 가계부 수정 화면 요청(+ 회원 업로드 가능한 이미지 최대 개수도 전달)</li>
	 *     </ul>
	 * </p>
	 *
	 * @param session 사용자 식별 및 정보를 저장하는 객체
	 * @param model   뷰에 전달할 객체
	 * @param id      	가계부 식별 번호
	 * @param mode		페이지 모드 (조회: {@code view}, 수정: {@code edit})
	 * @return 가계부 상세 화면 또는 수정 화면의 경로
	 */
	@GetMapping("/{id}")
	public String getLedgerDetailPage(@PathVariable String id, @RequestParam(required = false, defaultValue = "view") String mode, HttpSession session, Model model) {
		String memberId = (String) session.getAttribute("mid");

		if( mode.equalsIgnoreCase("edit") ) {
			LedgerEditResponse ledger = ledgerService.getLedgerEdit(memberId, id);
			List<CategoryResponse> categoryResponse = ledger.getCategory();

			model.addAttribute("ledger", ledger);
			model.addAttribute("category", categoryService.getAllCategoriesByCode(categoryResponse.get( categoryResponse.size() - 1 ).getCode()));
			model.addAttribute("paymentTypes", List.of(PaymentType.values()));

			return "/main/ledger_update";
		}else {
			LedgerDetailResponse ledger = ledgerService.getLedgerDetail(memberId, id);
			model.addAttribute("ledger", ledger);

			return "/main/ledger_detail";
		}
	}
}