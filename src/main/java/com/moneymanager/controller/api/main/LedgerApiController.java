package com.moneymanager.controller.api.main;


import com.moneymanager.domain.global.dto.ApiResultDTO;
import com.moneymanager.domain.global.dto.DateRequest;
import com.moneymanager.domain.global.dto.YearMonthRequest;
import com.moneymanager.domain.ledger.dto.request.CategoryRequest;
import com.moneymanager.domain.ledger.dto.request.LedgerSearchRequest;
import com.moneymanager.domain.ledger.dto.request.LedgerUpdateRequest;
import com.moneymanager.domain.ledger.dto.request.LedgerUpdateWithFileRequest;
import com.moneymanager.domain.ledger.dto.response.CategoryResponse;
import com.moneymanager.domain.ledger.dto.response.LedgerGroupForCardResponse;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.service.main.CategoryService;
import com.moneymanager.service.main.LedgerService;
import com.moneymanager.service.main.api.GoogleChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.time.YearMonth;
import java.util.List;

import static com.moneymanager.exception.ErrorUtil.createClientException;


/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.controller.api.main<br>
 * * 파일이름       : WebConfig<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 15<br>
 * * 설명              : 가계부 관련 API를 제공하는 클래스
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
 * 		 	      [클래스 이름] BudgetBookApiController → LedgerApiController<br>
 * 		 	      [메서드 이름] postBudgetBookChart → postLedgerChart
 * 		 	  </td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ledgers")
public class LedgerApiController {

	private final LedgerService ledgerService;
	private final CategoryService categoryService;
	private final GoogleChartService chartService;


	/**
	 * 주어진 년도(year)와 월(month)에 해당하는 마지막 날짜(일)를 반환합니다.
	 * <p>
	 *     예를 들어 {@code year=2025, month=2}이면 2025년 2월의 마지막 날인 28을 반환합니다.
	 * </p>
	 *
	 * @param date		년도와 월 정보를 담은 객체
	 * @return 해당 년월의 마지막 날짜(1~31)
	 */
	@PostMapping("/lastDay")
	public ResponseEntity<ApiResultDTO<Integer>> postLastDay(@RequestBody YearMonthRequest date ) {
		int year = Integer.parseInt(date.getYear());
		int month = Integer.parseInt(date.getMonth());

		YearMonth yearMonth = YearMonth.of(year, month);
		int lastDay = yearMonth.lengthOfMonth();

		return ResponseEntity.ok(ApiResultDTO.success(lastDay));
	}


	/**
	 * 클라이언트가 선택한 카테고리를 기준으로 하위 카테고리 목록을 반환합니다.
	 * <p>
	 *     처리 과정:
	 *     <ol>
	 *         <li>클라이언트로부터 {@link CategoryRequest} 객체를 받습니다.</li>
	 *         <li>{@link CategoryRequest}가 {@code null}이면 {@link ClientException} 을 발생시킵니다.</li>
	 *         <li>가계부 서비스의 {@code getCategoriesByCode()}를 호출하여 요청에 맞는 하위 카테고리 목록을 가져옵니다.</li>
	 *         <li>조회한 결과를 JSON 형태로 클라리언트에게 전달합니다.</li>
	 *     </ol>
	 * </p>
	 *
	 * @param request 		가계부 유형과 상위 카테고리 코드를 담은 객체
	 * @return 요청한 코드에 해당하는 하위 카테고리 목록
	 */
	@GetMapping("/category")
	public List<CategoryResponse> postCategories(@RequestBody CategoryRequest request) {
		//요청 정보가 없는 경우
		if (request == null) {
			throw createClientException(ErrorCode.LEDGER_CATEGORY_MISSING, "카테고리를 확인해주세요.");
		}

		return categoryService.getSubCategories(request);
	}


	/**
	 * 가계부 내역을 한눈에 보기위한 그래프 요청을 처리합니다.<br>
	 *
	 * @param session 사용자 식별 및 정보를 저장하는 객체
	 * @return 내역 기간에 따른 그래프
	 */
	@PostMapping("/charts")
	public List<Object> postLedgerChart(@RequestBody DateRequest date, HttpSession session) {
		String memberId = (String) session.getAttribute("mid");

		//차트날짜 범위에 따른 객체 생성
		return chartService.createChartData(memberId, date);
	}


	/**
	 * 회원의 가계부 내역을 조회하여 날짜별 카드 리스트로 그룹화하고, Thymeleaf 뷰에 필요한 모델 속성을 설정한 후 가계부 리스트 페이지를 반환합니다.
	 * <p>
	 *     처리과정:
	 *     <ol>
	 *         <li>세션에서 회원ID를 가져옵니다.</li>
	 *         <li>서비스(<code>ledgerService</code>)에서 가계부 내역 요약을 조회합니다.</li>
	 *         <li>Thymeleaf에서 쉽게 나열할 수 있도록 내역 리스트를 카드 형태로 변환합니다.</li>
	 *     </ol>
	 * </p>
	 *
	 * @param search		가계부 검색 조건 객채({@link LedgerSearchRequest})
	 * @param session		HTTP 세션에서 회원 ID를 가져오기 위해 사용
	 * @return	가계부 내역 리스트를 담은 {@link LedgerGroupForCardResponse} 객체
	 */
	@PatchMapping("/search")
	public ResponseEntity<LedgerGroupForCardResponse> patchSearch(@RequestBody LedgerSearchRequest search, HttpSession session) {
		String memberId = (String) session.getAttribute("mid");

		return ResponseEntity.ok(LedgerGroupForCardResponse.from(ledgerService.getLedgersForSummary(memberId, search)));
	}



	/**
	 *	가계부 정보를 수정하고, 첨부된 이미지를 함께 처리합니다. 처리된 결과를 JSON 응답으로 반환합니다.
	 *<p>
	 *     처리과정:
	 *     <ol>
	 *         <li>세션에서 회원ID를 가져옵니다.</li>
	 *         <li>가계부 수정정보({@link LedgerUpdateRequest})와 이미지 파일을 {@link LedgerUpdateWithFileRequest} 객체로 합칩니다.</li>
	 *         <li>서비스({@code ledgerService})에서 가계부 정보와 이미지 변경을 처리합니다.</li>
	 *         <li>수정 요청이 성공하면 성공 메시지를, 실패하면 안내 메시지를 반환합니다.</li>
	 *     </ol>
	 *</p>
	 *
	 * @param id				수정할 가계부 ID
	 * @param request		수정할 가계부 정보를 담은 {@link LedgerUpdateRequest} 객체
	 * @param files			변경할 가계부 이미지 파일 리스트
	 * @param session		로그인한 회원정보를 담은 객체
	 * @return	가계부 수정 처리 결과를 담은 {@link ApiResultDTO} 객체
	 */
	@PutMapping("/{id}")
	public ResponseEntity<ApiResultDTO> update(@PathVariable String id, @RequestPart("update") LedgerUpdateRequest request, @RequestPart(value = "image", required = false) List<MultipartFile> files, HttpSession session) {
		String memberId = (String)session.getAttribute("mid");
		LedgerUpdateWithFileRequest update = LedgerUpdateWithFileRequest.builder().update(request).images(files).build();

		try {
			ledgerService.updateLedger(memberId, id, update);

			return ResponseEntity.ok(ApiResultDTO.builder().success(true).message("수정 완료했습니다.").build());
		} catch (ClientException e) {
			return ResponseEntity.ok(ApiResultDTO.builder().success(false).message(e.getMessage()).build());
		}
	}


	/**
	 * 선택한 가계부 삭제 요청을 처리합니다.<br>
	 * 가계부를 선택하지 않으면 요청 처리를 진행하지 않고 안내 메시지만 노출됩니다.
	 *
	 * @param id      가계부 번호
	 * @param session 사용자 식별 및 정보를 저장하는 객체
	 * @return 안내 메시지
	 */
	@DeleteMapping
	public ResponseEntity<ApiResultDTO> delete(@RequestBody List<Long> id, HttpSession session) {
		try {
			ledgerService.deleteLedger((String) session.getAttribute("mid"), id);

			return ResponseEntity.ok(ApiResultDTO.builder().success(true).message("선택하신 가계부를 삭제했습니다.").build());
		} catch (ClientException e) {
			return ResponseEntity.ok(ApiResultDTO.builder().success(false).message(e.getMessage()).build());
		}
	}
}
