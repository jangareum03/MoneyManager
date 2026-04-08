package com.moneymanager.service.validation;

import com.moneymanager.exception.BusinessException;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.moneymanager.domain.global.enums.RegexPattern.*;
import static com.moneymanager.exception.error.ErrorCode.*;
import static com.moneymanager.utils.validation.ValidationUtils.*;

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
					"가계부를 등록할 수 없습니다.",
					FUNCTION_NAME + "   |   reason=객체없음   |   object=LedgerWriteRequest   |   value=null"
			);
		}

		//필수정보 검증
		DateValidator.validateLedgerDate(request.getDate());
		validateCategory(request.getCategoryCode());
		validateAmount(request.getAmount(), request.getAmountType());

		//선택정보 검증
		validateFixCycle(request.getFixCycle());
		validateMemo(request.getMemo());
		validatePlace(request.getPlaceName(), request.getRoadAddress(), request.getDetailAddress());

		if(request.hasImage()) {
			request.getImage().forEach(this::validateImage);
		}
	}

	//가계부 카테고리 검증
	private void validateCategory(String categoryCode) {
		if(isNullOrBlank(categoryCode)) {
			throw BusinessException.of(
					LEDGER_INPUT_NULL,
					"카테고리를 선택해주세요.",
					FUNCTION_NAME + "   |   reason=필수값누락   |   field=categoryCode   |   value=" + categoryCode
			);
		}

		if(!matchesPattern(categoryCode, "\\d{6}")) {
			throw BusinessException.of(
					LEDGER_INPUT_INVALID,
					"허용하지 않은 카테고리입니다.",
					FUNCTION_NAME + "   |   reason=형식오류   |   field=categoryCode   |   expectedFormat=6자리 숫자 (예: 123456)   |   value=" + categoryCode
			);
		}
	}

	//가계부 금액 검증
	private void validateAmount(Long amount, String amountType) {
		if(amount == null) {
			throw BusinessException.of(
					LEDGER_INPUT_NULL,
					"금액을 입력해주세요.",
					FUNCTION_NAME + "   |   reason=필수값누락   |   field=amount   |   value=" + amount
			);
		}

		if(isNullOrBlank(amountType)) {
			throw BusinessException.of(
					LEDGER_INPUT_NULL,
					"금액 유형을 선택해주세요.",
					FUNCTION_NAME + "   |   reason=필수값누락   |   field=amountType   |   value=" + amountType
			);
		}
	}

	//가계부 고정주기 검증
	private void validateFixCycle(String fixCycle) {
		if(!isNullOrBlank(fixCycle) && !matchesPattern(fixCycle, "^[a-zA-Z]$")) {
			throw BusinessException.of(
					LEDGER_INPUT_INVALID,
					"고정주기를 선택해주세요.",
					FUNCTION_NAME + "   |   reason=형식오류   |   field=fixCycle   |   expectedFormat=1자리 영어 (예: w)   |   value=" + fixCycle
			);
		}
	}

	//가계부 메모 내용 검증
	private void validateMemo(String memo) {
		if(!isNullOrBlank(memo) && memo.length() > 150) {
			throw BusinessException.of(
					LEDGER_INPUT_LENGTH,
					"메모는 최대 150자까지 입력해주세요.",
					FUNCTION_NAME + "   |   reason=길이오류   |   field=memo   |   maxLength=150   |   value=" + memo.length()
			);
		}
	}

	//가계부 주소 검증
	private void validatePlace(String placeName, String roadAddress, String detailAddress) {
		//장소명 확인
		if(!isNullOrBlank(placeName)) {
			if(placeName.length() > 100) {
				throw BusinessException.of(
						LEDGER_INPUT_LENGTH,
						"장소 정보가 올바르지 않습니다. 다시 선택해 주세요.",
						FUNCTION_NAME + "   |   reason=길이오류   |   field=placeName   |   maxLength=100   |   value=" + placeName.length()
				);
			}

			if(!matchesPattern(placeName, ADDRESS_PLACE_NAME.getPattern())) {
				throw BusinessException.of(
						LEDGER_INPUT_INVALID,
						"장소 정보가 올바르지 않습니다. 다시 선택해 주세요.",
						FUNCTION_NAME + "   |   reason=형식오류   |   field=placeName   |   expectedFormat=한글, 숫자, 영어, 공백 (예: CGV 강남점)   |   value=" + placeName
				);
			}
		}

		//기본주소 확인
		if(!isNullOrBlank(roadAddress)) {
			if(roadAddress.length() > 300)
				throw BusinessException.of(
						LEDGER_INPUT_LENGTH,
						"장소 정보가 올바르지 않습니다. 다시 선택해 주세요.",
						FUNCTION_NAME + "   |   reason=길이오류   |   field=roadAddress   |   maxLength=300   |   value=" + roadAddress.length()
				);
			}

			if(!matchesPattern(roadAddress,  ADDRESS_ROAD_NAME.getPattern())) {
				throw BusinessException.of(
						LEDGER_INPUT_INVALID,
						"장소 정보가 올바르지 않습니다. 다시 선택해 주세요.",
						FUNCTION_NAME + "   |   reason=형식오류   |   field=roadAddress   |   expectedFormat=한글, 숫자, ·, -, 공백"
				);
			}


		//상세주소 확인
		if(!isNullOrBlank(detailAddress)) {
			if(detailAddress.length() > 500) {
				throw BusinessException.of(
						LEDGER_INPUT_LENGTH,
						"상세 주소는 최대 500자까지만 입력 가능합니다.",
						FUNCTION_NAME + "   |   reason=길이오류   |   field=detailAddress   |   maxLength=500   |   value=" + detailAddress.length()
				);
			}
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