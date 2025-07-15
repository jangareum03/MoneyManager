package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.main.BudgetBookDao;
import com.areum.moneymanager.dto.request.main.BudgetBookRequestDTO;
import com.areum.moneymanager.dto.response.main.BudgetBookResponseDTO;
import com.areum.moneymanager.dto.response.main.CategoryResponseDTO;
import com.areum.moneymanager.entity.BudgetBook;
import com.areum.moneymanager.entity.Category;
import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.enums.ErrorCode;
import com.areum.moneymanager.enums.type.BudgetBookType;
import com.areum.moneymanager.exception.ErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static com.areum.moneymanager.enums.ErrorCode.BUDGET_UPDATE_UNKNOWN;


/**
 * 가계부와 관련된 작업을 처리하는 클래스</br>
 * 가계부 등록, 조회, 카테고리 조회, 삭제 등의 메서드를 구현
 *
 * @version 1.0
 */
@Service
public class BudgetBookService {

	private final CategoryService categoryService;
	private final ImageServiceImpl imageService;
	private final BudgetBookDao budgetBookDAO;

	private final Logger logger = LogManager.getLogger(this);

	public BudgetBookService( CategoryService categoryService, @Qualifier("budgetImage") ImageServiceImpl imageService, BudgetBookDao budgetBookDAO ) {
		this.categoryService = categoryService;
		this.imageService = imageService;
		this.budgetBookDAO = budgetBookDAO;
	}



	/**
	 * 가계부 작성에 필요한 기본 정보를 반환합니다.<p>
	 * 기본정보에는 제목, 카테고리 리스트, 등록 가능한 이미지 개수가 포함됩니다.<br>
	 * 제목은 가계부 날짜를 'yyyy년 mm월 dd일 e요일' 형식으로 저장됩니다.
	 *
	 * @param memberId		회원 고유번호
	 * @param set			가계부 날짜를 저장한 객체
	 * @return	작성에 필요한 기본 정보
	 */
	public BudgetBookResponseDTO.Write getWriteByData( String memberId, BudgetBookRequestDTO.Setting set ) throws ParseException {
		String title = new SimpleDateFormat("yyyy년 MM월 dd일 E요일").format( new SimpleDateFormat("yyyyMMdd").parse(set.getDate()) );
		int availableCount = imageService.getLimitImageCount(memberId);		//등록 가능한 이미지 개수

		//카테고리 리스트
		List<CategoryResponseDTO.Read> topCategory = categoryService.getTopCategories();

		String code;
		if( set.getType().equalsIgnoreCase(BudgetBookType.INCOME.getType()) ) {
			code = topCategory.get(0).getCode();
		}else {
			code = topCategory.get(1).getCode();
		}


		return BudgetBookResponseDTO.Write.builder().date(title).type(set.getType()).maxImage(availableCount).categories( categoryService.getMySubCategories(code) ).build();
	}



	/**
	 * 가계부를 등록합니다.<br>
	 * 등록할 가계부 정보거 없거나 가계부 사진이 등록되지 않은 경우에는 {@link ErrorException}이 발생합니다.
	 *
	 * @param memberId		회원번호
	 * @param create				가계부 정보
	 */
	@Transactional
	public void createBudgetBook( String memberId, BudgetBookRequestDTO.Create create ) {

		if( Objects.isNull(create) ) {
			throw new ErrorException(ErrorCode.BUDGET_REGISTER_UNKNOWN);
		}

		//날짜 변환
		LocalDate date = LocalDate.parse( create.getDate(), DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일") );
		String formatDate = date.format( DateTimeFormatter.ofPattern("yyyyMMdd") );


		//이미지를 담은 DTO 리스트
		List<BudgetBookRequestDTO.FileMeta> imageFiles
				= imageService.getImageList( memberId,
				Objects.nonNull(create.getImage()) ? create.getImage() : new ArrayList<>() );


		//DTO → Entity 변환
		BudgetBook entity = BudgetBook.builder()
						.member(Member.builder().id(memberId).build())
						.category(Category.builder().code(create.getCategory()).build())
						.bookDate(formatDate).memo(create.getMemo())
						.price(create.getPrice()).paymentType(create.getPaymentType().toUpperCase())
						.fix(create.getFix().getOption().toUpperCase()).fixCycle( create.getFix().getOption().equalsIgnoreCase("y") ? create.getFix().getCycle().toUpperCase() : null )
						.image1( imageFiles.get(0).getImageName() ).image2( imageFiles.get(1).getImageName() ).image3( imageFiles.get(2).getImageName() )
						.placeName( (Objects.isNull(create.getPlace())) ? null : create.getPlace().getName() )
						.roadAddress( (Objects.isNull(create.getPlace())) ? null : create.getPlace().getRoadAddress() )
						.address( Objects.isNull(create.getPlace()) ? null : create.getPlace().getAddress() )
						.build();


		try{
			//가계부 등록
			BudgetBook budgetBook = budgetBookDAO.saveBudgetBook( entity );

			for( int i=0; i<imageFiles.size(); i++ ) {
				if( Objects.nonNull(imageFiles.get(i).getFile()) ) {
					imageService.saveImage( budgetBook, imageFiles.get(i).getFile(), i );
				}
			}

		}catch ( NullPointerException | IOException e) {
			throw new ErrorException(ErrorCode.BUDGET_REGISTER_UNKNOWN);
		}
	}



	/**
	 * 입력한 날짜 값에 따른 제목을 반환하는 메서드
	 *
	 * @param range	 				검색할 가계부 날짜 값
	 * @return	입력한 날짜 형식에 따른 제목
	 */
	public String makeTitleByType( String type, BudgetBookRequestDTO.ChartJson range  ) {
		StringBuilder title = new StringBuilder();
		LocalDate today = LocalDate.now();

		switch ( type.toLowerCase() ) {
			case "week" :
				title.insert(0, range.getWeek() +"주");
			case "month":
				title.insert(0, range.getMonth() +"월 ");
			case "year":
				title.insert(0, range.getYear() +"년 ");
		}

		return title.toString().trim();
	}



	/**
	 * 가계부 조회 날짜에 포함되는 가계부 정보와 가격정보를 찾는 메서드
	 *
	 * @param memberId		회원 식별번호
	 * @param search				가계부 조회 정보 객체
	 * @return	가계부 정보와 가격
	 */
	public BudgetBookResponseDTO.Preview getBudgetBooksForSummary(String memberId, BudgetBookRequestDTO.Search search ) {
		Map<String , List<BudgetBookResponseDTO.Summary>> budgetBookDetails = getBudgetBooks( memberId, search );

		return BudgetBookResponseDTO.Preview.builder()
				.price( getPriceByCategory(budgetBookDetails) )
				.info( budgetBookDetails )
				.build();
	}



	/**
	 * 가계부 조회 날짜에 포함되는 가계부 리스트를 찾는 메서드
	 *
	 * @param memberId					회원 식별번호
	 * @param search			가계부 검색 조건
	 * @return	가계부 리스트
	 */
	public Map<String, List<BudgetBookResponseDTO.Summary>> getBudgetBooks( String memberId, BudgetBookRequestDTO.Search search )  {
		Map<String, List<BudgetBookResponseDTO.Summary>> resultMap = new HashMap<>();

		for(Map.Entry<String, List<BudgetBookResponseDTO.Summary>> entry : budgetBookDAO.findBudgetBooksBySearch( memberId, getDateForSearch( search.getRange()), search ).entrySet() ) {
			String key = entry.getKey();
			List<BudgetBookResponseDTO.Summary> value = entry.getValue();


			String newKey = LocalDate.parse(key, DateTimeFormatter.ofPattern("yyyyMMdd")).format( DateTimeFormatter.ofPattern("yyyy. MM. dd (E)", Locale.KOREAN));

			resultMap.put(newKey, value);
		}

		return resultMap;
	}




	/**
	 * 가계부 조회 날짜 범위로 시작날짜와 종료날짜를 찾는 메서드
	 *
	 * @param range		기계부 유형
	 * @return	검색 시작날짜와 종료날짜
	 */
	private LocalDate[] getDateForSearch( BudgetBookRequestDTO.ChartJson range ) {
		LocalDate from, to;

		//검색할 가계부의 날짜 범위의 타입 조회
		if( range.getType().equalsIgnoreCase("week") ) {
			int year = Integer.parseInt(range.getYear());
			int month = Integer.parseInt(range.getMonth());
			int week = Integer.parseInt(range.getWeek());


			/*
			 * [ 첫째주 시작일과 종료일을 알기위한 설정 ]
			 * 	- startIndex 	: 1일 시작요일 인덱스( 1:월요일 ~ 7: 일요일)
			 * 	- addDay				: 첫째 주의 마지막 날짜를 구하기 위한 추가 일수
			 *   - firstDay 			: 월의 첫 시작일로 설정된 LocalDate
			 */
			LocalDate firstDay = LocalDate.of( year, month, 1 );
			int startIndex = firstDay.get(ChronoField.DAY_OF_WEEK);
			int addDay = (startIndex == 7) ? startIndex - 1 : 6 - startIndex;

			//시작일과 종료일 초기화
			from = firstDay;
			to = firstDay.plusDays(addDay);

			//주에 맞게 날짜 설정
			if( week != 1 ) {
				to = to.plusDays(7L * (week-1) );
				from = to.minusDays( 6);

				if( to.getMonthValue() != month ) {	//마지막일자가 다음달로 넘어간 경우
					to = LocalDate.of( year, month, firstDay.lengthOfMonth() );
				}
			}


		}else if( range.getType().equalsIgnoreCase("year") ) {
			int year = Integer.parseInt(range.getYear());

			from = LocalDate.of( year, 1, 1 );
			to = LocalDate.of( year, 12, 31 );
		}else {
			int year = Integer.parseInt(range.getYear());
			int month = Integer.parseInt(range.getMonth());

			from = LocalDate.of( year, month, 1 );
			to = LocalDate.of( year, month, from.lengthOfMonth() );
		}

		return new LocalDate[] { from, to };
	}




	/**
	 * 가계부 타입에 따른 차트 본문을 반환하는 메서드
	 *
	 * @param memberId				회원 식별번호
	 * @param chartJson				검색할 가계부 날짜 값
	 * @return	가계부 차트의 본문을 담은 리스트
	 */
	public List<BudgetBookResponseDTO.Chart> getBudgetBookForChart( String memberId, BudgetBookRequestDTO.ChartJson chartJson ) {
		List<LocalDate[]> dateRange = new ArrayList<>();

		String type = chartJson.getType().toLowerCase();

		switch ( type ) {
			case "year" :
				//해당 년의 월 구하기
				for( int i=1; i<=12; i++ ) {
					LocalDate from = LocalDate.of( Integer.parseInt(chartJson.getYear()), i, 1);
					LocalDate to = LocalDate.of( Integer.parseInt(chartJson.getYear()), i, from.lengthOfMonth() );

					dateRange.add( new LocalDate[] {from, to} );
				}

				return new ArrayList<> (budgetBookDAO.findSumPriceByYear(memberId, dateRange));
			case "week" :
				//해당 월의 총 주차 구하기
				LocalDate localDate = LocalDate.of( Integer.parseInt(chartJson.getYear()), Integer.parseInt(chartJson.getMonth()), 1 );
				int weekCount = (int)Math.ceil( (localDate.get(ChronoField.DAY_OF_WEEK)+ localDate.lengthOfMonth() ) / 7.0 );

				//주의 시작일과 종료일을 담은 리스트
				for( int i=1; i<=weekCount; i++ ) {
					dateRange.add( getDateForSearch(  new BudgetBookRequestDTO.ChartJson( type, chartJson.getYear(), chartJson.getMonth(), String.valueOf(i) )) );
				}

				return new ArrayList<>( budgetBookDAO.findSumPriceByWeek( memberId, dateRange ) );
			case "month":
			default:
				LocalDate from = LocalDate.of( Integer.parseInt(chartJson.getYear()), Integer.parseInt(chartJson.getMonth()), 1 );
				LocalDate to = LocalDate.of( Integer.parseInt(chartJson.getYear()), Integer.parseInt(chartJson.getMonth()), from.lengthOfMonth() );

				return new ArrayList<>(budgetBookDAO.findSumPriceByCategoryAndMonth( memberId, new LocalDate[] {from, to}) );
		}
	}



	/**
	 * 가계부 조회 날짜에 포함되는 카테고리별로 가격을 찾는 메서드
	 *
	 * @param detailMap		조회날짜에 포함되는 가계부 맵
	 * @return	카테고리별로 가격을 담은 맵
	 */
	private Map<String, Long> getPriceByCategory( Map<String, List<BudgetBookResponseDTO.Summary>> detailMap ) {
		Map<String, Long> categoryPrice = detailMap.values().stream()
				.flatMap(List::stream)
				.collect(Collectors.groupingBy(
						book -> {
							String type = book.getCode().substring(0, 2);

							if( type.equalsIgnoreCase("01") ) return "income";
							else return "outlay";
						},
						Collectors.summingLong(BudgetBookResponseDTO.Summary::getPrice)
				));

		long total = categoryPrice.getOrDefault("income", 0L) + categoryPrice.getOrDefault("outlay", 0L);
		categoryPrice.put("total", total);

		return categoryPrice;
	}



	/**
	 * 카테고리 코드의 하위 카테고리들을 찾는 메서드
	 *
	 * @param code	카테고리 코드
	 * @return	하위 카테고리 리스트
	 */
	public List<CategoryResponseDTO.Read> getCategoriesByCode( String code ) {
		if( Objects.isNull(code) ) {
			return categoryService.getTopCategories();
		}

		return categoryService.getMySubCategories(code);
	}



	/**
	 * 가계부 번호에 해당하는 가계부 정보를 반환합니다. <br>
	 * 접근한 가계부 작성자를 확인하여 작성자가 아닌 다른 회원이 접근하면 {@link ErrorException} 이 발생합니다.
	 *
	 * @param memberId		회원 식별번호
	 * @param id								가계부 번호
	 * @return	번호에 해당하는 가계부 상세정보
	 */
	public BudgetBookResponseDTO.Detail getBudgetBookById( String memberId, Long id, String mode ) {
		BudgetBook entity = budgetBookDAO.findBudgetBookById(id);

		//날짜 포맷
		LocalDate date = LocalDate.parse(entity.getBookDate(), DateTimeFormatter.ofPattern("yyyyMMdd") );
		String formatDate = date.format(DateTimeFormatter.ofPattern("yyyy. MM. dd (E)", Locale.KOREAN));
		if( mode.equalsIgnoreCase("edit") ) {
			formatDate = date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일",  Locale.KOREAN));
		}

		//카테고리 변환
		CategoryResponseDTO.Read category = CategoryResponseDTO.Read.builder()
				.code(entity.getCategory().getCode())
				.name(entity.getCategory().getName())
				.build();

		//위치 변환
		BudgetBookResponseDTO.Place place = Objects.isNull(entity.getPlaceName()) ?
				null : BudgetBookResponseDTO.Place.builder().name(entity.getPlaceName()).roadAddress(entity.getRoadAddress()).address(entity.getAddress()).build();


		//이미지 변환
		List<String> filesName = imageService.findImageUrl( entity );

		if( memberId.equals( entity.getMember().getId() ) ) {
			return BudgetBookResponseDTO.Detail.builder()
											.id(entity.getId())
											.fix(entity.getFix().toLowerCase()).fixCycle( Objects.isNull(entity.getFixCycle()) ? null : entity.getFixCycle().toLowerCase() )
											.category( category )
											.date( BudgetBookResponseDTO.FormatDate.builder().text(formatDate).read(date.toString()).build() ).memo(entity.getMemo())
											.price(entity.getPrice()).paymentType(entity.getPaymentType().toLowerCase())
											.images( filesName )
											.place(place)
											.build();
		}else {
			logger.debug("가계부 정보조회 실패 - 번호: {}, 작성자: {}, 접근자: {}", entity.getId(), entity.getMember().getId(), memberId);
			throw new ErrorException(ErrorCode.BUDGET_AUTHOR_MISMATCH);
		}
	}



	/**
	 * 하위카테고리의 모든 상위 카테고리의 이름을 조회하는 메서드
	 *
	 * @param code	하위카테고리 코드
	 * @return	하위카테고리 포함한 상위 카테고리 이름 리스트
	 */
	public List<CategoryResponseDTO.Read> getCategoryByStep( String code ) {
		return categoryService.getMyParentCategories(code);
	}



	/**
	 *	카테고리 타입에 따라 해당 타입의 모든 하위 카테고리를 반환합니다.<br>
	 * <li>in : 모든 수입 카테고리</li>
	 * <li>out : 모든 지출 카테고리</li>
	 *
	 * @param categories			가계부 카테고리 정보
	 * @return	유형별 모든 하위 카테고리
	 */
	public Map<String, Object> getCategoriesByCode( List<CategoryResponseDTO.Read> categories ){
		Map<String, Object> map = new HashMap<>();

		//상위카테고리
		List<CategoryResponseDTO.Read> topCategory = categoryService.getTopCategories();
		map.put("top", topCategory);

		//중간카테고리
		List<CategoryResponseDTO.Read> middleCategory = categoryService.getMySubCategories( categories.get(0).getCode() );
		map.put( "middle", middleCategory );

		//하위카테고리
		List<CategoryResponseDTO.Read> bottomCategory = categoryService.getMySubCategories( categories.get(1).getCode() );
		map.put( "bottom", bottomCategory );


		return map;
	}



	/**
	 * 가계부를 수정하는 메서드
	 *
	 * @param memberId	회원 식별번호
	 * @param update			수정할 가계부 정보
	 */
	@Transactional
	public void updateBudgetBook( String memberId, Long id, BudgetBookRequestDTO.Update update ) {

		if( Objects.isNull(update) ) {
			throw new ErrorException(BUDGET_UPDATE_UNKNOWN);
		}

		//이미지를 담은 DTO 리스트
		List<BudgetBookRequestDTO.FileMeta> imageFiles
				= imageService.getImageList( memberId, Objects.nonNull(update.getImage()) ? update.getImage() : new ArrayList<>() );

		//날짜 변환
		LocalDate date = LocalDate.parse( update.getDate(), DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일") );
		String formatDate = date.format( DateTimeFormatter.ofPattern("yyyyMMdd") );

		//DTO → Entity 변환
		BudgetBook entity = BudgetBook.builder()
						.member(Member.builder().id(memberId).build())
						.id(id).bookDate( formatDate )
						.fix(update.getFix().getOption().toUpperCase()).fixCycle( Objects.isNull(update.getFix().getCycle()) ? null : update.getFix().getCycle().toUpperCase())
						.category(Category.builder().code(update.getCategory()).build())
						.memo(update.getMemo())
						.price(update.getPrice()).paymentType(update.getPaymentType().toUpperCase())
						.image1(imageFiles.get(0).getImageName()).image2(imageFiles.get(1).getImageName()).image3(imageFiles.get(2).getImageName())
						.placeName(Objects.isNull(update.getMap().getName()) ? null : update.getMap().getName())
						.roadAddress(Objects.isNull(update.getMap().getRoadAddress()) ? null : update.getMap().getRoadAddress())
						.address(Objects.isNull(update.getMap().getAddress()) ? null : update.getMap().getAddress())
						.build();



		try{
			boolean isUpdate = budgetBookDAO.updateBudgetBook(entity);

			if( !isUpdate ) {
				throw new ErrorException(BUDGET_UPDATE_UNKNOWN);
			}

			imageService.changeImage( memberId, entity,  imageFiles );

		}catch ( IOException e ) {
			logger.debug("변경하려는 가계부 이미지 미존재로 저장 실패");
			throw new ErrorException(BUDGET_UPDATE_UNKNOWN);
		}
	}



	/**
	 * 번호에 해당하는 가계부를 삭제하는 메서드
	 *
	 * @param memberId	회원 식별번호
	 * @param id				삭제할 가계부 번호
	 */
	public void deleteBudgetBook( String memberId, List<Long> id ) {
		if( budgetBookDAO.deleteBudgetBookById( memberId, id ) ) {
			logger.debug("{} 회원의 {} 가계부 삭제 완료", memberId, id);
		}else{
			throw new ErrorException(ErrorCode.BUDGET_DELETE_UNKNOWN);
		}
	}
}
