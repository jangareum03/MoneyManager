package com.moneymanager.ledger.controller;

import com.moneymanager.global.domain.dto.response.api.ApiBody;
import com.moneymanager.global.exception.annotation.ApiController;
import com.moneymanager.global.operation.annotation.Operation;
import com.moneymanager.global.operation.enums.ServiceAction;
import com.moneymanager.ledger.domain.dto.request.LedgerSearchRequest;
import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.dto.response.history.LedgerHistoryDisplay;
import com.moneymanager.ledger.domain.dto.response.item.CategoryItem;
import com.moneymanager.ledger.service.application.LedgerHistoryService;
import com.moneymanager.ledger.service.application.LedgerService;
import com.moneymanager.ledger.service.read.CategoryReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.controller.web.ledger<br>
 * 파일이름       : LedgerApiController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 5. 11<br>
 * 설명              : 가계부 관련 데이터 요청을 처리하는 클래스
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
 * 		 	  <td>26. 5. 11</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@RestController
@ApiController
@RequiredArgsConstructor
@RequestMapping("/api/ledgers")
public class LedgerApiController {

    private final LedgerService ledgerService;
    private final LedgerHistoryService ledgerHistoryService;
    private final CategoryReadService categoryReadService;

    @GetMapping
    public ApiBody<List<LedgerHistoryDisplay>> getLedgerHistory(LedgerSearchRequest request) {
        List<LedgerHistoryDisplay> history = ledgerHistoryService.searchLedgersByCondition(request);

        return ApiBody.data("", history);
    }

    @GetMapping("/category/{code}/children")
    @Operation(ServiceAction.LEDGER_CATEGORY)
    public List<CategoryItem> getCategories(@PathVariable String code) {
        return categoryReadService.getChildrenByParentCode(code).stream()
                .map(CategoryItem::from)
                .toList();
    }

    @GetMapping("/dates")
    @Operation(ServiceAction.LEDGER_REGISTER_DATE)
    public List<Integer> getDateList(@RequestParam String unit, @RequestParam String date) {
        return ledgerService.fetchDateOptionsByUnit(unit, date);
    }

    @PutMapping("/{code}")
    public ApiBody<Void> updateLedger(@PathVariable String code, @RequestPart("ledger") LedgerUpdateRequest request, @RequestPart(value = "images", required = false) List<MultipartFile> files) {
        request.attachImages(files);

        ledgerService.processLedgerUpdate(code, request);

        return ApiBody.next(
                "가계부 수정 완료했습니다.",
                "/ledgers/" + code
        );
    }

    @DeleteMapping
    @Operation(ServiceAction.LEDGER_DELETE)
    public ApiBody<Void> deleteLedger(@RequestBody List<String> codes) {
        int delCount = ledgerService.processLedgerDelete(codes);

        if (delCount == 0) {
            return ApiBody.message("삭제된 내역이 없습니다.");
        }

        return ApiBody.message(delCount + "건의 내역이 삭제되었습니다.");
    }

}