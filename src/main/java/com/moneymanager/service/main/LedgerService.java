package com.moneymanager.service.main;

import com.moneymanager.dao.main.LedgerDao;
import com.moneymanager.domain.global.dto.DateRequest;
import com.moneymanager.domain.global.dto.GoogleChartResponse;
import com.moneymanager.domain.global.enums.DatePatterns;
import com.moneymanager.domain.global.vo.DateGroupable;
import com.moneymanager.domain.ledger.dto.LedgerCategoryDto;
import com.moneymanager.domain.ledger.dto.request.LedgerSearchRequest;
import com.moneymanager.domain.ledger.dto.request.LedgerUpdateRequest;
import com.moneymanager.domain.ledger.dto.request.LedgerUpdateWithFileRequest;
import com.moneymanager.domain.ledger.dto.response.LedgerGroupResponse;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.CategoryType;
import com.moneymanager.domain.ledger.enums.DateType;
import com.moneymanager.domain.ledger.vo.*;
import com.moneymanager.utils.date.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.moneymanager.utils.validation.ValidationUtils.isNullOrBlank;


/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.service.main<br>
 * * 파일이름       : LedgerReadService<br>
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

	private final LedgerDao ledgerDAO;

	public LedgerService(CategoryService categoryService, @Qualifier("ledgerImage") ImageServiceImpl imageService,LedgerDao ledgerDAO) {
		this.categoryService = categoryService;
		this.imageService = imageService;

		this.ledgerDAO = ledgerDAO;
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

		return switch (type) {
			case YEAR -> DateTimeUtils.formatDate(date, DatePatterns.KOREAN_YEAR.getPattern());
			case WEEK ->
					String.format("%s %s주", DateTimeUtils.formatDate(date, DatePatterns.KOREAN_YEAR_MONTH_WEEK.getPattern()), scope.getWeek());
			default -> DateTimeUtils.formatDate(date, DatePatterns.KOREAN_YEAR_MONTH.getPattern());
		};
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
		long income = getTotalAmount(cards, CategoryType.INCOME);
		long expense = getTotalAmount(cards, CategoryType.OUTLAY);

		return IncomeExpenseSummary.of(income, expense);
	}

	//금액 유형별 총합 구하기
	private long getTotalAmount(LedgerByDate cards, CategoryType type) {
		return cards.getDateGroups().values().stream()
				.flatMap(Collection::stream)
				.filter(summary -> summary.getType().equals(type))
				.mapToLong(LedgerSummary::getAmount)
				.sum();
	}

	//DateScope VO 생성 및 검증
	private DateScope createAndValidateScope(LedgerSearchRequest search) {
		DateScope scope = createPeriod(search);

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
	 *	가계부 정보와 함께 변경된 이미지 정보를 변경합니다.
	 *<p>
	 *     가계부 정보(금액, 메모, 카테고리 등)를 수정한 뒤, 첨부된 이미지 파일 변경을 처리합니다.
	 *     가계부 정보 수정은 성공하고 이미지 변경이 실패해도 수정된 정보는 그대로 반영됩니다.
	 *</p>
	 *
	 * @param memberId		수정할 가계부 정보를 요청한 회워 ID
	 * @param update			수정할 가계부 정보
	 */
	public void updateLedger(String memberId, String id, LedgerUpdateWithFileRequest update) {
		//기존 Ledger 조회
		Ledger ledger = ledgerDAO.findById(id);
		if( ledger == null ) {
			//TODO: 예외 던지기
		}else if( !ledger.getMemberId().equals(memberId) ) {
			//TODO: 예외 던지기
		}

		boolean isUpdated = changeLedgerInfo( ledger, update.getUpdate() );
		//TODO: isUpdated가 true면 변경로그 작성, false이면 로그없음

		imageService.changeImages(ledger, update.getImages());
	}

	//가계부 정보만 변경
	private boolean changeLedgerInfo(Ledger ledger, LedgerUpdateRequest updateLedger) {
		//필수값 검증 후 수정
		AmountInfo amount = new AmountInfo( updateLedger.getAmount(), updateLedger.getPaymentType() );
		FixedStatus fixed = new FixedStatus(updateLedger.isFixed(), updateLedger.getPeriod());

		ledger.updateBasicInfo(updateLedger.getCategory(), updateLedger.getMemo());
		//ledger.changeFixCycle(fixed);
		ledger.updateAmount(amount);

		//선택값 검증 후 수정
		if( !isNullOrBlank(updateLedger.getPlaceName()) && !isNullOrBlank(updateLedger.getRoadAddress()) ) {
			Place place = new Place( updateLedger.getPlaceName(), updateLedger.getRoadAddress(), updateLedger.getDetailAddress() );
			ledger.updatePlace(place);
		}

		return ledgerDAO.updateLedger( ledger ) == 1;
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