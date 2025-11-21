package com.moneymanager.service.main;

import com.moneymanager.dao.main.BudgetBookDao;
import com.moneymanager.domain.global.vo.DateGroupable;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.dto.*;
import com.moneymanager.domain.ledger.dto.LedgerSearchRequest;
import com.moneymanager.domain.ledger.dto.LedgerResponse;
import com.moneymanager.domain.ledger.dto.LedgerListResponse;
import com.moneymanager.domain.ledger.dto.LedgerWriteResponse;
import com.moneymanager.domain.global.dto.ImageDTO;
import com.moneymanager.domain.global.dto.DateRequest;
import com.moneymanager.domain.global.dto.GoogleChartResponse;
import com.moneymanager.domain.ledger.vo.LedgerDate;
import com.moneymanager.domain.ledger.vo.PeriodSearch;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.domain.ledger.enums.DateType;
import com.moneymanager.domain.ledger.enums.BudgetBookType;
import com.moneymanager.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static com.moneymanager.utils.DateTimeUtils.*;


/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.service.main<br>
 * * 파일이름       : BudgetBookService<br>
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
 * 		</tbody>
 * </table>
 */
@Slf4j
@Service
public class BudgetBookService {

	private final CategoryService categoryService;
	private final ImageServiceImpl imageService;
	private final BudgetBookDao budgetBookDAO;

	public BudgetBookService(CategoryService categoryService, @Qualifier("budgetImage") ImageServiceImpl imageService, BudgetBookDao budgetBookDAO) {
		this.categoryService = categoryService;
		this.imageService = imageService;
		this.budgetBookDAO = budgetBookDAO;
	}


	/**
	 * 가계부 작성에 필요한 기본 정보를 반환합니다.<p>
	 * 기본정보에는 제목, 카테고리 리스트, 등록 가능한 이미지 개수가 포함됩니다.<br>
	 * 제목은 가계부 날짜를 {@code yyyy년 mm월 dd일 E요일} 형식으로 저장됩니다.
	 *
	 * @param id 			회원 식별번호
	 * @param type      작성할 가계부 유형
	 * @param date		작성할 가계부 날짜 문자열
	 * @return 작성에 필요한 기본 정보
	 */
	public LedgerWriteResponse.InitialBudget getWriteByData(String id, String type, String date) {
		LedgerDate ledgerDate = new LedgerDate(date);
		String title = formatDateAsString(ledgerDate.getDate(), "yyyy년 MM월 dd일 E요일");

		int availableCount = imageService.getLimitImageCount(id);        //등록 가능한 이미지 개수

		//카테고리 리스트
		List<LedgerCategoryResponse> topCategory = categoryService.getTopCategories();
		String code = type.equalsIgnoreCase(BudgetBookType.INCOME.getType()) ? topCategory.get(0).getCode() : topCategory.get(1).getCode();

		return LedgerWriteResponse.InitialBudget.builder()
				.date(title).type(type).maxImage(availableCount)
				.categories(categoryService.getMySubCategories(code))
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
	public void createBudgetBook(String memberId, LedgerWriteRequest create) {
		//TODO: 값 검증 로직 추가

	}


	/**
	 *	가계부 요약 화면에 필요한 데이터를 생성하여 {@link LedgerListResponse} 형태로 반환합니다.
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
	 * @return	제목, 통계, 일자별 카드 데이터를 포함한 {@link LedgerListResponse}
	 */
	public LedgerListResponse getBudgetBooksForSummary(String memberId, LedgerSearchRequest search) {
		//PeriodSearch VO 생성
		DateGroupable period = createPeriod(search);

		//가계부 데이터 조회
		List<LedgerListResponse.DayCards> dayCards = getBudgetBooks(memberId, search);

		//제목 생성
		String title = formatDateAsString(period.getStartDate(), DateType.from(search.getType()));

		return LedgerListResponse.builder()
				.title(title)
				.stats(getPriceByCategory(dayCards))
				.cards(dayCards)
				.build();
	}


	//일자별 카드 리스트 리스트 반환
	private List<LedgerListResponse.DayCards> getBudgetBooks(String memberId, LedgerSearchRequest search) {
		List<LedgerListResponse.DayCards> cards = new ArrayList<>();

		//PeriodSearch VO 생성
		DateGroupable period = createPeriod(search);

		for (LedgerListResponse.DayCards dayCards : budgetBookDAO.findBudgetBooksBySearch(memberId, search.getMode(), search.getKeywords(), period) ) {
			List<LedgerListResponse.Card> cardList = dayCards.getCardList();

			String formatDate = formatDateAsString(new LedgerDate(dayCards.getDate()).getDate(), "yyyy. MM. dd (E)");

			cards.add( LedgerListResponse.DayCards.builder().date(formatDate).cardList(cardList).build() );
		}

		return cards;
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
	public List<GoogleChartResponse> getBudgetBookForChart(String memberId, DateRequest date) {
		List<DateGroupable> periodList = new ArrayList<>();

		switch (date.getType()) {
			case YEAR:
				for (int i = 1; i <= 12; i++) {
					periodList.add( PeriodSearch.ofYearMonth(date.getYear(), i) );
				}

				return new ArrayList<>(budgetBookDAO.findSumPriceByMonthRange(memberId, periodList));
			case WEEK:
				//해당 월의 총 주차 구하기
				int totalWeeks = DateTimeUtils.getTotalWeeksOfMonth(YearMonth.of(date.getYear(), date.getMonth()));

				for (int i = 1; i <= totalWeeks; i++) {
					periodList.add( PeriodSearch.ofYearMonthWeek( date.getYear(), date.getMonth(), i ) );
				}

				return new ArrayList<>(budgetBookDAO.findSumPriceByWeek(memberId, periodList));
			case MONTH:
			default:
				return new ArrayList<>(budgetBookDAO.findSumPriceByCategoryAndMonth(memberId, PeriodSearch.ofYearMonth(date.getYear(), date.getMonth())));
		}
	}


	//수입, 지출 카테고리별 금액 합계 반환
	private LedgerListResponse.Stats getPriceByCategory(List<LedgerListResponse.DayCards> dayCards) {
		List<LedgerListResponse.Card> cards = dayCards.stream().flatMap(day -> day.getCardList().stream()).collect(Collectors.toList());

		Map<String, Long> categoryPrice = cards.stream()
				.collect(Collectors.groupingBy(
						card -> {
							String type = card.getCategory().getCode().substring(0,2);

							return type.equals("01") ? "income" : "outlay";
						},

						Collectors.summingLong( LedgerListResponse.Card::getPrice )
				));

		long income = Objects.isNull(categoryPrice.get("income")) ? 0L : categoryPrice.get("income");
		long outlay = Objects.isNull(categoryPrice.get("outlay")) ? 0L : categoryPrice.get("outlay");
		long total = income + outlay;

		return LedgerListResponse.Stats.builder()
				.total(total).income(income).outlay(outlay).build();
	}


	/**
	 * 카테고리 코드의 하위 카테고리들을 찾는 메서드
	 *
	 * @param code 카테고리 코드
	 * @return 하위 카테고리 리스트
	 */
	public List<LedgerCategoryResponse> getCategoriesByCode(String code) {
		if (Objects.isNull(code)) {
			return categoryService.getTopCategories();
		}

		return categoryService.getMySubCategories(code);
	}


	/**
	 * 가계부 번호에 해당하는 가계부 정보를 반환합니다. <br>
	 * 접근한 가계부 작성자를 확인하여 작성자가 아닌 다른 회원이 접근하면 이 발생합니다.
	 *
	 * @param memberId 회원 식별번호
	 * @param id       가계부 번호
	 * @return 번호에 해당하는 가계부 상세정보
	 */
	public LedgerResponse getBudgetBookById(String memberId, Long id, String mode) {
		Ledger ledger = budgetBookDAO.findBudgetBookById(id);

		//날짜 포맷
		String formatDate = formatDateAsString( ledger.getLedgerDate(), "yyyy. MM. dd (E)" );
		if( mode.equalsIgnoreCase("edit") ) {
			formatDate = formatDateAsString( ledger.getLedgerDate(), "yyyy년 MM월 dd일 E요일" );
		}

		//고정주기 변환
		LedgerFixResponse fix = Objects.isNull(ledger.getFixCycle()) ?
				LedgerFixResponse.defaultValue() : LedgerFixResponse.builder().option(ledger.getFix().toLowerCase()).cycle(ledger.getFixCycle().toLowerCase()).build();


		//카테고리 변환
		LedgerCategoryResponse category = LedgerCategoryResponse.builder()
				.code(ledger.getCategory().getCode())
				.name(ledger.getCategory().getName())
				.build();

		//위치 변환
		Place place = Objects.isNull(ledger.getPlaceName()) ?
				null : Place.builder().placeName(ledger.getPlaceName()).roadAddress(ledger.getRoadAddress()).detailAddress(ledger.getAddress()).build();

		//이미지 변환
		List<String> profileImage = imageService.findImageUrl(ledger);

		if (memberId.equals(ledger.getMember().getId())) {
			return LedgerResponse.builder()
					.date( formatDate )
					.image(profileImage)
					.fix(fix).category(category).place(place)
					.id(ledger.getId()).memo(ledger.getMemo()).price(ledger.getPrice()).paymentType(ledger.getPaymentType().getText())
					.build();
		} else {
			throw new RuntimeException("");
		}
	}


	/**
	 * 하위카테고리의 모든 상위 카테고리의 이름을 조회하는 메서드
	 *
	 * @param code 하위카테고리 코드
	 * @return 하위카테고리 포함한 상위 카테고리 이름 리스트
	 */
	public List<LedgerCategoryResponse> getCategoryByStep(String code) {
		return categoryService.getMyParentCategories(code);
	}


	/**
	 * 카테고리 타입에 따라 해당 타입의 모든 하위 카테고리를 반환합니다.<br>
	 * <li>in : 모든 수입 카테고리</li>
	 * <li>out : 모든 지출 카테고리</li>
	 *
	 * @param categories 가계부 카테고리 정보
	 * @return 유형별 모든 하위 카테고리
	 */
	public Map<String, Object> getCategoriesByCode(List<LedgerCategoryResponse> categories) {
		Map<String, Object> map = new HashMap<>();

		//상위카테고리
		List<LedgerCategoryResponse> topCategory = categoryService.getTopCategories();
		map.put("top", topCategory);

		//중간카테고리
		List<LedgerCategoryResponse> middleCategory = categoryService.getMySubCategories(categories.get(0).getCode());
		map.put("middle", middleCategory);

		//하위카테고리
		List<LedgerCategoryResponse> bottomCategory = categoryService.getMySubCategories(categories.get(1).getCode());
		map.put("bottom", bottomCategory);


		return map;
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
	public void updateBudgetBook(String memberId, LedgerUpdateRequest update) {
		//이미지리스트
		List<ImageDTO> imageFiles = update.getImage();

		Ledger ledger = update.toEntity();
		try {
			boolean isUpdate = budgetBookDAO.updateBudgetBook(ledger);

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
	public void deleteBudgetBook(String memberId, List<Long> id) {
		if (budgetBookDAO.deleteBudgetBookById(memberId, id)) {
			log.debug("{} 회원의 {} 가계부 삭제 완료", memberId, id);
		} else {

		}
	}


	//날짜 타입에 따른 PeriodSearch VO 생성
	private DateGroupable createPeriod(LedgerSearchRequest search) {
		switch ( DateType.from(search.getType()) ) {
			case YEAR:
				return PeriodSearch.ofYear(search.getYear());
			case WEEK:
				return PeriodSearch.ofYearMonthWeek(search.getYear(), search.getMonth(), search.getWeek());
			case MONTH:
			default:
				if( search.getYear() == null ) {
					LocalDate today = LocalDate.now();

					return PeriodSearch.ofYearMonth(today.getYear(), today.getMonthValue());
				}

				return PeriodSearch.ofYearMonth(search.getYear(), search.getMonth());
		}
	}
}
