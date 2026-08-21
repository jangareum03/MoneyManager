package com.moneymanager.ledger.service.application;

import com.moneymanager.global.domain.enums.DatePatterns;
import com.moneymanager.global.security.CurrentUser;
import com.moneymanager.global.util.date.DateRangeUtils;
import com.moneymanager.global.util.date.DateTimeUtil;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.response.ImageSlot;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep1Response;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep2Response;
import com.moneymanager.ledger.domain.dto.response.item.CategoryItem;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.CategoryType;
import com.moneymanager.ledger.domain.enums.DateUnit;
import com.moneymanager.ledger.service.command.LedgerCommandService;
import com.moneymanager.ledger.service.policy.LedgerPolicy;
import com.moneymanager.ledger.service.read.CategoryReadService;
import com.moneymanager.ledger.service.validation.LedgerRegisterValidator;
import com.moneymanager.member.service.read.MemberReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.application<br>
 * 파일이름       : LedgerService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 13<br>
 * 설명              : 가계부 로직 흐름을 관리하는 클래스
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
 * 		 	  <td>26. 8. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class LedgerService {

	private final CurrentUser currentUser;

    private final LedgerImageService imageService;
    private final CategoryReadService categoryReadService;
    private final MemberReadService memberReadService;
    private final LedgerCommandService ledgerCommandService;

    private final LedgerRegisterValidator registerValidator;
    private final LedgerPolicy ledgerPolicy;


    public LedgerWriteStep1Response getStep1() {
        LocalDate minDate = ledgerPolicy.minimumDate();
        LocalDate maxDate = ledgerPolicy.maximumDate();

        List<Integer> years = DateRangeUtils.getYearsInRange(minDate.getYear(), maxDate.getYear());
        List<Integer> months = DateRangeUtils.getMonthsInRange(1, maxDate.getMonthValue());
        List<Integer> days = DateRangeUtils.getDaysInRange(1, maxDate.getDayOfMonth());

        return LedgerWriteStep1Response.of(years, months, days);
    }

    public LedgerWriteStep2Response getStep2(String type, String date) {
        //1. 가계부 유형과 날짜 확인
        CategoryType ledgerType = parseCategoryTypeOrDefault(type);
        LocalDate localDate = DateTimeUtil.parseDateOrToday(date);

        //2. 카테고리 목록 조회
        List<CategoryItem> categories = categoryReadService.getMiddleCategories(ledgerType);

        //3. 제목 변환
        String title = DateTimeUtil.formatDate(localDate, DatePatterns.KOREAN_DATE_WITH_DAY.getPattern());

        //4. 이미지 생성
        int availImgCnt = memberReadService.getAvailableImageCount(currentUser.getMemberId());

        List<ImageSlot> imageSlots = ledgerPolicy.imageSlots(availImgCnt);

        return LedgerWriteStep2Response.of(
                title,
                ledgerType,
                categories,
                imageSlots
        );

    }

    public List<Integer> fetchDateOptionsByUnit(String unit, String date) {
        DateUnit dateUnit = DateUnit.from(unit);

        dateUnit.validateDate(date);

        return ledgerPolicy.dateOptions(dateUnit, date);
    }

    @Transactional
    public void processLedgerRegistration(LedgerWriteRequest request) {
        String memberId = currentUser.getMemberId();

        registerValidator.validate(request);

        Ledger ledger = ledgerCommandService.create(memberId, request);
        ledgerPolicy.validateCreatable(ledger);

        Long ledgerId = ledgerCommandService.save(ledger);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            imageService.processImageUpload(memberId, ledgerId, request.getImages());
        }
    }


    //===== getStep2 보조 메서드 =====
    private CategoryType parseCategoryTypeOrDefault(String type) {
        try {
            return CategoryType.from(type);
        } catch (NoSuchElementException e) {
            return CategoryType.INCOME;
        }
    }

}