package com.moneymanager.controller.api.main;


import com.moneymanager.domain.ledger.dto.*;
import com.moneymanager.domain.global.dto.ApiResultDTO;
import com.moneymanager.domain.global.dto.ImageDTO;
import com.moneymanager.domain.global.dto.DateRequest;
import com.moneymanager.domain.global.dto.YearMonthRequest;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.service.main.CategoryService;
import com.moneymanager.service.main.LedgerService;
import com.moneymanager.service.main.api.GoogleChartService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
	 * 가계부 내역에서 원하는 내역을 검색 요청을 처리합니다.<br>
	 * 검색어가 없는 경우에는 요청처리를 진행하지 않습니다.
	 *
	 * @param search  검색정보
	 * @param session 사용자 식별 및 정보를 저장하는 객체
	 * @return 검색어에 해당하는
	 */
	@PatchMapping("/search")
	public ResponseEntity<LedgerListResponse> patchSearch(@RequestBody LedgerSearchRequest search, HttpSession session) {
		String memberId = (String) session.getAttribute("mid");

		return ResponseEntity.ok(ledgerService.getLedgersForSummary(memberId, search));
	}



	/**
	 * 특정 가계부 내역 수정 요청을 처리합니다.
	 *
	 * @param session 사용자 식별 및 정보를 저장하는 객체
	 * @param id      가계부 번호
	 * @param update  수정할 가계부 정보
	 * @return 안내 메시지
	 */
	@PutMapping("/{id}")
	public ResponseEntity<ApiResultDTO> update(HttpSession session, @PathVariable String id,
											   @RequestPart("update") LedgerUpdateRequest update,
											   @RequestPart(value = "image", required = false) List<MultipartFile> imageFiles) {
		try {
			//이미지 리스트 변환 후 LedgerUpdateRequest 맵칭
			List<ImageDTO> imageList = imageFiles.stream()
							.filter(Objects::nonNull)
							.map( file -> { return ImageDTO.builder().file(file).fileName(file.getOriginalFilename()).fileExtension(FilenameUtils.getExtension(file.getOriginalFilename())).build(); })
									.collect(Collectors.toList());

			LedgerUpdateRequest updateReqDTO = LedgerUpdateRequest.of( id, update, imageList );

			ledgerService.updateLedger((String) session.getAttribute("mid"), updateReqDTO );
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
