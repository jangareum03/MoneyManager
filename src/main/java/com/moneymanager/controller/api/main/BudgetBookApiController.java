package com.moneymanager.controller.api.main;


import com.moneymanager.dto.budgetBook.CategoryDTO;
import com.moneymanager.dto.budgetBook.request.BudgetBookSearchRequest;
import com.moneymanager.dto.budgetBook.request.BudgetBookUpdateRequest;
import com.moneymanager.dto.budgetBook.response.BudgetBookListResponse;
import com.moneymanager.dto.common.ApiResultDTO;
import com.moneymanager.dto.common.ImageDTO;
import com.moneymanager.dto.common.request.DateRequest;
import com.moneymanager.dto.member.request.MemberAttendanceRequest;
import com.moneymanager.exception.ErrorException;
import com.moneymanager.service.main.BudgetBookService;
import com.moneymanager.service.main.api.GoogleChartService;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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
 * 		</tbody>
 * </table>
 */
@RestController
@RequestMapping("/api/budgetBook")
public class BudgetBookApiController {

	private final BudgetBookService budgetBookService;
	private final GoogleChartService chartService;

	public BudgetBookApiController(BudgetBookService budgetBookService, GoogleChartService googleChartService) {
		this.budgetBookService = budgetBookService;
		this.chartService = googleChartService;
	}


	/**
	 * 가계부 날짜의 마지막 일을 찾는 요청을 처리합니다.
	 *
	 * @param calendar 값을 확인할 년도와 월
	 * @return 월의 마지막 일
	 */
	@PostMapping("/lastDay")
	public int postLastDay(@RequestBody MemberAttendanceRequest.CalendarView calendar) {
		DateRequest.MonthRange date = calendar.getDate();

		return LocalDate.of(Integer.parseInt(date.getYear()), Integer.parseInt(date.getMonth()), 1).lengthOfMonth();
	}


	/**
	 * 하위 카테고리의 이름과 코드를 조회하는 요청을 처리합니다.
	 *
	 * @param code 사용자가 선택한 카테고리
	 * @return 하위 카테고리의 이름과 코드
	 */
	@PostMapping("/category")
	public List<CategoryDTO> postCategories(@RequestBody(required = false) String code) {
		return budgetBookService.getCategoriesByCode(code);
	}


	/**
	 * 가계부 내역을 한눈에 보기위한 그래프 요청을 처리합니다.<br>
	 *
	 * @param session 사용자 식별 및 정보를 저장하는 객체
	 * @return 내역 기간에 따른 그래프
	 */
	@GetMapping("/charts")
	public List<Object> postBudgetBookChart(HttpSession session) {
		String memberId = (String) session.getAttribute("mid");

		//차트날짜 범위에 따른 객체 생성
		List<Object> chartData = chartService.createChartData(memberId, (DateRequest) session.getAttribute("chart"));
		session.removeAttribute("chart");

		return chartData;
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
	public ResponseEntity<BudgetBookListResponse> patchSearch(@RequestBody BudgetBookSearchRequest search, HttpSession session) {
		String memberId = (String) session.getAttribute("mid");

		return ResponseEntity.ok(budgetBookService.getBudgetBooksForSummary(memberId, search));
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
	public ResponseEntity<ApiResultDTO> update(HttpSession session, @PathVariable Long id,
											   @RequestPart("update") BudgetBookUpdateRequest update,
											   @RequestPart(value = "image", required = false) List<MultipartFile> imageFiles) {
		try {
			//이미지 리스트 변환 후 BudgetBookUpdateRequest 맵칭
			List<ImageDTO> imageList = imageFiles.stream()
							.filter(Objects::nonNull)
							.map( file -> { return ImageDTO.builder().file(file).fileName(file.getOriginalFilename()).fileExtension(FilenameUtils.getExtension(file.getOriginalFilename())).build(); })
									.collect(Collectors.toList());

			BudgetBookUpdateRequest updateReqDTO = BudgetBookUpdateRequest.from( update, imageList );

			budgetBookService.updateBudgetBook((String) session.getAttribute("mid"), id, updateReqDTO );
			return ResponseEntity.ok(ApiResultDTO.builder().success(true).message("수정 완료했습니다.").build());
		} catch (ErrorException e) {
			return ResponseEntity.ok(ApiResultDTO.builder().success(false).message(e.getErrorMessage()).build());
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
			budgetBookService.deleteBudgetBook((String) session.getAttribute("mid"), id);

			return ResponseEntity.ok(ApiResultDTO.builder().success(true).message("선택하신 가계부를 삭제했습니다.").build());
		} catch (ErrorException e) {
			return ResponseEntity.ok(ApiResultDTO.builder().success(false).message(e.getErrorMessage()).build());
		}
	}
}
