package com.moneymanager.ledger.controller;

import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.operation.annotation.Operation;
import com.moneymanager.global.operation.enums.ServiceAction;
import com.moneymanager.global.util.date.DateTimeUtil;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.response.HistoryDashboardResponse;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep1Response;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep2Response;
import com.moneymanager.ledger.domain.enums.*;
import com.moneymanager.ledger.service.command.LedgerCommandService;
import com.moneymanager.ledger.service.read.LedgerReadService;
import com.moneymanager.ledger.service.validation.LedgerValidator;
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
@RequiredArgsConstructor
@RequestMapping("/ledgers")
public class LedgerController {

	private final LedgerReadService ledgerReadService;
	private final LedgerCommandService ledgerCommandService;
	private final LedgerValidator ledgerValidator;

	@GetMapping
	public String getHistories(@RequestParam(required = false) String viewType, Model model) {
		HistoryType type = parseHistoryTypeOrDefault(viewType);

		HistoryDashboardResponse response = ledgerReadService.getHistoryDashboard(type);

		model.addAttribute("history", response);
		model.addAttribute("type", type);
		model.addAttribute("activeMenu", HistoryMenuType.ALL.name());

		return "/ledger/ledger_history";
	}

	private HistoryType parseHistoryTypeOrDefault(String type) {
		try{
			return HistoryType.from(type);
		}catch (IllegalArgumentException e) {
			return HistoryType.MONTH;
		}
	}

	@GetMapping("/{code}")
	public String getLedgerDetail(@PathVariable String code, Model model) {
		model.addAttribute("ledger", ledgerReadService.getDetailData(code));

		return "/ledger/ledger_detail";
	}

	@GetMapping("/{code}/edit")
	public String showEditForm(@PathVariable String code, Model model) {
		model.addAttribute("ledger", ledgerReadService.getEditData(code));

		model.addAttribute("fixes", FixedType.values());
		model.addAttribute("fixCycles", FixCycle.values());
		model.addAttribute("paymentTypes", PaymentType.values());

		return "/ledger/ledger_edit";
	}

	@GetMapping("/new/step1")
	@Operation(ServiceAction.LEDGER_REGISTER_STEP1_VIEW)
	public String showWriteStep1Form(Model model){
		LedgerWriteStep1Response response = ledgerReadService.getWriteStep1Data();

		model.addAttribute("ledger", response);

		return "/ledger/ledger_writeStep1";
	}

	@GetMapping("/new/step2")
	public String showWriteStep2Form(@RequestParam String type, @RequestParam String date, Model model) {
		//입력값 확인
		CategoryType ledgerType = parseCategoryTypeOrDefault(type);
		LocalDate localDate = DateTimeUtil.parseDateFromYyyyMMdd(date);

		LedgerWriteStep2Response response = ledgerReadService.getWriteStep2Data(ledgerType, localDate);

		model.addAttribute("ledger", response);

		return "/ledger/ledger_writeStep2";
	}

	private CategoryType parseCategoryTypeOrDefault(String type) {
		try{
			return CategoryType.from(type);
		}catch (ValidationException e) {
			return CategoryType.INCOME;
		}
	}

	@PostMapping
	public String createLedger(@ModelAttribute("ledger") LedgerWriteRequest request) {
		ledgerValidator.register(request);

		ledgerCommandService.register(request);

		return "redirect:/ledgers";
	}

}