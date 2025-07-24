package com.areum.moneymanager.controller.api.main;


import com.areum.moneymanager.dto.request.main.BudgetBookRequestDTO;
import com.areum.moneymanager.dto.request.main.ValidationRequestDTO;
import com.areum.moneymanager.dto.response.ApiResponseDTO;
import com.areum.moneymanager.dto.response.ValidationResponseDTO;
import com.areum.moneymanager.dto.response.main.BudgetBookResponseDTO;
import com.areum.moneymanager.dto.response.main.CategoryResponseDTO;
import com.areum.moneymanager.exception.ErrorException;
import com.areum.moneymanager.service.main.BudgetBookService;
import com.areum.moneymanager.service.main.api.GoogleChartService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;



/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.controller.api.main<br>
 *  * 파일이름       : WebConfig<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 가계부 관련 API를 제공하는 클래스
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
@RestController
@RequestMapping("/api/budgetBook")
public class BudgetBookApiController {

	private final Logger logger = LogManager.getLogger(this);
	private final BudgetBookService budgetBookService;
	private final GoogleChartService chartService;

	public BudgetBookApiController( BudgetBookService budgetBookService, GoogleChartService googleChartService ) {
		this.budgetBookService = budgetBookService;
		this.chartService = googleChartService;
	}


	/**
	 * 가계부 날짜의 마지막 일을 찾는 요청을 처리합니다.
	 *
	 * @param lastDay		 	값을 확인할 년도와 월
	 * @return	월의 마지막 일
	 */
	@PostMapping("/lastDay")
	public int postLastDay( @RequestBody ValidationRequestDTO.JsonLastDay lastDay ) {
		return LocalDate.of( Integer.parseInt(lastDay.getYear()), Integer.parseInt(lastDay.getMonth()), 1 ).lengthOfMonth();
	}




	/**
	 * 하위 카테고리의 이름과 코드를 조회하는 요청을 처리합니다.
	 *
	 * @param code	사용자가 선택한 카테고리
	 * @return	하위 카테고리의 이름과 코드
	 */
	@PostMapping("/category")
	public List<CategoryResponseDTO.Read> postCategories( @RequestBody(required = false) String code ) {
		return budgetBookService.getCategoriesByCode(code);
	}



	/**
	 * 가계부 내역을 한눈에 보기위한 그래프 요청을 처리합니다.<br>
	 *
	 *
	 * @param session			사용자 식별 및 정보를 저장하는 객체
	 * @return	내역 기간에 따른 그래프
	 */
	@GetMapping("/charts")
	public List<Object> postBudgetBookChart( HttpSession session ) {
		String memberId = (String) session.getAttribute("mid");

		//차트날짜 범위에 따른 객체 생성
		List<Object> chartData = chartService.createChartData( memberId, (BudgetBookRequestDTO.ChartJson) session.getAttribute("chart"));
		session.removeAttribute("chart");

		return chartData;
	}



	/**
	 * 가계부 내역에서 원하는 내역을 검색 요청을 처리합니다.<br>
	 * 검색어가 없는 경우에는 요청처리를 진행하지 않습니다.
	 * @param search		검색정보
	 * @param session		사용자 식별 및 정보를 저장하는 객체
	 * @return	검색어에 해당하는
	 */
	@PatchMapping("/search")
	public ResponseEntity<BudgetBookResponseDTO.Preview> patchSearch( @RequestBody BudgetBookRequestDTO.Search search, HttpSession session ) {
		String memberId = (String)session.getAttribute("mid");

		return ResponseEntity.ok(budgetBookService.getBudgetBooksForSummary( memberId, search ));
	}



	/**
	 * 특정 가계부 내역 수정 요청을 처리합니다.
	 *
	 * @param session		사용자 식별 및 정보를 저장하는 객체
	 * @param id						가계부 번호
	 * @param update		수정할 가계부 정보
	 *
	 * @return	안내 메시지
	 */
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponseDTO> update( HttpSession session, @PathVariable Long id,
										  @RequestPart("update") BudgetBookRequestDTO.Update update,
										  @RequestPart(value = "image", required = false) List<MultipartFile> imageFiles ) {
		try{
			update.setImage( imageFiles );

			budgetBookService.updateBudgetBook( (String)session.getAttribute("mid"), id, update );
			return ResponseEntity.ok( ApiResponseDTO.builder().success(true).message("수정 완료했습니다.").build() );
		}catch ( ErrorException e ) {
			return ResponseEntity.ok( ApiResponseDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
	}



	/**
	 * 선택한 가계부 삭제 요청을 처리합니다.<br>
	 * 가계부를 선택하지 않으면 요청 처리를 진행하지 않고 안내 메시지만 노출됩니다.
	 * @param id				가계부 번호
	 * @param session	사용자 식별 및 정보를 저장하는 객체
	 * @return	안내 메시지
	 */
	@DeleteMapping
	public ResponseEntity<ApiResponseDTO> delete(@RequestBody List<Long> id, HttpSession session ) {
		try{
			budgetBookService.deleteBudgetBook( (String)session.getAttribute("mid"), id );

			return ResponseEntity.ok(ApiResponseDTO.builder().success(true).message("선택하신 가계부를 삭제했습니다.").build());
		}catch ( ErrorException e ) {
			return ResponseEntity.ok( ApiResponseDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
	}
}
