package com.moneymanager.ledger.service.command;

import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.global.exception.code.CategoryErrorCode;
import com.moneymanager.global.exception.exception.BusinessException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.response.LedgerDetailResponse;
import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.repository.LedgerRepository;
import com.moneymanager.ledger.service.policy.LedgerDatePolicy;
import com.moneymanager.ledger.service.read.CategoryReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


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


	public Ledger create(String memberId, LedgerWriteRequest request) {
		String code = UlidCreator.getUlid().toString();
		LocalDate date = LocalDate.parse(request.getDate(), LedgerDatePolicy.DATE_FORMATTER);

		Money money = Money.of(request.getAmount(), request.getPaymentType());
		Place place = Place.ofOrNull(request.getPlaceName(), request.getRoadAddress(), request.getDetailAddress());

		if(!categoryReadService.exists(request.getCategoryCode())) {
			throw BusinessException.of(
					CategoryErrorCode.DATA_NOT_FOUND,
					LogContent.of(
							"Ledger 생성",
							LedgerWriteRequest.class,
							"categoryCode", request.getCategoryCode()
					)
			);
		}

		return Ledger.create(
				code, memberId, date, request.getCategoryCode(), request.getFixed(), request.getFixCycle(), request.getMemo(),
				money, place
		);
	}

	public Long save(Ledger ledger) {
		return ledgerRepository.save(ledger);
	}

	@Transactional
	public LedgerDetailResponse update(String code, LedgerUpdateRequest request) {
		return null;
	}

}