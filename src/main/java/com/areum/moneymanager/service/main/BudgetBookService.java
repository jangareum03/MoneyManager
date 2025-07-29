package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.main.BudgetBookDao;
import com.areum.moneymanager.dto.budgetBook.CategoryDTO;
import com.areum.moneymanager.dto.budgetBook.FixDTO;
import com.areum.moneymanager.dto.budgetBook.PlaceDTO;
import com.areum.moneymanager.dto.budgetBook.request.BudgetBookSearchRequest;
import com.areum.moneymanager.dto.budgetBook.request.BudgetBookUpdateRequest;
import com.areum.moneymanager.dto.budgetBook.request.BudgetBookWriteRequest;
import com.areum.moneymanager.dto.budgetBook.response.BudgetBookDetailResponse;
import com.areum.moneymanager.dto.budgetBook.response.BudgetBookListResponse;
import com.areum.moneymanager.dto.budgetBook.response.BudgetBookWriteResponse;
import com.areum.moneymanager.dto.common.ImageDTO;
import com.areum.moneymanager.dto.common.request.DateRequest;
import com.areum.moneymanager.dto.external.google.GoogleChartResponse;
import com.areum.moneymanager.entity.BudgetBook;
import com.areum.moneymanager.entity.Category;
import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.enums.type.DateType;
import com.areum.moneymanager.exception.code.ErrorCode;
import com.areum.moneymanager.enums.type.BudgetBookType;
import com.areum.moneymanager.exception.ErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static com.areum.moneymanager.exception.code.ErrorCode.BUDGET_UPDATE_UNKNOWN;

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
@Service
public class BudgetBookService {

	private final CategoryService categoryService;
	private final ImageServiceImpl imageService;
	private final BudgetBookDao budgetBookDAO;

	private final Logger logger = LogManager.getLogger(this);

	public BudgetBookService(CategoryService categoryService, @Qualifier("budgetImage") ImageServiceImpl imageService, BudgetBookDao budgetBookDAO) {
		this.categoryService = categoryService;
		this.imageService = imageService;
		this.budgetBookDAO = budgetBookDAO;
	}


	/**
	 * 가계부 작성에 필요한 기본 정보를 반환합니다.<p>
	 * 기본정보에는 제목, 카테고리 리스트, 등록 가능한 이미지 개수가 포함됩니다.<br>
	 * 제목은 가계부 날짜를 'yyyy년 mm월 dd일 e요일' 형식으로 저장됩니다.
	 *
	 * @param memberId 회원 고유번호
	 * @param set      가계부 날짜를 저장한 객체
	 * @return 작성에 필요한 기본 정보
	 */
	public BudgetBookWriteResponse.InitialBudget getWriteByData(String memberId, BudgetBookWriteRequest.InitialBudget set) throws ParseException {
		String title = new SimpleDateFormat("yyyy년 MM월 dd일 E요일").format(new SimpleDateFormat("yyyyMMdd").parse(set.getDate()));
		int availableCount = imageService.getLimitImageCount(memberId);        //등록 가능한 이미지 개수

		//카테고리 리스트
		List<CategoryDTO> topCategory = categoryService.getTopCategories();

		String code;
		if (set.getType().equalsIgnoreCase(BudgetBookType.INCOME.getType())) {
			code = topCategory.get(0).getCode();
		} else {
			code = topCategory.get(1).getCode();
		}


		return BudgetBookWriteResponse.InitialBudget.builder().date(title).type(set.getType()).maxImage(availableCount).categories(categoryService.getMySubCategories(code)).build();
	}


	/**
	 * 가계부를 등록합니다.<br>
	 * 등록할 가계부 정보거 없거나 가계부 사진이 등록되지 않은 경우에는 {@link ErrorException}이 발생합니다.
	 *
	 * @param memberId 회원번호
	 * @param create   가계부 정보
	 */
	@Transactional
	public void createBudgetBook(String memberId, BudgetBookWriteRequest.DetailedBudget create) {

		if (Objects.isNull(create)) {
			throw new ErrorException(ErrorCode.BUDGET_REGISTER_UNKNOWN);
		}

		//날짜 변환
		LocalDate date = LocalDate.parse(create.getDate(), DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일"));
		String formatDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));


		//이미지를 담은 DTO 리스트
		List<ImageDTO> imageFiles
				= imageService.getImageList(memberId,
				Objects.nonNull(create.getImage()) ? create.getImage() : new ArrayList<>());


		//DTO → Entity 변환
		BudgetBook entity = BudgetBook.builder()
				.member(Member.builder().id(memberId).build())
				.category(Category.builder().code(create.getCategory()).build())
				.bookDate(formatDate).memo(create.getMemo())
				.price(create.getPrice()).paymentType(create.getPaymentType().toUpperCase())
				.fix(create.getFix().getOption().toUpperCase()).fixCycle(create.getFix().getOption().equalsIgnoreCase("y") ? create.getFix().getCycle().toUpperCase() : null)
				.image1(imageFiles.get(0).getFileName()).image2(imageFiles.get(1).getFileName()).image3(imageFiles.get(2).getFileName())
				.placeName((Objects.isNull(create.getPlace())) ? null : create.getPlace().getName())
				.roadAddress((Objects.isNull(create.getPlace())) ? null : create.getPlace().getRoadAddress())
				.address(Objects.isNull(create.getPlace()) ? null : create.getPlace().getAddress())
				.build();


		try {
			//가계부 등록
			BudgetBook budgetBook = budgetBookDAO.saveBudgetBook(entity);

			for (int i = 0; i < imageFiles.size(); i++) {
				if (Objects.nonNull(imageFiles.get(i).getFile())) {
					imageService.saveImage(budgetBook, imageFiles.get(i).getFile(), i);
				}
			}

		} catch (NullPointerException | IOException e) {
			throw new ErrorException(ErrorCode.BUDGET_REGISTER_UNKNOWN);
		}
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
	public BudgetBookListResponse getBudgetBooksForSummary(String memberId, BudgetBookSearchRequest search) {
		List<BudgetBookListResponse.DayCards> dayCards = getBudgetBooks(memberId, search);

		return BudgetBookListResponse.builder()
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
	public List<BudgetBookListResponse.DayCards> getBudgetBooks(String memberId, BudgetBookSearchRequest search) {
		List<BudgetBookListResponse.DayCards> cards = new ArrayList<>();

		for (BudgetBookListResponse.DayCards dayCards : budgetBookDAO.findBudgetBooksBySearch(memberId, getDateForSearch(search.getDate()), search) ) {
			String date = dayCards.getDate();
			List<BudgetBookListResponse.Card> cardList = dayCards.getCardList();

			String formatDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")).format(DateTimeFormatter.ofPattern("yyyy. MM. dd (E)", Locale.KOREAN));

			cards.add( BudgetBookListResponse.DayCards.builder().date(formatDate).cardList(cardList).build() );
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
	private BudgetBookListResponse.Stats getPriceByCategory(List<BudgetBookListResponse.DayCards> dayCards) {
		List<BudgetBookListResponse.Card> cards = dayCards.stream().flatMap(day -> day.getCardList().stream()).collect(Collectors.toList());

		Map<String, Long> categoryPrice = cards.stream()
				.collect(Collectors.groupingBy(
						card -> {
							String type = card.getCategory().getCode().substring(0,2);

							return type.equals("01") ? "income" : "outlay";
						},

						Collectors.summingLong( BudgetBookListResponse.Card::getPrice )
				));

		long income = categoryPrice.get("income");
		long outlay = categoryPrice.get("outlay");
		long total = income + outlay;

		return BudgetBookListResponse.Stats.builder()
				.total(total).income(income).outlay(outlay).build();
	}


	/**
	 * 카테고리 코드의 하위 카테고리들을 찾는 메서드
	 *
	 * @param code 카테고리 코드
	 * @return 하위 카테고리 리스트
	 */
	public List<CategoryDTO> getCategoriesByCode(String code) {
		if (Objects.isNull(code)) {
			return categoryService.getTopCategories();
		}

		return categoryService.getMySubCategories(code);
	}


	/**
	 * 가계부 번호에 해당하는 가계부 정보를 반환합니다. <br>
	 * 접근한 가계부 작성자를 확인하여 작성자가 아닌 다른 회원이 접근하면 {@link ErrorException} 이 발생합니다.
	 *
	 * @param memberId 회원 식별번호
	 * @param id       가계부 번호
	 * @return 번호에 해당하는 가계부 상세정보
	 */
	public BudgetBookDetailResponse getBudgetBookById(String memberId, Long id, String mode) {
		BudgetBook entity = budgetBookDAO.findBudgetBookById(id);

		//날짜 포맷
		LocalDate date = LocalDate.parse(entity.getBookDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
		String formatDate = date.format(DateTimeFormatter.ofPattern("yyyy. MM. dd (E)", Locale.KOREAN));
		if (mode.equalsIgnoreCase("edit")) {
			formatDate = date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일", Locale.KOREAN));
		}

		//고정주기 변환
		FixDTO fix = Objects.isNull(entity.getFixCycle()) ?
				FixDTO.defaultValue() : FixDTO.builder().option(entity.getFix().toLowerCase()).cycle(entity.getFixCycle().toLowerCase()).build();


		//카테고리 변환
		CategoryDTO category = CategoryDTO.builder()
				.code(entity.getCategory().getCode())
				.name(entity.getCategory().getName())
				.build();

		//위치 변환
		PlaceDTO place = Objects.isNull(entity.getPlaceName()) ?
				PlaceDTO.defaultValue() : PlaceDTO.builder().name(entity.getPlaceName()).roadAddress(entity.getRoadAddress()).address(entity.getAddress()).build();


		//이미지 변환
		List<String> profileImage = imageService.findImageUrl(entity);

		if (memberId.equals(entity.getMember().getId())) {
			return BudgetBookDetailResponse.builder()
					.date( BudgetBookDetailResponse.ReadDate.builder().read(date.toString()).text(formatDate).build() )
					.image(profileImage)
					.fix(fix).category(category).place(place)
					.id(entity.getId()).memo(entity.getMemo()).price(entity.getPrice()).paymentType(entity.getPaymentType().toLowerCase())
					.build();
		} else {
			logger.debug("가계부 정보조회 실패 - 번호: {}, 작성자: {}, 접근자: {}", entity.getId(), entity.getMember().getId(), memberId);
			throw new ErrorException(ErrorCode.BUDGET_AUTHOR_MISMATCH);
		}
	}


	/**
	 * 하위카테고리의 모든 상위 카테고리의 이름을 조회하는 메서드
	 *
	 * @param code 하위카테고리 코드
	 * @return 하위카테고리 포함한 상위 카테고리 이름 리스트
	 */
	public List<CategoryDTO> getCategoryByStep(String code) {
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
	public Map<String, Object> getCategoriesByCode(List<CategoryDTO> categories) {
		Map<String, Object> map = new HashMap<>();

		//상위카테고리
		List<CategoryDTO> topCategory = categoryService.getTopCategories();
		map.put("top", topCategory);

		//중간카테고리
		List<CategoryDTO> middleCategory = categoryService.getMySubCategories(categories.get(0).getCode());
		map.put("middle", middleCategory);

		//하위카테고리
		List<CategoryDTO> bottomCategory = categoryService.getMySubCategories(categories.get(1).getCode());
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
	public void updateBudgetBook(String memberId, Long id, BudgetBookUpdateRequest update) {

		if (Objects.isNull(update)) {
			throw new ErrorException(BUDGET_UPDATE_UNKNOWN);
		}

		//날짜 변환
		LocalDate date = LocalDate.parse(update.getDate(), DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일"));
		String formatDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		//이미지리스트
		List<ImageDTO> imageFiles = update.getImage();

		//DTO → Entity 변환
		BudgetBook entity = BudgetBook.builder()
				.member(Member.builder().id(memberId).build())
				.id(id).bookDate(formatDate)
				.fix(update.getFix().getOption().toUpperCase()).fixCycle(Objects.isNull(update.getFix().getCycle()) ? null : update.getFix().getCycle().toUpperCase())
				.category(Category.builder().code(update.getCategory()).build())
				.memo(update.getMemo())
				.price(update.getPrice()).paymentType(update.getPaymentType().toUpperCase())
				.image1(imageFiles.get(0).getFileName()).image2(imageFiles.get(1).getFileName()).image3(imageFiles.get(2).getFileName())
				.placeName(Objects.isNull(update.getPlace().getName()) ? null : update.getPlace().getName())
				.roadAddress(Objects.isNull(update.getPlace().getRoadAddress()) ? null : update.getPlace().getRoadAddress())
				.address(Objects.isNull(update.getPlace().getAddress()) ? null : update.getPlace().getAddress())
				.build();


		try {
			boolean isUpdate = budgetBookDAO.updateBudgetBook(entity);

			if (!isUpdate) {
				throw new ErrorException(BUDGET_UPDATE_UNKNOWN);
			}

			imageService.changeImage(memberId, entity, imageFiles);

		} catch (IOException e) {
			logger.debug("변경하려는 가계부 이미지 미존재로 저장 실패");
			throw new ErrorException(BUDGET_UPDATE_UNKNOWN);
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
			logger.debug("{} 회원의 {} 가계부 삭제 완료", memberId, id);
		} else {
			throw new ErrorException(ErrorCode.BUDGET_DELETE_UNKNOWN);
		}
	}
}
