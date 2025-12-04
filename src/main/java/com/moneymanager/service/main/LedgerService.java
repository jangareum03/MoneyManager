package com.moneymanager.service.main;

import com.moneymanager.dao.main.LedgerDao;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.global.vo.DateGroupable;
import com.moneymanager.domain.ledger.dto.*;
import com.moneymanager.domain.ledger.dto.LedgerSearchRequest;
import com.moneymanager.domain.ledger.dto.LedgerResponse;
import com.moneymanager.domain.ledger.dto.LedgerWriteResponse;
import com.moneymanager.domain.global.dto.ImageDTO;
import com.moneymanager.domain.global.dto.DateRequest;
import com.moneymanager.domain.global.dto.GoogleChartResponse;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.domain.ledger.vo.*;
import com.moneymanager.domain.ledger.enums.DateType;
import com.moneymanager.service.main.validation.CategoryValidator;
import com.moneymanager.service.main.validation.DateScopeValidator;
import com.moneymanager.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;


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
	 * 가계부 작성에 필요한 기본 정보를 반환합니다.<p>
	 * 기본정보에는 제목, 카테고리 리스트, 등록 가능한 이미지 개수가 포함됩니다.<br>
	 * 제목은 가계부 날짜를 {@code yyyy년 mm월 dd일 E요일} 형식으로 저장됩니다.
	 *
	 * @param id 			회원 식별번호
	 * @param type      작성할 가계부 유형("income", "outlay")
	 * @param date		작성할 가계부 날짜 문자열
	 * @return 작성에 필요한 기본 정보
	 */
	public LedgerWriteResponse.ledgerDetail getWriteByData(String id, String type, String date) {
		LedgerDate ledgerDate = new LedgerDate(date);
		String title = DateTimeUtils.formatDateAsString(ledgerDate.getTransactionDate(), "yyyy년 MM월 dd일 E요일");

		int availableCount = imageService.getLimitImageCount(id);        //등록 가능한 이미지 개수

		//중간 카테고리를 가계부 유형에 따라 가져오기
		String code = LedgerType.fromUrl(type).getDbCode();
		CategoryValidator.validate(CategoryRequest.ofMiddleCategory(code));

		List<CategoryResponse> categories = categoryService.getSubCategories(CategoryRequest.ofMiddleCategory(code));

		return LedgerWriteResponse.ledgerDetail.builder()
				.date(title).type(type)
				.maxImage(availableCount)
				.categories(categories)
				.build();
	}


	/**
	 * 가계부를 등록합니다.<br>
	 * 등록할 가계부 정보거 없거나 가계부 사진이 등록되지 않은 경우에는 이 발생합니다.
	 *
	 * @param memberId 회원번호
	 * @param create   가계부 정보
	 */
	@Transactional
	public void createLedger(String memberId, LedgerWriteRequest create) {
		//TODO: 값 검증 로직 추가

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

		List<Ledger> ledgers = ledgerDAO.findLedgersBySearch(memberId, search.getMode(), search.getKeywords(), period);


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
				.mapToLong(LedgerSummary::getAmountValue)
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
	 * 가계부 번호에 해당하는 가계부 정보를 반환합니다. <br>
	 * 접근한 가계부 작성자를 확인하여 작성자가 아닌 다른 회원이 접근하면 이 발생합니다.
	 *
	 * @param memberId 회원 식별번호
	 * @param id       가계부 번호
	 * @return 번호에 해당하는 가계부 상세정보
	 */
	public LedgerResponse getLedgerById(String memberId, Long id, String mode) {
		Ledger ledger = ledgerDAO.findLedgerById(id);

		//날짜 포맷
		String formatDate = DateTimeUtils.formatDateAsString( ledger.getTransActionDate(), "yyyy. MM. dd (E)" );
		if( mode.equalsIgnoreCase("edit") ) {
			formatDate = DateTimeUtils.formatDateAsString( ledger.getTransActionDate(), "yyyy년 MM월 dd일 E요일" );
		}

		//고정주기 변환
		FixedStatus fix = new FixedStatus( ledger.isReturning(), ledger.getCycleType() );

		//위치 변환
		Place place = Objects.isNull(ledger.getPlace().getPlaceName()) ?
				null : Place
							.builder()
							.placeName(ledger.getPlace().getPlaceName())
							.roadAddress(ledger.getPlace().getRoadAddress())
							.detailAddress(ledger.getPlace().getDetailAddress())
							.build();

		//이미지 변환
		List<String> profileImage = imageService.findImageUrl(ledger);

		if (memberId.equals(ledger.getMember().getId())) {
			return LedgerResponse.builder()
					.date( formatDate )
					.image(profileImage)
					.fix(fix)
					.category(CategoryResponse.from(ledger.getCategory()))
					.place(place)
					.id(ledger.getId())
					.memo(ledger.getMemo())
					.amount(ledger.getAmountInfo())
					.build();
		} else {
			throw new RuntimeException("");
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