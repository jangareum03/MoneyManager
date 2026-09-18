package com.moneymanager.ledger.service.application;

import com.moneymanager.global.domain.enums.DatePatterns;
import com.moneymanager.global.security.CurrentUser;
import com.moneymanager.global.util.date.DateRangeUtils;
import com.moneymanager.global.util.date.DateTimeUtil;
import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.response.ImageSlot;
import com.moneymanager.ledger.domain.dto.response.LedgerDetailResponse;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep1Response;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep2Response;
import com.moneymanager.ledger.domain.dto.response.edit.CategoryOptions;
import com.moneymanager.ledger.domain.dto.response.edit.LedgerEditResponse;
import com.moneymanager.ledger.domain.dto.response.item.CategoryItem;
import com.moneymanager.ledger.domain.entity.Category;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.entity.LedgerImage;
import com.moneymanager.ledger.domain.enums.DateUnit;
import com.moneymanager.ledger.domain.enums.LedgerType;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.ledger.service.command.LedgerCommandService;
import com.moneymanager.ledger.service.policy.LedgerPolicy;
import com.moneymanager.ledger.service.read.CategoryReadService;
import com.moneymanager.ledger.service.read.LedgerReadService;
import com.moneymanager.ledger.service.validation.LedgerRegisterValidator;
import com.moneymanager.ledger.service.validation.LedgerUpdateValidator;
import com.moneymanager.member.service.read.MemberReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.application<br>
 * 파일이름       : LedgerService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 13<br>
 * 설명              : 가계부 변경 로직 흐름을 관리하는 클래스
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
    private final LedgerReadService ledgerReadService;

    private final LedgerRegisterValidator registerValidator;
    private final LedgerUpdateValidator updateValidator;
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
        LedgerType ledgerType = parseLedgerTypeOrDefault(type);
        LocalDate localDate = DateTimeUtil.parseDateOrToday(date);

        //2. 카테고리 목록 조회
        List<CategoryItem> categories
                = categoryReadService.getMiddleCategories(ledgerType).stream()
                .map(CategoryItem::from)
                .toList();

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

    public LedgerDetailResponse getDetail(String code) {
        String memberId = currentUser.getMemberId();

        Ledger ledger = ledgerReadService.getOwnerLedger(memberId, code);

        LedgerType type = LedgerType.fromCode(ledger.getCategory());
        Category category = categoryReadService.getCategory(ledger.getCategory());
        PaymentType paymentType = ledger.getMoney().getPaymentType();

        List<LedgerImage> images = imageService.getLedgerImages(ledger.getId());
        List<ImageSlot> imageSlots = ledgerPolicy.imageSlots(
                images.stream()
                        .map(LedgerImage::getImagePath)
                        .toList()
        );

        String title = DateTimeUtil.formatDate(ledger.getDate(), DatePatterns.DATE_DOT_WITH_DAY.getPattern());

        return LedgerDetailResponse.of(
                type,
                title,
                ledger.getMoney().getAmount(),
                category.getName(),
                paymentType,
                ledger.getMemo(),
                imageSlots,
                ledger.getPlace()
        );
    }

    public LedgerEditResponse getEdit(String code) {
        String memberId = currentUser.getMemberId();

        Ledger ledger = ledgerReadService.getOwnerLedger(memberId, code);
        LedgerType type = LedgerType.fromCode(ledger.getCategory());

        String title = DateTimeUtil.formatDate(ledger.getDate(), DatePatterns.KOREAN_DATE_WITH_DAY.getPattern());

        int availImgCnt = memberReadService.getAvailableImageCount(currentUser.getMemberId());
        List<LedgerImage> images = imageService.getLedgerImages(ledger.getId());
        List<ImageSlot> imageSlots = ledgerPolicy.imageSlots(
                availImgCnt,
                images.stream().map(LedgerImage::getImagePath).toList()
        );

        List<String> categories
                = categoryReadService.getAncestorsByCode(ledger.getCategory()).stream()
                .filter(category -> category.getParentCode() != null)
                .map(Category::getCode)
                .toList();

        CategoryOptions options = CategoryOptions.of(
                categories,
                categoryReadService.getMiddleCategories(type)
                        .stream()
                        .map(CategoryItem::from)
                        .toList(),
                categoryReadService.getLowCategories(type)
                        .stream()
                        .filter(low -> Objects.equals(low.getParentCode(), categories.get(0)))
                        .map(CategoryItem::from)
                        .toList()
        );

        return LedgerEditResponse.of(
                type,
                title,
                ledger.getFix(),
                ledger.getFixCycle(),
                ledger.getMoney(),
                ledger.getMemo(),
                imageSlots,
                ledger.getPlace(),
                options
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

        Ledger ledger = ledgerCommandService.toCreateEntity(memberId, request);
        ledgerPolicy.validateCreatable(ledger);

        Long ledgerId = ledgerCommandService.save(ledger);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            imageService.processImageUpload(memberId, ledgerId, request.getImages());
        }
    }

    @Transactional
    public void processLedgerUpdate(String code, LedgerUpdateRequest request) {
        String memberId = currentUser.getMemberId();

        Ledger ledger = ledgerReadService.getOwnerLedger(memberId, code);
        updateValidator.validate(request);

        ledgerCommandService.updateLedger(request, ledger);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            imageService.processImageUpload(memberId, ledger.getId(), request.getImages());
        }
    }

    @Transactional
    public int processLedgerDelete(List<String> codes) {
        String memberId = currentUser.getMemberId();

        if (codes == null || codes.isEmpty()) {
            return 0;
        }

        List<Ledger> ledgers = ledgerReadService.getOwnerLedgers(memberId, codes);
        imageService.processImagesDelete(ledgers);

        return ledgerCommandService.deleteAll(ledgers);
    }


    //===== getStep2 보조 메서드 =====
    private LedgerType parseLedgerTypeOrDefault(String type) {
        try {
            return LedgerType.from(type);
        } catch (NoSuchElementException e) {
            return LedgerType.INCOME;
        }
    }

}