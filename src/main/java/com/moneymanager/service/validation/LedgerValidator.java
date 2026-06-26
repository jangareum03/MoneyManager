package com.moneymanager.service.validation;

import com.moneymanager.domain.ledger.dto.request.LedgerUpdateRequest;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.moneymanager.exception.error.ErrorCode.*;
import static com.moneymanager.utils.string.StringUtil.isNullOrBlank;
import static com.moneymanager.utils.string.StringUtil.matchesPattern;

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

	private final String FUNCTION_NAME = "가계부 검증 실패";

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
			throw BusinessException.of(
					LEDGER_INPUT_MISSING,
					FUNCTION_NAME + "   |   reason=객체없음   |   object=LedgerWriteRequest   |   value=null"
			).withUserMessage("가계부를 등록할 수 없습니다.");
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
			throw BusinessException.of(
					LEDGER_INPUT_MISSING,
					FUNCTION_NAME + "   |   reason=객체없음   |   object=LedgerUpdateRequest   |   value=null"
			).withUserMessage("가계부를 수정할 수 없습니다.");
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
			throw BusinessException.of(
					LEDGER_INPUT_NULL,
					FUNCTION_NAME + "   |   reason=필수값누락   |   field=categoryCode   |   value=" + categoryCode
			).withUserMessage("카테고리를 선택해주세요.");
		}

		if(!matchesPattern(categoryCode, "\\d{6}")) {
			throw BusinessException.of(
					LEDGER_INPUT_INVALID,
					FUNCTION_NAME + "   |   reason=형식오류   |   field=categoryCode   |   expectedFormat=6자리 숫자 (예: 123456)   |   value=" + categoryCode
			).withUserMessage("허용하지 않은 카테고리입니다.");
		}
	}

	//가계부 금액 검증
	private void validateAmount(Long amount){
		if(amount == null) {
			throw BusinessException.of(
					LEDGER_INPUT_NULL,
					FUNCTION_NAME + "   |   reason=필수값누락   |   field=amount   |   value=" + null
			).withUserMessage("금액을 입력해주세요.");
		}
	}

	private void validatePaymentType(String paymentType) {
		if(isNullOrBlank(paymentType)) {
			throw BusinessException.of(
					LEDGER_INPUT_NULL,
					FUNCTION_NAME + "   |   reason=필수값누락   |   field=paymentType   |   value=" + paymentType
			).withUserMessage("금액 유형을 선택해주세요.");
		}
	}

	//가계부 고정주기 검증
	private void validateFixCycle(String fixCycle) {
		if(!isNullOrBlank(fixCycle) && !matchesPattern(fixCycle, "^[a-zA-Z]$")) {
			throw BusinessException.of(
					LEDGER_INPUT_INVALID,
					FUNCTION_NAME + "   |   reason=형식오류   |   field=fixCycle   |   expectedFormat=1자리 영어 (예: w)   |   value=" + fixCycle
			).withUserMessage("고정주기를 선택해주세요.");
		}
	}

	//가계부 메모 내용 검증
	private void validateMemo(String memo) {
		if(!isNullOrBlank(memo) && memo.length() > 150) {
			throw BusinessException.of(
					LEDGER_INPUT_LENGTH,
					FUNCTION_NAME + "   |   reason=길이오류   |   field=memo   |   maxLength=150   |   value=" + memo.length()
			).withUserMessage("메모는 최대 150자까지 입력해주세요.");
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