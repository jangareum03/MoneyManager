package com.moneymanager.service.ledger;

import com.moneymanager.domain.ledger.dto.request.LedgerUpdateRequest;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.vo.Money;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ServiceAction;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import static com.moneymanager.exception.error.ErrorCode.*;


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
	public void registerLedger(LedgerWriteRequest request) {
		ServiceAction action = ServiceAction.LEDGER_REGISTER;

		try{
			String memberId = securityUtil.getMemberId();

			Ledger ledger = createLedger(memberId, request);

			Ledger savedLedger = save(ledger);

			imageCommandService.processImages(savedLedger, request);
		}catch (BusinessException e) {
			throw e.withService(action)
					.withUserMessage("가계부 등록 중 문제가 발생했습니다. 다시 시도해 주세요.");
		}
	}

	private Ledger createLedger(String memberId, LedgerWriteRequest request) {
		return Ledger.create(memberId, request);
	}

	@Transactional
	public void update(String code, LedgerUpdateRequest request) {
		ServiceAction action = ServiceAction.LEDGER_EDIT;

		try{
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
		}catch(BusinessException e) {
			throw e.withService(action);
		}
	}

	private void updateLedgerFields(Ledger ledger, LedgerUpdateRequest updateRequest) {
		//필수정보
		ledger.updateCategory(updateRequest.getCategoryCode());
		ledger.updateFixInfo(updateRequest.getFixed(), updateRequest.getFixCycle());

		Money updateMoney = new Money(updateRequest.getAmount(), updateRequest.getPaymentType());
		ledger.updateMoney(updateMoney);

		//선택정보
		ledger.updateMemo(updateRequest.getMemo());

		Place updatePlace = new Place(updateRequest.getPlaceName(), updateRequest.getRoadAddress(), updateRequest.getDetailAddress());
		ledger.updatePlace(updatePlace);
	}

	public Ledger save(Ledger ledger) {
		try{
			Long id;

			if( ledger.getId() == null ) {
				id = ledgerRepository.insert(ledger);
			}else {
				int updatedRow = ledgerRepository.update(ledger);

				if(updatedRow != 1) {
					throw BusinessException.of(
							LEDGER_STATE_INVALID,
							"가계부 수정 실패   |   reason=수정된 데이터 없음   |   object=Ledger   |   value=" + ledger.getId()
					);
				}

				id = ledger.getId();
			}

			Ledger savedLedger = ledgerRepository.findById(id);
			if(savedLedger == null) {
				throw BusinessException.of(
						LEDGER_TARGET_NOT_FOUND,
						"가계부 조회 실패   |   reason=객체없음   |   object=Ledger   |   value=" + id
				);
			}

			return savedLedger;
		}catch (DuplicateKeyException e) {
			throw BusinessException.of(
					LEDGER_COLLISION_UNIQUE_CONSTRAINT,
					"가계부 저장 실패   |   reason=DB저장실패   |   detail=중복키   |   object=Ledger   |   value={memberId:" + ledger.getMemberId() + ", ledgerCode:" + ledger.getCode() + "}"
			)
					.withCause(e);
		}catch (DataIntegrityViolationException e) {
			throw BusinessException.of(
					LEDGER_COLLISION_UNIQUE_CONSTRAINT,
					"가계부 저장 실패   |   reason=DB저장실패   |   detail=데이터 무결성 위반   |   object=Ledger   |   value={memberId:" + ledger.getMemberId() + ", ledgerCode:" + ledger.getCode() + "}"
			)
					.withCause(e);
		}
	}

}
