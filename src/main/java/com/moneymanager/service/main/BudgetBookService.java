package com.moneymanager.service.main;

import com.moneymanager.dao.main.BudgetBookDao;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.dto.*;
import com.moneymanager.domain.ledger.dto.LedgerSearchRequest;
import com.moneymanager.domain.ledger.dto.LedgerResponse;
import com.moneymanager.domain.ledger.dto.LedgerListResponse;
import com.moneymanager.domain.ledger.dto.LedgerWriteResponse;
import com.moneymanager.domain.global.dto.ImageDTO;
import com.moneymanager.domain.global.dto.DateRequest;
import com.moneymanager.domain.global.dto.GoogleChartResponse;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.ledger.vo.LedgerDate;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.domain.ledger.vo.YearMonthDayVO;
import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.ledger.enums.DateType;
import com.moneymanager.domain.ledger.enums.BudgetBookType;
import com.moneymanager.service.validation.BudgetBookValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

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
	 * @param date		작성할 가계부 날짜 정보를 담은 객체
	 * @return 작성에 필요한 기본 정보
	 */
	public LedgerWriteResponse.InitialBudget getWriteByData(String id, String type, YearMonthDayVO date) {
		String title = date.formatDate("yyyy년 MM월 dd일 E요일");

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
		BudgetBookValidator.validateWrite( create.toRequiredFields() );
	}


	/**
	 * 입력한 날짜 값에 따른 제목을 반환하는 메서드
	 *
	 * @param date 검색할 가계부 날짜 값
	 * @return 입력한 날짜 형식에 따른 제목
	 */
	public String makeTitleByType(DateRequest date) {

		DateType type = date.getType();
		switch (type) {
			case WEEK:
				return String.format("%d %02d %02d", date.getYear(), date.getMonth(), date.getWeek());
			case YEAR:
				return String.format("%d", date.getYear());
			case MONTH:
			default:
				return String.format("%d %02d", date.getYear(), date.getMonth());
		}
	}


	/**
	 * 가계부 조회 날짜에 포함되는 가계부 정보와 가격정보를 찾는 메서드
	 *
	 * @param memberId 회원 식별번호
	 * @param search   가계부 조회 정보 객체
	 * @return 가계부 정보와 가격
	 */
	public LedgerListResponse getBudgetBooksForSummary(String memberId, LedgerSearchRequest search) {
		List<LedgerListResponse.DayCards> dayCards = getBudgetBooks(memberId, search);

		return LedgerListResponse.builder()
				.stats(getPriceByCategory(dayCards))
				.cards(dayCards)
				.build();
	}


	/**
	 * 가계부 조회 날짜에 포함되는 가계부 리스트를 찾는 메서드
	 *
	 * @param memberId 회원 식별번호
	 * @param search   가계부 검색 조건
	 * @return 가계부 리스트
	 */
	public List<LedgerListResponse.DayCards> getBudgetBooks(String memberId, LedgerSearchRequest search) {
		List<LedgerListResponse.DayCards> cards = new ArrayList<>();

		for (LedgerListResponse.DayCards dayCards : budgetBookDAO.findBudgetBooksBySearch(memberId, getDateForSearch(search.getDate()), search) ) {
			String date = dayCards.getDate();
			List<LedgerListResponse.Card> cardList = dayCards.getCardList();

			String formatDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")).format(DateTimeFormatter.ofPattern("yyyy. MM. dd (E)", Locale.KOREAN));

			cards.add( LedgerListResponse.DayCards.builder().date(formatDate).cardList(cardList).build() );
		}

		return cards;
	}


	/**
	 * 가계부 조회 날짜 범위로 시작날짜와 종료날짜를 찾는 메서드
	 *
	 * @param date 가계부 조회 날짜 정보
	 * @return 검색 시작날짜와 종료날짜
	 */
	private LocalDate[] getDateForSearch(DateRequest date) {
		DateType type = date.getType();
		LocalDate from, to;

		//검색할 가계부의 날짜 범위의 타입 조회
		if (type == DateType.WEEK) {
			/*
			 * [ 첫째주 시작일과 종료일을 알기위한 설정 ]
			 * 	- startIndex 	: 1일 시작요일 인덱스( 1:월요일 ~ 7: 일요일)
			 * 	- addDay				: 첫째 주의 마지막 날짜를 구하기 위한 추가 일수
			 *   - firstDay 			: 월의 첫 시작일로 설정된 LocalDate
			 */
			LocalDate firstDay = LocalDate.of(date.getYear(), date.getMonth(), 1);
			int startIndex = firstDay.get(ChronoField.DAY_OF_WEEK);
			int addDay = (startIndex == 7) ? startIndex - 1 : 6 - startIndex;

			//시작일과 종료일 초기화
			from = firstDay;
			to = firstDay.plusDays(addDay);

			//주에 맞게 날짜 설정
			if (date.getWeek() != 1) {
				to = to.plusDays(7L * (date.getWeek() - 1));
				from = to.minusDays(6);

				if (to.getMonthValue() != date.getMonth()) {    //마지막일자가 다음달로 넘어간 경우
					to = LocalDate.of(date.getYear(), date.getMonth(), firstDay.lengthOfMonth());
				}
			}


		} else if (type == DateType.YEAR) {
			from = LocalDate.of(date.getYear(), 1, 1);
			to = LocalDate.of(date.getYear(), 12, 31);
		} else {
			from = LocalDate.of(date.getYear(), date.getMonth(), 1);
			to = LocalDate.of(date.getYear(), date.getMonth(), from.lengthOfMonth());
		}

		return new LocalDate[]{from, to};
	}


	/**
	 * 가계부 타입에 따른 차트 본문을 반환하는 메서드
	 *
	 * @param memberId 회원 식별번호
	 * @param date     검색할 가계부 날짜 값
	 * @return 가계부 차트의 본문을 담은 리스트
	 */
	public List<GoogleChartResponse> getBudgetBookForChart(String memberId, DateRequest date) {
		List<LocalDate[]> dateRange = new ArrayList<>();

		DateType type = date.getType();
		switch (type) {
			case YEAR:
				//해당 년의 월 구하기
				for (int i = 1; i <= 12; i++) {
					LocalDate from = LocalDate.of(date.getYear(), i, 1);
					LocalDate to = LocalDate.of(date.getYear(), i, from.lengthOfMonth());

					dateRange.add(new LocalDate[]{from, to});
				}

				return new ArrayList<>(budgetBookDAO.findSumPriceByYear(memberId, dateRange));
			case WEEK:
				//해당 월의 총 주차 구하기
				LocalDate localDate = LocalDate.of(date.getYear(), date.getMonth(), 1);
				int weekCount = (int) Math.ceil((localDate.get(ChronoField.DAY_OF_WEEK) + localDate.lengthOfMonth()) / 7.0);

				//주의 시작일과 종료일을 담은 리스트
				for (int i = 1; i <= weekCount; i++) {
					DateRequest.WeekRange week = DateRequest.WeekRange.builder().year( String.valueOf((date.getYear())) ).month( String.valueOf(date.getMonth()) ).week(String.valueOf(i)).build();
					dateRange.add(getDateForSearch(new DateRequest(week)));
				}

				return new ArrayList<>(budgetBookDAO.findSumPriceByWeek(memberId, dateRange));
			case MONTH:
			default:
				LocalDate from = LocalDate.of(date.getYear(), date.getMonth(), 1);
				LocalDate to = LocalDate.of(date.getYear(), date.getMonth(), from.lengthOfMonth());

				return new ArrayList<>(budgetBookDAO.findSumPriceByCategoryAndMonth(memberId, new LocalDate[]{from, to}));
		}
	}


	/**
	 * 가계부 조회 날짜에 포함되는 카테고리별로 가격을 찾는 메서드
	 *
	 * @param dayCards	날짜별로 묶은 가계부 리스트
	 * @return 카테고리별로 가격을 담은 맵
	 */
	private LedgerListResponse.Stats getPriceByCategory(List<LedgerListResponse.DayCards> dayCards) {
		List<LedgerListResponse.Card> cards = dayCards.stream().flatMap(day -> day.getCardList().stream()).collect(Collectors.toList());

		Map<String, Long> categoryPrice = cards.stream()
				.collect(Collectors.groupingBy(
						card -> {
							System.out.println("🍒" + card.getCategory());
							System.out.println("🍒" + card.getCategory().getCode());
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
		Ledger entity = budgetBookDAO.findBudgetBookById(id);

		//날짜 포맷
		LocalDate date = LocalDate.parse(entity.getBookDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
		String formatDate = date.format(DateTimeFormatter.ofPattern("yyyy. MM. dd (E)", Locale.KOREAN));
		if (mode.equalsIgnoreCase("edit")) {
			formatDate = date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일", Locale.KOREAN));
		}

		//고정주기 변환
		LedgerFixResponse fix = Objects.isNull(entity.getFixCycle()) ?
				LedgerFixResponse.defaultValue() : LedgerFixResponse.builder().option(entity.getFix().toLowerCase()).cycle(entity.getFixCycle().toLowerCase()).build();


		//카테고리 변환
		LedgerCategoryResponse category = LedgerCategoryResponse.builder()
				.code(entity.getCategory().getCode())
				.name(entity.getCategory().getName())
				.build();

		//위치 변환
		Place place = Objects.isNull(entity.getPlaceName()) ?
				null : Place.builder().placeName(entity.getPlaceName()).roadAddress(entity.getRoadAddress()).detailAddress(entity.getAddress()).build();


		//이미지 변환
		List<String> profileImage = imageService.findImageUrl(entity);

		if (memberId.equals(entity.getMember().getId())) {
			return LedgerResponse.builder()
					.date( LedgerResponse.ReadDate.builder().read(date.toString()).text(formatDate).build() )
					.image(profileImage)
					.fix(fix).category(category).place(place)
					.id(entity.getId()).memo(entity.getMemo()).price(entity.getPrice()).paymentType(entity.getPaymentType().getText())
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
	 * 가계부를 수정하는 메서드
	 *
	 * @param memberId 회원 식별번호
	 * @param update   수정할 가계부 정보
	 */
	@Transactional
	public void updateBudgetBook(String memberId, Long id, LedgerUpdateRequest update) {

		if (Objects.isNull(update)) {
		}

		//날짜 변환
		LocalDate date = LocalDate.parse(update.getDate(), DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일"));
		String formatDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		//이미지리스트
		List<ImageDTO> imageFiles = update.getImage();

		//DTO → Entity 변환
		Ledger entity = Ledger.builder()
				.member(Member.builder().id(memberId).build())
				.id(id).bookDate(formatDate)
				.fix(update.getFix().getOption().toUpperCase()).fixCycle(Objects.isNull(update.getFix().getCycle()) ? null : update.getFix().getCycle().toUpperCase())
				.category(Category.builder().code(update.getCategory()).build())
				.memo(update.getMemo())
				.price(update.getPrice()).paymentType(PaymentType.valueOf(update.getPaymentType().toUpperCase()))
				.image1(imageFiles.get(0).getFileName()).image2(imageFiles.get(1).getFileName()).image3(imageFiles.get(2).getFileName())
				.placeName(Objects.isNull(update.getPlace().getPlaceName()) ? null : update.getPlace().getPlaceName())
				.roadAddress(Objects.isNull(update.getPlace().getRoadAddress()) ? null : update.getPlace().getRoadAddress())
				.address(Objects.isNull(update.getPlace().getDetailAddress()) ? null : update.getPlace().getDetailAddress())
				.build();


		try {
			boolean isUpdate = budgetBookDAO.updateBudgetBook(entity);

			if (!isUpdate) {

			}

			imageService.changeImage(memberId, entity, imageFiles);

		} catch (IOException e) {
			log.debug("변경하려는 가계부 이미지 미존재로 저장 실패");;
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
}
