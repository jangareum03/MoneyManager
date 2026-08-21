package com.moneymanager.ledger.service.read;

import com.moneymanager.global.domain.vo.DateRange;
import com.moneymanager.global.security.CurrentUser;
import com.moneymanager.ledger.domain.dto.response.*;
import com.moneymanager.ledger.domain.dto.response.category.CategoryEditInfo;
import com.moneymanager.ledger.domain.dto.response.history.HistoryDashboardResponse;
import com.moneymanager.ledger.domain.dto.response.history.LedgerStatistics;
import com.moneymanager.ledger.domain.dto.response.item.CategoryItem;
import com.moneymanager.ledger.domain.dto.response.item.HistoryItem;
import com.moneymanager.ledger.domain.dto.response.item.MenuItem;
import com.moneymanager.ledger.domain.entity.Category;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.CategoryType;
import com.moneymanager.ledger.domain.enums.HistoryMenuType;
import com.moneymanager.ledger.domain.enums.HistoryType;
import com.moneymanager.ledger.domain.query.LedgerHistoryQuery;
import com.moneymanager.ledger.repository.LedgerRepository;
import com.moneymanager.ledger.service.mapper.LedgerMapper;
import com.moneymanager.ledger.service.policy.LedgerHistoryPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.ledger<br>
 * 파일이름       : LedgerReadService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 4<br>
 * 설명              : 가계부 정보를 조회하는 클래스
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
 * 		 	  <td>26. 1. 4</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 1. 9</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 이름] getInitialData → getWriteStep1Data</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerReadService {

	private final CategoryReadService categoryReadService;
	private final LedgerImageReadService imageReadService;

	private final LedgerRepository ledgerRepository;
	private final LedgerHistoryPolicy ledgerHistoryPolicy;
	private final LedgerMapper ledgerMapper;

	private final CurrentUser currentUser;
	private final Clock clock;


	public HistoryDashboardResponse getHistoryDashboard(HistoryType historyType) {
		// 1. 인증된 사용자 조회
		String memberId = currentUser.getMemberId();

		// 2. 기간 생성
		LocalDate today = LocalDate.now(clock);
		DateRange dateRange = ledgerHistoryPolicy.calculateDateRange(historyType, today);

		// 3. 내역 조회
		List<LedgerHistoryQuery> histories = ledgerRepository.findHistoriesByMemberAndDateBetween(memberId, dateRange.getFrom(), dateRange.getTo());

		// 4. 내역 그룹화 (날짜 기준)
		Map<LocalDate, List<HistoryItem>> listGroups = groupByDate(histories);

		// 5. 통계 생성
		LedgerStatistics statistics = calculateStatistics(histories);

		// 6. 제목 생성
		String title = ledgerHistoryPolicy.getTitleByHistoryType(historyType);

		// 7. 검색 메뉴 생성
		List<MenuItem> menu = createMenu();

		// 8. 응답 생성
		return HistoryDashboardResponse.of(title, menu, statistics, listGroups);
	}


	//날짜 기준으로 내림차순 정렬
	private Map<LocalDate, List<HistoryItem>> groupByDate(List<LedgerHistoryQuery> histories) {
		return histories.stream()
				.filter(history -> history.getDate() != null)
				.collect(Collectors.groupingBy(
						LedgerHistoryQuery::getDate,
						() -> new TreeMap<>(Comparator.reverseOrder()),
						Collectors.mapping(
								HistoryItem::from,
								Collectors.toList()
						)
				));
	}

	//내역에서 카테고리별 금액 합계 구하기
	private LedgerStatistics calculateStatistics(List<LedgerHistoryQuery> histories) {
		Map<CategoryType, Long> sumByCategory = histories.stream()
				.filter(h -> h.getCategoryCode() != null)
				.collect(Collectors.groupingBy(
						h -> CategoryType.fromCode(h.getCategoryCode()),
						Collectors.summingLong(LedgerHistoryQuery::getAmount)
				));

		Long income = sumByCategory.getOrDefault(CategoryType.INCOME, 0L);
		Long outlay = sumByCategory.getOrDefault(CategoryType.OUTLAY, 0L);

		return LedgerStatistics.of(income, outlay);
	}

	//내역 메뉴 생성
	private List<MenuItem> createMenu() {
		return List.of(
			new MenuItem("전체", HistoryMenuType.ALL.name()),
			new MenuItem("수입/지출", HistoryMenuType.CATEGORY.name()),
			new MenuItem("카테고리", HistoryMenuType.SUB_CATEGORY.name()),
			new MenuItem("메모", HistoryMenuType.MEMO.name()),
			new MenuItem("기간", HistoryMenuType.DATE.name())
		);
	}


	public LedgerDetailResponse getDetailData(String code) {
		//1. 인증된 사용자 조회
		String memberId = currentUser.getMemberId();

		//2. 가계부 조회
		Ledger ledger = getLedger(memberId, code);

		//3. 카테고리 조회
		Category category = categoryReadService.getCategory(ledger.getCategory());

		//4. 정책에 맞춰 가계부 이미지 리스트 조회
		List<String> images
				= imageReadService.resolveImageSlots(ledger.getId())
				.stream()
				.map(ImageSlot::getFilePath)
				.toList();

		return ledgerMapper.toDetailDto(ledger, category, images);
	}


	public LedgerEditResponse getEditData(String code) {
		//1. 인증된 사용자 조회
		String memberId = currentUser.getMemberId();

		//2. 가계부 조회
		Ledger ledger = getLedger(memberId, code);

		//3. 카테고리 조회
		CategoryEditInfo categoryEditInfo = buildCategoryInfo(ledger);

		//4. 정책에 맞춰 가계부 이미지 리스트 조회
		List<ImageSlot> images = imageReadService.resolveImageSlots(ledger.getId());

		return ledgerMapper.toEditDto(ledger, images, categoryEditInfo);
	}

	private CategoryEditInfo buildCategoryInfo(Ledger ledger) {
		String categoryCode = ledger.getCategory();
		CategoryType type = CategoryType.fromCode(categoryCode);

		return CategoryEditInfo.builder()
				.selected(getSelectedCategories(categoryCode))
				.middleOptions(categoryReadService.getMiddleCategories(type))
				.lowOptions(categoryReadService.getLowCategories(type))
				.build();
	}

	private List<String> getSelectedCategories(String categoryCode) {
		return categoryReadService.findCategoryHierarchy(categoryCode)
				.stream()
				.map(CategoryItem::getCode)
				.skip(1)	//대분류는 수정 화면에서 선택 대상이 아니므로 제외
				.toList();
	}

	public Ledger getLedger(String memberId, String code) {
		return ledgerRepository.findByCode(memberId, code);
	}

}
