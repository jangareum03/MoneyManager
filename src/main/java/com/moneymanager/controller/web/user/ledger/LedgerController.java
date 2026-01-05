package com.moneymanager.controller.web.user.ledger;

import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep1Response;
import com.moneymanager.service.ledger.LedgerReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
		LedgerWriteStep1Response response = ledgerReadService.getInitialData();

		model.addAttribute("ledger", response);

		return "/main/ledger_writeStep1";
	}

}
