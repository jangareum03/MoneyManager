package com.moneymanager.controller.web.main;

import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.dto.response.LedgerWriteStep1Response;
import com.moneymanager.domain.ledger.enums.DateType;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.service.main.LedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;

import static com.moneymanager.utils.ValidationUtils.isNullOrBlank;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.controller.web.main<br>
 *  * 파일이름       : WriteController<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 가계부 작성 관련 화면을 처리하는 클래스
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
@RequestMapping("/ledgers/write")
@RequiredArgsConstructor
public class WriteController {

	private final LedgerService ledgerService;


	/**
	 * 가계부 초기 작성에 필요한 정보를 조회 후 가계부 작성 1단계 페이지를 반환합니다.
	 * <p>
	 *     가계부 신규 작성 흐름의 첫 번째 단계로, 현재 날짜 정보를 포함한 작성에 필요한 초기 데이터를 전달합니다.
	 * </p>
	 *
	 * @param  model		뷰에 전달할 객체
	 * @return 가계부 작성 1단계 화면의 경로
	 */
	@GetMapping
	public String getStep1Page( Model model ) {
		model.addAttribute("data", new LedgerWriteStep1Response(LocalDate.now()));

		return "/main/ledger_writeStep1";
	}



	/**
	 * 가계부 초기 작성에서 선택한 가계부 유형, 날짜 정보를 전달받아,
	 * 가계부 상세 작성에 필요한 데이터(카테고리, 이미지 슬롯 등)를 조회한 후 화면에 전달합니다.
	 * <p>
	 *     처리 과정:
	 *     <ol>
	 *         <li>가계부 유형({@code type}) 또는 날짜({@code date})가 없다면 가계부 작성 1단계 화면으로 리다이렉트 합니다.</li>
	 *         <li>세션에서 로그인된 회원의 ID를 가져옵니다.</li>
	 *         <li>서비스를 호출하여 가계부 상세 작성에 필요한 데이터를 조회합니다.</li>
	 *         <li>조회된 데이터를 모델에 담아 작성 2단계 화면으로 이동합니다.</li>
	 *         <li>처리 중 {@link ClientException} 예외가 발생하면, 1단계 화면으로 이동합니다.</li>
	 *     </ol>
	 * </p>
	 *
	 * @param type				가계부 유형(URL 경로 변수)
	 * @param date				선택한 가계부 날짜
	 * @param session			사용자 식별 및 정보를 저장하는 객체
	 * @param model			뷰에 전달할 데이터를 담은 객체
	 * @return	가계부 작성 2단계 또는 1단계 화면 경로
	 */
	@PostMapping("/{type}")
	public String getStep2Page( @PathVariable String type, @RequestParam(required = false) String date, HttpSession session, Model model ) {
		if( isNullOrBlank(date) ) return "redirect:/ledgers/write";

		try{
			String memberId = (String) session.getAttribute("mid");

			model.addAttribute("ledger", ledgerService.getWriteByData( memberId, type, date ));
			model.addAttribute("defaultPaymentType", PaymentType.NONE.getValue());
			model.addAttribute("defaultFix", FixedYN.VARIABLE.getValue());

			return "/main/ledger_writeStep2";
		}catch ( ClientException e ) {
			return "redirect:/ledgers/write";
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
	 * @param redirectAttributes  	뷰에 전달할 일회용 객체
	 * @return	가계부 리스트 페이지
	 */
	@PostMapping
	public String postWrite(@ModelAttribute("ledger") LedgerWriteRequest create, HttpSession session, RedirectAttributes redirectAttributes ) {
		String memberId = (String)session.getAttribute("mid");

		try{
			ledgerService.createLedger( memberId, create );

			redirectAttributes.addAttribute("type", DateType.MONTH.name().toLowerCase());
			return "redirect:/ledgers/list/{type}";
		}catch ( ClientException  e ) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());

			return "redirect:/ledgers/write";
		}
	}
}
