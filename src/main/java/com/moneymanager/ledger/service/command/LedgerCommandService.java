package com.moneymanager.ledger.service.command;

import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.response.LedgerDetailResponse;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.global.exception.exception.BusinessException;
import com.moneymanager.global.log.DeveloperLogInfo;
import com.moneymanager.ledger.repository.LedgerRepository;
import com.moneymanager.global.security.utils.SecurityUtil;
import com.moneymanager.ledger.service.read.LedgerReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.moneymanager.global.exception.code.LedgerErrorCode.NOT_FOUND_DATA;


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
@Slf4j
public class LedgerCommandService {

	private final SecurityUtil securityUtil;

	private final LedgerReadService ledgerReadService;
	private final LedgerImageCommandService imageCommandService;

	private final LedgerRepository ledgerRepository;


	@Transactional
	public void register(LedgerWriteRequest request) {
		String memberId = securityUtil.getMemberId();

		Ledger ledger = createLedger(memberId, request);

		Ledger savedLedger = save(ledger);

		imageCommandService.processImages(savedLedger, request);
	}

	private Ledger createLedger(String memberId, LedgerWriteRequest request) {
		return Ledger.create(memberId, request);
	}

	@Transactional
	public LedgerDetailResponse update(String code, LedgerUpdateRequest request) {
		//1. 인증된 회원 조회
		String memberId = securityUtil.getMemberId();

		//2. 기존 가계부 조회
		Ledger ledger = ledgerReadService.getLedger(memberId, code);

		//3. 수정값 반영
		updateLedgerFields(ledger, request);

		//4. 가계부 저장
		save(ledger);

		//5. 이미지 삭제 및 추가
		imageCommandService.processImages(ledger, request);

		//6. 가계부 조회 후 반환
		return ledgerReadService.getDetailData(code);
	}

	private void updateLedgerFields(Ledger ledger, LedgerUpdateRequest updateRequest) {
		//필수정보
		ledger.changeCategory(updateRequest.getCategoryCode());
		ledger.changeFixInfo(updateRequest.getFixed(), updateRequest.getFixCycle());

		Money updateMoney = Money.of(updateRequest.getAmount(), PaymentType.from(updateRequest.getPaymentType()));
		ledger.changeMoney(updateMoney);

		//선택정보
		ledger.changeMemo(updateRequest.getMemo());

		Place updatePlace = Place.of(updateRequest.getPlaceName(), updateRequest.getRoadAddress(), updateRequest.getDetailAddress());
		ledger.changePlace(updatePlace);
	}

	public Ledger save(Ledger ledger) {
		Long id;

		if( ledger.getId() == null ) {
			id = ledgerRepository.insert(ledger);
		}else {
			int updatedRow = ledgerRepository.update(ledger);

			if(updatedRow != 1) {
				throw BusinessException.of(
						NOT_FOUND_DATA,
						DeveloperLogInfo.of("가계부 수정", "수정 대상 없음", Ledger.class, DeveloperLogInfo.valueOf("memberId", ledger.getMemberId(), "ledgerId", ledger.getId())),
						"가계부를 수정할 수 없습니다. 잠시 후 다시 시도해주세요."
				);
			}

			id = ledger.getId();
		}

		return ledgerRepository.findById(id);
	}

}
