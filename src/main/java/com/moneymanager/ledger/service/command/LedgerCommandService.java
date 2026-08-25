package com.moneymanager.ledger.service.command;

import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.ObjectUtils;
import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.FixedType;
import com.moneymanager.ledger.repository.LedgerRepository;
import com.moneymanager.ledger.service.policy.LedgerDatePolicy;
import com.moneymanager.ledger.service.read.CategoryReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.moneymanager.global.exception.code.ErrorCode.INVALID_VALUE;


/**
 * <p>
 * 패키지이름    : com.moneymanager.service.ledger<br>
 * 파일이름       : LedgerCommandService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 10<br>
 * 설명              : 가계부 정보를 변경하는 클래스
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
 * 		 	  <td>26. 1. 10.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class LedgerCommandService {

	private final CategoryReadService categoryReadService;

	private final LedgerRepository ledgerRepository;


	public Ledger toCreateEntity(String memberId, LedgerWriteRequest request) {
		String code = UlidCreator.getUlid().toString();
		LocalDate date = LocalDate.parse(request.getDate(), LedgerDatePolicy.DATE_FORMATTER);

		FixedType fix = FixedType.from(request.getFixed());
		FixCycle cycle = ObjectUtils.getValueOrNull(request.getFixCycle(), FixCycle::from);

		Money money = Money.of(request.getAmount(), request.getPaymentType());
		Place place = Place.ofOrNull(request.getPlaceName(), request.getRoadAddress(), request.getDetailAddress());

		if(!categoryReadService.exists(request.getCategoryCode())) {
			throw new ApplicationException(
					INVALID_VALUE,
					LogContent.of(
							"Ledger 생성",
							LedgerWriteRequest.class,
							"categoryCode", request.getCategoryCode()
					)
			);
		}

		return Ledger.of(
				code, memberId, date, request.getCategoryCode(), fix, cycle, request.getMemo(),
				money, place
		);
	}

	public Long save(Ledger ledger) {
		return ledgerRepository.save(ledger);
	}

	public void updateLedger(LedgerUpdateRequest request, Ledger ledger) {
		//1. 카테고리 코드 존재여부 확인
		if(!categoryReadService.exists(request.getCategoryCode())) {
			throw new ApplicationException(
					INVALID_VALUE,
					LogContent.of(
							"Ledger 수정",
							LedgerUpdateRequest.class,
							"categoryCode", request.getCategoryCode()
					)
			);
		}

		//2. 가계부 정보 변경
		ledger.changeCategory(request.getCategoryCode());
		ledger.changeFixInfo(request.getFixed(), request.getFixCycle());
		ledger.changeMoney(Money.of(request.getAmount(), request.getPaymentType()));
		ledger.changePlace(Place.ofOrNull(request.getPlaceName(), request.getRoadAddress(), request.getDetailAddress()));

		ledger.changeMemo(request.getMemo());

		//3. 변경된 가계부 저장
		if(ledger.isChanged()) {
			ledgerRepository.save(ledger);
		}

	}

}