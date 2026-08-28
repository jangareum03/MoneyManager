package com.moneymanager.ledger.controller;

import com.moneymanager.global.exception.annotation.WebController;
import com.moneymanager.global.operation.annotation.Operation;
import com.moneymanager.global.operation.enums.ServiceAction;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep1Response;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep2Response;
import com.moneymanager.ledger.domain.dto.response.history.HistoryDashboardResponse;
import com.moneymanager.ledger.service.application.LedgerHistoryService;
import com.moneymanager.ledger.service.application.LedgerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 패키지이름    : com.moneymanager.controller.web.user.ledger<br>
 * 파일이름       : LedgerController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 5<br>
 * 설명              : 가계부 관련 화면 요청을 처리하는 클래스
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
@WebController
@RequestMapping("/ledgers")
public class LedgerController {

	private final LedgerService ledgerService;
	private final LedgerHistoryService ledgerHistoryService;

	public  LedgerController(LedgerService ledgerService, LedgerHistoryService ledgerHistoryService) {
		this.ledgerService = ledgerService;
		this.ledgerHistoryService = ledgerHistoryService;
	}

	@GetMapping
	@Operation(ServiceAction.LEDGER_HISTORY_VIEW)
	public String ledgers(@RequestParam String type, Model model) {
		HistoryDashboardResponse response = ledgerHistoryService.searchLedgersByDate(type);

		model.addAttribute("history", response);
		model.addAttribute("type", type);

		return "/ledger/ledger_history";
	}

	@GetMapping("/new/step1")
	@Operation(ServiceAction.LEDGER_REGISTER_STEP1_VIEW)
	public String showWriteStep1Form(Model model){
		LedgerWriteStep1Response response = ledgerService.getStep1();

		model.addAttribute("ledger", response);

		return "/ledger/ledger_writeStep1";
	}

	@GetMapping("/new/step2")
	@Operation(ServiceAction.LEDGER_REGISTER_STEP2_VIEW)
	public String showWriteStep2Form(@RequestParam String type, @RequestParam String date, Model model) {
		LedgerWriteStep2Response response = ledgerService.getStep2(type, date);

		model.addAttribute("ledger", response);

		return "/ledger/ledger_writeStep2";
	}

	@GetMapping("/map")
	@Operation(ServiceAction.LEDGER_MAP_VIEW)
	public String getMapByKakao() {
		return "/map/kakao_map";
	}

	@PostMapping
	@Operation(ServiceAction.LEDGER_REGISTER)
	public String createLedger(@ModelAttribute("ledger") LedgerWriteRequest request) {
		ledgerService.processLedgerRegistration(request);

		return "redirect:/ledgers";
	}

}