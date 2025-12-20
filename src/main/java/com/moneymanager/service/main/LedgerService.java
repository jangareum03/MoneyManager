package com.moneymanager.service.main;

import com.moneymanager.dao.main.LedgerDao;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.global.vo.DateGroupable;
import com.moneymanager.domain.ledger.dto.*;
import com.moneymanager.domain.ledger.dto.LedgerSearchRequest;
import com.moneymanager.domain.ledger.dto.LedgerDetailResponse;
import com.moneymanager.domain.global.dto.ImageDTO;
import com.moneymanager.domain.global.dto.DateRequest;
import com.moneymanager.domain.global.dto.GoogleChartResponse;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.domain.ledger.enums.*;
import com.moneymanager.domain.ledger.vo.*;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.service.main.validation.DateScopeValidator;
import com.moneymanager.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static com.moneymanager.exception.ErrorUtil.createClientException;
import static com.moneymanager.utils.DateTimeUtils.parseDateFlexible;


/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.service.main<br>
 * * 파일이름       : LedgerService<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 22. 11. 15<br>
 * * 설명              : 가계부 관련 비즈니스 로직을 처리하는 클래스
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
 * 		 	  <td>22. 11. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성(버전 1.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 7. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 12. 05</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 삭제] getWeekByMonth, getPriceByCategory</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@Service
public class LedgerService {

	private final CategoryService categoryService;
	private final ImageServiceImpl imageService;
	private final DateScopeValidator dateScopeValidator;

	private final LedgerDao ledgerDAO;

	public LedgerService(CategoryService categoryService, @Qualifier("ledgerImage") ImageServiceImpl imageService, DateScopeValidator dateScopeValidator, LedgerDao ledgerDAO) {
		this.categoryService = categoryService;
		this.imageService = imageService;
		this.dateScopeValidator = dateScopeValidator;

		this.ledgerDAO = ledgerDAO;
	}


	/**
	 * 가계부 상세 작성 단계에 필요한 데이터를 생성 후 반환합니다.
	 * <p>
	 *    가계부 초기 단계에서 선택한 가계부 유형({@code type})과 날짜({@code date})를 기준으로,
	 *    카테고리 목록, 결제 수단, 이미지 슬롯 정보 등을 포함한 {@link LedgerWriteStep2Response} 객체를 구성합니다.
	 * </p>
	 * <p>
	 *     이미지 슬롯 정보는 회원별 이미지 등록 제한 정책을 따릅니다.
	 * </p>
	 *
	 * @param id			가계부 작성할 회원 ID
	 * @param type		가계부 유형(URL 기준값)
	 * @param date		가계부 날짜 문자열
	 * @return	가계부 상세 작성 단계에 필요한 데이터를 담은 {@link LedgerWriteStep2Response} 객체
	 */
	public LedgerWriteStep2Response getWriteByData(String id, String type, String date) {
		//가계부 유형에 따른 카테고리 목록 조회
		LedgerType ledgerType = LedgerType.fromUrl(type);
		CategoryRequest categoryRequest = CategoryRequest.ofMiddleCategory(ledgerType.getDbCode());
		List<CategoryResponse> categories = categoryService.getSubCategories(categoryRequest);

		//날짜 문자열을 LocalDate로 변환
		LocalDate localDate = parseDateFlexible(date);

		//사용 가능한 이미지 슬롯 여부
		List<Boolean> availableSlots = imageService.getImageSlots(id);

		return LedgerWriteStep2Response.builder()
				.title(DateTimeUtils.formatDateAsString(localDate, "yyyy년 MM월 dd일 E요일"))
				.type(LedgerType.fromUrl(type))
				.fixed(List.of(FixedYN.values()))
				.categories(categories)
				.paymentTypes(List.of(PaymentType.values()))
				.imageSlot(availableSlots)
				.build();
	}



	@Transactional
	public void createLedger(String memberId, LedgerWriteRequest ledger) {
	}


	/**
	 *	가계부 요약 화면에 필요한 데이터를 생성하여 {@link LedgerGroupResponse} 형태로 반환합니다.
	 *<p>
	 *     반환되는 항목:
	 *     <ul>
	 *         <li><b>title</b> - 조회 기간을 나타내는 제목</li>
	 *         <li><b>stats</b> - 총합/수입/지출별 합계 정보</li>
	 *         <li><b>cards</b> - 일자별 가계부 정보를 담은 카드 리스트</li>
	 *     </ul>
	 *</p>
	 *
	 * @param memberId		가계부를 조회할 회원 ID
	 * @param search			가계부 검색 조건 객체({@link LedgerSearchRequest})
	 * @return	제목, 통계, 일자별 카드 데이터를 포함한 {@link LedgerGroupResponse}
	 */
	public LedgerGroupResponse getLedgersForSummary(String memberId, LedgerSearchRequest search) {
		//검증된 조회범위 VO 로 제목 생성
		DateScope scope = createAndValidateScope(search);
		String title = formatDateAsString(scope, search.getType());

		//날짜별 가계부 리스트
		LedgerByDate cards = getLedgers(memberId, search);

		//금액 통계
		IncomeExpenseSummary stats = calculateStats(cards);

		return LedgerGroupResponse.builder()
				.title(title)
				.summary(cards)
				.stats( stats )
				.build();
	}

	//범위(type)에 따라 날짜형식의 문자열로 반환
	private String formatDateAsString(DateScope scope, DateType type) {
		LocalDate date = scope.getStartDate();

		switch (type) {
			case YEAR:
				return DateTimeUtils.formatDateAsString(date, "yyyy년");
			case WEEK:
				return String.format("%s %s주", DateTimeUtils.formatDateAsString(date, "yyyy년 MM월"), scope.getWeek() );
			case MONTH:
			default:
				return DateTimeUtils.formatDateAsString(date, "yyyy년 MM월");
		}
	}

	//일자별 가계부 내역 리스트 반환
	private LedgerByDate getLedgers(String memberId, LedgerSearchRequest search) {
		//SearchPeriod VO 생성 및 검증
		SearchPeriod period;
		if( search.getMode().equalsIgnoreCase("period") ) {
			List<String> dates = search.getKeywords();

			period = new SearchPeriod(dates.get(0), dates.get(1));
		}else {
			//DateScope VO 생성 및 검증
			DateScope scope = createAndValidateScope(search);

			period = new SearchPeriodAdapter(scope).toSearchPeriod();
		}

		List<LedgerCategoryDto> ledgerWithCategory = ledgerDAO.findLedgersBySearch(memberId, search.getMode(), search.getKeywords(), period);
		List<Ledger> ledgers = ledgerWithCategory.stream()
				.map(LedgerCategoryDto::getLedger)
				.distinct()
				.collect(Collectors.toList());

		return new LedgerByDate(ledgers);
	}

	//조회된 금액별 통계 계산
	private IncomeExpenseSummary calculateStats(LedgerByDate cards) {
		long income = getTotalAmount(cards, LedgerType.INCOME);
		long expense = getTotalAmount(cards, LedgerType.OUTLAY);

		return IncomeExpenseSummary.of(income, expense);
	}

	//금액 유형별 총합 구하기
	private long getTotalAmount(LedgerByDate cards, LedgerType type) {
		return cards.getDateGroups().values().stream()
				.flatMap(Collection::stream)
				.filter(summary -> summary.getType().equals(type.getUrlCode()))
				.mapToLong(LedgerSummary::getAmount)
				.sum();
	}

	//DateScope VO 생성 및 검증
	private DateScope createAndValidateScope(LedgerSearchRequest search) {
		DateScope scope = createPeriod(search);
		dateScopeValidator.validate(scope, search.getType());

		return scope;
	}

	//날짜 타입에 따른 DateScope VO 생성
	private DateScope createPeriod(LedgerSearchRequest search) {
		switch ( search.getType() ) {
			case YEAR:
				return DateScope.ofYear(search.getYear());
			case WEEK:
				return DateScope.ofYearMonthWeek(search.getYear(), search.getMonth(), search.getWeek());
			case MONTH:
			default:
				if( search.getYear() == null ) {
					LocalDate today = LocalDate.now();

					return DateScope.ofYearMonth(today.getYear(), today.getMonthValue());
				}

				return DateScope.ofYearMonth(search.getYear(), search.getMonth());
		}
	}


	/**
	 * 구글 차트에 필요한 데이터를 생성하여 {@link GoogleChartResponse} 리스트로 반환합니다.
	 * <p>
	 *     날짜 범위:
	 *     <ul>
	 *         <li>{@link DateType#YEAR} : 월별 수입과 지출 합계</li>
	 *         <li>{@link DateType#MONTH} : 카테고리별 지출 합계</li>
	 *         <li>{@link DateType#WEEK} : 월의 주차별 수입과 지출 합계</li>
	 *     </ul>
	 * </p>
	 *
	 * @param memberId		가계부 조회할 회원 ID
	 * @param date				가계부 조회할 날짜 객체({@link DateRequest})
	 * @return	날짬 범위별로 필요한 금액(수입, 지출) 합계
	 */
	public List<GoogleChartResponse> getLedgerForChart(String memberId, DateRequest date) {
		List<DateGroupable> periodList = new ArrayList<>();

		switch (date.getType()) {
			case YEAR:
				for (int i = 1; i <= 12; i++) {
					periodList.add( DateScope.ofYearMonth(date.getYear(), i) );
				}

				return new ArrayList<>(ledgerDAO.findSumPriceByMonthRange(memberId, periodList));
			case WEEK:
				//해당 월의 총 주차 구하기
				int totalWeeks = DateTimeUtils.getTotalWeeksOfMonth(YearMonth.of(date.getYear(), date.getMonth()));

				for (int i = 1; i <= totalWeeks; i++) {
					periodList.add( DateScope.ofYearMonthWeek( date.getYear(), date.getMonth(), i ) );
				}

				return new ArrayList<>(ledgerDAO.findSumPriceByWeek(memberId, periodList));
			case MONTH:
			default:
				return new ArrayList<>(ledgerDAO.findSumPriceByCategoryAndMonth(memberId, DateScope.ofYearMonth(date.getYear(), date.getMonth())));
		}
	}


	/**
	 *	회원({@code memberId})이 작성한 가계부 상세 정보를 조회 후 반환합니다.
	 *<p>
	 *	가계부 번호({@code id})에 해당하는 가계부 상세 정보를 데이터베이스에서 조회 후 요청한 회원이 작성한 것인지 확인합니다.
	 *	만약 작성자가 아니거나, 요청한 가계부 번호가 없다면 {@link com.moneymanager.exception.custom.ClientException} 예외가 발생합니다.
	 *</p>
	 * 조회한 가계부 정보({@link Ledger})를 {@link LedgerDetailResponse} 객체로 변환 후 반환합니다.
	 *
	 * @param memberId		가계부 상세 정보를 요청한 회원 ID
	 * @param id					조회할 가계부 번호
	 * @return	가계부 상세 정보를 포함한 {@link LedgerDetailResponse} 객체
	 */
	public LedgerDetailResponse getLedgerDetail(String memberId, String id) {
		try{
			LedgerCategoryDto ledgerAndCategory = ledgerDAO.findLedgerDetailForUser(id);
			Ledger ledger = ledgerAndCategory.getLedger();
			Category category = ledgerAndCategory.getCategory();

			if (!memberId.equals(ledger.getMemberId())) {
				throw createClientException(ErrorCode.MEMBER_ID_MISMATCH, "작성자가 아닌 사용자는 해당 가계부에 접근이 불가능합니다.", memberId);
			}

			//이미지 조회
			int limit = imageService.getLimitImageCount(memberId);
			List<LedgerImage> imageList = imageService.getImageListByLedger(ledger.getNum(), limit);

			return LedgerDetailResponse.from(ledger, category, imageList);
		}catch ( EmptyResultDataAccessException e ) {
			throw createClientException(ErrorCode.LEDGER_ID_NONE, "존재하지 않는 가계부입니다.", id);
		}
	}


	/**
	 * 회원({@code memberId})이 수정할 가계부 상세 정보를 조회 후 반환합니다.
	 * <p>
	 *     수정할 가계부 정보를 데이터베이스에서 조회 후 요청한 회원이 작성한 것인지 확인합니다. 만약 작성자가 아니거나, 요청한 가계부 번호가 없다면 {@link com.moneymanager.exception.custom.ClientException} 예외가 발생합니다.
	 *     조회한 가계부 정보({@link Ledger})를 가계부 수정 정보({@link LedgerEditResponse})에 맞게 변환 후 반환합니다.
	 * </p>
	 *
	 * @param memberId		수정할 가계부 정보를 요청한 회원 ID
	 * @param id					조회할 가계부 고유 번호
	 * @return	수정할 가계부 정보를 포함한 {{@link LedgerEditResponse}} 객체
	 */
	public LedgerEditResponse getLedgerEdit(String memberId, String id) {
		try{
			LedgerCategoryDto ledgerAndCategory = ledgerDAO.findLedgerEditForUser(id);
			Ledger ledger = ledgerAndCategory.getLedger();
			Category category = ledgerAndCategory.getCategory();

			if (!memberId.equals(ledger.getMemberId())) {
				throw createClientException(ErrorCode.MEMBER_ID_MISMATCH, "작성자가 아닌 사용자는 해당 가계부에 접근이 불가능합니다.", memberId);
			}

			List<CategoryResponse> categories = categoryService.getAncestorCategoriesByCode(category.getCode());

			//이미지 조회
			int limit = imageService.getLimitImageCount(memberId);
			List<LedgerImage> imageList = imageService.getImageListByLedger(ledger.getNum(), limit);

			return LedgerEditResponse.from(ledger, category, categories, imageList);
		}catch ( EmptyResultDataAccessException e ) {
			throw createClientException(ErrorCode.LEDGER_ID_NONE, "존재하지 않는 가계부입니다.", id);
		}
	}


	/**
	 *	회원의 가계부 내역을 수정하고, 수정된 가계부와 관련된 이미지를 업데이트 합니다.
	 *<p>
	 * 가계부 내역 수정이 성공하고 {@link LedgerUpdateRequest} 객체애 담긴 이미지의 리스트가 0이 아니라면 이미지 수정을 진행합니다.
	 * 만약 두 개의 조건을 모두 만족하지 못 한다면 이미지 수정은 불가합니다.
	 *
	 * @param memberId		수정할 가계부를 작성한 회원 ID
	 * @param update			수정할 내역과 이미지 정보를 담은 {@link LedgerUpdateRequest} 객체
	 */
	@Transactional
	public void updateLedger(String memberId, LedgerUpdateRequest update) {
		//이미지리스트
		List<ImageDTO> imageFiles = update.getImage();

		Ledger ledger = update.toEntity();
		try {
			boolean isUpdate = ledgerDAO.updateLedger(ledger);

			if( isUpdate && !imageFiles.isEmpty()) {
				imageService.changeImage(memberId, ledger, imageFiles);
			}

		} catch (IOException e) {
			log.debug("변경하려는 가계부 이미지 미존재로 저장 실패");
		}
	}


	/**
	 * 번호에 해당하는 가계부를 삭제하는 메서드
	 *
	 * @param memberId 회원 식별번호
	 * @param id       삭제할 가계부 번호
	 */
	public void deleteLedger(String memberId, List<Long> id) {
		if (ledgerDAO.deleteLedgerById(memberId, id)) {
			log.debug("{} 회원의 {} 가계부 삭제 완료", memberId, id);
		}
	}
}