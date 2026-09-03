package com.moneymanager.ledger.service.application;

import com.moneymanager.global.domain.enums.DatePatterns;
import com.moneymanager.global.exception.code.ErrorCode;
import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.security.CurrentUser;
import com.moneymanager.global.util.date.DateTimeUtil;
import com.moneymanager.ledger.domain.dto.request.LedgerSearchRequest;
import com.moneymanager.ledger.domain.dto.response.history.*;
import com.moneymanager.ledger.domain.dto.response.item.*;
import com.moneymanager.ledger.domain.dto.vo.LedgerPeriod;
import com.moneymanager.ledger.domain.entity.Category;
import com.moneymanager.ledger.domain.enums.HistoryMenu;
import com.moneymanager.ledger.domain.enums.HistoryType;
import com.moneymanager.ledger.domain.enums.LedgerType;
import com.moneymanager.ledger.domain.query.LedgerHistoryQuery;
import com.moneymanager.ledger.service.policy.LedgerPolicy;
import com.moneymanager.ledger.service.read.CategoryReadService;
import com.moneymanager.ledger.service.read.LedgerReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.application<br>
 * 파일이름       : LedgerHistoryService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 28<br>
 * 설명              : 가계부 내역 로직 흐름을 관리하는 클래스
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
 * 		 	  <td>26. 8. 28</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class LedgerHistoryService {

    private final static int HISTORY_MAX_ROW = 3;

    private final Clock clock;
    private final CurrentUser currentUser;
    private final LedgerPolicy ledgerPolicy;

    private final LedgerReadService ledgerReadService;
    private final CategoryReadService categoryReadService;


    public HistoryDashboardResponse findHistories(String type, Integer year, Integer month, Integer week) {
        //1. 인증된 회원 조회
        String memberId = currentUser.getMemberId();

        //2. 유형값 설정
        HistoryType historyType = parseHistoryTypeOrThrows(type);
        HistoryDateFilter dateFilter = new HistoryDateFilter(year, month, week);

        if(dateFilter.hasDate()) {
            ledgerPolicy.validateHistoryDate(historyType, dateFilter);
        }

        //3. 내역 유형별로 시작일과 종료일 기간 계산
        LocalDate date = dateFilter.hasDate() ? getLocalDate(dateFilter) :  LocalDate.now(clock);
        LedgerPeriod period = ledgerPolicy.resolveHistoryPeriod(historyType, date);

        //4. 기간 별 회원이 작성한 가계부 내역 조회
        List<LedgerHistoryQuery> historyQueries = ledgerReadService.findLedgerByDate(memberId, period.getFromDate(), period.getToDate());

        //5. 날짜별로 3개로 나열하여 그룹화
        List<LedgerHistoryDisplay> chunkHistory = chunkLedgersByRow(historyQueries, HISTORY_MAX_ROW);

        //6. 차트 데이터 조회
        List<ChartBarItem> chart = fetchChartDataByType(memberId, historyType, date);

        //7. 기간 내 조회된 가계부 정보를 응답 객체로 구성
        return HistoryDashboardResponse.of(
                ledgerPolicy.getTitleByHistoryType(historyType, date),
                calculateTotalAmount(historyQueries),
                chunkHistory,
                chart
        );
    }

    public List<LedgerHistoryDisplay> searchLedgersByCondition(LedgerSearchRequest request) {
        //1. 인증된 회원 조회
        String memberId = currentUser.getMemberId();

        //2, 내역 유형 및 메뉴 조회 (기본값 세팅)
        HistoryType historyType = parseHistoryTypeOrThrows(request.getType());
        HistoryMenu historyMenu = parseHistoryMenuDefault(request.getMenu());

        //3. 메뉴별 조건 검증
        ledgerPolicy.validateSearchCondition(historyMenu, request);

        //4. 내역 유형 및 메뉴로 시작일과 종료일 기간 계산
        LedgerPeriod period = ledgerPolicy.resolveHistoryPeriod(historyType, LocalDate.now(clock));
        if (historyMenu.equals(HistoryMenu.PERIOD)) {
            LocalDate fromDate = DateTimeUtil.parseDateOrToday(request.getFromDate());
            LocalDate toDate = DateTimeUtil.parseDateOrToday(request.getToDate());

            period = LedgerPeriod.of(fromDate, toDate);
        }

        LedgerSearchCondition searchCondition = getSearchCondition(historyMenu, request);

        //5. 기간 별 회원이 작성한 가계부 내역 조회
        List<LedgerHistoryQuery> historyQueries = ledgerReadService.findLedgerByCondiction(memberId, period.getFromDate(), period.getToDate(), searchCondition);

        return chunkLedgersByRow(historyQueries, HISTORY_MAX_ROW);
    }

    public MenuResponse buildSubMenu(String type) {
        HistoryType historyType = parseHistoryTypeOrThrows(type);

        return switch (historyType) {
            case YEAR -> MenuResponse.from(
                    Arrays.stream(HistoryMenu.values())
                            .map(this::createMenuByType)
                            .toList()
            );
            case MONTH, WEEK -> MenuResponse.from(
                    Arrays.stream(HistoryMenu.values())
                            .filter(m -> m != HistoryMenu.PERIOD)
                            .map(this::createMenuByType)
                            .toList()
            );
        };
    }


    //===== searchLedgersByDateAndConditions 보조 메서드 =====
    private HistoryMenu parseHistoryMenuDefault(String menu) {
        try {
            return HistoryMenu.from(menu);
        } catch (NoSuchElementException e) {
            return HistoryMenu.ALL;
        }
    }

    private LedgerSearchCondition getSearchCondition(HistoryMenu menu, LedgerSearchRequest request) {
        return switch (menu) {
            case ALL, PERIOD -> LedgerSearchCondition.of(menu);
            case CATEGORY -> LedgerSearchCondition.ofKeyword(menu, request.getCategories().get(0));
            case SUB_CATEGORY-> LedgerSearchCondition.ofKeywords(menu, request.getCategories());
            case MEMO ->  LedgerSearchCondition.ofKeyword(menu, request.getMemo());
        };
    }


    //===== findHistories 보조 메서드 =====
    private LocalDate getLocalDate(HistoryDateFilter dateFilter) {
        int month =  dateFilter.getMonth() == null ? 1 : dateFilter.getMonth();
        int week =  dateFilter.getWeek() == null ? 1 : dateFilter.getWeek();

        int day = 1 + 7 * (week - 1);
        int lastDay = LocalDate.of(dateFilter.getYear(), month, 1).lengthOfMonth();
        if(day > lastDay) {
            day = lastDay;
        }

        return LocalDate.of(dateFilter.getYear(), month, day);
    }

    private List<HistoryGroup> groupByDate(List<LedgerHistoryQuery> historyQueries) {
        return historyQueries.stream()
                .filter(query -> query.getDate() != null)
                .collect(Collectors.groupingBy(
                        LedgerHistoryQuery::getDate,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                HistoryItem::from,
                                Collectors.toList()
                        )
                ))
                .entrySet()
                .stream()
                .map(entry ->
                        HistoryGroup.of(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();
    }

    private List<LedgerHistoryDisplay> toGridRows(List<HistoryGroup> historyGroups, int size) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePatterns.DATE_DOT_WITH_DAY.getPattern());

        return historyGroups.stream()
                .map(group -> {
                    String date = group.getDate().format(formatter);

                    List<HistoryItem> items = group.getItems();

                    List<List<HistoryItem>> rows =
                            IntStream.range(0, (items.size() + (size - 1)) / size)
                                    .mapToObj(index -> {
                                        return items.subList(index * size, Math.min(index * size + size, items.size()));
                                    })
                                    .toList();

                    return LedgerHistoryDisplay.of(date, rows);
                })
                .toList();
    }

    private LedgerStatistics calculateTotalAmount(List<LedgerHistoryQuery> queries) {
        Map<LedgerType, Long> amounts = queries.stream()
                .filter(query -> query.getCategoryCode() != null)
                .collect(Collectors.groupingBy(
                        q -> LedgerType.fromCode(q.getCategoryCode()),
                        Collectors.summingLong(LedgerHistoryQuery::getAmount)
                ));

        long income = amounts.getOrDefault(LedgerType.INCOME, 0L);
        long outlay = amounts.getOrDefault(LedgerType.OUTLAY, 0L);

        return LedgerStatistics.of(income, outlay);
    }

    private List<ChartBarItem> fetchChartDataByType(String memberId, HistoryType type, LocalDate date) {
        //1. 내역 유형별로 시작일과 종료일 기간 계산
        LedgerPeriod period = ledgerPolicy.resolveChartPeriod(type, date);

        //2. 유형별 조회된 통계 정보를 응답 객체로 구성
        return ledgerReadService.generateChartDataByType(memberId, type, period.getFromDate(), period.getToDate());
    }


    //===== buildSubMenu 보조 메서드 =====
    private MenuItem createMenuByType(HistoryMenu menu) {
        HistoryMenu selected = HistoryMenu.ALL;

        return MenuItem.from(
                menu,
                switch (menu) {
                    case ALL, MEMO, PERIOD -> SubMenuItem.of();
                    case CATEGORY -> createCategorySubMenu();
                    case SUB_CATEGORY -> createSubCategorySubMenu();
                },
                selected
        );
    }

    private SubMenuItem createCategorySubMenu() {
        Map<String, List<CategoryItem>> subItems = categoryReadService.getRootCategories()
                .stream()
                .collect(Collectors.toMap(
                        Category::getName,
                        category -> List.of(CategoryItem.from(category))
                ));

        return SubMenuItem.of(subItems);
    }

    private SubMenuItem createSubCategorySubMenu() {
        List<Category> income = categoryReadService.getMiddleCategories(LedgerType.INCOME);
        List<Category> outlay = categoryReadService.getMiddleCategories(LedgerType.OUTLAY);

        Map<String, List<CategoryItem>> subItems =
                Stream.concat(income.stream(), outlay.stream())
                        .collect(Collectors.toMap(
                                Category::getName,
                                category ->
                                        categoryReadService.getChildrenByParentCode(category.getCode())
                                                .stream()
                                                .map(CategoryItem::from)
                                                .toList()
                        ));


        return SubMenuItem.of(subItems);
    }


    //===== fetchChartDataByType 보조 메서드 =====


    //===== 유틸 메서드 =====
    private HistoryType parseHistoryTypeOrThrows(String type) {
        try {
            return HistoryType.from(type);
        } catch (NoSuchElementException e) {
           throw new ApplicationException(
                   ErrorCode.POLICY_VIOLATION,
                   LogContent.of(
                           "HistoryType 변환",
                           HistoryType.class,
                           type
                   ).withCause("지원하지 않은 HistoryType")
           );
        }
    }

    private List<LedgerHistoryDisplay> chunkLedgersByRow(List<LedgerHistoryQuery> historyQueries, int size) {
        if (historyQueries.isEmpty()) {
            return Collections.emptyList();
        }

        List<HistoryGroup> groups = groupByDate(historyQueries);

        return toGridRows(groups, size);
    }

}