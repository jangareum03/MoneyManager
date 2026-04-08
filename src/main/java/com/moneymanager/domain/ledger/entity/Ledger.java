package com.moneymanager.domain.ledger.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.domain.global.enums.DatePatterns;
import com.moneymanager.domain.global.Policy;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.enums.FixCycle;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.enums.AmountType;
import com.moneymanager.domain.ledger.vo.AmountInfo;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.utils.date.DateTimeUtils;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.moneymanager.domain.global.enums.RegexPattern.ADDRESS_DETAIL_NAME;
import static com.moneymanager.exception.error.ErrorCode.*;
import static com.moneymanager.utils.date.DateTimeUtils.isDateInRange;
import static com.moneymanager.utils.validation.ValidationUtils.isNullOrBlank;
import static com.moneymanager.utils.validation.ValidationUtils.matchesPattern;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.domain.ledger.entity<br>
 *  * 파일이름       : Ledger<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 11. 15<br>
 *  * 설명              : LEDGER 테이블과 매칭되는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>22. 11. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>최초 생성(버전 1.0)</td>
 *		 	</tr>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>22. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Builder
@Getter
public class Ledger {
    private Long id;											//가계부 번호(내부용)
	private String code;									//가계부 코드(외부용)
    private String memberId;							//작성자(회원 고유번호)
	private LocalDate date;								//거래 날짜
    private String category;							//카테고리 코드
	private String memo;									//메모

	private Long amount;								//금액
	private AmountType amountType;			//금액 유형

	private Place place;									//장소

	private FixedYN fix;								//고정여부
	private FixCycle fixCycle;						//고정주기

	private LocalDateTime createdAt;			//등록일
    private LocalDateTime updatedAt;			//수정일

	/**
	 *	요청 객체({@link LedgerWriteRequest}) 필드값이 비즈니스 규칙과 일치하는지 검증하고,
	 * 검증에 성공하면 {@link Ledger} 객체를 생성합니다.
	 *<p>비즈니스 규칙: </p>
	 * <ul>
	 *     <li><b>필수정보</b>
	 *     	<ul>
	 *        		<li>거래날짜: 현재로부터 5년전 날짜인지 확인</li>
	 *      	  	<li>카테고리 코드: 01/02로 시작하는지 확인</li>
	 *       	 	<li>금액: 0보다 큰지 확인</li>
	 *   	     	<li>금액유형: 금액유형이 유효한지 확인</li>
	 *     	</ul>
	 *     </li>
	 *     <li><b>선택정보</b>
	 *     	<ul>
	 *         	<li>고정주기: 고정주기가 유효한지 확인</li>
	 *         	<li>장소: 장소명과 기본주소 둘 다 존재하는지 확인, 상세주소 형식이 일치하는지 확인</li>
	 *     	</ul>
	 *     </li>
	 * </ul>
	 *
	 *  <p>검증 실패 시 {@link BusinessException}이 발생합니다.</p>
	 *
	 * @param memberId	가계부 작성한 회원번호
	 * @param request	가계부 작성 요청 데이터
	 * @return 검증된 정보를 기반으로 생성된 {@link Ledger} 객체
	 * @throws BusinessException    비즈니스 규칙과 일치하지 않을 경우 발생
	 */
	public static Ledger create(String memberId, LedgerWriteRequest request){
		String code = UlidCreator.getUlid().toString();

		//필수값
		validateDate(request.getDate());
		validateCategory(request.getCategoryCode());
		validateAmount(request.getAmount());
		validateAmountType(request.getAmountType());

		//선택값
		validateFixCycle(request.isFixed(), request.getFixCycle());
		validatePlace(request.getPlaceName(), request.getRoadAddress(), request.getDetailAddress());

		return Ledger.builder()
				.code(code)
				.memberId(memberId)
				.date(DateTimeUtils.parseDateFromYyyyMMdd(request.getDate()))
				.fix(FixedYN.of(request.isFixed()))
				.fixCycle(request.getFixCycle() != null ? FixCycle.of(request.getFixCycle()) : null)
				.category(request.getCategoryCode())
				.amount(request.getAmount())
				.amountType(AmountType.of(request.getAmountType()))
				.memo(request.getMemo())
				.place(new Place(request.getPlaceName(), request.getRoadAddress(), request.getDetailAddress()))
				.build();
	}

	//날짜 규칙검증
	private static void validateDate(String date) {
		try{
			LocalDate transDate = DateTimeUtils.parseDateFromYyyyMMdd(date);		//가계부 거래날짜
			LocalDate today = LocalDate.now();	//오늘날짜

			LocalDate fiveYearsAgo = today.minusYears(Policy.LEDGER_MAX_YEAR);	//오늘 기준 5년 전
			if(!isDateInRange(transDate, fiveYearsAgo, today)) {
				throw BusinessException.of(
						LEDGER_INPUT_RANGE,
						String.format("최근 %d년 이내에 날짜만 가능합니다.", Policy.LEDGER_MAX_YEAR),
						String.format("가계부 검증 실패   |   reason=범위오류   |   field=date   |   min=%s   |   max=%s   |   value=%s", DateTimeUtils.formatDate(fiveYearsAgo, DatePatterns.DATE.getPattern()), DateTimeUtils.formatDate(today, DatePatterns.DATE.getPattern()), date)
				);
			}
		}catch (IllegalArgumentException e) {
			String format = "yyyyMMdd";

			throw BusinessException.of(
					LEDGER_INPUT_FORMAT,
					String.format("날짜는 %s 형식으로 입력해주세요.", format),
					String.format("가계부 검증 실패   |   %s", e.getMessage()),
					e
			);
		}
	}

	//카테고리 규칙검증
	private static void validateCategory(String categoryCode) {
		if(!(categoryCode.startsWith("01") || categoryCode.startsWith("02"))) {
			throw BusinessException.of(
					LEDGER_INPUT_FORMAT,
					"사용할 수 없는 카테고리 입니다.",
					"가계부 검증 실패   |   reason=형식오류   |   field=categoryCode   |   expectedFormat=01/02로 시작하는 6자리 숫자 (예: 010101)   |   value="+categoryCode
			);
		}

		//TODO: 범위 검증 추가
	}

	//금액 규칙검증
	private static void validateAmount(Long amount) {
		if(amount < 1) {
			throw BusinessException.of(
					LEDGER_INPUT_RANGE,
					"금액은 1 이상 입력해주세요.",
					"가계부 검증 실패   |   reason=범위오류   |   field=amount   |   min=1   |   value="+amount
			);
		}
	}

	//금액유형 규칙
	private static void validateAmountType(String type) {
		try{
			AmountType.of(type);
		}catch (IllegalArgumentException e) {
			throw BusinessException.of(
					LEDGER_INPUT_INVALID,
					"사용할 수 없는 금액유형 입니다.",
					"가게부 검증 실패   |   " + e.getMessage(),
					e
			);
		}
	}

	//고정주기 규칙
	private static void validateFixCycle(boolean fix, String fixCycle) {
		//고정이 아닌 경우 주기가 없어야 함
		if(!fix) {
			if(!isNullOrBlank(fixCycle)) {
				throw BusinessException.of(
						LEDGER_POLICY_NOT_ALLOWED,
						"고정이 아닌 경우에는 주기를 설정할 수 없습니다. 고정 여부를 확인해주세요.",
						"가계부 검증 실패   |   reason=정책위반   |   field=fixCycle   |   policy=고정이 아닌데 주기가 존재   |   value=" + fixCycle
				);
			}

			return;
		}

		//고정인 경우
		try{
			FixCycle.of(fixCycle);
		}catch (IllegalArgumentException e) {
			throw BusinessException.of(
					LEDGER_INPUT_INVALID,
					"사용할 수 없는 고정주기 입니다.",
					"가계부 검증 실패   |   " + e.getMessage(),
					e
			);
		}
	}

	//장소 규칙
	private static void validatePlace(String placeName, String roadAddress, String detailAddress) {
		if(isNullOrBlank(placeName) ^ isNullOrBlank(roadAddress)) {
			throw BusinessException.of(
					LEDGER_POLICY_NOT_ALLOWED,
					"장소명과 기본주소 둘 다 있어야 합니다.",
					"가계부 검증 실패   |   reason=정책위반   |   policy=장소명과 기본주소 둘 다 필요   |   value={placeName:" + placeName + ", roadAddress:"+roadAddress
			);
		}

		if(detailAddress != null && !matchesPattern(detailAddress, ADDRESS_DETAIL_NAME.getPattern())) {
			throw BusinessException.of(
					LEDGER_INPUT_FORMAT,
					"상세 주소는 한글,숫자,영문, -, (, ), /, .만 입력 가능합니다.",
					"가계부 검증 실패   |   reason=형식오류   |   field=detailAddress   |   expectedFormat=한글, 숫자, 영문, 공백, -, (, ), /, .   |   value=" + detailAddress
			);
		}
	}

	public void updateBasicInfo(String category, String memo) {
		if( !this.category.equals(category) ) {
			this.category = category;
			this.updatedAt = LocalDateTime.now();
		}

		if( !isNullOrBlank(this.memo) && !this.memo.equals(memo) ) {
			this.memo = memo;
			this.updatedAt = LocalDateTime.now();
		}
	}

	public void updateFixCycle(String fixCycle) {
		if(isNullOrBlank(fixCycle)) {
			throw new IllegalArgumentException("가계부 고정주기 없음");
		}

		this.fixCycle = FixCycle.of(fixCycle);
	}

	public void updateAmount(AmountInfo amountInfo) {
		AmountInfo entityAmount = new AmountInfo(amount, amountType);

		if( entityAmount.equals(amountInfo) ) return;

		this.amount = amountInfo.getAmount();
		this.amountType = amountInfo.getType();
	}

	public void updatePlace(Place place) {
		if(isNullOrBlank(place.getName()) && isNullOrBlank(place.getRoadAddress())) {
			if( place.equals(this.place) ) return;
		}

		this.place = place;
	}

	public void updateTime() {
		this.updatedAt = LocalDateTime.now();
	}
}