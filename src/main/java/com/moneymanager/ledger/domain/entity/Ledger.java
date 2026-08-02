package com.moneymanager.ledger.domain.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.global.exception.code.CommonErrorCode;
import com.moneymanager.ledger.service.policy.Policy;
import com.moneymanager.global.domain.enums.DatePatterns;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.FixedYN;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.global.exception.exception.BusinessException;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.exception.log.DeveloperLogInfo;
import com.moneymanager.global.util.date.DateTimeUtil;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.moneymanager.global.exception.code.CommonErrorCode.INVALID_VALUE;
import static com.moneymanager.global.exception.code.LedgerErrorCode.OUT_OF_RANGE;
import static com.moneymanager.global.exception.code.LedgerErrorCode.POLICY_VIOLATION;
import static com.moneymanager.global.util.string.StringUtil.isNullOrBlank;


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
	private final String code;							//가계부 코드(외부용)
    private final String memberId;					//작성자(회원 고유번호)
	private LocalDate date;								//거래 날짜
    private String category;							//카테고리 코드
	private String memo;									//메모

	private Money money;								//금액정보
	private Place place;									//장소

	private FixedYN fix;									//고정여부
	private FixCycle fixCycle;							//고정주기

	private final LocalDateTime createdAt;			//등록일
    private LocalDateTime updatedAt;			//수정일


	public static Ledger create(String memberId, LedgerWriteRequest request){
		String code = UlidCreator.getUlid().toString();

		//필수값 검증
		validateDate(request.getDate());
		validateCategory(request.getCategoryCode());
		validateFixInfo(request.getFixed(), request.getFixCycle());

		//선택값 검증
		validateMemo(request.getMemo());

		return Ledger.builder()
				.code(code)
				.memberId(memberId)
				.date(DateTimeUtil.parseDateFromYyyyMMdd(request.getDate()))
				.fix(FixedYN.from(request.getFixed()))
				.fixCycle(request.getFixCycle() != null ? FixCycle.from(request.getFixCycle()) : null)
				.category(request.getCategoryCode())
				.memo(request.getMemo())
				.money(Money.of(request.getAmount(), PaymentType.from(request.getPaymentType())))
				.place(Place.of(request.getPlaceName(), request.getRoadAddress(), request.getDetailAddress()))
				.build();
	}

	public void changeFixInfo(String fixed, String fixCycle) {
		validateFixInfo(fixed, fixCycle);

		FixedYN newFix = FixedYN.from(fixed);
		FixCycle newCycle = fixCycle == null ? null : FixCycle.from(fixCycle);

		if(Objects.equals(this.fix, newFix) && Objects.equals(this.fixCycle, newCycle)) {
			return;
		}

		this.fix = newFix;
		this.fixCycle = newCycle;
		this.updatedAt = LocalDateTime.now();
	}

	public void changeCategory(String category) {
		validateCategory(category);

		if( !this.category.equals(category) ) {
			this.category = category;
			this.updatedAt = LocalDateTime.now();
		}
	}

	public void changeMemo(String memo) {
		validateMemo(memo);

		if(!Objects.equals(this.memo, memo)) {
			this.memo = memo;
			this.updatedAt = LocalDateTime.now();
		}
	}

	public void changeMoney(Money money) {
		if(this.money.equals(money)) return;

		this.money = money;
		this.updatedAt = LocalDateTime.now();
	}

	public void changePlace(Place place) {
		if(Objects.equals(this.place, place)) {
			return;
		}

		this.place = place;
		this.updatedAt = LocalDateTime.now();
	}


	// ===== 필수값 검증 =====
	private static void validateDate(String date) {
		String format = DatePatterns.DATE.getPattern();

		LocalDate transDate = DateTimeUtil.parseDateFromYyyyMMdd(date);		//가계부 거래날짜
		LocalDate today = LocalDate.now();	//오늘날짜

		LocalDate fiveYearsAgo = today.minusYears(Policy.LEDGER_MAX_YEAR);	//오늘 기준 5년 전
		if(!DateTimeUtil.isDateInRange(transDate, fiveYearsAgo, today)) {
			throw BusinessException.of(
					OUT_OF_RANGE,
					DeveloperLogInfo.of("가계부 검증", "거래날짜 허용 범위 초과", "date", date)
							.addOption("min", DateTimeUtil.formatDate(fiveYearsAgo, format))
							.addOption("max", DateTimeUtil.formatDate(today, format)),
					String.format("최근 %d년 이내 날짜만 가능합니다.", Policy.LEDGER_MAX_YEAR)
			);
		}
	}

	private static void validateCategory(String code) {
		if(!(code.startsWith("01") || code.startsWith("02"))) {
			throw ValidationException.of(
					INVALID_VALUE,
					DeveloperLogInfo.of("가계부 검증", "허용되지 않은 카테고리 코드", "category", code)
							.addOption("allowedPrefix", "01, 02"),
					"사용할 수 없는 카테고리 입니다."
			);
		}

		//TODO: 범위 검증 추가
	}

	private static void validateFixInfo(String fix, String cycle) {
		FixedYN fixedYN = FixedYN.from(fix);

		if(fixedYN == FixedYN.REPEAT) {
			FixCycle.from(cycle);

			return;
		}

		if(cycle != null) {
			throw ValidationException.of(
					POLICY_VIOLATION,
					DeveloperLogInfo.of("가계부 검증", "고정 여부와 주기 불일치", "fixCycle", cycle)
							.addOption("policy", "고정이 아닌 경우 주기 설정 불가"),
					"고정이 아닌 경우에는 주기를 설정할 수 없습니다. 고정 여부를 확인해주세요."
			);
		}
	}

	// ===== 선택값 검증 =====
	private static void validateMemo(String memo) {
		if(!isNullOrBlank(memo) && memo.length() > 150) {
			throw BusinessException.of(
					CommonErrorCode.OUT_OF_RANGE,
					DeveloperLogInfo.of("가계부 검증", "길이 초과", "memo", String.valueOf(memo.length()))
							.addOption("min", 0)
							.addOption("max", 150),
					"메모는 최대 150자까지 입력해주세요."
			);
		}
	}

}