package com.moneymanager.controller.web.ledger;

import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.dto.response.HistoryDashboardResponse;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep1Response;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep2Response;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.domain.ledger.enums.HistoryType;
import com.moneymanager.service.ledger.LedgerCommandService;
import com.moneymanager.service.ledger.LedgerReadService;
import com.moneymanager.service.validation.LedgerValidator;
import com.moneymanager.utils.date.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


/**
 * <p>
 * 패키지이름    : com.moneymanager.controller.web.user.ledger<br>
 * 파일이름       : LedgerController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 5<br>
 * 설명              : 가계부 관련 요청을 처리하는 클래스
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
 * 		 	  <td>26. 1. 5.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/ledgers")
public class LedgerController {

	private final LedgerReadService ledgerReadService;
	private final LedgerCommandService ledgerCommandService;

	private final LedgerValidator ledgerValidator;


	@GetMapping
	public String getMyHistories(@RequestParam(required = false) String viewType, Model model) {
		HistoryType type = parseHistoryTypeOrDefault(viewType);

		HistoryDashboardResponse response = ledgerReadService.getHistoryDashboard(type);

		model.addAttribute("history", response);

		return "/ledger/ledger_history";
	}

	private HistoryType parseHistoryTypeOrDefault(String type) {
		try{
			return HistoryType.from(type);
		}catch (IllegalArgumentException e) {
			return HistoryType.MONTH;
		}
	}


	/**
	 * 가계부 초기 작성에 필요한 정보를 조회 후 가계부 작성 1단계 페이지를 반환합니다.
	 * <p>
	 *     가계부 신규 작성 흐름의 첫 번째 단계로, 현재 날짜 정보를 포함한 작성에 필요한 초기 데이터를 전달합니다.
	 * </p>
	 *
	 * @param  model		뷰에 전달할 객체
	 * @return 가계부 작성 1단계 화면의 경로
	 */
	@GetMapping("/new/step1")
	public String writeStep1View(Model model){
		LedgerWriteStep1Response response = ledgerReadService.getWriteStep1Data();

		model.addAttribute("ledger", response);

		return "/ledger/ledger_writeStep1";
	}


	/**
	 * 가계부 2단계 작성에 필요한 정보와 가계부 작성 2단계 페이지를 반환합니다.
	 * <p>
	 *		가계부 유형과 거래날짜를 이용해 필요한 데이터를 조회합니다.
	 *		유형과 거래날짜가 올바르지 않으면, 기본값으로 대체하여 진행합니다.
	 * </p>
	 * <p>
	 *     기본값은 아래와 같습니다.
	 *     <ul>
	 *         <li>가계부 유형({@code type}): {@link CategoryType#INCOME}</li>
	 *         <li>가계부 거래날짜({@code date}): {@link LocalDate#now()}</li>
	 *     </ul>
	 * </p>
	 *
	 * @param type		가계부 유형 문자열(예: {@code income})
	 * @param date		가계부 거래 날짜(형식: {@code yyyyMMdd})
	 * @param model	뷰에 전달할 데이터를 담은 객체
	 * @return 가계부 작성 2단계 화면의 경로
	 */
	@GetMapping("/new/step2")
	public String writeStep2View(@RequestParam String type, @RequestParam String date, Model model) {
		LocalDate defaultDate = LocalDate.now();

		//입력값 확인
		CategoryType ledgerType = parseCategoryTypeOrDefault(type);
		LocalDate localDate = DateTimeUtils.parseDateOrElse(date, defaultDate);

		LedgerWriteStep2Response response = ledgerReadService.getWriteStep2Data(ledgerType, localDate);

		model.addAttribute("ledger", response);

		return "/ledger/ledger_writeStep2";
	}

	private CategoryType parseCategoryTypeOrDefault(String type) {
		try{
			return CategoryType.fromApiCode(type);
		}catch (IllegalArgumentException e) {
			return CategoryType.INCOME;
		}
	}

	/**
	 * 가계부 등록 요청을 처리합니다.
	 * <p>
	 *    사용자로부터 전달받은 가계부 작성 데이터를 통해 가계부를 생성하고 목록 페이지로 리다이렉트합니다.
	 * </p>
	 *
	 * @param request	가계부 작성 요청 데이터
	 * @return	가계부 목록 페이지로의 리다이렉트 경로
	 */
	@PostMapping
	public String writeLedger(@ModelAttribute("ledger") LedgerWriteRequest request) {
		ledgerValidator.register(request);

		ledgerCommandService.registerLedger(request);

		return "redirect:/ledgers";
	}
}
