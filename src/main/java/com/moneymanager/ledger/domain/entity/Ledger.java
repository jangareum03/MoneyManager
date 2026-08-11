package com.moneymanager.ledger.domain.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.global.domain.enums.DatePatterns;
import com.moneymanager.global.util.date.DateTimeUtil;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.FixedType;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.ledger.service.policy.Policy;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


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

	private FixedType fix;									//고정여부
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
				.fix(FixedType.from(request.getFixed()))
				.fixCycle(request.getFixCycle() != null ? FixCycle.from(request.getFixCycle()) : null)
				.category(request.getCategoryCode())
				.memo(request.getMemo())
				.money(Money.of(request.getAmount(), PaymentType.from(request.getPaymentType())))
				.place(Place.of(request.getPlaceName(), request.getRoadAddress(), request.getDetailAddress()))
				.build();
	}

	public void changeFixInfo(String fixed, String fixCycle) {
		validateFixInfo(fixed, fixCycle);

		FixedType newFix = FixedType.from(fixed);
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
	}

	private static void validateCategory(String code) {

	}

	private static void validateFixInfo(String fix, String cycle) {
		FixedType fixedYN = FixedType.from(fix);

		if(fixedYN == FixedType.REPEAT) {
			FixCycle.from(cycle);

			return;
		}
	}

	// ===== 선택값 검증 =====
	private static void validateMemo(String memo) {

	}

}