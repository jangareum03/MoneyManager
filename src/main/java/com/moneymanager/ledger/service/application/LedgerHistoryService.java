package com.moneymanager.ledger.service.application;

import com.moneymanager.global.domain.enums.DatePatterns;
import com.moneymanager.global.security.CurrentUser;
import com.moneymanager.ledger.domain.dto.response.history.HistoryDashboardResponse;
import com.moneymanager.ledger.domain.dto.response.history.HistoryGroup;
import com.moneymanager.ledger.domain.dto.response.history.LedgerHistoryDisplay;
import com.moneymanager.ledger.domain.dto.response.history.LedgerStatistics;
import com.moneymanager.ledger.domain.dto.response.item.HistoryItem;
import com.moneymanager.ledger.domain.dto.vo.LedgerPeriod;
import com.moneymanager.ledger.domain.enums.HistoryType;
import com.moneymanager.ledger.domain.enums.LedgerType;
import com.moneymanager.ledger.domain.query.LedgerHistoryQuery;
import com.moneymanager.ledger.service.policy.LedgerPolicy;
import com.moneymanager.ledger.service.read.LedgerReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private final CurrentUser currentUser;
    private final LedgerPolicy ledgerPolicy;

    private final LedgerReadService ledgerReadService;


    public HistoryDashboardResponse searchLedgersByDate(String type) {
        //1. 인증된 회원 조회
        String memberId = currentUser.getMemberId();

        //2, 내역 유형 조회 (기본값 세팅)
        HistoryType historyType = parseHistoryTypeOrDefault(type);

        //3. 내역 유형별로 시작일과 종료일 기간 계산
        LedgerPeriod period = ledgerPolicy.resolveHistoryPeriod(historyType);

        //4. 기간 별 회원이 작성한 가계부 내역 조회
        List<LedgerHistoryQuery> historyQueries = ledgerReadService.findLedgerByDate(memberId, period.getFromDate(), period.getToDate());

        //5. 날짜별로 3개로 나열하여 그룹화
        List<LedgerHistoryDisplay> chunkHistory = chunkLedgersByRow(historyQueries, HISTORY_MAX_ROW);

        //6. 기간 내 조회된 가계부 정보를 응답 객체로 구성
        return HistoryDashboardResponse.of(
                ledgerPolicy.getTitleByHistoryType(historyType),
                calculateTotalAmount(historyQueries),
                chunkHistory
        );
    }


    //===== searchLedgersByDateAndConditions 보조 메서드 =====
    private HistoryType parseHistoryTypeOrDefault(String type) {
        try {
            return HistoryType.from(type);
        } catch (NoSuchElementException e) {
            return HistoryType.MONTH;
        }
    }

    private List<LedgerHistoryDisplay> chunkLedgersByRow(List<LedgerHistoryQuery> historyQueries, int size) {
        if(historyQueries.isEmpty()) {
            return Collections.emptyList();
        }

        List<HistoryGroup> groups = groupByDate(historyQueries);

        return toGridRows(groups, size);
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

}