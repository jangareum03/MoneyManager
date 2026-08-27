package com.moneymanager.ledger.service.read;

import com.moneymanager.global.exception.code.ErrorCode;
import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.ledger<br>
 * 파일이름       : LedgerReadService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 4<br>
 * 설명              : 가계부 정보를 조회하는 클래스
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
 * 		 	  <td>26. 1. 4</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 1. 9</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 이름] getInitialData → getWriteStep1Data</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerReadService {

	private final LedgerRepository ledgerRepository;

	public List<Ledger> getOwnerLedgers(String memberId, List<String> codes) {
		List<Ledger> ledgerList = ledgerRepository.findByCodeIn(codes);

		return ledgerList.stream()
				.filter(ledger -> isLedgerAuthor(memberId, ledger))
				.toList();
	}

	public Ledger getOwnerLedger(String memberId, String code) {
		Ledger ledger = ledgerRepository.findByCode(code);

		if(ledger == null) {
			throw new ApplicationException(
					DATA_NOT_FOUND,
					LogContent.of(
							"가계부 조회",
							Ledger.class,
							"code", code
					)
			);
		}

		if(!isLedgerAuthor(memberId, ledger)) {
			throw new ApplicationException(
					ErrorCode.OWNER_ONLY,
					LogContent.of(
							"가계부 작성자 확인",
							Ledger.class,
							"code", code,
							"requester", memberId
					)
			);
		}

		return ledger;
	}


	//===== 유틸 메서드 =====
	private boolean isLedgerAuthor(String memberId, Ledger ledger) {
		return  ledger.getMemberId().equals(memberId);
	}

}