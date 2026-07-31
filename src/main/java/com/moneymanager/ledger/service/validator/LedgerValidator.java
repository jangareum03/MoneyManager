package com.moneymanager.ledger.service.validator;

import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.global.exception.exception.BusinessException;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.exception.log.DeveloperLogInfo;
import com.moneymanager.global.validation.BaseImageValidator;
import com.moneymanager.global.validation.DateValidator;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.moneymanager.global.exception.code.CommonErrorCode.*;
import static com.moneymanager.global.util.string.StringUtil.isNullOrBlank;
import static com.moneymanager.global.util.string.StringUtil.matchesPattern;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.validation<br>
 * 파일이름       : LedgerValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 22<br>
 * 설명              : 가계부 관련 검증 로직을 처리하는 클래스
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
 * 		 	  <td>26. 1. 22.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class LedgerValidator extends BaseImageValidator {

	private final String work = "가계부 검증";

	/**
	 * 가계부 등록 요청 시 입력된 데이터가 정상적인지 검증합니다.
	 * <p>
	 *     입력값이 없거나 형식이 올바르지 않으면, {@link BusinessException}이 발생합니다.
	 *     <ul>
	 *         검증 항목
	 *         <li>
	 *             필수정보:	거래날짜, 카테고리 코드, 금액, 금액유형
	 *         </li>
	 *         <li>
	 *             선택정보:	고정주기, 메모, 장소
	 *         </li>
	 *     </ul>
	 * </p>
	 *
	 * @param request	가계부 작성 요청 데이터
	 */
	public void register(LedgerWriteRequest request) {
		if(request == null) {
			throw ValidationException.of(
					REQUIRED_VALUE,
					DeveloperLogInfo.of(work, "요청 객체 없음", LedgerWriteRequest.class, null),
					"가계부를 등록할 수 없습니다."
			);
		}

		//필수정보 검증
		DateValidator.validateLedgerDate(request.getDate());
		validateCategory(request.getCategoryCode());
		validateAmount(request.getAmount());
		validatePaymentType(request.getPaymentType());

		//선택정보 검증
		validateFixCycle(request.getFixCycle());
		validateMemo(request.getMemo());
	}


	public void update(LedgerUpdateRequest request) {
		//1. 객체 null 검증
		if(request == null) {
			throw ValidationException.of(
					REQUIRED_VALUE,
					DeveloperLogInfo.of(work,  "요청 객체 없음", LedgerUpdateRequest.class, null),
					"가계부를 수정할 수 없습니다."
			);
		}

		//2. 필수정보 검증
		validateCategory(request.getCategoryCode());
		validateAmount(request.getAmount());
		validatePaymentType(request.getPaymentType());

		//3. 선택정보 검증
		validateFixCycle(request.getFixCycle());
		validateMemo(request.getMemo());
	}

	//가계부 카테고리 검증
	private void validateCategory(String categoryCode) {
		if(isNullOrBlank(categoryCode)) {
			throw ValidationException.of(
					REQUIRED_VALUE,
					DeveloperLogInfo.of(work, "카테고리 없음", "category", categoryCode),
					"카테고리를 선택해주세요."
			);
		}

		if(!matchesPattern(categoryCode, "\\d{6}")) {
			throw ValidationException.of(
					INVALID_FORMAT,
					DeveloperLogInfo.of(work, "카테고리 형식 불일치", "category", categoryCode)
							.addOption("format", "6자리 숫자 (예: 123456)"),
					"허용하지 않은 카테고리입니다."
			);
		}
	}

	//가계부 금액 검증
	private void validateAmount(Long amount){
		if(amount == null) {
			throw ValidationException.of(
					REQUIRED_VALUE,
					DeveloperLogInfo.of(work, "금액 없음", "amount", null),
					"금액을 입력해주세요."
			);
		}
	}

	private void validatePaymentType(String paymentType) {
		if(isNullOrBlank(paymentType)) {
			throw ValidationException.of(
					REQUIRED_VALUE,
					DeveloperLogInfo.of(work, "금액 유형 없음", "paymentType", paymentType),
					"금액 유형을 선택해주세요."
			);
		}
	}

	//가계부 고정주기 검증
	private void validateFixCycle(String fixCycle) {
		if(!isNullOrBlank(fixCycle) && !matchesPattern(fixCycle, "^[a-zA-Z]$")) {
			throw ValidationException.of(
					INVALID_FORMAT,
					DeveloperLogInfo.of(work, "고정주기 형식 불일치", "fixCycle", fixCycle)
							.addOption("format", "영어"),
					"고정주기를 선택해주세요."
			);
		}
	}

	//가계부 메모 내용 검증
	private void validateMemo(String memo) {
		if(!isNullOrBlank(memo) && memo.length() > 150) {
			throw BusinessException.of(
					OUT_OF_RANGE,
					DeveloperLogInfo.of(work, "최대 길이 초과", "memo", String.valueOf(memo.length()))
							.addOption("min", 0)
							.addOption("max", 150),
					"메모는 최대 150자까지 입력해주세요."
			);
		}
	}


	@Override
	public void validateImage(MultipartFile file) {
		checkSize(file.getSize());
		checkExtension(file.getOriginalFilename(), List.of("png", "jpg", "jpeg"));
		checkIsImage(file.getContentType());
		checkHeader(file, List.of("89504E47", "FFD8FFE0"));	//png(89504E47), jpg(FFD8FFE0)
	}
}